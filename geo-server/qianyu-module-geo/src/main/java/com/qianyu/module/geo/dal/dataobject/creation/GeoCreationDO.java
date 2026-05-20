package com.qianyu.module.geo.dal.dataobject.creation;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.qianyu.framework.mybatis.core.dataobject.BaseDO;

/**
 * AI创作 DO
 *
 * @author 系统管理员
 */
@TableName("geo_creation")
@KeySequence("geo_creation_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoCreationDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 创作名称
     */
    private String name;
    /**
     * 蒸馏词id
     */
    private Long wordId;
    /**
     * 创作文章数量
     */
    private Integer count;
    /**
     * 已创作文章数量
     */
    private Integer successCount;
    /**
     * 知识库id
     */
    private Long knowledgeId;
    /**
     * 图片id
     */
    private String pictureIds;
    /**
     * 配图数量
     */
    private Integer pictureCount;
    /**
     * 创作状态
     *
     * 枚举 {@link TODO geo_creation_status 对应的类}
     */
    private Integer status;
    /**
     * 错误消息
     */
    private String errorMessage;
    /**
     * 内容指令id
     */
    private Long contentId;
    /**
     * 标题指令id
     */
    private Long titleId;
    /**
     * 创作时间
     */
    private LocalDateTime creationTime;


}
