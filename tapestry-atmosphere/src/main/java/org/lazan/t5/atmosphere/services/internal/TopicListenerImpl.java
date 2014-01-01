package org.lazan.t5.atmosphere.services.internal;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.UsesOrderedConfiguration;
import org.atmosphere.cpr.AtmosphereResource;
import org.lazan.t5.atmosphere.services.TopicListener;

@UsesOrderedConfiguration(TopicListener.class)
public class TopicListenerImpl implements TopicListener {
	private final List<TopicListener> listeners;
	
	public TopicListenerImpl(List<TopicListener> listeners) {
		super();
		this.listeners = listeners;
	}

	@Override
	public void onConnect(AtmosphereResource resource, String topic) {
		for (TopicListener listener : listeners) {
			listener.onConnect(resource, topic);
		}
	} 
	
	@Override
	public void onDisconnect(AtmosphereResource resource, String topic) {
		for (TopicListener listener : listeners) {
			listener.onDisconnect(resource, topic);
		}
	}
}
