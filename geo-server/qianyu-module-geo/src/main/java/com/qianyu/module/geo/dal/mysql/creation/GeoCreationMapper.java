package com.qianyu.module.geo.dal.mysql.creation;

import java.util.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianyu.framework.common.pojo.PageParam;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.collection.ArrayUtils;
import com.qianyu.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.qianyu.framework.mybatis.core.mapper.BaseMapperX;
import com.qianyu.module.geo.dal.dataobject.creation.GeoCreationDO;
import com.qianyu.module.geo.dal.dataobject.creation.GeoCreationItemDO;
import com.qianyu.module.geo.dal.dataobject.question.GeoQuestionDO;
import com.qianyu.module.geo.dal.dataobject.question.GeoQuestionItemDO;
import com.qianyu.module.geo.dal.dataobject.word.GeoWordDO;
import com.qianyu.module.infra.dal.dataobject.file.FileDO;
import com.qianyu.module.system.util.oauth2.UserUtils;
import org.apache.ibatis.annotations.Mapper;
import com.qianyu.module.geo.controller.admin.creation.vo.*;
import org.springframework.util.ObjectUtils;

/**
 * AI创作 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface GeoCreationMapper extends BaseMapperX<GeoCreationDO> {

    default PageResult<GeoCreationItemDO> selectPage(GeoCreationPageReqVO reqVO) {
        String userId = UserUtils.getUserIdStrNoAdmin();
        /*return selectPage(reqVO, new LambdaQueryWrapperX<GeoCreationDO>()
                .likeIfPresent(GeoCreationDO::getName, reqVO.getName())
                .eqIfPresent(GeoCreationDO::getWordId, reqVO.getWordId())
                .eqIfPresent(GeoCreationDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(GeoCreationDO::getCreationTime, reqVO.getCreationTime())
                .betweenIfPresent(GeoCreationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(GeoCreationDO::getId));*/
        MPJLambdaWrapper<GeoCreationDO> wrapper = new MPJLambdaWrapper<GeoCreationDO>()
                .selectAll(GeoCreationItemDO.class)
                .selectAs(GeoWordDO::getWord,GeoCreationItemDO::getWord)
                .eq(!ObjectUtils.isEmpty(userId), GeoCreationDO::getCreator, userId)
                .like(!ObjectUtils.isEmpty(reqVO.getName()),GeoCreationDO::getName, reqVO.getName())
                .eq(!ObjectUtils.isEmpty(reqVO.getStatus()),GeoCreationDO::getStatus, reqVO.getStatus())
                .between(!ObjectUtils.isEmpty(reqVO.getCreateTime()), GeoQuestionDO::getCreateTime
                        , ArrayUtils.get(reqVO.getCreateTime(), 0)
                        , ArrayUtils.get(reqVO.getCreateTime(), 1))
                .innerJoin(GeoWordDO.class, GeoWordDO::getId, GeoCreationDO::getWordId)
                .orderByDesc(GeoQuestionDO::getId);
        if (PageParam.PAGE_SIZE_NONE.equals(reqVO.getPageSize())) {
            List<GeoCreationItemDO> list = selectJoinList(GeoCreationItemDO.class,
                    wrapper);
            return new PageResult<>(list, (long) list.size());
        }
        Page<GeoCreationItemDO> mpPage = selectJoinPage(new Page(reqVO.getPageNo(), reqVO.getPageSize()),
                GeoCreationItemDO.class,
                wrapper
        );
        // 转换返回
        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());
    }

}