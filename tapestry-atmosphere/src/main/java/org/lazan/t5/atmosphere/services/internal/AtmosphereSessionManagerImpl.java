package org.lazan.t5.atmosphere.services.internal;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.atmosphere.cpr.AtmosphereResource;
import org.lazan.t5.atmosphere.services.AtmosphereSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmosphereSessionManagerImpl implements AtmosphereSessionManager {
	private static final Logger logger = LoggerFactory.getLogger(AtmosphereSessionManagerImpl.class);
	private final ConcurrentMap<String, ConcurrentMap<String, Object>> sessionsByUuid = new ConcurrentHashMap<String, ConcurrentMap<String,Object>>();
	
	@Override
	public void createSession(AtmosphereResource resource) {
		logger.debug("Create session ({})", resource.uuid());
		ConcurrentMap<String, Object> session = new ConcurrentHashMap<String, Object>();
		ConcurrentMap<String, Object> prevSession = sessionsByUuid.putIfAbsent(resource.uuid(), session);
		if (prevSession != null) {
			logger.warn("Session already exists ({})", resource.uuid());
		}
	}
	
	@Override
	public void destroySession(AtmosphereResource resource) {
		logger.debug("Destroy session ({})", resource.uuid());
		ConcurrentMap<String, Object> prevSession = sessionsByUuid.remove(resource.uuid());
		if (prevSession == null) {
			logger.warn("Session could not be destroyed ({})", resource.uuid());
		}		
	}
	
	@Override
	public Object getAttribute(AtmosphereResource resource, String key) {
		ConcurrentMap<String, Object> session = getSession(resource);
		return session.get(key);
	}
	
	@Override
	public <T> T getAttribute(AtmosphereResource resource, String key, Class<T> type) {
		return type.cast(getAttribute(resource, key));
	}
	
	@Override
	public Collection<String> getAttributeNames(AtmosphereResource resource) {
		return getSession(resource).keySet();
	}
	
	@Override
	public boolean isSessionInitialized(AtmosphereResource resource) {
		ConcurrentMap<String, Object> session = sessionsByUuid.get(resource.uuid());
		return session != null;
	}
	
	@Override
	public void setAttribute(AtmosphereResource resource, String key, Object value) {
		getSession(resource).put(key, value);
	}

	protected ConcurrentMap<String, Object> getSession(AtmosphereResource resource) {
		ConcurrentMap<String, Object> session = sessionsByUuid.get(resource.uuid());
		if (session == null) {
			throw new IllegalStateException("Session does not exist for uuid " + resource.uuid());
		}
		return session;
	}
}
