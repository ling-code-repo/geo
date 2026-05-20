package com.qianyu.module.geo.controller.admin.record;

import com.qianyu.module.geo.dal.dataobject.record.GeoPublishRecordItemDO;
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

import com.qianyu.module.geo.controller.admin.record.vo.*;
import com.qianyu.module.geo.dal.dataobject.record.GeoPublishRecordDO;
import com.qianyu.module.geo.service.record.GeoPublishRecordService;

@Tag(name = "管理后台 - 发布记录")
@RestController
@RequestMapping("/geo/publish-record")
@Validated
public class GeoPublishRecordController {

    @Resource
    private GeoPublishRecordService publishRecordService;

    @PostMapping("/create")
    @Operation(summary = "创建发布记录")
//    @PreAuthorize("@ss.hasPermission('geo:publish-record:create')")
    public CommonResult<Long> createPublishRecord(@Valid @RequestBody GeoPublishRecordSaveReqVO createReqVO) {
        return success(publishRecordService.createPublishRecord(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新发布记录")
//    @PreAuthorize("@ss.hasPermission('geo:publish-record:update')")
    public CommonResult<Boolean> updatePublishRecord(@Valid @RequestBody GeoPublishRecordSaveReqVO updateReqVO) {
        publishRecordService.updatePublishRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除发布记录")
    @Parameter(name = "id", description = "编号", required = true)
//    @PreAuthorize("@ss.hasPermission('geo:publish-record:delete')")
    public CommonResult<Boolean> deletePublishRecord(@RequestParam("id") Long id) {
        publishRecordService.deletePublishRecord(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除发布记录")
//                @PreAuthorize("@ss.hasPermission('geo:publish-record:delete')")
    public CommonResult<Boolean> deletePublishRecordList(@RequestParam("ids") List<Long> ids) {
        publishRecordService.deletePublishRecordListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得发布记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('geo:publish-record:query')")
    public CommonResult<GeoPublishRecordRespVO> getPublishRecord(@RequestParam("id") Long id) {
        GeoPublishRecordDO publishRecord = publishRecordService.getPublishRecord(id);
        return success(BeanUtils.toBean(publishRecord, GeoPublishRecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得发布记录分页")
//    @PreAuthorize("@ss.hasPermission('geo:publish-record:query')")
    public CommonResult<PageResult<GeoPublishRecordRespVO>> getPublishRecordPage(@Valid GeoPublishRecordPageReqVO pageReqVO) {
        PageResult<GeoPublishRecordItemDO> pageResult = publishRecordService.getPublishRecordPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, GeoPublishRecordRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出发布记录 Excel")
//    @PreAuthorize("@ss.hasPermission('geo:publish-record:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPublishRecordExcel(@Valid GeoPublishRecordPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<GeoPublishRecordItemDO> list = publishRecordService.getPublishRecordPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "发布记录.xls", "数据", GeoPublishRecordRespVO.class,
                        BeanUtils.toBean(list, GeoPublishRecordRespVO.class));
    }

}