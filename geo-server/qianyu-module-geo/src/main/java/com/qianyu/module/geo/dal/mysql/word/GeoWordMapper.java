package com.qianyu.module.geo.dal.mysql.word;

import java.util.*;

import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.datapermission.core.annotation.DataPermission;
import com.qianyu.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.qianyu.framework.mybatis.core.mapper.BaseMapperX;
import com.qianyu.framework.security.core.LoginUser;
import com.qianyu.framework.security.core.util.SecurityFrameworkUtils;
import com.qianyu.module.geo.dal.dataobject.word.GeoWordDO;
import com.qianyu.module.system.dal.dataobject.dept.PostDO;
import com.qianyu.module.system.util.oauth2.UserUtils;
import org.apache.ibatis.annotations.Mapper;
import com.qianyu.module.geo.controller.admin.word.vo.*;
import org.springframework.util.ObjectUtils;

/**
 * 蒸馏词 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface GeoWordMapper extends BaseMapperX<GeoWordDO> {


    default long count() {
        return selectCount(new LambdaQueryWrapperX<GeoWordDO>()
                .eqIfPresent(GeoWordDO::getCreator, UserUtils.getUserIdStrNoAdmin()));
    }

    default PageResult<GeoWordDO> selectPage(GeoWordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<GeoWordDO>()
                .eqIfPresent(GeoWordDO::getCreator, UserUtils.getUserIdStrNoAdmin())
                .eqIfPresent(GeoWordDO::getWord, reqVO.getWord())
                .eqIfPresent(GeoWordDO::getTarget, reqVO.getTarget())
                .eqIfPresent(GeoWordDO::getOptimized, reqVO.getOptimized())
                .eqIfPresent(GeoWordDO::getExpand, reqVO.getExpand())
                .betweenIfPresent(GeoWordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(GeoWordDO::getId));
    }


    default List<GeoWordDO> selectList(Collection<Long> ids) {
        return selectList(new LambdaQueryWrapperX<GeoWordDO>()
                .eqIfPresent(GeoWordDO::getCreator, UserUtils.getUserIdStrNoAdmin())
                .inIfPresent(GeoWordDO::getId, ids));
    }

}