package com.qianyu.module.geo.service.record;

import java.util.*;
import javax.validation.*;
import com.qianyu.module.geo.controller.admin.record.vo.*;
import com.qianyu.module.geo.dal.dataobject.record.GeoPublishRecordDO;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.pojo.PageParam;
import com.qianyu.module.geo.dal.dataobject.record.GeoPublishRecordItemDO;

/**
 * 发布记录 Service 接口
 *
 * @author 系统管理员
 */
public interface GeoPublishRecordService {

    /**
     * 创建发布记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPublishRecord(@Valid GeoPublishRecordSaveReqVO createReqVO);

    /**
     * 更新发布记录
     *
     * @param updateReqVO 更新信息
     */
    void updatePublishRecord(@Valid GeoPublishRecordSaveReqVO updateReqVO);

    /**
     * 删除发布记录
     *
     * @param id 编号
     */
    void deletePublishRecord(Long id);

    /**
    * 批量删除发布记录
    *
    * @param ids 编号
    */
    void deletePublishRecordListByIds(List<Long> ids);

    /**
     * 获得发布记录
     *
     * @param id 编号
     * @return 发布记录
     */
    GeoPublishRecordDO getPublishRecord(Long id);

    /**
     * 获得发布记录分页
     *
     * @param pageReqVO 分页查询
     * @return 发布记录分页
     */
    PageResult<GeoPublishRecordItemDO> getPublishRecordPage(GeoPublishRecordPageReqVO pageReqVO);

}