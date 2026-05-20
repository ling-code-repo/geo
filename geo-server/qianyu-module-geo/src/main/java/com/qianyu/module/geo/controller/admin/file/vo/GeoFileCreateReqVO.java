package com.qianyu.module.geo.controller.admin.file.vo;

import com.qianyu.module.geo.enums.GeoFileCategoryEnum;
import com.qianyu.module.infra.controller.admin.file.vo.file.FileCreateReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Schema(description = "管理后台 - 文件创建 Request VO")
@Data
public class GeoFileCreateReqVO extends FileCreateReqVO {

    @Schema(description = "Geo文件类别", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Geo文件类别不能为空")
    private GeoFileCategoryEnum category;
}
