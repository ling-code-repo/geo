package com.qianyu.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文档地址
 *
 * @author qianyu
 */
@Getter
@AllArgsConstructor
public enum DocumentEnum {

    REDIS_INSTALL("https://gitee.com/zhijiantianya/ruoyi-vue-pro/issues/I4VCSJ", "Redis 安装文档"),
    TENANT("127.0.0.1", "SaaS 多租户文档");

    private final String url;
    private final String memo;

}
