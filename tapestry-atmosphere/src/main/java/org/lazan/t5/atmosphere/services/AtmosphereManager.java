package org.lazan.t5.atmosphere.services;

import org.atmosphere.cpr.AtmosphereResource;

public interface AtmosphereManager {
	void register(AtmosphereResource resource, String[] topics);
}
