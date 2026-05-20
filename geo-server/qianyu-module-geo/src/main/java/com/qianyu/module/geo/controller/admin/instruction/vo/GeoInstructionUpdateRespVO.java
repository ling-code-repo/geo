package com.qianyu.module.geo.controller.admin.instruction.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.qianyu.module.geo.dal.dataobject.instruction.GeoInstructionDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 创作指令 Response VO")
@Data
@ExcelIgnoreUnannotated
public class GeoInstructionUpdateRespVO extends GeoInstructionRespVO {

    @Schema(description = "指令内容", requiredMode = Schema.RequiredMode.REQUIRED)
//    @ExcelProperty("指令内容")
    private String content;


}
