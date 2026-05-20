package com.qianyu.module.geo.controller.admin.file.vo;

import com.qianyu.module.geo.enums.GeoFileCategoryEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 蒸馏词的精简 Response VO")
@Data
public class FileSimpleRespVO {
    @Schema(description = "主词序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "主词", requiredMode = Schema.RequiredMode.REQUIRED, example = "小土豆")
    private String name;

    private String url;

    private GeoFileCategoryEnum category;


    public String getLabel() {
        int i = name.indexOf(".");
        if (i != -1) {
            return name.substring(0,i );
        }
        return name;
    }
}
