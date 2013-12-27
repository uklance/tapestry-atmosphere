package org.lazan.t5.atmosphere.services.internal;

import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.json.JSONObject;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.atmosphere.cpr.AtmosphereResponse;
import org.lazan.t5.atmosphere.services.AtmosphereManager;

@AtmosphereHandlerService
public class AtmosphereHandlerImpl implements AtmosphereHandler {
	private Registry registry;
	
	@Override
	public void destroy() {
	}

	@Override
	public void onRequest(AtmosphereResource resource) throws IOException {
		AtmosphereRequest request = resource.getRequest();
		String method = request.getMethod();
		if ("GET".equals(method)) {
			// suspend the connect request
			resource.suspend();
			if (registry == null) {
				registry = getRegistry(resource);
			}
		} else if ("POST".equals(method)) {
			String jsonData = request.getReader().readLine().trim();
			JSONObject data = new JSONObject(jsonData);

			// lookup the connect request
			String uuid = (String) request.getAttribute(ApplicationConfig.SUSPENDED_ATMOSPHERE_RESOURCE_UUID);
			AtmosphereResource suspendedResource = AtmosphereResourceFactory.getDefault().find(uuid);
			
			AtmosphereManager atmosphereManager = registry.getService(AtmosphereManager.class);
			atmosphereManager.initPushTargets(data, suspendedResource);
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
			System.out.println("NOT RESUMING");
		}
	}

	protected Registry getRegistry(AtmosphereResource resource) {
		ServletContext servletContext = resource.getRequest().getServletContext();
		Registry registry = (Registry) servletContext.getAttribute(TapestryFilter.REGISTRY_CONTEXT_NAME);
		if (registry == null) {
			throw new IllegalStateException(
					"Tapestry registry does not exist. Try configuring the atmosphere servlet to have a load-on-startup value greater than tapestry's in web.xml");
		}
		return registry;
	}
}
