package com.qianyu.module.geo.service.word;

import com.alibaba.fastjson.JSON;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.object.BeanUtils;
import com.qianyu.framework.datapermission.core.annotation.DataPermission;
import com.qianyu.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.qianyu.module.geo.component.GeoServerComponent;
import com.qianyu.module.geo.controller.admin.word.vo.GeoWordDistillReqVO;
import com.qianyu.module.geo.controller.admin.word.vo.GeoWordPageReqVO;
import com.qianyu.module.geo.controller.admin.word.vo.GeoWordSaveReqVO;
import com.qianyu.module.geo.dal.dataobject.question.GeoQuestionDO;
import com.qianyu.module.geo.dal.dataobject.word.GeoWordDO;
import com.qianyu.module.geo.dal.mysql.question.GeoQuestionMapper;
import com.qianyu.module.geo.dal.mysql.word.GeoWordMapper;
import com.qianyu.module.geo.service.question.GeoQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 蒸馏词 Service 实现类
 *
 * @author 系统管理员
 */
@Slf4j
@Service
@Validated
public class GeoWordServiceImpl implements GeoWordService {

    @Resource
    private GeoWordMapper wordMapper;

    @Autowired
    private GeoServerComponent geoServerComponent;

    @Autowired
    private GeoQuestionMapper questionMapper;


    @Transactional
    @Override
    public Long createWord(GeoWordSaveReqVO createReqVO) {
        try {
            // 插入
            GeoWordDO word = BeanUtils.toBean(createReqVO, GeoWordDO.class);
            List<String> questions = createReqVO.getQuestions();
            if (ObjectUtils.isEmpty(questions)) {
                questions = distillWord(createReqVO);
            }
            word.setQuestionCount(questions.size());
            wordMapper.insert(word);
            Set<GeoQuestionDO> questionDOSet = questions.stream().map(question -> {
                return GeoQuestionDO.builder().wordId(word.getId()).question(question).build();
            }).collect(Collectors.toSet());

            questionMapper.insertBatch(questionDOSet);
            return word.getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateWord(GeoWordSaveReqVO updateReqVO) {

        // 更新
        GeoWordDO updateObj = BeanUtils.toBean(updateReqVO, GeoWordDO.class);
        wordMapper.updateById(updateObj);
    }

    @Transactional
    @Override
    public void deleteWordListByIds(List<Long> ids) {
        questionMapper.delete(new LambdaQueryWrapperX<GeoQuestionDO>().in(GeoQuestionDO::getWordId, ids));
        // 删除
        wordMapper.deleteByIds(ids);
    }


    @Override
    public GeoWordDO getWord(Long id) {
        return wordMapper.selectById(id);
    }

    @Override
    public PageResult<GeoWordDO> getWordPage(GeoWordPageReqVO pageReqVO) {
        return wordMapper.selectPage(pageReqVO);
    }

    public List<GeoWordDO> selectList(Collection<Long> ids) {
        return wordMapper.selectList(ids);
    }

    @Transactional
    @Override
    public List<String> distillWord(GeoWordDistillReqVO vo) {
        List<String> questions = geoServerComponent.distillationWord(vo);
        return questions;
    }

}