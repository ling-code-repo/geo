package com.qianyu.module.geo.dal.mysql.question;

import java.util.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianyu.framework.common.pojo.PageParam;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.collection.ArrayUtils;
import com.qianyu.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.qianyu.framework.mybatis.core.mapper.BaseMapperX;
import com.qianyu.module.geo.controller.admin.file.vo.GeoFilePageReqVO;
import com.qianyu.module.geo.dal.dataobject.file.GeoFileDO;
import com.qianyu.module.geo.dal.dataobject.file.GeoFileItemDO;
import com.qianyu.module.geo.dal.dataobject.question.GeoQuestionDO;
import com.qianyu.module.geo.dal.dataobject.question.GeoQuestionItemDO;
import com.qianyu.module.geo.dal.dataobject.word.GeoWordDO;
import com.qianyu.module.infra.dal.dataobject.file.FileDO;
import com.qianyu.module.system.util.oauth2.UserUtils;
import org.apache.ibatis.annotations.Mapper;
import com.qianyu.module.geo.controller.admin.question.vo.*;
import org.springframework.util.ObjectUtils;

/**
 * 扩展问题 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface GeoQuestionMapper extends BaseMapperX<GeoQuestionDO> {

    default PageResult<GeoQuestionItemDO> selectPage(GeoQuestionPageReqVO reqVO) {
        String userId = UserUtils.getUserIdStrNoAdmin();
        MPJLambdaWrapper<GeoQuestionDO> wrapper = new MPJLambdaWrapper<GeoQuestionDO>()
                .selectAll(GeoQuestionItemDO.class)
                .selectAs(GeoWordDO::getWord, GeoQuestionItemDO::getWord)
                .selectAs(GeoWordDO::getTarget, GeoQuestionItemDO::getTarget)
                .eq(!ObjectUtils.isEmpty(userId), GeoWordDO::getCreator, userId)
                .like(!ObjectUtils.isEmpty(reqVO.getWord()), GeoWordDO::getWord, reqVO.getWord())
                .like(!ObjectUtils.isEmpty(reqVO.getQuestion()), GeoQuestionDO::getQuestion, reqVO.getQuestion())
                .eq(!ObjectUtils.isEmpty(reqVO.getStatus()), GeoQuestionDO::getStatus, reqVO.getStatus())
                .between(!ObjectUtils.isEmpty(reqVO.getCreateTime()), GeoQuestionDO::getCreateTime
                        , ArrayUtils.get(reqVO.getCreateTime(), 0)
                        , ArrayUtils.get(reqVO.getCreateTime(), 1))
                .innerJoin(GeoWordDO.class, GeoWordDO::getId, GeoQuestionDO::getWordId)
                .orderByDesc(GeoQuestionDO::getId);
        if (PageParam.PAGE_SIZE_NONE.equals(reqVO.getPageSize())) {
            List<GeoQuestionItemDO> list = selectJoinList(GeoQuestionItemDO.class,
                    wrapper);
            return new PageResult<>(list, (long) list.size());
        }
        Page<GeoQuestionItemDO> mpPage = selectJoinPage(new Page(reqVO.getPageNo(), reqVO.getPageSize()),
                GeoQuestionItemDO.class,
                wrapper
        );
        // 转换返回
        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());
    }

    default Long count() {
        return selectCount(new LambdaQueryWrapperX<GeoQuestionDO>()
                .eqIfPresent(GeoQuestionDO::getCreator, UserUtils.getUserIdStrNoAdmin()));
    }
}