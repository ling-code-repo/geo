package com.qianyu.module.geo.socket;

import lombok.Data;

/**
 * 示例：client -> server 发送消息
 *
 * @author qianyu
 */
@Data
public class GeoPlatformExpiredMessage {

    /**
     * 内容
     */
    private String platform;
    private String id;

}
