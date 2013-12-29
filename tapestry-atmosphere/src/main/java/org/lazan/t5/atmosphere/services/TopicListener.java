package org.lazan.t5.atmosphere.services;

import org.atmosphere.cpr.AtmosphereResource;

public interface TopicListener {
	void onConnect(AtmosphereResource resource, String topic);
	void onDisconnect(AtmosphereResource resource, String topic);
}
