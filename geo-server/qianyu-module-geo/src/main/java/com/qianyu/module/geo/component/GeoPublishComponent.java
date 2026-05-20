package com.qianyu.module.geo.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.qianyu.framework.common.util.json.JsonUtils;
import com.qianyu.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.qianyu.framework.security.core.util.SecurityFrameworkUtils;
import com.qianyu.module.geo.component.pojo.PublishTaskPOJO;
import com.qianyu.module.geo.dal.dataobject.account.GeoAccountDO;
import com.qianyu.module.geo.dal.dataobject.article.GeoArticleDO;
import com.qianyu.module.geo.dal.dataobject.file.GeoFileDO;
import com.qianyu.module.geo.dal.dataobject.publish.GeoPublishTaskDO;
import com.qianyu.module.geo.dal.mysql.account.GeoAccountMapper;
import com.qianyu.module.geo.dal.mysql.article.GeoArticleMapper;
import com.qianyu.module.geo.dal.mysql.file.GeoFileMapper;
import com.qianyu.module.geo.dal.mysql.publish.GeoPublishTaskMapper;
import com.qianyu.module.geo.enums.GeoCreationEnum;
import com.qianyu.module.geo.enums.PublishStatusEnum;
import com.qianyu.module.geo.enums.PublishTaskStatusEnum;
import com.qianyu.module.geo.socket.GeoPublishSendMessage;
import com.qianyu.module.geo.socket.GeoWebSocketMessageListener;
import com.qianyu.module.geo.socket.GeoWebsocketMessageSender;
import com.qianyu.module.geo.utils.DictKeyConst;
import com.qianyu.module.geo.utils.GeoUtils;
import com.qianyu.module.infra.dal.dataobject.file.FileDO;
import com.qianyu.module.infra.dal.mysql.file.FileMapper;
import com.qianyu.module.system.dal.dataobject.dict.DictDataDO;
import com.qianyu.module.system.dal.mysql.dict.DictDataMapper;
import com.qianyu.module.system.service.dict.DictDataService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GeoPublishComponent {

    private static final int AVAILABLE_PROCESSORS = GeoUtils.AVAILABLE_PROCESSORS;

    @Autowired
    private GeoPublishTaskMapper geoPublishTaskMapper;

    private static final ThreadPoolExecutor CONSUMER_POOL = GeoUtils.getPool("consumer-publish-exec-pool-%s", AVAILABLE_PROCESSORS * 2);

    @Autowired
    private GeoArticleMapper geoArticleMapper;

//    @Autowired
//    private RabbitTemplate rabbitTemplate;

    @Autowired
    private GeoFileMapper geoFileMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private GeoAccountMapper geoAccountMapper;

    @Autowired
    private DictDataService dictDataService;

    @Autowired
    private GeoWebsocketMessageSender geoWebsocketMessageSender;

    @PostConstruct
    public void init() {

//        List<GeoPublishTaskDO> item = geoPublishTaskMapper.selectList(new LambdaQueryWrapperX<GeoPublishTaskDO>()
//                .eq(GeoPublishTaskDO::getStatus, PublishStatusEnum.ENABLED.ordinal())
//                .ne(GeoPublishTaskDO::getTaskStatus, PublishTaskStatusEnum.PUBLISH_SUCCESS.ordinal()));
//        Set<GeoPublishTaskDO> noList = item.stream().filter(i -> PublishTaskStatusEnum.NOT_PUBLISH.equals(PublishTaskStatusEnum.enumOf(i.getTaskStatus()))).collect(Collectors.toSet());
//        Set<GeoPublishTaskDO> yesList = item.stream().filter(i -> GeoCreationEnum.CREATION.equals(GeoCreationEnum.enumOf(i.getTaskStatus()))).collect(Collectors.toSet());
//        for (GeoPublishTaskDO i : noList) {
//            PublishTaskPOJO msg = PublishTaskPOJO.builder().id(i.getId()).build();
//            rabbitTemplate.convertAndSend(
//                    GeoUtils.GEO_PUBLISH_ROUTING,
//                    GeoUtils.GEO_PUBLISH_ROUTING,
//                    msg
//            );
//        }
//        if (!yesList.isEmpty()) {
//            new Thread(() -> {
//                for (GeoPublishTaskDO i : yesList) {
//                    execute(i);
//                }
//
//            }, "consumer-publish-exec-thread").start();
//        }


    }

//    @RabbitListener(queues = GeoUtils.GEO_PUBLISH_QUEUE)
//    public void processFullMessage(Message message, Channel channel) {
//        try {
//            PublishTaskPOJO pojo = JsonUtils.parseObject(new String(message.getBody()), new TypeReference<PublishTaskPOJO>() {
//
//            });
//            submit(pojo);
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//        } catch (IOException e) {
//            log.error("消息确认失败", e);
//            try {
//                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
//            } catch (IOException ex) {
//                log.error("消息拒绝失败", e);
//            }
//        }
//    }

    @Transactional
    public void submit(PublishTaskPOJO pojo) {
        GeoPublishTaskDO item = geoPublishTaskMapper.selectById(pojo.getId());
//        TODO 加分布式锁
//        log.info("data",JsonUtils.toJsonString(pojo));
        if (item != null && PublishStatusEnum.ENABLED.equals(PublishStatusEnum.enumOf(item.getStatus()))
                && PublishTaskStatusEnum.NOT_PUBLISH.equals(PublishTaskStatusEnum.enumOf(item.getTaskStatus()))) {
            geoPublishTaskMapper.updateById(GeoPublishTaskDO
                    .builder().id(pojo.getId())
                    .taskStatus(PublishTaskStatusEnum.PUBLISHING.ordinal()).build());
            CONSUMER_POOL.execute(() -> {
                try {
                    execute(item);
                } catch (Exception e) {
                    log.error("消费任务失败！", e);
                }
            });
        }
    }

    @Transactional
    public void immediatelySubmit(PublishTaskPOJO pojo) {
        GeoPublishTaskDO item = geoPublishTaskMapper.selectById(pojo.getId());
        if (item != null && PublishTaskStatusEnum.PUBLISHING.equals(PublishTaskStatusEnum.enumOf(item.getTaskStatus()))) {
            geoPublishTaskMapper.updateById(GeoPublishTaskDO
                    .builder().id(pojo.getId())
                            .status(PublishStatusEnum.ENABLED.ordinal())
                    .taskStatus(PublishTaskStatusEnum.PUBLISHING.ordinal()).build());
            CONSUMER_POOL.execute(() -> {
                try {
                    execute(item);
                } catch (Exception e) {
                    log.error("消费任务失败！", e);
                }
            });
        }
    }


    @Transactional
    public void execute(GeoPublishTaskDO item) {
        Long id = item.getId();
        Integer declareAi = item.getDeclareAi();
        Long articleId = item.getArticleId();
        GeoArticleDO article = geoArticleMapper.selectById(articleId);
        String contentMarkdown = article.getContentMarkdown();
        String pictureIds = article.getPictureIds();
        String title = article.getTitle();
        String[] pictureIdList = pictureIds.split(GeoUtils.COMMA);
        String[] platformValueList = item.getPlatforms().split(GeoUtils.COMMA);
        List<DictDataDO> dictDataList = dictDataService.getDictDataListByDictType(DictKeyConst.GEO_PUBLISH_PLATFORM);


        List<GeoAccountDO> geoAccountDOS = geoAccountMapper.selectList(new LambdaQueryWrapperX<GeoAccountDO>()
                .in(GeoAccountDO::getPlatform, Arrays.asList(platformValueList)));
        if (geoAccountDOS.isEmpty()) {
            geoPublishTaskMapper.updateById(GeoPublishTaskDO
                    .builder().id(id)
                    .taskStatus(PublishTaskStatusEnum.PUBLISH_SUCCESS.ordinal())
                    .errorMessage("未找到发布平台！").build());
            return;
        }

        Set<String> platforms = geoAccountDOS.stream().map(a->String.valueOf(a.getPlatform())).collect(Collectors.toSet());
        List<String> platformNames = new ArrayList<>();
        for (DictDataDO dict : dictDataList){
            if (platforms.contains(dict.getValue())){
                platformNames.add(dict.getLabel());
            }
        }

        if (platformNames.isEmpty()){
            geoPublishTaskMapper.updateById(GeoPublishTaskDO
                    .builder().id(id)
                    .taskStatus(PublishTaskStatusEnum.PUBLISH_SUCCESS.ordinal())
                    .errorMessage("未找到发布平台！").build());
            return;
        }

        List<GeoFileDO> geoFileDOS = geoFileMapper.selectByIds(Arrays.asList(pictureIdList));
        List<Long> fileIdList = geoFileDOS.stream().map(GeoFileDO::getFileId).collect(Collectors.toList());

        List<FileDO> fileDOS = fileMapper.selectByIds(fileIdList);
        List<String> fileList = fileDOS.stream().map(FileDO::getUrl).collect(Collectors.toList());
        GeoPublishSendMessage msg = GeoPublishSendMessage.builder().id(id).contentMarkdown(contentMarkdown)
                .title(title)
                .fileList(fileList)
                .headless(item.getHeadless())
                .platformNames(platformNames)
                .declareAi(declareAi)
                .build();

        geoWebsocketMessageSender.sendObject(SecurityFrameworkUtils.getLoginUserId(), GeoWebSocketMessageListener.TYPE,msg);

    }

}
