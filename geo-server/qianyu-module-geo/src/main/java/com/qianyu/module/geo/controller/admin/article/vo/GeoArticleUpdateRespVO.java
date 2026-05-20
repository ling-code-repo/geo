package com.qianyu.module.geo.controller.admin.article.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.qianyu.framework.excel.core.annotations.DictFormat;
import com.qianyu.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 创作文章 Response VO")
@Data
@ExcelIgnoreUnannotated
public class GeoArticleUpdateRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "16350")
    private Long id;

    @Schema(description = "文章标题")
    private String title;

    private String pictureIds;

    @Schema(description = "文章内容")
    private String contentMarkdown;

}
