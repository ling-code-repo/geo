package com.qianyu.module.geo.socket;

import lombok.Data;

/**
 * 示例：client -> server 发送消息
 *
 * @author qianyu
 */
@Data
public class GeoPingMessage {

    /**
     * 内容
     */
    private Long timestamp;

}
