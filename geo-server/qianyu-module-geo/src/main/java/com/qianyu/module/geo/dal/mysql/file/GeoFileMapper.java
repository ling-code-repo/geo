package com.qianyu.module.geo.dal.mysql.file;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianyu.framework.common.pojo.PageParam;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.collection.ArrayUtils;
import com.qianyu.framework.mybatis.core.mapper.BaseMapperX;
import com.qianyu.module.geo.controller.admin.file.vo.GeoFilePageReqVO;
import com.qianyu.module.geo.dal.dataobject.file.GeoFileDO;
import com.qianyu.module.geo.dal.dataobject.file.GeoFileItemDO;
import com.qianyu.module.geo.dal.dataobject.instruction.GeoInstructionDO;
import com.qianyu.module.infra.dal.dataobject.file.FileDO;
import com.qianyu.module.system.util.oauth2.UserUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 企业图库 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface GeoFileMapper extends BaseMapperX<GeoFileDO> {

    default PageResult<GeoFileItemDO> selectPage(GeoFilePageReqVO reqVO) {
        String userId = UserUtils.getUserIdStrNoAdmin();
        MPJLambdaWrapper<GeoFileDO> wrapper = new MPJLambdaWrapper<GeoFileDO>()
                .selectAs(GeoFileDO::getId, GeoFileItemDO::getId)
                .selectAs(FileDO::getId, GeoFileItemDO::getFileId)
                .selectAs(FileDO::getConfigId, GeoFileItemDO::getConfigId)
                .selectAs(FileDO::getName, GeoFileItemDO::getName)
                .selectAs(FileDO::getUrl, GeoFileItemDO::getUrl)
                .selectAs(FileDO::getType, GeoFileItemDO::getType)
                .selectAs(FileDO::getSize, GeoFileItemDO::getSize)
                .selectAs(FileDO::getCreateTime, GeoFileItemDO::getCreateTime)
                .eq(!ObjectUtils.isEmpty(userId), FileDO::getCreator, userId)
//                .distinct()
                .like(!ObjectUtils.isEmpty(reqVO.getName()), FileDO::getName, reqVO.getName())
                .like(!ObjectUtils.isEmpty(reqVO.getType()), FileDO::getType, reqVO.getType())
                .between(!ObjectUtils.isEmpty(reqVO.getCreateTime()), GeoFileDO::getCreateTime
                        , ArrayUtils.get(reqVO.getCreateTime(), 0)
                        , ArrayUtils.get(reqVO.getCreateTime(), 1))
                .eq(!ObjectUtils.isEmpty(reqVO.getCategory()), GeoFileDO::getCategory, reqVO.getCategory())
                .innerJoin(FileDO.class, FileDO::getId, GeoFileDO::getFileId)
                .orderByDesc(GeoFileDO::getId);
        if (PageParam.PAGE_SIZE_NONE.equals(reqVO.getPageSize())) {
            List<GeoFileItemDO> list = selectJoinList(GeoFileItemDO.class,
                    wrapper);
            return new PageResult<>(list, (long) list.size());
        }
        Page<GeoFileItemDO> mpPage = selectJoinPage(new Page(reqVO.getPageNo(), reqVO.getPageSize()),
                GeoFileItemDO.class,
                wrapper
        );
        // 转换返回
        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());
    }

    default List<GeoFileItemDO> selectSimpleList() {
        String userId = UserUtils.getUserIdStrNoAdmin();
        MPJLambdaWrapper<GeoFileDO> wrapper = new MPJLambdaWrapper<GeoFileDO>()
                .selectAs(GeoFileDO::getId, GeoFileItemDO::getId)
                .selectAs(FileDO::getUrl, GeoFileItemDO::getUrl)
                .selectAs(GeoFileDO::getCategory, GeoFileItemDO::getCategory)
                .selectAs(FileDO::getName, GeoFileItemDO::getName)
                .eq(!ObjectUtils.isEmpty(userId), FileDO::getCreator, userId)
                .orderByDesc(GeoFileDO::getId).innerJoin(FileDO.class, FileDO::getId, GeoFileDO::getFileId);
        List<GeoFileItemDO> list = selectJoinList(GeoFileItemDO.class,
                wrapper);
        return list;
    }

}