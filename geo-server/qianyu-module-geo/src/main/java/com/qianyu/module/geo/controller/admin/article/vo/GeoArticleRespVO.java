package com.qianyu.module.geo.controller.admin.article.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import com.qianyu.framework.excel.core.annotations.DictFormat;
import com.qianyu.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 创作文章 Response VO")
@Data
@ExcelIgnoreUnannotated
public class GeoArticleRespVO {

    private Long id;

    @Schema(description = "文章标题")
    @ExcelProperty("文章标题")
    private String title;

    @Schema(description = "发布次数", example = "3496")
    @ExcelProperty("发布次数")
    private Integer publishCount;

    @Schema(description = "0未发布 1已发布", example = "1")
    @ExcelProperty(value = "0未发布 1已发布", converter = DictConvert.class)
    @DictFormat("geo_article_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
