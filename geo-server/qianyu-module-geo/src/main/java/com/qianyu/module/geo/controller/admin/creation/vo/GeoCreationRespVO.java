package com.qianyu.module.geo.controller.admin.creation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import com.qianyu.framework.excel.core.annotations.DictFormat;
import com.qianyu.framework.excel.core.convert.DictConvert;

import javax.validation.constraints.NotEmpty;

@Schema(description = "管理后台 - AI创作 Response VO")
@Data
@ExcelIgnoreUnannotated
public class GeoCreationRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "7552")
//    @ExcelProperty("id")
    private Long id;

    @Schema(description = "创作名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("创作名称")
    private String name;


    private Long wordId;

    @Schema(description = "蒸馏词", requiredMode = Schema.RequiredMode.REQUIRED, example = "18123")
    @ExcelProperty("蒸馏词")
    private String word;

    @Schema(description = "创作文章数量", example = "22461")
    @ExcelProperty("创作数量")
    private Integer count;

    @Schema(description = "已创作文章数量", example = "19774")
    @ExcelProperty("已创作数量")
    private Integer successCount;

    @Schema(description = "知识库id", example = "26595")
    private Long knowledgeId;

    @Schema(description = "引用知识库", example = "26595")
    @ExcelProperty("知识库")
    private String quoteKnowledge;

    public String getQuoteKnowledge() {
        return knowledgeId == null ? "未引用知识库" : "引用知识库";
    }

    @Schema(description = "创作状态", example = "2")
    @ExcelProperty(value = "创作状态", converter = DictConvert.class)
    @DictFormat("geo_creation_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "错误消息")
    @ExcelProperty("错误消息")
    private String errorMessage;

    @Schema(description = "文章id", example = "21118")
//    @ExcelProperty("文章")
    private Long articleId;

    @Schema(description = "创作时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("最新创作时间")
    private LocalDateTime creationTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "内容指令id", example = "23846")
    private Long contentId;

    @Schema(description = "标题指令id", example = "19864")
    private Long titleId;

    @Schema(description = "配图数量", example = "27523")
    private Integer pictureCount;

    @Schema(description = "图片id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String pictureIds;

}
