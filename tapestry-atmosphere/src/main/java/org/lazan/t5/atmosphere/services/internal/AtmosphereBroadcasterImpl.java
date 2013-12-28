package org.lazan.t5.atmosphere.services.internal;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.lazan.t5.atmosphere.model.TopicMessage;
import org.lazan.t5.atmosphere.services.AtmosphereBroadcaster;

public class AtmosphereBroadcasterImpl implements AtmosphereBroadcaster {
	private final BroadcasterFactory broadcasterFactory;
	
	public AtmosphereBroadcasterImpl(BroadcasterFactory broadcasterFactory) {
		super();
		this.broadcasterFactory = broadcasterFactory;
	}

	@Override
	public void broadcast(String topic, Object message) {
		Broadcaster broadcaster = broadcasterFactory.lookup(topic);
		if (broadcaster != null) {
			broadcaster.broadcast(new TopicMessage(topic, message));
		}
	}
}
