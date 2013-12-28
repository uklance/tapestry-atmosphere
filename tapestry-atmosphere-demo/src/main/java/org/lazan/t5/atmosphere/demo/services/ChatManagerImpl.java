package org.lazan.t5.atmosphere.demo.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.lazan.t5.atmosphere.demo.model.ChatMessage;
import org.lazan.t5.atmosphere.services.AtmosphereBroadcaster;

public class ChatManagerImpl implements ChatManager {
	private final String ADMINISTRATOR = "administrator";
	private static final List<String> ROOMS = Arrays.asList("cars", "dogs", "tapestry", "java");
	
	private final ConcurrentMap<String, Set<String>> usersByRoom = new ConcurrentHashMap<String, Set<String>>();
	private final AtmosphereBroadcaster broadcaster;
	
	public ChatManagerImpl(AtmosphereBroadcaster broadcaster) {
		super();
		this.broadcaster = broadcaster;
	}

	@Override
	public Collection<String> getRooms() {
		return ROOMS;
	}

	@Override
	public Collection<String> getRoomUsers(String room) {
		Set<String> roomUsers = usersByRoom.get(room);
		return roomUsers == null ? Collections.<String> emptySet() : Collections.unmodifiableSet(roomUsers);
	}

	@Override
	public void joinRoom(String room, String user) {
		Set<String> roomUsers = usersByRoom.get(room);
		if (roomUsers == null) {
			roomUsers = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
			Set<String> existing = usersByRoom.putIfAbsent(room, roomUsers);
			if (existing != null) {
				roomUsers = existing;
			}
		}
		boolean added = roomUsers.add(user);
		if (added) {
			// broadcast an update for the room users
			String topic = String.format("rooms/%s/users", room);
			broadcaster.broadcast(topic, new TreeSet<String>(roomUsers));
			
			// send a message for user joining
			sendRoomMessage(room, ADMINISTRATOR, user + " joined the chat room");
		}
	}

	@Override
	public void leaveRoom(String room, String user) {
		Set<String> roomUsers = usersByRoom.get(room);
		if (roomUsers != null) {
			boolean removed = roomUsers.remove(user);
			
			if (removed) {
				// broadcast an update for the room users
				String topic = String.format("rooms/%s/users", room);
				broadcaster.broadcast(topic, new TreeSet<String>(roomUsers));
				
				// send a message for user joining
				sendRoomMessage(room, ADMINISTRATOR, user + " left the chat room");
			}
		}
	}

	@Override
	public void sendRoomMessage(String room, String user, String message) {
		String topic = String.format("rooms/%s/messages", room);
		ChatMessage chatMessage = new ChatMessage(user, message);
		broadcaster.broadcast(topic, chatMessage);
	}

	@Override
	public void sendPrivateMessage(String fromUser, String toUser, String message) {
		String topic = String.format("users/%s/messages", toUser);
		ChatMessage chatMessage = new ChatMessage(fromUser, message);
		broadcaster.broadcast(topic, chatMessage);
	}
}
