package org.lazan.t5.atmosphere.model;

public class TopicMessage {
	private final String topic;
	private final Object message;
	
	public TopicMessage(String topic, Object message) {
		super();
		this.topic = topic;
		this.message = message;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public Object getMessage() {
		return message;
	}
}
