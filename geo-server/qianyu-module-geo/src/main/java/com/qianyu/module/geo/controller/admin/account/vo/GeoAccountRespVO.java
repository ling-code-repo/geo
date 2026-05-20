package com.qianyu.module.geo.controller.admin.account.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import com.qianyu.framework.excel.core.annotations.DictFormat;
import com.qianyu.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 授权账号 Response VO")
@Data
@ExcelIgnoreUnannotated
public class GeoAccountRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "24777")
    private Long id;

    @Schema(description = "账号名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("账号名称")
    private String name;
    @Schema(description = "头像路径", requiredMode = Schema.RequiredMode.REQUIRED)
    private String path;

    @Schema(description = "平台")
    @ExcelProperty(value = "平台", converter = DictConvert.class)
    @DictFormat("geo_publish_platform") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer platform;

    @Schema(description = "发布状态", example = "2")
    @ExcelProperty(value = "发布状态", converter = DictConvert.class)
    @DictFormat("geo_publish_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer publishStatus;

    @Schema(description = "每日发布次数", example = "32716")
    @ExcelProperty("每日发布次数")
    private Integer publishCount;

    @Schema(description = "授权状态 0未授权 1已授权", example = "1")
//    @ExcelProperty(value = "授权状态", converter = DictConvert.class)
    @DictFormat("geo_authorize_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer authorizeStatus;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
