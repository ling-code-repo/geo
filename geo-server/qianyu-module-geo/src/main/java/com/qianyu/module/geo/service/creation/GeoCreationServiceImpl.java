package com.qianyu.module.geo.service.creation;

import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.object.BeanUtils;
import com.qianyu.module.geo.component.GeoCreationComponent;
import com.qianyu.module.geo.component.pojo.CreationTaskPOJO;
import com.qianyu.module.geo.controller.admin.creation.vo.GeoCreationPageReqVO;
import com.qianyu.module.geo.controller.admin.creation.vo.GeoCreationSaveReqVO;
import com.qianyu.module.geo.dal.dataobject.creation.GeoCreationDO;
import com.qianyu.module.geo.dal.dataobject.creation.GeoCreationItemDO;
import com.qianyu.module.geo.dal.mysql.creation.GeoCreationMapper;
import com.qianyu.module.geo.utils.GeoUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * AI创作 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class GeoCreationServiceImpl implements GeoCreationService {

    @Resource
    private GeoCreationMapper creationMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private GeoCreationComponent geoCreationComponent;

    @Override
    public Long createCreation(GeoCreationSaveReqVO createReqVO) {
        // 插入
        GeoCreationDO creation = BeanUtils.toBean(createReqVO, GeoCreationDO.class);
        creationMapper.insert(creation);
        geoCreationComponent.submit(CreationTaskPOJO.builder().id(creation.getId()).build());
        // 返回
        return creation.getId();
    }

    @Override
    public void updateCreation(GeoCreationSaveReqVO updateReqVO) {
        // 更新
        GeoCreationDO updateObj = BeanUtils.toBean(updateReqVO, GeoCreationDO.class);
        creationMapper.updateById(updateObj);
        geoCreationComponent.submit(CreationTaskPOJO.builder().id(updateObj.getId()).build());
    }

    @Override
    public void deleteCreation(Long id) {
        // 删除
        creationMapper.deleteById(id);
    }

    @Override
        public void deleteCreationListByIds(List<Long> ids) {
        // 删除
        creationMapper.deleteByIds(ids);
        }




    @Override
    public GeoCreationDO getCreation(Long id) {
        return creationMapper.selectById(id);
    }

    @Override
    public PageResult<GeoCreationItemDO> getCreationPage(GeoCreationPageReqVO pageReqVO) {
        return creationMapper.selectPage(pageReqVO);
    }

}