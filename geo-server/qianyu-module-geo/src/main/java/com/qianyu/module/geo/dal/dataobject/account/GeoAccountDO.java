package com.qianyu.module.geo.dal.dataobject.account;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.qianyu.framework.mybatis.core.dataobject.BaseDO;

/**
 * 授权账号 DO
 *
 * @author 系统管理员
 */
@TableName("geo_account")
@KeySequence("geo_account_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoAccountDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 账号名称
     */
    private String name;
    /**
     * 头像url
     */
    private String path;
    /**
     * 平台
     */
    private Long platform;
    /**
     * 发布状态 0不发布 1需要发布
     *
     * 枚举 {@link TODO geo_publish_status 对应的类}
     */
    private Integer publishStatus;
    /**
     * 每日发布次数
     */
    private Integer publishCount;
    /**
     * 成功发布次数
     */
    private Integer publishSuccess;
    /**
     * ip:port
     */
    private String proxy;
    /**
     * username:password
     */
    private String account;
    /**
     * 授权状态 0未授权 1已授权
     *
     * 枚举 {@link TODO geo_authorize_status 对应的类}
     */
    private Integer authorizeStatus;


}
