package com.qianyu.module.geo.controller.admin.word.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.qianyu.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.qianyu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 蒸馏词分页 Request VO")
@Data
public class GeoWordPageReqVO extends PageParam {

    @Schema(description = "主词")
    private String word;

    @Schema(description = "转化目标")
    private String target;

    @Schema(description = "是否优化")
    private Integer optimized;

    @Schema(description = "是否查询拓展词")
    private Integer expand;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}