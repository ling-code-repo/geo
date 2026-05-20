package com.qianyu.module.geo.controller.admin.creation;

import com.qianyu.module.geo.dal.dataobject.creation.GeoCreationItemDO;
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

import com.qianyu.module.geo.controller.admin.creation.vo.*;
import com.qianyu.module.geo.dal.dataobject.creation.GeoCreationDO;
import com.qianyu.module.geo.service.creation.GeoCreationService;

@Tag(name = "管理后台 - AI创作")
@RestController
@RequestMapping("/geo/creation")
@Validated
public class GeoCreationController {

    @Resource
    private GeoCreationService creationService;

    @PostMapping("/create")
    @Operation(summary = "创建AI创作")
//    @PreAuthorize("@ss.hasPermission('geo:creation:create')")
    public CommonResult<Long> createCreation(@Valid @RequestBody GeoCreationSaveReqVO createReqVO) {
        return success(creationService.createCreation(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新AI创作")
//    @PreAuthorize("@ss.hasPermission('geo:creation:update')")
    public CommonResult<Boolean> updateCreation(@Valid @RequestBody GeoCreationSaveReqVO updateReqVO) {
        creationService.updateCreation(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除AI创作")
    @Parameter(name = "id", description = "编号", required = true)
//    @PreAuthorize("@ss.hasPermission('geo:creation:delete')")
    public CommonResult<Boolean> deleteCreation(@RequestParam("id") Long id) {
        creationService.deleteCreation(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除AI创作")
//                @PreAuthorize("@ss.hasPermission('geo:creation:delete')")
    public CommonResult<Boolean> deleteCreationList(@RequestParam("ids") List<Long> ids) {
        creationService.deleteCreationListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得AI创作")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('geo:creation:query')")
    public CommonResult<GeoCreationRespVO> getCreation(@RequestParam("id") Long id) {
        GeoCreationDO creation = creationService.getCreation(id);
        return success(BeanUtils.toBean(creation, GeoCreationRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得AI创作分页")
//    @PreAuthorize("@ss.hasPermission('geo:creation:query')")
    public CommonResult<PageResult<GeoCreationRespVO>> getCreationPage(@Valid GeoCreationPageReqVO pageReqVO) {
        PageResult<GeoCreationItemDO> pageResult = creationService.getCreationPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, GeoCreationRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出AI创作 Excel")
//    @PreAuthorize("@ss.hasPermission('geo:creation:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCreationExcel(@Valid GeoCreationPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<GeoCreationItemDO> list = creationService.getCreationPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "AI创作.xls", "数据", GeoCreationRespVO.class,
                        BeanUtils.toBean(list, GeoCreationRespVO.class));
    }

}