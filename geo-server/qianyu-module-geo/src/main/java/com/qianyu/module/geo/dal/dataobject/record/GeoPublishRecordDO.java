package com.qianyu.module.geo.dal.dataobject.record;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.qianyu.framework.mybatis.core.dataobject.BaseDO;

/**
 * 发布记录 DO
 *
 * @author 系统管理员
 */
@TableName("geo_publish_record")
@KeySequence("geo_publish_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoPublishRecordDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 任务
     */
    private Long taskId;
    /**
     * 账号
     */
    private Long accountId;
    /**
     * 文章
     */
    private Long articleId;
    /**
     * 发布状态
     */
    private Integer status;
    /**
     * 错误消息
     */
    private String errorMessage;


}