package com.qianyu.module.geo.service.publish;

import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.date.DateUtils;
import com.qianyu.framework.common.util.json.JsonUtils;
import com.qianyu.framework.common.util.object.BeanUtils;
import com.qianyu.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.qianyu.module.geo.component.GeoCreationComponent;
import com.qianyu.module.geo.component.GeoPublishComponent;
import com.qianyu.module.geo.component.pojo.PublishTaskPOJO;
import com.qianyu.module.geo.controller.admin.publish.vo.GeoPublishRecordSaveReqVO;
import com.qianyu.module.geo.controller.admin.publish.vo.GeoPublishTaskPageReqVO;
import com.qianyu.module.geo.controller.admin.publish.vo.GeoPublishTaskSaveReqVO;
import com.qianyu.module.geo.dal.dataobject.account.GeoAccountDO;
import com.qianyu.module.geo.dal.dataobject.article.GeoArticleDO;
import com.qianyu.module.geo.dal.dataobject.publish.GeoPublishTaskDO;
import com.qianyu.module.geo.dal.dataobject.record.GeoPublishRecordDO;
import com.qianyu.module.geo.dal.mysql.account.GeoAccountMapper;
import com.qianyu.module.geo.dal.mysql.article.GeoArticleMapper;
import com.qianyu.module.geo.dal.mysql.publish.GeoPublishTaskMapper;
import com.qianyu.module.geo.dal.mysql.record.GeoPublishRecordMapper;
import com.qianyu.module.geo.enums.PublishStatusEnum;
import com.qianyu.module.geo.enums.PublishTaskStatusEnum;
import com.qianyu.module.geo.enums.RecordStatusEnum;
import com.qianyu.module.geo.utils.DictKeyConst;
import com.qianyu.module.geo.utils.GeoUtils;
import com.qianyu.module.system.dal.dataobject.dict.DictDataDO;
import com.qianyu.module.system.service.dict.DictDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * 发布任务 Service 实现类
 *
 * @author 系统管理员
 */
@Slf4j
@Service
@Validated
public class GeoPublishTaskServiceImpl implements GeoPublishTaskService {

    @Resource
    private GeoPublishTaskMapper publishTaskMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private GeoPublishComponent geoPublishComponent;

    @Autowired
    private GeoPublishRecordMapper recordMapper;

    @Autowired
    private GeoAccountMapper geoAccountMapper;

    @Autowired
    private DictDataService dictDataService;

    @Autowired
    private GeoArticleMapper geoArticleMapper;

    @Override
    public Long createPublishTask(GeoPublishTaskSaveReqVO createReqVO) {
        // 插入
        GeoPublishTaskDO publishTask = BeanUtils.toBean(createReqVO, GeoPublishTaskDO.class);
        publishTaskMapper.insert(publishTask);
        // 返回
        return publishTask.getId();
    }

    @Override
    public void updatePublishTask(GeoPublishTaskSaveReqVO updateReqVO) {
        // 更新
        GeoPublishTaskDO updateObj = BeanUtils.toBean(updateReqVO, GeoPublishTaskDO.class);
        publishTaskMapper.updateById(updateObj);
    }


    @Override
    public void deletePublishTask(Long id) {
        // 删除
        publishTaskMapper.deleteById(id);
    }

    @Override
        public void deletePublishTaskListByIds(List<Long> ids) {
        // 删除
        publishTaskMapper.deleteByIds(ids);
        }


    @Override
    public GeoPublishTaskDO getPublishTask(Long id) {
        return publishTaskMapper.selectById(id);
    }

    @Override
    public PageResult<GeoPublishTaskDO> getPublishTaskPage(GeoPublishTaskPageReqVO pageReqVO) {
        return publishTaskMapper.selectPage(pageReqVO);
    }

    @Override
    public void execute(Long id) {
        geoPublishComponent.immediatelySubmit(PublishTaskPOJO.builder().id( id).build() );
    }

    @Transactional
    @Override
    public void updateRecord(GeoPublishRecordSaveReqVO vo) {
        Long id = vo.getId();
        GeoPublishTaskDO geoPublishTaskDO = publishTaskMapper.selectById(id);
        if (geoPublishTaskDO == null){
            log.error("未获取到记录 {}", JsonUtils.toJsonString(geoPublishTaskDO));
            return;
        }

        Integer successCount = geoPublishTaskDO.getSuccessCount();

        DictDataDO dictDataDO = dictDataService.parseDictData(DictKeyConst.GEO_PUBLISH_PLATFORM, vo.getPlatform());
        if (dictDataDO == null){
            log.error("未获取到平台 {}", JsonUtils.toJsonString(vo));
            return;
        }

        GeoArticleDO geoArticleDO = geoArticleMapper.selectById(geoPublishTaskDO.getArticleId());
        if (geoArticleDO == null){
            log.error("未获取到文章 {}", JsonUtils.toJsonString(vo));
            return;
        }

        GeoAccountDO geoAccountDO = geoAccountMapper.selectOne(new LambdaQueryWrapperX<GeoAccountDO>()
                .eq(GeoAccountDO::getPlatform, dictDataDO.getValue()));
        if (geoAccountDO == null){
            log.error("未获取到账号 {}", JsonUtils.toJsonString(vo));
            return;
        }
        GeoPublishRecordDO record = recordMapper.selectOne(new LambdaQueryWrapperX<GeoPublishRecordDO>()
                .eq(GeoPublishRecordDO::getTaskId, id)
                .eq(GeoPublishRecordDO::getAccountId, geoAccountDO.getId()));

        if (record == null){
            record = new GeoPublishRecordDO();
        }

        record.setTaskId(id);
        record.setAccountId(geoAccountDO.getId());
        record.setArticleId(geoPublishTaskDO.getArticleId());
        record.setStatus(vo.getCode());
        recordMapper.insert(record);

        geoArticleDO.setStatus(vo.getCode());
        geoArticleMapper.updateById(geoArticleDO);

        if (RecordStatusEnum.SUCCESS.equals(RecordStatusEnum.enumOf(vo.getCode()))){
            geoPublishTaskDO.setSuccessCount(successCount ==  null ? 1 : successCount + 1);
            geoPublishTaskDO.setExecuteTime(GeoUtils.ofMilli(vo.getTimestamp()));
            publishTaskMapper.updateById(geoPublishTaskDO);

        }

    }

}