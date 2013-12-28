package org.lazan.t5.atmosphere.services.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.internal.services.ArrayEventContext;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.PerRequestBroadcastFilter;
import org.lazan.t5.atmosphere.model.ContainerClientModel;
import org.lazan.t5.atmosphere.model.PushTargetClientModel;
import org.lazan.t5.atmosphere.model.TopicMessage;
import org.lazan.t5.atmosphere.services.AtmosphereManager;
import org.lazan.t5.offline.DefaultOfflineRequestContext;
import org.lazan.t5.offline.OfflineRequestContext;
import org.lazan.t5.offline.services.OfflineComponentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerRequestBroadcastFilterImpl implements PerRequestBroadcastFilter {
	private static final EventContext EMPTY_EVENT_CONTEXT = new EmptyEventContext();
	private static final Logger logger = LoggerFactory.getLogger(PerRequestBroadcastFilterImpl.class);

	private final AtmosphereManager atmosphereManager;
	private final OfflineComponentRenderer offlineComponentRenderer;
	private final TypeCoercer typeCoercer;
	
	public PerRequestBroadcastFilterImpl(AtmosphereManager atmosphereManager, OfflineComponentRenderer offlineComponentRenderer,
			TypeCoercer typeCoercer) {
		super();
		this.atmosphereManager = atmosphereManager;
		this.offlineComponentRenderer = offlineComponentRenderer;
		this.typeCoercer = typeCoercer;
	}

	@Override
	public BroadcastAction filter(AtmosphereResource resource, Object originalMessage, Object message) {
		TopicMessage tMessage = (TopicMessage) message;
		
		ContainerClientModel containerModel = atmosphereManager.getContainerClientModel(resource);
		EventContext eventContext = createEventContext(tMessage.getEventContext());
		Collection<PushTargetClientModel> pushTargets = containerModel.getPushTargetsForTopic(tMessage.getTopic());
		
		JSONObject allResults = new JSONObject();
		OfflineRequestContext requestContext = createOfflineRequestContext(resource);
		for (PushTargetClientModel pushTarget : pushTargets) {
			ComponentEventRequestParameters params = createComponentEventRequestParams(containerModel, pushTarget, eventContext);

			try {
				JSONObject eventResult = offlineComponentRenderer.renderComponent(requestContext, params);
				allResults.put(pushTarget.getClientId(), eventResult);
			} catch (Exception e) {
				logger.error("Error rendering " + params, e);
			}
		}
		
		return new BroadcastAction(allResults.toCompactString());
	}

	protected ComponentEventRequestParameters createComponentEventRequestParams(
			ContainerClientModel containerModel,
			PushTargetClientModel pushTarget, 
			EventContext eventContext) 
	{
		return new ComponentEventRequestParameters(
			containerModel.getActivePageName(),
			containerModel.getContainingPageName(), 
			pushTarget.getNestedComponentId(), 
			pushTarget.getEvent(),
			containerModel.getPageActivationContext(), 
			eventContext
		);
	}
	
	@SuppressWarnings("unchecked")
	protected OfflineRequestContext createOfflineRequestContext(AtmosphereResource resource) {
		HttpSession session = resource.getRequest().getSession(false);
		DefaultOfflineRequestContext requestContext = new DefaultOfflineRequestContext();
		requestContext.setXHR(true);
		if (session != null) {
			Map<String, Object> sessionMap = new HashMap<String, Object>();
			Enumeration<String> attNames = session.getAttributeNames();
			while (attNames.hasMoreElements()) {
				String attName = attNames.nextElement();
				sessionMap.put(attName, session.getAttribute(attName));
			}
			requestContext.setSession(sessionMap);
		}
		return requestContext;
	}
	
	@Override
	public BroadcastAction filter(Object originalMessage, Object message) {
		return new BroadcastAction(message);
	}
	
	protected EventContext createEventContext(Object[] array) {
		if (array == null || array.length == 0) {
			return EMPTY_EVENT_CONTEXT;
		}
		return new ArrayEventContext(typeCoercer, array);
	}	
}
