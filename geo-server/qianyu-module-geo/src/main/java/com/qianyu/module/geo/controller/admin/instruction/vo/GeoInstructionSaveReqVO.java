package com.qianyu.module.geo.controller.admin.instruction.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 创作指令新增/修改 Request VO")
@Data
public class GeoInstructionSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "8152")
    private Long id;

    @Schema(description = "指令名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "指令名称不能为空")
    private String instructionName;

    @Schema(description = "指令类型", example = "1")
    private Integer instructionType;

    @Schema(description = "指令内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "指令内容不能为空")
    private String content;

}