package org.lazan.t5.atmosphere.services.internal;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.lazan.t5.atmosphere.services.AtmosphereManager;

public class AtmosphereManagerImpl implements AtmosphereManager {
	private final BroadcasterFactory broadcasterFactory;
	
	public AtmosphereManagerImpl(BroadcasterFactory broadcasterFactory) {
		super();
		this.broadcasterFactory = broadcasterFactory;
	}

	protected void register(AtmosphereResource resource, Collection<String> topics) {
		for (String topic : topics) {
			Broadcaster broadcaster = broadcasterFactory.lookup(topic, true);
			broadcaster.addAtmosphereResource(resource);
		}
	}
	
	@Override
	public void initPushTargets(JSONObject data, AtmosphereResource resource) {
		Set<String> topicSet = new LinkedHashSet<String>();
		JSONArray pushTargets = data.getJSONArray("pushTargets");
		JSONArray pageActivationContext = data.getJSONArray("ac");
		String activePageName = data.getString("activePageName");
		String containingPageName = data.getString("containingPageName");
		for (int i = 0; i < pushTargets.length(); ++i) {
			JSONObject pushTarget = pushTargets.getJSONObject(i);
			String nestedComponentId = pushTarget.getString("nestedComponentId");
			String event = pushTarget.getString("event");
			String clientId = pushTarget.getString("id");
			JSONArray topics = pushTarget.getJSONArray("topics");
			
			for (int j = 0; j < topics.length(); ++j) {
				topicSet.add(topics.getString(j));
			}
		}
		register(resource, topicSet);
	}
}
