package org.lazan.t5.atmosphere.demo.services;

import java.util.Collection;
import java.util.List;

import org.lazan.t5.atmosphere.demo.model.ChatMessage;

public interface ChatManager {
	Collection<String> getRooms();
	Collection<String> getRoomUsers(String room);
	void joinRoom(String room, String user);
	void leaveRoom(String room, String user);
	void sendRoomMessage(String room, String user, String message);
	void sendPrivateMessage(String fromUser, String toUser, String message);
	List<ChatMessage> getRecentMessages(String room);
}
