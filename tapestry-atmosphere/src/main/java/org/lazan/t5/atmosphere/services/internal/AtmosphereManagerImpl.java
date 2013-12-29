package org.lazan.t5.atmosphere.services.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.internal.services.ArrayEventContext;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.lazan.t5.atmosphere.model.ContainerClientModel;
import org.lazan.t5.atmosphere.model.PushTargetClientModel;
import org.lazan.t5.atmosphere.services.AtmosphereManager;
import org.lazan.t5.atmosphere.services.AtmosphereSessionManager;
import org.lazan.t5.atmosphere.services.TopicAuthorizer;
import org.lazan.t5.atmosphere.services.TopicListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmosphereManagerImpl implements AtmosphereManager {
	private static final Logger logger = LoggerFactory.getLogger(AtmosphereManagerImpl.class);
	private static final EventContext EMPTY_EVENT_CONTEXT = new EmptyEventContext();
	private static final String ATTRIBUTE_CONTAINER_CLIENT_MODEL = AtmosphereManager.class.getName() + ".ContainerClientModel";
	
	private final BroadcasterFactory broadcasterFactory;
	private final TypeCoercer typeCoercer;
	private final AtmosphereSessionManager sessionManager;
	private final TopicAuthorizer topicAuthorizer;
	private final TopicListener topicListener;
	
	public AtmosphereManagerImpl(BroadcasterFactory broadcasterFactory, TypeCoercer typeCoercer,
			AtmosphereSessionManager sessionManager, TopicAuthorizer topicAuthorizer, TopicListener topicListener) {
		super();
		this.broadcasterFactory = broadcasterFactory;
		this.typeCoercer = typeCoercer;
		this.sessionManager = sessionManager;
		this.topicAuthorizer = topicAuthorizer;
		this.topicListener = topicListener;
	}

	@Override
	public ContainerClientModel initContainerClientModel(AtmosphereResource resource, JSONObject data) {
		ContainerClientModel containerModel = createContainerClientModel(data);
		sessionManager.setAttribute(resource, ATTRIBUTE_CONTAINER_CLIENT_MODEL, containerModel);
		register(resource, containerModel.getTopics());
		return containerModel;
	}
	
	@Override
	public ContainerClientModel getContainerClientModel(AtmosphereResource resource) {
		return sessionManager.getAttribute(resource, ATTRIBUTE_CONTAINER_CLIENT_MODEL, ContainerClientModel.class);
	}
	
	protected void register(AtmosphereResource resource, Collection<String> topics) {
		for (String topic : topics) {
			if (topicAuthorizer.isAuthorized(resource, topic)) {
				Broadcaster broadcaster = broadcasterFactory.lookup(topic, true);
				broadcaster.addAtmosphereResource(resource);
				
				topicListener.onConnect(resource, topic);
			} else {
				logger.error("Unauthorized topic {} for uuid {}", topic, resource.uuid());
			}
		}
	}

	protected ContainerClientModel createContainerClientModel(JSONObject data) {
		JSONArray pushTargets = data.getJSONArray("pushTargets");
		JSONArray ac = data.getJSONArray("ac");
		List<PushTargetClientModel> pushTargetModels = new ArrayList<PushTargetClientModel>();
		for (int i = 0; i < pushTargets.length(); ++i) {
			JSONObject pushTarget = pushTargets.getJSONObject(i);
			pushTargetModels.add(createPushTargetClientModel(pushTarget));
		}
		String activePageName = data.getString("activePageName");
		String containingPageName = data.getString("containingPageName");
		EventContext pageActivationContext = createEventContext(ac);

		ContainerClientModel containerModel = new ContainerClientModel(activePageName, containingPageName, pageActivationContext, pushTargetModels);
		return containerModel;
	}

	protected PushTargetClientModel createPushTargetClientModel(JSONObject pushTarget) {
		JSONArray jsonTopics = pushTarget.getJSONArray("topics");
		Set<String> topics = new LinkedHashSet<String>();
		for (int j = 0; j < jsonTopics.length(); ++j) {
			String topic = jsonTopics.getString(j);
			topics.add(topic);
		}

		String nestedComponentId = pushTarget.getString("nestedComponentId");
		String event = pushTarget.getString("event");
		String clientId = pushTarget.getString("id");
		PushTargetClientModel pushTargetModel = new PushTargetClientModel(nestedComponentId, clientId, event, topics);
		return pushTargetModel;
	}
	
	protected EventContext createEventContext(JSONArray jsonArray) {
		if (jsonArray == null || jsonArray.length() == 0) {
			return EMPTY_EVENT_CONTEXT;
		}
		Object[] array = new String[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); ++i) {
			array[i] = jsonArray.getString(i);
		}
		return new ArrayEventContext(typeCoercer, array);
	}
}
