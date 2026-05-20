package com.qianyu.module.geo.dal.dataobject.question;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.qianyu.framework.mybatis.core.dataobject.BaseDO;

/**
 * 扩展问题 DO
 *
 * @author 系统管理员
 */
@TableName("geo_question")
@KeySequence("geo_question_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoQuestionDO extends BaseDO {

    /**
     * 问题id
     */
    @TableId
    private Long id;
    /**
     * 主词id
     */
    private Long wordId;
    /**
     * 问题
     */
    private String question;
    /**
     * 收录状态
     *
     * 枚举 {@link TODO geo_include_status 对应的类}
     */
    private Integer status;


}