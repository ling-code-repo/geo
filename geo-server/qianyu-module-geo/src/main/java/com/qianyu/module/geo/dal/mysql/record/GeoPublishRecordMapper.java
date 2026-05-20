package com.qianyu.module.geo.dal.mysql.record;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianyu.framework.common.pojo.PageParam;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.collection.ArrayUtils;
import com.qianyu.framework.mybatis.core.mapper.BaseMapperX;
import com.qianyu.module.geo.controller.admin.record.vo.GeoPublishRecordPageReqVO;
import com.qianyu.module.geo.controller.admin.statistics.vo.DayCountVO;
import com.qianyu.module.geo.dal.dataobject.account.GeoAccountDO;
import com.qianyu.module.geo.dal.dataobject.article.GeoArticleDO;
import com.qianyu.module.geo.dal.dataobject.publish.GeoPublishTaskDO;
import com.qianyu.module.geo.dal.dataobject.question.GeoQuestionDO;
import com.qianyu.module.geo.dal.dataobject.record.GeoPublishRecordDO;
import com.qianyu.module.geo.dal.dataobject.record.GeoPublishRecordItemDO;
import com.qianyu.module.system.util.oauth2.UserUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 发布记录 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface GeoPublishRecordMapper extends BaseMapperX<GeoPublishRecordDO> {

    default PageResult<GeoPublishRecordItemDO> selectPage(GeoPublishRecordPageReqVO reqVO) {

        String userId = UserUtils.getUserIdStrNoAdmin();
        MPJLambdaWrapper<GeoPublishRecordDO> wrapper = new MPJLambdaWrapper<GeoPublishRecordDO>()
                .selectAll(GeoPublishRecordItemDO.class)
                .selectAs(GeoAccountDO::getName,GeoPublishRecordItemDO::getAccountName)
                .selectAs(GeoAccountDO::getPlatform,GeoPublishRecordItemDO::getPlatform)
                .selectAs(GeoArticleDO::getTitle,GeoPublishRecordItemDO::getTitle)
                .selectAs(GeoPublishTaskDO::getName,GeoPublishRecordItemDO::getTaskName)
                .eq(!ObjectUtils.isEmpty(userId),GeoPublishRecordDO::getCreator, userId)
                .between(!ObjectUtils.isEmpty(reqVO.getCreateTime()), GeoQuestionDO::getCreateTime
                        , ArrayUtils.get(reqVO.getCreateTime(), 0)
                        , ArrayUtils.get(reqVO.getCreateTime(), 1))
                .innerJoin(GeoAccountDO.class, GeoAccountDO::getId, GeoPublishRecordDO::getAccountId)
                .innerJoin(GeoArticleDO.class,GeoArticleDO::getId, GeoPublishRecordDO::getArticleId)
                .innerJoin(GeoPublishTaskDO.class,GeoPublishTaskDO::getId, GeoPublishRecordDO::getTaskId)
                .orderByDesc(GeoPublishRecordDO::getId);
        if (PageParam.PAGE_SIZE_NONE.equals(reqVO.getPageSize())) {
            List<GeoPublishRecordItemDO> list = selectJoinList(GeoPublishRecordItemDO.class,
                    wrapper);
            return new PageResult<>(list, (long) list.size());
        }
        Page<GeoPublishRecordItemDO> mpPage = selectJoinPage(new Page(reqVO.getPageNo(), reqVO.getPageSize()),
                GeoPublishRecordItemDO.class,
                wrapper
        );
        // 转换返回
        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());
//
//
//        return selectPage(reqVO, new LambdaQueryWrapperX<GeoPublishRecordDO>()
//                .betweenIfPresent(GeoPublishRecordDO::getCreateTime, reqVO.getCreateTime())
//                .orderByDesc(GeoPublishRecordDO::getId));
    }

    @Select("SELECT " +
            "DATE_FORMAT(create_time, '%m-%d') as date, " +
            "COUNT(*) as count " +
            "FROM geo_publish_record " +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 4 DAY) " +
            "AND create_time <= CURDATE() " +
            "GROUP BY DATE_FORMAT(create_time, '%m-%d') " +
            "ORDER BY DATE_FORMAT(create_time, '%m-%d') ASC")
    List<DayCountVO> countRecords();
}