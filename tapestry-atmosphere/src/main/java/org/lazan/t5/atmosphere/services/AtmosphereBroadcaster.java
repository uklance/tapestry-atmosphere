package org.lazan.t5.atmosphere.services;

public interface AtmosphereBroadcaster {
	void broadcast(String topic, Object message);
}
