package com.qianyu.module.geo.dal.dataobject.publish;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.qianyu.framework.mybatis.core.dataobject.BaseDO;

/**
 * 发布任务 DO
 *
 * @author 系统管理员
 */
@TableName("geo_publish_task")
@KeySequence("geo_publish_task_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoPublishTaskDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 任务名
     */
    private String name;

    private Integer headless;
    /**
     * 发布状态
     */
    private Integer status;

    private Integer taskStatus;
    /**
     * 错误消息
     */
    private String errorMessage;
    /**
     * 执行时间
     */
    private LocalDateTime executeTime;
    /**
     * 声明AI创作
     */
    private Integer declareAi;

    /**
     * 成功发布次数
     */
    private Integer successCount;
    /**
     * 文章id
     */
    private Long articleId;
    /**
     * 平台
     */
    private String platforms;


}
