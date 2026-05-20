package com.qianyu.module.geo.socket;

import com.qianyu.framework.common.enums.UserTypeEnum;
import com.qianyu.framework.websocket.core.listener.WebSocketMessageListener;
import com.qianyu.framework.websocket.core.sender.WebSocketMessageSender;
import com.qianyu.framework.websocket.core.util.WebSocketFrameworkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocket 示例：单发消息
 *
 * @author qianyu
 */
@Component
public class GeoWebSocketMessageListener implements WebSocketMessageListener<GeoMessage> {

    public static final String TYPE = "geo-publish-message";

    @Override
    public void onMessage(WebSocketSession session, GeoMessage message) {
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
