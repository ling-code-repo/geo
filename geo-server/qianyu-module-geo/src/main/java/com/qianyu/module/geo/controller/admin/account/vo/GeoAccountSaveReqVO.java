package com.qianyu.module.geo.controller.admin.account.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 授权账号新增/修改 Request VO")
@Data
public class GeoAccountSaveReqVO {


    private Long id;

    @Schema(description = "发布状态 0不发布 1需要发布", example = "2")
    private Integer publishStatus;

    @Schema(description = "每日发布次数", example = "32716")
    private Integer publishCount;

}