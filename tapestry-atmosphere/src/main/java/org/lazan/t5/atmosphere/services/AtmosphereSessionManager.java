package org.lazan.t5.atmosphere.services;

import java.util.Collection;

import org.atmosphere.cpr.AtmosphereResource;

public interface AtmosphereSessionManager {
	Collection<String> getAttributeNames(AtmosphereResource resource);
	void setAttribute(AtmosphereResource resource, String key, Object value);
	Object getAttribute(AtmosphereResource resource, String key);
	<T> T getAttribute(AtmosphereResource resource, String key, Class<T> type);
	void createSession(AtmosphereResource resource);
	void destroySession(AtmosphereResource resource);
	boolean isSessionInitialized(AtmosphereResource resource);
}
