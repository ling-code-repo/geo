package com.qianyu.module.geo.controller.admin.record.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 发布记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class GeoPublishRecordRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "29546")
    @ExcelProperty("id")
    private Long id;

   private String taskName;
   private String accountName;
   private String title;

   private String platform;

    @Schema(description = "发布状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("发布状态")
    private Integer status;

    @Schema(description = "错误消息", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("错误消息")
    private String errorMessage;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
