package com.qianyu.module.geo.dal.dataobject.article;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.qianyu.framework.mybatis.core.dataobject.BaseDO;

/**
 * 创作文章 DO
 *
 * @author 系统管理员
 */
@TableName("geo_article")
@KeySequence("geo_article_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoArticleDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 创作id
     */
    private Long creationId;

    private String pictureIds;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 文章内容
     */
    private String contentMarkdown;
    /**
     * 发布次数
     */
    private Integer publishCount;
    /**
     * 来源：0AI 1人工 2复刻
     *
     * 枚举 {@link TODO geo_source_type 对应的类}
     */
    private String sourceType;
    /**
     * 0未发布 1已发布
     *
     * 枚举 {@link TODO geo_article_status 对应的类}
     */
    private Integer status;


}
