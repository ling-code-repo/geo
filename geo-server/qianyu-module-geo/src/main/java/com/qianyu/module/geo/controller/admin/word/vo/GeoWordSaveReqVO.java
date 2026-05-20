package com.qianyu.module.geo.controller.admin.word.vo;

import com.qianyu.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 蒸馏词新增/修改 Request VO")
@Data
public class GeoWordSaveReqVO extends GeoWordDistillReqVO {

    private Long id;

    @Schema(description = "是否优化")
    private Integer optimized;

    @Schema(description = "是否查询拓展词")
    private Integer expand;

    private List<String> questions;

}