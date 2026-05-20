package com.qianyu.module.geo.dal.dataobject.instruction;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.qianyu.framework.mybatis.core.dataobject.BaseDO;

/**
 * 创作指令 DO
 *
 * @author 系统管理员
 */
@TableName("geo_instruction")
@KeySequence("geo_instruction_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoInstructionDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 指令名称
     */
    private String instructionName;
    /**
     * 指令类型
     */
    private Integer instructionType;
    /**
     * 指令内容
     */
    private String content;


}