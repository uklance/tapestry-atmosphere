package org.lazan.t5.atmosphere.services.internal;

import org.atmosphere.cpr.AtmosphereResource;
import org.lazan.t5.atmosphere.services.TopicAuthorizer;

public class NoopTopicAuthorizer implements TopicAuthorizer {
	@Override
	public boolean isAuthorized(AtmosphereResource resource, String topic) {
		return true;
	}
}
