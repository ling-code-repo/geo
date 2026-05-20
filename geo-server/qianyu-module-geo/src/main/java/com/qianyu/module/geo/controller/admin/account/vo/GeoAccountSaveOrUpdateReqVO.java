package com.qianyu.module.geo.controller.admin.account.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 授权账号新增/修改 Request VO")
@Data
public class GeoAccountSaveOrUpdateReqVO {

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "名称")
    private String name;
    @NotBlank(message = "头像不能为空")
    @Schema(description = "头像")
    private String avatar;
    @NotNull(message = "平台值不能为空")
    @Schema(description = "平台值", example = "0是抖音")
    private Long value;

}