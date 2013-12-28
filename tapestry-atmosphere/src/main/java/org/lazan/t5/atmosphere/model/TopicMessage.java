package org.lazan.t5.atmosphere.model;

public interface TopicMessage {
	String getTopic();
	Object[] getEventContext();
}
