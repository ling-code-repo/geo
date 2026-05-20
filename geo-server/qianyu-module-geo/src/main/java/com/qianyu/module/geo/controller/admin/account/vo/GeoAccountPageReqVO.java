package com.qianyu.module.geo.controller.admin.account.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.qianyu.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.qianyu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 授权账号分页 Request VO")
@Data
public class GeoAccountPageReqVO extends PageParam {

    @Schema(description = "账号名称", example = "张三")
    private String name;

    @Schema(description = "平台")
    private Integer platform;

    @Schema(description = "发布状态 0不发布 1需要发布", example = "2")
    private Integer publishStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}