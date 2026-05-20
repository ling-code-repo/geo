package com.qianyu.module.geo.controller.admin.creation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - AI创作新增/修改 Request VO")
@Data
public class GeoCreationSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "7552")
    private Long id;

    @Schema(description = "创作名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "创作名称不能为空")
    private String name;

    @Schema(description = "蒸馏词id", requiredMode = Schema.RequiredMode.REQUIRED, example = "18123")
    @NotNull(message = "蒸馏词id不能为空")
    private Long wordId;

    @Schema(description = "创作文章数量", example = "22461")
    private Integer count;

    @Schema(description = "知识库id", example = "26595")
    private Long knowledgeId;

    @Schema(description = "图片id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "图片id不能为空")
    private String pictureIds;

    @Schema(description = "配图数量", example = "27523")
    private Integer pictureCount;

    @Schema(description = "内容指令id", example = "23846")
    private Long contentId;

    @Schema(description = "标题指令id", example = "19864")
    private Long titleId;

}