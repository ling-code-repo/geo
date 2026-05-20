package com.qianyu.module.geo.dal.mysql.article;

import java.time.LocalDateTime;
import java.util.*;

import cn.idev.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianyu.framework.common.pojo.PageParam;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.collection.ArrayUtils;
import com.qianyu.framework.excel.core.annotations.DictFormat;
import com.qianyu.framework.excel.core.convert.DictConvert;
import com.qianyu.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.qianyu.framework.mybatis.core.mapper.BaseMapperX;
import com.qianyu.module.geo.controller.admin.statistics.vo.DayCountVO;
import com.qianyu.module.geo.dal.dataobject.article.GeoArticleDO;
import com.qianyu.module.geo.dal.dataobject.creation.GeoCreationDO;
import com.qianyu.module.geo.dal.dataobject.creation.GeoCreationItemDO;
import com.qianyu.module.geo.dal.dataobject.question.GeoQuestionDO;
import com.qianyu.module.geo.dal.dataobject.word.GeoWordDO;
import com.qianyu.module.system.util.oauth2.UserUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.ibatis.annotations.Mapper;
import com.qianyu.module.geo.controller.admin.article.vo.*;
import org.apache.ibatis.annotations.Select;
import org.springframework.util.ObjectUtils;

/**
 * 创作文章 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface GeoArticleMapper extends BaseMapperX<GeoArticleDO> {

    default PageResult<GeoArticleDO> selectPage(GeoArticlePageReqVO reqVO) {
        /*return selectPage(reqVO, new LambdaQueryWrapperX<GeoArticleDO>()
                .likeIfPresent(GeoArticleDO::getTitle, reqVO.getTitle())
                .eqIfPresent(GeoArticleDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(GeoArticleDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(GeoArticleDO::getId));
                */
        String userId = UserUtils.getUserIdStrNoAdmin();
        MPJLambdaWrapper<GeoArticleDO> wrapper = new MPJLambdaWrapper<GeoArticleDO>()
                .selectAs(GeoArticleDO::getId,GeoArticleDO::getId)
                .selectAs(GeoArticleDO::getTitle,GeoArticleDO::getTitle)
                .selectAs(GeoArticleDO::getPublishCount,GeoArticleDO::getPublishCount)
                .selectAs(GeoArticleDO::getStatus,GeoArticleDO::getStatus)
                .selectAs(GeoArticleDO::getCreateTime,GeoArticleDO::getCreateTime)
                .eq(!ObjectUtils.isEmpty(userId), GeoArticleDO::getCreator, userId)
                .like(!ObjectUtils.isEmpty(reqVO.getTitle()),GeoArticleDO::getTitle, reqVO.getTitle())
                .eq(!ObjectUtils.isEmpty(reqVO.getStatus()),GeoArticleDO::getStatus, reqVO.getStatus())
                .between(!ObjectUtils.isEmpty(reqVO.getCreateTime()), GeoQuestionDO::getCreateTime
                        , ArrayUtils.get(reqVO.getCreateTime(), 0)
                        , ArrayUtils.get(reqVO.getCreateTime(), 1))
                .orderByDesc(GeoArticleDO::getId);
        if (PageParam.PAGE_SIZE_NONE.equals(reqVO.getPageSize())) {
            List<GeoArticleDO> list = selectJoinList(GeoArticleDO.class,
                    wrapper);
            return new PageResult<>(list, (long) list.size());
        }
        Page<GeoArticleDO> mpPage = selectJoinPage(new Page(reqVO.getPageNo(), reqVO.getPageSize()),
                GeoArticleDO.class,
                wrapper
        );
        // 转换返回
        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());
    }

    default Long count(){
        return selectCount(new LambdaQueryWrapperX<GeoArticleDO>()
                .eqIfPresent(GeoArticleDO::getCreator, UserUtils.getUserIdStrNoAdmin()));
    }

    @Select("SELECT " +
            "DATE_FORMAT(create_time, '%m-%d') as date, " +
            "COUNT(*) as count " +
            "FROM geo_article " +
            "WHERE creation_id IS NOT NULL AND create_time >= DATE_SUB(CURDATE(), INTERVAL 4 DAY) " +
            "AND create_time <= CURDATE() " +
            "GROUP BY DATE_FORMAT(create_time, '%m-%d') " +
            "ORDER BY DATE_FORMAT(create_time, '%m-%d') ASC")
    List<DayCountVO> countAIArticles();

}