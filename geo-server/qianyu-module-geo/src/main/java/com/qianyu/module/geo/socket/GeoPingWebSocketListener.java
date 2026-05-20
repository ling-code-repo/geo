package com.qianyu.module.geo.socket;

import com.qianyu.framework.common.util.json.JsonUtils;
import com.qianyu.framework.websocket.core.listener.WebSocketMessageListener;
import com.qianyu.framework.websocket.core.sender.WebSocketMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocket 示例：单发消息
 *
 * @author qianyu
 */
@Slf4j
@Component
public class GeoPingWebSocketListener implements WebSocketMessageListener<GeoPingMessage> {

    public static final String TYPE = "geo-ping";

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false) // 由于 yudao.websocket.enable 配置项，可以关闭 WebSocket 的功能，所以这里只能不强制注入
    private WebSocketMessageSender webSocketMessageSender;

    @Override
    public void onMessage(WebSocketSession session, GeoPingMessage message) {
        log.debug("[onMessage][session({}) message({})]", session.getId(), JsonUtils.toJsonString(message));
       /* Long fromUserId = WebSocketFrameworkUtils.getLoginUserId(session);
        // 情况一：单发
        if (message.getToUserId() != null) {
            DemoReceiveMessage toMessage = new DemoReceiveMessage().setFromUserId(fromUserId)
                    .setText(message.getText()).setSingle(true);
            webSocketMessageSender.sendObject(UserTypeEnum.ADMIN.getValue(), message.getToUserId(), // 给指定用户
                    "demo-message-receive", toMessage);
            return;
        }*/

    }

    @Override
    public String getType() {
        return TYPE;
    }

}
