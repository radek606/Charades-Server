package com.ick.kalambury.service;

import com.ick.kalambury.messages.GameDataMessage;
import org.springframework.web.socket.CloseStatus;

import java.util.List;

public interface TableActionsCallbacks {

    void sendMessage(GameDataMessage message, List<String> userIds);
    void endPlayerSession(String userId, CloseStatus status);
    void clearTable(String tableId);

}
