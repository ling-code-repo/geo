package com.qianyu.module.geo.dal.mysql.publish;

import java.util.*;

import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.qianyu.framework.mybatis.core.mapper.BaseMapperX;
import com.qianyu.module.geo.dal.dataobject.publish.GeoPublishTaskDO;
import com.qianyu.module.geo.dal.dataobject.word.GeoWordDO;
import com.qianyu.module.system.util.oauth2.UserUtils;
import org.apache.ibatis.annotations.Mapper;
import com.qianyu.module.geo.controller.admin.publish.vo.*;
import org.springframework.util.ObjectUtils;

/**
 * 发布任务 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface GeoPublishTaskMapper extends BaseMapperX<GeoPublishTaskDO> {

    default PageResult<GeoPublishTaskDO> selectPage(GeoPublishTaskPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<GeoPublishTaskDO>()
                .eqIfPresent(GeoPublishTaskDO::getCreator, UserUtils.getUserIdStrNoAdmin())
                .likeIfPresent(GeoPublishTaskDO::getName, reqVO.getName())
                .eqIfPresent(GeoPublishTaskDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(GeoPublishTaskDO::getExecuteTime, reqVO.getExecuteTime())
                .betweenIfPresent(GeoPublishTaskDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(GeoPublishTaskDO::getId));
    }

}