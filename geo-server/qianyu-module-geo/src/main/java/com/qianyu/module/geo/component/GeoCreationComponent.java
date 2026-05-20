package com.qianyu.module.geo.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.qianyu.framework.common.util.json.JsonUtils;
import com.qianyu.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.qianyu.module.geo.component.article.ChatCompletions;
import com.qianyu.module.geo.component.pojo.CreationTaskPOJO;
import com.qianyu.module.geo.dal.dataobject.article.GeoArticleDO;
import com.qianyu.module.geo.dal.dataobject.creation.GeoCreationDO;
import com.qianyu.module.geo.dal.dataobject.file.GeoFileDO;
import com.qianyu.module.geo.dal.dataobject.instruction.GeoInstructionDO;
import com.qianyu.module.geo.dal.dataobject.word.GeoWordDO;
import com.qianyu.module.geo.dal.mysql.article.GeoArticleMapper;
import com.qianyu.module.geo.dal.mysql.creation.GeoCreationMapper;
import com.qianyu.module.geo.dal.mysql.file.GeoFileMapper;
import com.qianyu.module.geo.dal.mysql.instruction.GeoInstructionMapper;
import com.qianyu.module.geo.dal.mysql.word.GeoWordMapper;
import com.qianyu.module.geo.enums.GeoCreationEnum;
import com.qianyu.module.geo.utils.GeoUtils;
import com.qianyu.module.infra.dal.dataobject.file.FileDO;
import com.qianyu.module.infra.dal.mysql.file.FileMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GeoCreationComponent {


    private static final Pattern titlePattern = Pattern.compile(
            "(?s)<title>(.*?)</title>"
    );

    private static final Pattern articlePattern = Pattern.compile(
            "(?s)<article>(.*?)</article>"
    );
    //    private static final String CREATION_INFO_KEY = "geo:creation:info";
    private static final int AVAILABLE_PROCESSORS = GeoUtils.AVAILABLE_PROCESSORS;

    @Autowired
    private GeoCreationMapper geoCreationMapper;

    @Autowired
    private GeoWordMapper geoWordMapper;

    @Autowired
    private GeoInstructionMapper geoInstructionMapper;

    @Autowired
    private GeoFileMapper geoFileMapper;


    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private ChatCompletions chatCompletions;

    private static final ThreadPoolExecutor CONSUMER_POOL = GeoUtils.getPool("consumer-article-exec-pool-%s", AVAILABLE_PROCESSORS * 2);

    private static final String WORD_KEY = "\\{训练词占位符}";
    private static final String TARGET_KEY = "\\{转化词占位符}";
    @Autowired
    private GeoArticleMapper geoArticleMapper;

    @PostConstruct
    public void init() {

        List<GeoCreationDO> geoCreationDOS = geoCreationMapper.selectList(new LambdaQueryWrapperX<GeoCreationDO>()
                .ne(GeoCreationDO::getStatus, GeoCreationEnum.CREATION_SUCCESS.ordinal()));
        Set<GeoCreationDO> collect = geoCreationDOS.stream().filter(geoCreationDO -> GeoCreationEnum.WAIT_CREATION.equals(GeoCreationEnum.enumOf(geoCreationDO.getStatus()))).collect(Collectors.toSet());
        Set<GeoCreationDO> collect2 = geoCreationDOS.stream().filter(geoCreationDO -> GeoCreationEnum.CREATION.equals(GeoCreationEnum.enumOf(geoCreationDO.getStatus()))).collect(Collectors.toSet());
        for (GeoCreationDO geoCreationDO : collect){
            submit( CreationTaskPOJO.builder().id(geoCreationDO.getId()).build());
        }
        if (!collect2.isEmpty()){
            new Thread(() -> {
                for (GeoCreationDO geoCreationDO : collect2){
                    execute(geoCreationDO);
                }

            },"consumer-article-exec-thread").start();
        }


    }

    @Transactional
    public void submit(CreationTaskPOJO pojo) {
        GeoCreationDO creation = geoCreationMapper.selectById(pojo.getId());
        if (creation != null && GeoCreationEnum.WAIT_CREATION.equals(GeoCreationEnum.enumOf(creation.getStatus()))) {
            geoCreationMapper.updateById(GeoCreationDO
                    .builder().id(pojo.getId())
                    .status(GeoCreationEnum.CREATION.ordinal()).build());
            CONSUMER_POOL.execute(() -> {
                try{
                    execute(creation);
                }catch (Exception e){
                    log.error("消费任务失败！",e);
                }
            });
        }
    }


    @Transactional
    public void execute(GeoCreationDO creation){
        Long contentId = creation.getContentId();
        Long titleId = creation.getTitleId();
        Long wordId = creation.getWordId();
        Long knowledgeId = creation.getKnowledgeId();
        String pictureIds = creation.getPictureIds();
        Integer pictureCount = creation.getPictureCount();

        List<Long> fileIds = Arrays.stream(pictureIds.split(GeoUtils. COMMA)).map(Long::parseLong).collect(Collectors.toList());
        List<GeoFileDO> files = geoFileMapper.selectByIds(fileIds);
        GeoFileDO knowledge = geoFileMapper.selectById(knowledgeId);

        GeoWordDO geoWordDO = geoWordMapper.selectById(wordId);
        String word = geoWordDO.getWord();
        String target = geoWordDO.getTarget();

        GeoInstructionDO title = geoInstructionMapper.selectById(titleId);
        GeoInstructionDO content = geoInstructionMapper.selectById(contentId);
        String contentStr = content.getContent().replaceAll(WORD_KEY, word).replaceAll(TARGET_KEY, target);
        String titleStr = title.getContent();

        int mainCount = pictureCount == 0 ? 1 : pictureCount;
        int i = GeoUtils.GENERATOR.nextInt(mainCount - 1, files.size() + 1);

        Collections.shuffle(files);
        List<GeoFileDO> mainFiles = files.subList(0, i);

        List<FileDO> fileDOS = fileMapper.selectByIds(mainFiles.stream().map(GeoFileDO::getFileId).collect(Collectors.toSet()));
        List<String> images = fileDOS.stream().map(FileDO::getUrl).collect(Collectors.toList());
        FileDO knowledgeFile = null;
        if (knowledge != null){
            knowledgeFile = fileMapper.selectById(knowledge.getFileId());
        }
        String article = chatCompletions.generateArticle(contentStr, titleStr, knowledgeFile, images);
        System.out.println("article = " + article);
        if (StringUtils.hasText(article)){
            GeoArticleDO geoArticleDO = new GeoArticleDO();
            geoArticleDO.setCreationId(creation.getId());
            geoArticleDO.setPictureIds(pictureIds);
            Matcher matcher = titlePattern.matcher(article);
            matcher.find();
            geoArticleDO.setTitle(matcher.group(1).trim());
            matcher = articlePattern.matcher(article);
            matcher.find();
            geoArticleDO.setContentMarkdown(matcher.group(1).trim());
            geoArticleMapper.insert(geoArticleDO);
            creation.setStatus(GeoCreationEnum.CREATION_SUCCESS.ordinal());
            geoCreationMapper.updateById(creation);
        }

    }

}
