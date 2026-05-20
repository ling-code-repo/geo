package com.qianyu.module.geo.controller.admin.publish.vo;

import cn.idev.excel.util.StringUtils;
import com.qianyu.framework.excel.core.annotations.DictFormat;
import com.qianyu.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 发布任务 Response VO")
@Data
@ExcelIgnoreUnannotated
public class GeoPublishTaskRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "20109")
//    @ExcelProperty("id")
    private Long id;

    @Schema(description = "任务名", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("任务名")
    private String name;

    @Schema(description = "发布状态", example = "1")
    @ExcelProperty(value = "发布状态", converter = DictConvert.class)
    @DictFormat("geo_publish_status")
    private Integer status;

    private Integer taskStatus;

    private Integer headless;


    @Schema(description = "AI创作声明")
    @ExcelProperty(value = "AI创作声明", converter = DictConvert.class)
    @DictFormat("geo_declare_ai")
    private Integer declareAi;

    private Long articleId;

    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED)
    private String platforms;
    @Schema(description = "平台数", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("平台数")
    private Integer platformCount;

    @Schema(description = "错误消息")
    @ExcelProperty("错误消息")
    private String errorMessage;

    @Schema(description = "执行时间")
    @ExcelProperty("执行时间")
    private LocalDateTime executeTime;
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    public Integer getPlatformCount() {
        return StringUtils.isEmpty(platforms) ? 0 : platforms.split(",").length;
    }

}
