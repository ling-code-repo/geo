package com.qianyu.module.geo.controller.admin.file.vo;

import com.qianyu.module.geo.enums.GeoFileCategoryEnum;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.qianyu.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.qianyu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 企业图库分页 Request VO")
@Data
public class GeoFilePageReqVO extends PageParam {

    @Schema(description = "文件名称，模糊匹配", example = "test")
    private String name;

    private String type;

    @Schema(description = "Geo文件类别", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件类别不能为空")
    private GeoFileCategoryEnum category;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}