package org.lazan.t5.atmosphere.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.tapestry5.EventContext;

public class ContainerClientModel {
	private final Collection<PushTargetClientModel> pushTargets;
	private final EventContext pageActivationContext;
	private final String activePageName;
	private final String containingPageName;
	
	public ContainerClientModel(String activePageName, String containingPageName, EventContext pageActivationContext,
			Collection<PushTargetClientModel> pushTargets) {
		super();
		this.activePageName = activePageName;
		this.containingPageName = containingPageName;
		this.pageActivationContext = pageActivationContext;
		this.pushTargets = pushTargets;
	}

	public Set<String> getTopics() {
		Set<String> topics = new LinkedHashSet<String>();
		for (PushTargetClientModel pushTarget : pushTargets) {
			topics.addAll(pushTarget.getTopics());
		}
		return topics;
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

	/**
	 * TODO: hash lookup
	 */
	public Collection<PushTargetClientModel> getPushTargetsForTopic(String topic) {
		Collection<PushTargetClientModel> matches = new ArrayList<PushTargetClientModel>();
		for (PushTargetClientModel pushTarget : pushTargets) {
			if (pushTarget.getTopics().contains(topic)) {
				matches.add(pushTarget);
			}
		}
		return matches;
	}
}