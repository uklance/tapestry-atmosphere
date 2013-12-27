package org.lazan.t5.atmosphere.model;

import java.util.List;

public class PushTargetModel {
	private final List<String> topics;
	private final String event;
	private final UpdateStrategy updateStrategy;
	private final String clientId;
	private final String nestedId;

	public PushTargetModel(List<String> topics, String event, UpdateStrategy updateStrategy, String clientId, String nestedId) {
		super();
		this.topics = topics;
		this.event = event;
		this.updateStrategy = updateStrategy;
		this.clientId = clientId;
		this.nestedId = nestedId;
	}

	public List<String> getTopics() {
		return topics;
	}

	public String getEvent() {
		return event;
	}

	public UpdateStrategy getUpdateStrategy() {
		return updateStrategy;
	}

	public String getClientId() {
		return clientId;
	}

	public String getNestedId() {
		return nestedId;
	}
}
