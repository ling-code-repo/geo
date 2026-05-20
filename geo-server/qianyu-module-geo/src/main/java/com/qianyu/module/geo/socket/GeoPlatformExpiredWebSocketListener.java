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
public class GeoPlatformExpiredWebSocketListener implements WebSocketMessageListener<GeoPlatformExpiredMessage> {

    public static final String TYPE = "geo-platform-expire";

    @Override
    public void onMessage(WebSocketSession session, GeoPlatformExpiredMessage message) {
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
