package com.qianyu.module.geo.service.creation;

import java.util.*;
import javax.validation.*;
import com.qianyu.module.geo.controller.admin.creation.vo.*;
import com.qianyu.module.geo.dal.dataobject.creation.GeoCreationDO;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.pojo.PageParam;
import com.qianyu.module.geo.dal.dataobject.creation.GeoCreationItemDO;

/**
 * AI创作 Service 接口
 *
 * @author 系统管理员
 */
public interface GeoCreationService {

    /**
     * 创建AI创作
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCreation(@Valid GeoCreationSaveReqVO createReqVO);

    /**
     * 更新AI创作
     *
     * @param updateReqVO 更新信息
     */
    void updateCreation(@Valid GeoCreationSaveReqVO updateReqVO);

    /**
     * 删除AI创作
     *
     * @param id 编号
     */
    void deleteCreation(Long id);

    /**
    * 批量删除AI创作
    *
    * @param ids 编号
    */
    void deleteCreationListByIds(List<Long> ids);

    /**
     * 获得AI创作
     *
     * @param id 编号
     * @return AI创作
     */
    GeoCreationDO getCreation(Long id);

    /**
     * 获得AI创作分页
     *
     * @param pageReqVO 分页查询
     * @return AI创作分页
     */
    PageResult<GeoCreationItemDO> getCreationPage(GeoCreationPageReqVO pageReqVO);

}