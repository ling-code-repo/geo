package com.qianyu.module.geo.controller.admin.word.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class GeoWordDistillReqVO implements Serializable {

    @Schema(description = "主词", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "主词不能为空")
    private String word;

    @Schema(description = "转化目标", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "转化目标不能为空")
    private String target;
}
