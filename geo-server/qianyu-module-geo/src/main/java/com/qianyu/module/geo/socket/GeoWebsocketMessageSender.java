package com.qianyu.module.geo.socket;

import com.qianyu.framework.common.enums.UserTypeEnum;
import com.qianyu.framework.websocket.core.sender.WebSocketMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeoWebsocketMessageSender {

    @Autowired(required = false) // 由于 yudao.websocket.enable 配置项，可以关闭 WebSocket 的功能，所以这里只能不强制注入
    private WebSocketMessageSender webSocketMessageSender;

    public void sendObject(Long userId, String messageType, Object messageContent) {
        webSocketMessageSender.sendObject(UserTypeEnum.ADMIN.getValue(), userId, messageType, messageContent);
    }
}
