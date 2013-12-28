package org.lazan.t5.atmosphere.services.internal;

import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.lazan.t5.atmosphere.services.AtmosphereSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmosphereResourceEventListenerImpl implements AtmosphereResourceEventListener {
	private static final Logger logger = LoggerFactory.getLogger(AtmosphereResourceEventListenerImpl.class);
	
	private final AtmosphereSessionManager sessionManager;
	
	public AtmosphereResourceEventListenerImpl(AtmosphereSessionManager sessionManager) {
		super();
		this.sessionManager = sessionManager;
	}

	@Override
	public void onSuspend(AtmosphereResourceEvent event) {
		logger.debug("onSuspend(%s)", event.getResource().uuid());
		sessionManager.createSession(event.getResource());
	}

	@Override
	public void onDisconnect(AtmosphereResourceEvent event) {
		logger.debug("debug(%s)", event.getResource().uuid());
		sessionManager.destroySession(event.getResource());
	}

	@Override
	public void onClose(AtmosphereResourceEvent event) {
		logger.debug("onClose(%s)", event.getResource().uuid());
		sessionManager.destroySession(event.getResource());
	}

	@Override
	public void onResume(AtmosphereResourceEvent event) {}

	@Override
	public void onBroadcast(AtmosphereResourceEvent event) {}

	@Override
	public void onThrowable(AtmosphereResourceEvent event) {}

	@Override
	public void onPreSuspend(AtmosphereResourceEvent event) {}
}
