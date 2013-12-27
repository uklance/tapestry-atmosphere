package org.lazan.t5.atmosphere.services.internal;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.lazan.t5.atmosphere.services.AtmosphereManager;

public class AtmosphereManagerImpl implements AtmosphereManager {
	private final BroadcasterFactory broadcasterFactory;
	
	public AtmosphereManagerImpl(BroadcasterFactory broadcasterFactory) {
		super();
		this.broadcasterFactory = broadcasterFactory;
	}

	@Override
	public void register(AtmosphereResource resource, String[] topics) {
		for (String topic : topics) {
			Broadcaster broadcaster = broadcasterFactory.lookup(topic, true);
			broadcaster.addAtmosphereResource(resource);
		}
	}
}
