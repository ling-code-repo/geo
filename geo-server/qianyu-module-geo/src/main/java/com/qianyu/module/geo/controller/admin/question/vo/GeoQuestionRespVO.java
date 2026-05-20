package com.qianyu.module.geo.controller.admin.question.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import com.qianyu.framework.excel.core.annotations.DictFormat;
import com.qianyu.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 扩展问题 Response VO")
@Data
@ExcelIgnoreUnannotated
public class GeoQuestionRespVO {

    @Schema(description = "问题id", requiredMode = Schema.RequiredMode.REQUIRED, example = "28425")
//    @ExcelProperty("问题id")
    private Long id;

    @Schema(description = "主词", requiredMode = Schema.RequiredMode.REQUIRED, example = "5664")
    @ExcelProperty("主词")
    private String word;

    @Schema(description = "转化目标", requiredMode = Schema.RequiredMode.REQUIRED, example = "5664")
    private String target;

    private Long wordId;

    @Schema(description = "问题", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("问题")
    private String question;

    @Schema(description = "收录状态", example = "1")
    @ExcelProperty(value = "收录状态", converter = DictConvert.class)
    @DictFormat("geo_include_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
