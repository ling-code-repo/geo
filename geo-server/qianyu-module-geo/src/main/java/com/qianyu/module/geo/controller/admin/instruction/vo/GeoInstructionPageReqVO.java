package com.qianyu.module.geo.controller.admin.instruction.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.qianyu.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.qianyu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 创作指令分页 Request VO")
@Data
public class GeoInstructionPageReqVO extends PageParam {

    @Schema(description = "指令名称", example = "赵六")
    private String instructionName;

    @Schema(description = "指令类型", example = "1")
    private Integer instructionType;

//    @Schema(description = "指令内容")
//    private String content;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}