package com.qianyu.module.geo.controller.admin.publish.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Schema(description = "管理后台")
@Data
public class GeoExecutePublishTaskReqVO implements Serializable {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "20109")
    private Long id;

}