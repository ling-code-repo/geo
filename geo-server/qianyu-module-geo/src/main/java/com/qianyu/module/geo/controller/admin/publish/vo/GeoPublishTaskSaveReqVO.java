package com.qianyu.module.geo.controller.admin.publish.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 发布任务新增/修改 Request VO")
@Data
public class GeoPublishTaskSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "20109")
    private Long id;

    @Schema(description = "任务名", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "任务名不能为空")
    private String name;

    @Schema(description = "声明AI创作")
    private Integer declareAi;

    @Schema(description = "文章id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文章id不能为空")
    private Long articleId;
    private Integer headless;

    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "平台不能为空")
    private String platforms;

}