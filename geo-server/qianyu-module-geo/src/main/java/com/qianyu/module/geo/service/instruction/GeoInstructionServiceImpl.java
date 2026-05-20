package com.qianyu.module.geo.service.instruction;

import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.util.object.BeanUtils;
import com.qianyu.module.geo.controller.admin.instruction.vo.GeoInstructionPageReqVO;
import com.qianyu.module.geo.controller.admin.instruction.vo.GeoInstructionSaveReqVO;
import com.qianyu.module.geo.dal.dataobject.instruction.GeoInstructionDO;
import com.qianyu.module.geo.dal.mysql.instruction.GeoInstructionMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * 创作指令 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class GeoInstructionServiceImpl implements GeoInstructionService {

    @Resource
    private GeoInstructionMapper instructionMapper;

    @Override
    public Long createInstruction(GeoInstructionSaveReqVO createReqVO) {
        // 插入
        GeoInstructionDO instruction = BeanUtils.toBean(createReqVO, GeoInstructionDO.class);
        instructionMapper.insert(instruction);

        // 返回
        return instruction.getId();
    }

    @Override
    public void updateInstruction(GeoInstructionSaveReqVO updateReqVO) {
        // 更新
        GeoInstructionDO updateObj = BeanUtils.toBean(updateReqVO, GeoInstructionDO.class);
        instructionMapper.updateById(updateObj);
    }

    @Override
    public void deleteInstruction(Long id) {
        // 删除
        instructionMapper.deleteById(id);
    }

    @Override
        public void deleteInstructionListByIds(List<Long> ids) {
        // 删除
        instructionMapper.deleteByIds(ids);
        }




    @Override
    public GeoInstructionDO getInstruction(Long id) {
        return instructionMapper.selectById(id);
    }

    @Override
    public PageResult<GeoInstructionDO> getInstructionPage(GeoInstructionPageReqVO pageReqVO) {
        return instructionMapper.selectPage(pageReqVO);
    }

}