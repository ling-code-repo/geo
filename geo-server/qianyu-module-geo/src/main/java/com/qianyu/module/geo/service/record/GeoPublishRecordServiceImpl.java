package com.qianyu.module.geo.service.record;

import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.object.BeanUtils;
import com.qianyu.module.geo.controller.admin.record.vo.GeoPublishRecordPageReqVO;
import com.qianyu.module.geo.controller.admin.record.vo.GeoPublishRecordRespVO;
import com.qianyu.module.geo.controller.admin.record.vo.GeoPublishRecordSaveReqVO;
import com.qianyu.module.geo.dal.dataobject.record.GeoPublishRecordDO;
import com.qianyu.module.geo.dal.dataobject.record.GeoPublishRecordItemDO;
import com.qianyu.module.geo.dal.mysql.record.GeoPublishRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * 发布记录 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class GeoPublishRecordServiceImpl implements GeoPublishRecordService {

    @Resource
    private GeoPublishRecordMapper publishRecordMapper;

    @Override
    public Long createPublishRecord(GeoPublishRecordSaveReqVO createReqVO) {
        // 插入
        GeoPublishRecordDO publishRecord = BeanUtils.toBean(createReqVO, GeoPublishRecordDO.class);
        publishRecordMapper.insert(publishRecord);

        // 返回
        return publishRecord.getId();
    }

    @Override
    public void updatePublishRecord(GeoPublishRecordSaveReqVO updateReqVO) {
        // 更新
        GeoPublishRecordDO updateObj = BeanUtils.toBean(updateReqVO, GeoPublishRecordDO.class);
        publishRecordMapper.updateById(updateObj);
    }

    @Override
    public void deletePublishRecord(Long id) {
        // 删除
        publishRecordMapper.deleteById(id);
    }

    @Override
        public void deletePublishRecordListByIds(List<Long> ids) {
        // 删除
        publishRecordMapper.deleteByIds(ids);
        }


    @Override
    public GeoPublishRecordDO getPublishRecord(Long id) {
        return publishRecordMapper.selectById(id);
    }

    @Override
    public PageResult<GeoPublishRecordItemDO> getPublishRecordPage(GeoPublishRecordPageReqVO pageReqVO) {
        return publishRecordMapper.selectPage(pageReqVO);
    }



}