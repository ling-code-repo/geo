package com.qianyu.module.geo.controller.admin.publish.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Schema(description = "管理后台")
@Data
public class GeoPublishRecordSaveReqVO implements Serializable {
    @NotNull(message = "不能为空")
    private Long id;

    @NotEmpty(message = "平台不能为空")
    private String platform;

    @NotNull(message = "时间戳不能为空")
    private Long timestamp;

    @NotNull(message = "状态不能为空")
    private Integer code;

}