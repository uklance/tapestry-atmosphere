package org.lazan.t5.atmosphere.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.Registry;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.cpr.Broadcaster;

@AtmosphereHandlerService
public class AtmosphereHandlerImpl implements AtmosphereHandler {
	@Override
	public void destroy() {
	}

	@Override
	public void onRequest(AtmosphereResource resource) throws IOException {
		resource.suspend();

		AtmosphereRequest request = resource.getRequest();
		String topicsCsv = request.getParameter("topics");
		if (topicsCsv != null) {
			topicsCsv = topicsCsv.replaceAll("%2C", ",");
		}
		String[] topics = topicsCsv.split(",");
		Registry registry = getRegistry(resource);
		BroadcasterManager broadcasterManager = registry.getService(BroadcasterManager.class);
		List<Broadcaster> broadcasters = broadcasterManager.getBroadcasters(topics);
		for (Broadcaster broadcaster : broadcasters) {
			broadcaster.addAtmosphereResource(resource);
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
