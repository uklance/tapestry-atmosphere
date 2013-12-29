package org.lazan.t5.atmosphere.services;

import org.atmosphere.cpr.AtmosphereResource;

public interface TopicAuthorizer {
	boolean isAuthorized(AtmosphereResource resource, String topic);
}
