package com.qianyu.module.geo.dal.mysql.account;

import java.util.*;

import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.qianyu.framework.mybatis.core.mapper.BaseMapperX;
import com.qianyu.module.geo.dal.dataobject.account.GeoAccountDO;
import com.qianyu.module.geo.dal.dataobject.article.GeoArticleDO;
import com.qianyu.module.geo.dal.dataobject.word.GeoWordDO;
import com.qianyu.module.system.util.oauth2.UserUtils;
import org.apache.ibatis.annotations.Mapper;
import com.qianyu.module.geo.controller.admin.account.vo.*;

/**
 * 授权账号 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface GeoAccountMapper extends BaseMapperX<GeoAccountDO> {

    default PageResult<GeoAccountDO> selectPage(GeoAccountPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<GeoAccountDO>()
                .likeIfPresent(GeoAccountDO::getName, reqVO.getName())
                .eqIfPresent(GeoAccountDO::getPlatform, reqVO.getPlatform())
                .eqIfPresent(GeoAccountDO::getPublishStatus, reqVO.getPublishStatus())
                .betweenIfPresent(GeoAccountDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(GeoAccountDO::getCreator, UserUtils.getUserIdStrNoAdmin())
                .orderByDesc(GeoAccountDO::getId));
    }

    default Long count(){
        return selectCount(new LambdaQueryWrapperX<GeoAccountDO>()
                .eqIfPresent(GeoAccountDO::getCreator, UserUtils.getUserIdStrNoAdmin()));
    }
}