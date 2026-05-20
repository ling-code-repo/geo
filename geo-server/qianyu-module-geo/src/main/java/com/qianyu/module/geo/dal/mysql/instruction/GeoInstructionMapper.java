package com.qianyu.module.geo.dal.mysql.instruction;

import java.util.*;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.collection.ArrayUtils;
import com.qianyu.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.qianyu.framework.mybatis.core.mapper.BaseMapperX;
import com.qianyu.module.geo.dal.dataobject.instruction.GeoInstructionDO;
import com.qianyu.module.geo.dal.dataobject.question.GeoQuestionDO;
import com.qianyu.module.geo.dal.dataobject.word.GeoWordDO;
import com.qianyu.module.system.util.oauth2.UserUtils;
import org.apache.ibatis.annotations.Mapper;
import com.qianyu.module.geo.controller.admin.instruction.vo.*;
import org.springframework.util.ObjectUtils;

/**
 * 创作指令 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface GeoInstructionMapper extends BaseMapperX<GeoInstructionDO> {

    default PageResult<GeoInstructionDO> selectPage(GeoInstructionPageReqVO reqVO) {
        /*return selectPage(reqVO, new LambdaQueryWrapperX<GeoInstructionDO>()
                .likeIfPresent(GeoInstructionDO::getInstructionName, reqVO.getInstructionName())
                .eqIfPresent(GeoInstructionDO::getInstructionType, reqVO.getInstructionType())
//                .eqIfPresent(GeoInstructionDO::getContent, reqVO.getContent())
                .betweenIfPresent(GeoInstructionDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(GeoInstructionDO::getId));*/
        String userId = UserUtils.getUserIdStrNoAdmin();
        return selectPage(reqVO, new MPJLambdaWrapper<GeoInstructionDO>()
                .selectAs(GeoInstructionDO::getId,GeoInstructionDO::getId)
                .selectAs(GeoInstructionDO::getInstructionName,GeoInstructionDO::getInstructionName)
                .selectAs(GeoInstructionDO::getCreateTime,GeoInstructionDO::getCreateTime)
                .selectAs(GeoInstructionDO::getInstructionType,GeoInstructionDO::getInstructionType)
                .eq(!ObjectUtils.isEmpty(userId), GeoInstructionDO::getCreator, userId)
                .like(!ObjectUtils.isEmpty(reqVO.getInstructionName()),GeoInstructionDO::getInstructionName, reqVO.getInstructionName())
                .eq(!ObjectUtils.isEmpty(reqVO.getInstructionType()),GeoInstructionDO::getInstructionType, reqVO.getInstructionType())
//                .eqIfPresent(GeoInstructionDO::getContent, reqVO.getContent())
                .between(!ObjectUtils.isEmpty(reqVO.getCreateTime()), GeoInstructionDO::getCreateTime
                        , ArrayUtils.get(reqVO.getCreateTime(), 0)
                        , ArrayUtils.get(reqVO.getCreateTime(), 1))
//                .betweenIfPresent(GeoInstructionDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(GeoInstructionDO::getId));
    }

}