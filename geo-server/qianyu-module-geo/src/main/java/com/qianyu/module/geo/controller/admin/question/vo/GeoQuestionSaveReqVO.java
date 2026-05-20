package com.qianyu.module.geo.controller.admin.question.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 扩展问题新增/修改 Request VO")
@Data
public class GeoQuestionSaveReqVO {

    @Schema(description = "问题id", requiredMode = Schema.RequiredMode.REQUIRED, example = "28425")
    private Long id;

    @Schema(description = "主词id", requiredMode = Schema.RequiredMode.REQUIRED, example = "5664")
    @NotNull(message = "主词id不能为空")
    private Long wordId;

    @Schema(description = "问题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "问题不能为空")
    private String question;

    @Schema(description = "收录状态", example = "1")
    private Integer status;

}