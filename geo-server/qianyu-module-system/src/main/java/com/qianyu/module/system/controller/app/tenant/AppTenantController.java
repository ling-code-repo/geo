package com.qianyu.module.system.controller.app.tenant;

import com.qianyu.framework.common.enums.CommonStatusEnum;
import com.qianyu.framework.common.pojo.CommonResult;
import com.qianyu.framework.common.util.object.BeanUtils;
import com.qianyu.framework.tenant.core.aop.TenantIgnore;
import com.qianyu.module.system.controller.app.tenant.vo.AppTenantRespVO;
import com.qianyu.module.system.dal.dataobject.tenant.TenantDO;
import com.qianyu.module.system.service.tenant.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;

import static com.qianyu.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 租户")
@RestController
@RequestMapping("/system/tenant")
public class AppTenantController {

    @Resource
    private TenantService tenantService;

    @GetMapping("/get-by-website")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "使用域名，获得租户信息", description = "根据用户的域名，获得租户信息")
    @Parameter(name = "website", description = "域名", required = true, example = "www.iocoder.cn")
    public CommonResult<AppTenantRespVO> getTenantByWebsite(@RequestParam("website") String website) {
        TenantDO tenant = tenantService.getTenantByWebsite(website);
        if (tenant == null || CommonStatusEnum.isDisable(tenant.getStatus())) {
            return success(null);
        }
        return success(BeanUtils.toBean(tenant, AppTenantRespVO.class));
    }

}
