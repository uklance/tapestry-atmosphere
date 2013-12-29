package org.lazan.t5.atmosphere.services.internal;

import org.atmosphere.cpr.AtmosphereResource;
import org.lazan.t5.atmosphere.services.TopicListener;

public class NoopTopicListener implements TopicListener {
	@Override
	public void onConnect(AtmosphereResource resource, String topic) {}

	@Override
	public void onDisconnect(AtmosphereResource resource, String topic) { }
}
