package org.lazan.t5.atmosphere.services.internal;

import java.io.IOException;

import org.apache.tapestry5.json.JSONObject;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.atmosphere.cpr.AtmosphereResponse;
import org.lazan.t5.atmosphere.services.AtmosphereManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmosphereHandlerImpl implements AtmosphereHandler {
	private static final Logger logger = LoggerFactory.getLogger(AtmosphereHandlerImpl.class);
	private final AtmosphereManager manager;
	private final AtmosphereResourceFactory resourceFactory;
	private final AtmosphereResourceEventListener resourceEventListener;
	
	public AtmosphereHandlerImpl(AtmosphereManager manager, AtmosphereResourceFactory resourceFactory,
			AtmosphereResourceEventListener resourceEventListener) {
		super();
		this.manager = manager;
		this.resourceFactory = resourceFactory;
		this.resourceEventListener = resourceEventListener;
	}

	@Override
	public void onRequest(AtmosphereResource resource) throws IOException {
		AtmosphereRequest request = resource.getRequest();
		String method = request.getMethod();
		if ("GET".equals(method)) {
			resource.addEventListener(resourceEventListener);

			// suspend the connect request
			resource.suspend();
		} else if ("POST".equals(method)) {
			String jsonData = request.getReader().readLine().trim();
			JSONObject data = new JSONObject(jsonData);

			// lookup the connect request
			String uuid = (String) request.getAttribute(ApplicationConfig.SUSPENDED_ATMOSPHERE_RESOURCE_UUID);
			AtmosphereResource suspendedResource = resourceFactory.find(uuid);
			
			manager.initContainerClientModel(suspendedResource, data);
		}
	}

	@Override
	public void onStateChange(AtmosphereResourceEvent event) throws IOException {
		AtmosphereResource resource = event.getResource();
		AtmosphereResponse response = resource.getResponse();
		if (resource.isSuspended()) {
			String message = event.getMessage().toString();
			response.getWriter().write(message);
			switch (resource.transport()) {
			case JSONP:
			case LONG_POLLING:
				event.getResource().resume();
				break;
			case WEBSOCKET:
			case STREAMING:
				response.getWriter().flush();
			}
		} else if (!event.isResuming()) {
			logger.debug("Not resuming {}", event.getResource().uuid());
		}
	}
	
	@Override
	public void destroy() {
	}
}
