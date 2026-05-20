package com.qianyu.module.geo.controller.admin.instruction;

import com.qianyu.module.geo.controller.admin.file.vo.FileSimpleRespVO;
import com.qianyu.module.geo.dal.dataobject.file.GeoFileItemDO;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import com.qianyu.framework.common.pojo.PageParam;
import com.qianyu.framework.common.pojo.PageResult;
import com.qianyu.framework.common.pojo.CommonResult;
import com.qianyu.framework.common.util.object.BeanUtils;
import static com.qianyu.framework.common.pojo.CommonResult.success;

import com.qianyu.framework.excel.core.util.ExcelUtils;

import com.qianyu.framework.apilog.core.annotation.ApiAccessLog;
import static com.qianyu.framework.apilog.core.enums.OperateTypeEnum.*;

import com.qianyu.module.geo.controller.admin.instruction.vo.*;
import com.qianyu.module.geo.dal.dataobject.instruction.GeoInstructionDO;
import com.qianyu.module.geo.service.instruction.GeoInstructionService;

@Tag(name = "管理后台 - 创作指令")
@RestController
@RequestMapping("/geo/instruction")
@Validated
public class GeoInstructionController {

    @Resource
    private GeoInstructionService instructionService;

    @PostMapping("/create")
    @Operation(summary = "创建创作指令")
//    @PreAuthorize("@ss.hasPermission('geo:instruction:create')")
    public CommonResult<Long> createInstruction(@Valid @RequestBody GeoInstructionSaveReqVO createReqVO) {
        return success(instructionService.createInstruction(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新创作指令")
//    @PreAuthorize("@ss.hasPermission('geo:instruction:update')")
    public CommonResult<Boolean> updateInstruction(@Valid @RequestBody GeoInstructionSaveReqVO updateReqVO) {
        instructionService.updateInstruction(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除创作指令")
    @Parameter(name = "id", description = "编号", required = true)
//    @PreAuthorize("@ss.hasPermission('geo:instruction:delete')")
    public CommonResult<Boolean> deleteInstruction(@RequestParam("id") Long id) {
        instructionService.deleteInstruction(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除创作指令")
//                @PreAuthorize("@ss.hasPermission('geo:instruction:delete')")
    public CommonResult<Boolean> deleteInstructionList(@RequestParam("ids") List<Long> ids) {
        instructionService.deleteInstructionListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得创作指令")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('geo:instruction:query')")
    public CommonResult<GeoInstructionUpdateRespVO> getInstruction(@RequestParam("id") Long id) {
        GeoInstructionDO instruction = instructionService.getInstruction(id);
        return success(BeanUtils.toBean(instruction, GeoInstructionUpdateRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得创作指令分页")
//    @PreAuthorize("@ss.hasPermission('geo:instruction:query')")
    public CommonResult<PageResult<GeoInstructionRespVO>> getInstructionPage(@Valid GeoInstructionPageReqVO pageReqVO) {
        PageResult<GeoInstructionDO> pageResult = instructionService.getInstructionPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, GeoInstructionRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出创作指令 Excel")
//    @PreAuthorize("@ss.hasPermission('geo:instruction:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportInstructionExcel(@Valid GeoInstructionPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<GeoInstructionDO> list = instructionService.getInstructionPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "创作指令.xls", "数据", GeoInstructionRespVO.class,
                        BeanUtils.toBean(list, GeoInstructionRespVO.class));
    }

    @GetMapping(value = {"/list-all-simple", "simple-list"})
    @Operation(summary = "获取岗位全列表", description = "只包含被开启的岗位，主要用于前端的下拉选项")
    public CommonResult<List<InstructionSimpleRespVO>> getSimplePostList() {
        // 获得岗位列表，只要开启状态的
        GeoInstructionPageReqVO vo = new GeoInstructionPageReqVO();
        vo.setPageSize(PageParam.PAGE_SIZE_NONE);
        vo.setPageNo(PageParam.PAGE_SIZE_NONE);
        List<GeoInstructionDO> list = instructionService.getInstructionPage(vo).getList();
        return success(BeanUtils.toBean(list, InstructionSimpleRespVO.class));
    }

}