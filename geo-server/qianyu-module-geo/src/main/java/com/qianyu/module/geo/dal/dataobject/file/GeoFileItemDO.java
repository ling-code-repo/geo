package com.qianyu.module.geo.dal.dataobject.file;

import cn.idev.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianyu.module.geo.enums.GeoFileCategoryEnum;
import com.qianyu.module.infra.dal.dataobject.file.FileDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 企业图库 DO
 *
 * @author 系统管理员
 */
@TableName("geo_file")
@KeySequence("geo_file_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@ToString(callSuper = true)
public class GeoFileItemDO extends FileDO {

    private  Long id;

    private  Long fileId;

    private String name;

    private String url;

    private GeoFileCategoryEnum category;

//    @Schema(description = "文件编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
//    private Long id;
//
//    @Schema(description = "配置编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
//    private Long configId;
//
//    @Schema(description = "原文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao.jpg")
//    private String name;
//
//    @Schema(description = "文件 URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn/yudao.jpg")
//    private String url;
//
//    @Schema(description = "文件MIME类型", example = "application/octet-stream")
//    private String type;
//
//    @Schema(description = "文件大小", example = "2048", requiredMode = Schema.RequiredMode.REQUIRED)
//    private Integer size;
//
//    @Schema(description = "创建时间")
//    @ExcelProperty("创建时间")
//    private LocalDateTime createTime;


}
