package com.qianyu.module.geo.controller.admin.word.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import com.qianyu.framework.excel.core.annotations.DictFormat;
import com.qianyu.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 蒸馏词 Response VO")
@Data
@ExcelIgnoreUnannotated
public class GeoWordRespVO {

    private Long id;

    @Schema(description = "主词", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主词")
    private String word;



    @Schema(description = "转化目标", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("转化目标")
    private String target;

    @Schema(description = "问题数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("问题数量")
    private Integer questionCount;

    @Schema(description = "是否优化")
    @ExcelProperty(value = "是否优化", converter = DictConvert.class)
    @DictFormat("geo_optimized") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer optimized;

    @Schema(description = "是否查询拓展词")
    @ExcelProperty(value = "是否查询拓展词", converter = DictConvert.class)
    @DictFormat("geo_expand") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer expand;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;




}
