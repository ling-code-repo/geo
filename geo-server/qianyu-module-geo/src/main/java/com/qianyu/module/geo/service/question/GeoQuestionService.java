package com.qianyu.module.geo.service.question;

import java.util.*;
import javax.validation.*;
import com.qianyu.module.geo.controller.admin.question.vo.*;
import com.qianyu.module.geo.dal.dataobject.question.GeoQuestionDO;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.pojo.PageParam;
import com.qianyu.module.geo.dal.dataobject.question.GeoQuestionItemDO;

/**
 * 扩展问题 Service 接口
 *
 * @author 系统管理员
 */
public interface GeoQuestionService {

    /**
     * 创建扩展问题
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createQuestion(@Valid GeoQuestionSaveReqVO createReqVO);

    /**
     * 更新扩展问题
     *
     * @param updateReqVO 更新信息
     */
    void updateQuestion(@Valid GeoQuestionSaveReqVO updateReqVO);

    /**
     * 删除扩展问题
     *
     * @param id 编号
     */
    void deleteQuestion(Long id);

    /**
    * 批量删除扩展问题
    *
    * @param ids 编号
    */
    void deleteQuestionListByIds(List<Long> ids);

    /**
     * 获得扩展问题
     *
     * @param id 编号
     * @return 扩展问题
     */
    GeoQuestionDO getQuestion(Long id);

    /**
     * 获得扩展问题分页
     *
     * @param pageReqVO 分页查询
     * @return 扩展问题分页
     */
    PageResult<GeoQuestionItemDO> getQuestionPage(GeoQuestionPageReqVO pageReqVO);

}