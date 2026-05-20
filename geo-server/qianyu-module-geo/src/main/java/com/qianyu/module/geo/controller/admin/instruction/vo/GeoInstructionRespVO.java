package com.qianyu.module.geo.controller.admin.instruction.vo;

import com.qianyu.framework.excel.core.annotations.DictFormat;
import com.qianyu.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 创作指令 Response VO")
@Data
@ExcelIgnoreUnannotated
public class GeoInstructionRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "8152")
//    @ExcelProperty("id")
    private Long id;

    @Schema(description = "指令名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("指令名称")
    private String instructionName;

    @Schema(description = "指令类型", example = "1")
    @ExcelProperty(value = "指令类型", converter = DictConvert.class)
    @DictFormat("geo_instruction_type")
    private Integer instructionType;

//    @Schema(description = "指令内容", requiredMode = Schema.RequiredMode.REQUIRED)
//    @ExcelProperty("指令内容")
//    private String content;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
