package org.lazan.t5.atmosphere.demo.pages;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionAttribute;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.lazan.t5.atmosphere.demo.model.ChatMessage;
import org.lazan.t5.atmosphere.demo.services.ChatManager;

public class ChatDemo {
	@Property
	@PageActivationContext
	private String room;

	@SessionAttribute("CHAT_USER")
	@Property
	private String user;
	
	@Property
	private String currentUser;
	
	@Property
	private String currentRoom;
	
	@Property
	private ChatMessage chatMessage;
	
	@Property
	private Set<String> updatedUsers;
	
	@Property
	private String outMessage;
	
	@Inject
	private ChatManager chatManager;

	@Inject
	private Block messageBlock;

	@Inject
	private Block usersBlock;
	
	@InjectComponent
	private Zone chatFormZone;
	
	Object onSuccessFromLoginForm() {
		return this;
	}
	
	Object onChangeRoom(String newRoom) {
		String oldRoom = this.room;
		chatManager.leaveRoom(oldRoom, user);
		this.room = newRoom;
		return this;
	}
	
	Object onSuccessFromChatForm() {
		chatManager.sendRoomMessage(room, user, outMessage);
		return chatFormZone.getBody();
	}
	
	Object onLogout() {
		chatManager.leaveRoom(room, user);
		user = null;
		room = null;
		return this;
	}
	
	void setupRender() {
		if (room != null && user != null) {
			chatManager.joinRoom(room, user);
		}
	}
	
	public boolean isLoggedIn() {
		return user != null && room != null;
	}
	
	public Block onUsersChange(Set<String> users) {
		updatedUsers = users;
		return usersBlock;
	}
	
	public Block onChatMessage(ChatMessage chatMessage) {
		this.chatMessage = chatMessage;
		return messageBlock;
	}
	
	public Collection<String> getRooms() {
		return chatManager.getRooms();
	}
	
	public Collection<String> getRoomUsers() {
		return chatManager.getRoomUsers(room);
	}
	
	public boolean isOtherRoom() {
		return !currentRoom.equals(room);
	}
	
	public Collection<String> getMessageTopics() {
		return Arrays.asList(
			String.format("rooms/%s/messages", room),
			String.format("users/%s/messages", user)
		);
	}
	
	public List<ChatMessage> getRecentMessages() {
		return chatManager.getRecentMessages(room);
	}
}
