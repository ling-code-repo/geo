package com.qianyu.module.geo.controller.admin.word.vo;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 蒸馏词的精简 Response VO")
@Data
public class WorkSimpleRespVO {
    @Schema(description = "主词序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "主词", requiredMode = Schema.RequiredMode.REQUIRED, example = "小土豆")
    private String word;

    private Integer questionCount;

    public String getLabel() {
        return word+("（问题数量："+questionCount+"）");
    }
}
