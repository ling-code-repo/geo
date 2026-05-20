package com.qianyu.module.geo.controller.admin.article.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 创作文章简单对象")
@Data
public class ArticleSimpleRespVO {
    private Long id;

    private String title;
}
