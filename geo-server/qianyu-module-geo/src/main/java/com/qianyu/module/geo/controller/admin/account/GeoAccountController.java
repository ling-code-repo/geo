package com.qianyu.module.geo.controller.admin.account;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

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

import com.qianyu.module.geo.controller.admin.account.vo.*;
import com.qianyu.module.geo.dal.dataobject.account.GeoAccountDO;
import com.qianyu.module.geo.service.account.GeoAccountService;

@Tag(name = "管理后台 - 授权账号")
@RestController
@RequestMapping("/geo/account")
@Validated
public class GeoAccountController {

    @Resource
    private GeoAccountService accountService;

    @PutMapping("/update")
    @Operation(summary = "更新授权账号")
//    @PreAuthorize("@ss.hasPermission('geo:account:update')")
    public CommonResult<Boolean> updateAccount(@Valid @RequestBody GeoAccountSaveReqVO updateReqVO) {
        accountService.updateAccount(updateReqVO);
        return success(true);
    }


    @PostMapping("/saveOrUpdate")
    @Operation(summary = "创建授权账号")
//    @PreAuthorize("@ss.hasPermission('geo:account:create')")
    public CommonResult<Long> saveOrUpdateAccount(@Valid @RequestBody GeoAccountSaveOrUpdateReqVO vo) {
        return success(accountService.saveOrUpdateAccount(vo));
    }



    @DeleteMapping("/delete")
    @Operation(summary = "删除授权账号")
    @Parameter(name = "id", description = "编号", required = true)
//    @PreAuthorize("@ss.hasPermission('geo:account:delete')")
    public CommonResult<Boolean> deleteAccount(@RequestParam("id") Long id) {
        accountService.deleteAccount(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除授权账号")
//                @PreAuthorize("@ss.hasPermission('geo:account:delete')")
    public CommonResult<Boolean> deleteAccountList(@RequestParam("ids") List<Long> ids) {
        accountService.deleteAccountListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得授权账号")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('geo:account:query')")
    public CommonResult<GeoAccountRespVO> getAccount(@RequestParam("id") Long id) {
        GeoAccountDO account = accountService.getAccount(id);
        return success(BeanUtils.toBean(account, GeoAccountRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得授权账号分页")
//    @PreAuthorize("@ss.hasPermission('geo:account:query')")
    public CommonResult<PageResult<GeoAccountRespVO>> getAccountPage(@Valid GeoAccountPageReqVO pageReqVO) {
        PageResult<GeoAccountDO> pageResult = accountService.getAccountPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, GeoAccountRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出授权账号 Excel")
//    @PreAuthorize("@ss.hasPermission('geo:account:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportAccountExcel(@Valid GeoAccountPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<GeoAccountDO> list = accountService.getAccountPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "授权账号.xls", "数据", GeoAccountRespVO.class,
                        BeanUtils.toBean(list, GeoAccountRespVO.class));
    }

}