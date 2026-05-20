package com.qianyu.module.geo.controller.admin.creation.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.qianyu.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.qianyu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - AI创作分页 Request VO")
@Data
public class GeoCreationPageReqVO extends PageParam {

    @Schema(description = "创作名称", example = "张三")
    private String name;

    @Schema(description = "蒸馏词id", example = "18123")
    private Long wordId;


    @Schema(description = "创作状态", example = "2")
    private Integer status;

    @Schema(description = "创作时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] creationTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}