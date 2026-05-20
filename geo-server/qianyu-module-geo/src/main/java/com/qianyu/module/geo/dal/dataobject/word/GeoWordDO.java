package com.qianyu.module.geo.dal.dataobject.word;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.qianyu.framework.mybatis.core.dataobject.BaseDO;

/**
 * 蒸馏词 DO
 *
 * @author 系统管理员
 */
@TableName("geo_word")
@KeySequence("geo_word_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoWordDO extends BaseDO {

    /**
     * 蒸馏词id
     */
    @TableId
    private Long id;
    /**
     * 主词
     */
    private String word;
    /**
     * 转化目标
     */
    private String target;
    /**
     * 是否优化
     *
     * 枚举 {@link TODO geo_optimize 对应的类}
     */
    private Integer optimized;
    /**
     * 是否查询拓展词
     *
     * 枚举 {@link TODO geo_expand 对应的类}
     */
    private Integer expand;

    private Integer questionCount;


}
