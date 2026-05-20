package com.qianyu.module.geo.controller.admin.question.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.qianyu.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.qianyu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 扩展问题分页 Request VO")
@Data
public class GeoQuestionPageReqVO extends PageParam {

    @Schema(description = "主词", example = "5664")
    private String word;

    @Schema(description = "问题")
    private String question;

    @Schema(description = "收录状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}