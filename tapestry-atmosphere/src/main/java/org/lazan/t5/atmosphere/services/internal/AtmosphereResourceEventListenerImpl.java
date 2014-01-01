package org.lazan.t5.atmosphere.services.internal;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.lazan.t5.atmosphere.model.ContainerClientModel;
import org.lazan.t5.atmosphere.services.AtmosphereManager;
import org.lazan.t5.atmosphere.services.AtmosphereSessionManager;
import org.lazan.t5.atmosphere.services.TopicListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmosphereResourceEventListenerImpl extends AtmosphereResourceEventListenerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(AtmosphereResourceEventListenerImpl.class);
	
	private final AtmosphereSessionManager sessionManager;
	private final TopicListener topicListener;
	private final AtmosphereManager atmosphereManager;
	
	public AtmosphereResourceEventListenerImpl(AtmosphereSessionManager sessionManager, TopicListener topicListener,
			AtmosphereManager atmosphereManager) {
		super();
		this.sessionManager = sessionManager;
		this.topicListener = topicListener;
		this.atmosphereManager = atmosphereManager;
	}

	@Override
	public void onDisconnect(AtmosphereResourceEvent event) {
		AtmosphereResource resource = event.getResource();
		logger.debug("onDisconnect({})", resource.uuid());
		
		try {
			ContainerClientModel containerModel = atmosphereManager.getContainerClientModel(resource);
			if (containerModel != null) {
				for (String topic : containerModel.getTopics()) {
					try {
						topicListener.onDisconnect(resource, topic);
					} catch (Exception e) {
						logger.error("Error with topic listener", e);
					}
				}	
			}
		} finally {
			sessionManager.destroySession(event.getResource());
		}
	}
}
