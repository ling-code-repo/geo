package com.qianyu.module.geo.controller.admin.article.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 创作文章新增/修改 Request VO")
@Data
public class GeoArticleSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "16350")
    private Long id;

    @Schema(description = "文章标题")
    private String title;

    private String pictureIds;

    @Schema(description = "文章内容")
    private String contentMarkdown;

}