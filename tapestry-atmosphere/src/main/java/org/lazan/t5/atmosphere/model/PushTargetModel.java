package org.lazan.t5.atmosphere.model;

import java.util.List;

public class PushTargetModel {
	private final String clientId;
	private final UpdateStrategy updateStrategy;
	private final List<String> topics;
	
	public PushTargetModel(String clientId, UpdateStrategy updateStrategy, List<String> topics) {
		super();
		this.clientId = clientId;
		this.updateStrategy = updateStrategy;
		this.topics = topics;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public UpdateStrategy getUpdateStrategy() {
		return updateStrategy;
	}
	
	public List<String> getTopics() {
		return topics;
	}
}
