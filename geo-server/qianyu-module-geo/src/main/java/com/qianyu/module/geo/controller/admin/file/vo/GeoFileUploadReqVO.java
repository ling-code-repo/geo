package com.qianyu.module.geo.controller.admin.file.vo;

import com.qianyu.module.geo.enums.GeoFileCategoryEnum;
import com.qianyu.module.infra.controller.admin.file.vo.file.FileUploadReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "Geo优化 - 企业知识库和图库 Request VO")
@Data
public class GeoFileUploadReqVO extends FileUploadReqVO {

    @Schema(description = "Geo文件类别", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件类别不能为空")
    private GeoFileCategoryEnum category;
}
