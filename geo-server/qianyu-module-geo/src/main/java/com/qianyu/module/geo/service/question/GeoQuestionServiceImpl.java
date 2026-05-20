package com.qianyu.module.geo.service.question;

import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.object.BeanUtils;
import com.qianyu.module.geo.controller.admin.question.vo.GeoQuestionPageReqVO;
import com.qianyu.module.geo.controller.admin.question.vo.GeoQuestionSaveReqVO;
import com.qianyu.module.geo.dal.dataobject.question.GeoQuestionDO;
import com.qianyu.module.geo.dal.dataobject.question.GeoQuestionItemDO;
import com.qianyu.module.geo.dal.mysql.question.GeoQuestionMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * 扩展问题 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class GeoQuestionServiceImpl implements GeoQuestionService {

    @Resource
    private GeoQuestionMapper questionMapper;

    @Override
    public Long createQuestion(GeoQuestionSaveReqVO createReqVO) {
        // 插入
        GeoQuestionDO question = BeanUtils.toBean(createReqVO, GeoQuestionDO.class);
        questionMapper.insert(question);

        // 返回
        return question.getId();
    }

    @Override
    public void updateQuestion(GeoQuestionSaveReqVO updateReqVO) {
        // 更新
        GeoQuestionDO updateObj = BeanUtils.toBean(updateReqVO, GeoQuestionDO.class);
        questionMapper.updateById(updateObj);
    }

    @Override
    public void deleteQuestion(Long id) {
        // 删除
        questionMapper.deleteById(id);
    }

    @Override
        public void deleteQuestionListByIds(List<Long> ids) {
        // 删除
        questionMapper.deleteByIds(ids);
        }


    @Override
    public GeoQuestionDO getQuestion(Long id) {
        return questionMapper.selectById(id);
    }

    @Override
    public PageResult<GeoQuestionItemDO> getQuestionPage(GeoQuestionPageReqVO pageReqVO) {
        return questionMapper.selectPage(pageReqVO);
    }

}