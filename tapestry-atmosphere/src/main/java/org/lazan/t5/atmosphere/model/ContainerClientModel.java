package org.lazan.t5.atmosphere.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry5.EventContext;

public class ContainerClientModel {
	private final Collection<PushTargetClientModel> pushTargets;
	private final EventContext pageActivationContext;
	private final String activePageName;
	private final String containingPageName;
	private final Map<String, List<PushTargetClientModel>> pushTargetsByTopic;
	
	public ContainerClientModel(String activePageName, String containingPageName, EventContext pageActivationContext,
			Collection<PushTargetClientModel> pushTargets) {
		super();
		this.activePageName = activePageName;
		this.containingPageName = containingPageName;
		this.pageActivationContext = pageActivationContext;
		this.pushTargets = pushTargets;
		this.pushTargetsByTopic = new LinkedHashMap<String, List<PushTargetClientModel>>();
		for (PushTargetClientModel pushTarget : pushTargets) {
			for (String topic : pushTarget.getTopics()) {
				List<PushTargetClientModel> list = pushTargetsByTopic.get(topic);
				if (list == null) {
					list = new ArrayList<PushTargetClientModel>();
					pushTargetsByTopic.put(topic,  list);
				}
				list.add(pushTarget);
			}
		}
	}

	public Set<String> getTopics() {
		return pushTargetsByTopic.keySet();
	}

	public Collection<PushTargetClientModel> getPushTargets() {
		return pushTargets;
	}

	public EventContext getPageActivationContext() {
		return pageActivationContext;
	}

	public String getActivePageName() {
		return activePageName;
	}

	public String getContainingPageName() {
		return containingPageName;
	}

	public List<PushTargetClientModel> getPushTargetsForTopic(String topic) {
		List<PushTargetClientModel> matches = pushTargetsByTopic.get(topic);
		if (matches == null) {
			return Collections.emptyList();
		}
		return matches;
	}
}