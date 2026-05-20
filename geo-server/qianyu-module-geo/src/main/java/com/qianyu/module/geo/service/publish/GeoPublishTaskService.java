package com.qianyu.module.geo.service.publish;

import java.util.*;
import javax.validation.*;
import com.qianyu.module.geo.controller.admin.publish.vo.*;
import com.qianyu.module.geo.dal.dataobject.publish.GeoPublishTaskDO;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.pojo.PageParam;

/**
 * 发布任务 Service 接口
 *
 * @author 系统管理员
 */
public interface GeoPublishTaskService {

    /**
     * 创建发布任务
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPublishTask(@Valid GeoPublishTaskSaveReqVO createReqVO);

    /**
     * 更新发布任务
     *
     * @param updateReqVO 更新信息
     */
    void updatePublishTask(@Valid GeoPublishTaskSaveReqVO updateReqVO);

    /**
     * 删除发布任务
     *
     * @param id 编号
     */
    void deletePublishTask(Long id);

    /**
    * 批量删除发布任务
    *
    * @param ids 编号
    */
    void deletePublishTaskListByIds(List<Long> ids);

    /**
     * 获得发布任务
     *
     * @param id 编号
     * @return 发布任务
     */
    GeoPublishTaskDO getPublishTask(Long id);

    /**
     * 获得发布任务分页
     *
     * @param pageReqVO 分页查询
     * @return 发布任务分页
     */
    PageResult<GeoPublishTaskDO> getPublishTaskPage(GeoPublishTaskPageReqVO pageReqVO);

    void execute(Long id);

    void updateRecord(@Valid GeoPublishRecordSaveReqVO vo);
}