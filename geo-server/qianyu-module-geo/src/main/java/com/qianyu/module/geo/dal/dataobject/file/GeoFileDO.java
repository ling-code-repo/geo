package com.qianyu.module.geo.dal.dataobject.file;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianyu.framework.mybatis.core.dataobject.BaseDO;
import com.qianyu.module.geo.enums.GeoFileCategoryEnum;
import lombok.*;

/**
 * 企业图库 DO
 *
 * @author 系统管理员
 */
@TableName("geo_file")
@KeySequence("geo_file_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoFileDO extends BaseDO {

    /**
     * 文件编号
     */
    @TableId
    private Long id;
    /**
     * 类别
     */
    private GeoFileCategoryEnum category;
    /**
     * 文件编号-infra_file表
     */
    private Long fileId;




}
