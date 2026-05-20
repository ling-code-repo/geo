package com.qianyu.module.geo.service.instruction;

import java.util.*;
import javax.validation.*;
import com.qianyu.module.geo.controller.admin.instruction.vo.*;
import com.qianyu.module.geo.dal.dataobject.instruction.GeoInstructionDO;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.pojo.PageParam;

/**
 * 创作指令 Service 接口
 *
 * @author 系统管理员
 */
public interface GeoInstructionService {

    /**
     * 创建创作指令
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createInstruction(@Valid GeoInstructionSaveReqVO createReqVO);

    /**
     * 更新创作指令
     *
     * @param updateReqVO 更新信息
     */
    void updateInstruction(@Valid GeoInstructionSaveReqVO updateReqVO);

    /**
     * 删除创作指令
     *
     * @param id 编号
     */
    void deleteInstruction(Long id);

    /**
    * 批量删除创作指令
    *
    * @param ids 编号
    */
    void deleteInstructionListByIds(List<Long> ids);

    /**
     * 获得创作指令
     *
     * @param id 编号
     * @return 创作指令
     */
    GeoInstructionDO getInstruction(Long id);

    /**
     * 获得创作指令分页
     *
     * @param pageReqVO 分页查询
     * @return 创作指令分页
     */
    PageResult<GeoInstructionDO> getInstructionPage(GeoInstructionPageReqVO pageReqVO);

}