package org.lazan.t5.atmosphere.services.internal;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.UsesOrderedConfiguration;
import org.atmosphere.cpr.AtmosphereResource;
import org.lazan.t5.atmosphere.services.TopicAuthorizer;

@UsesOrderedConfiguration(TopicAuthorizer.class)
public class TopicAuthorizerImpl implements TopicAuthorizer {
	private final List<TopicAuthorizer> topicAuthorizers;
	
	public TopicAuthorizerImpl(List<TopicAuthorizer> topicAuthorizers) {
		super();
		this.topicAuthorizers = topicAuthorizers;
	}

	@Override
	public boolean isAuthorized(AtmosphereResource resource, String topic) {
		for (TopicAuthorizer topicAuthorizer : topicAuthorizers) {
			if (!topicAuthorizer.isAuthorized(resource, topic)) {
				return false;
			}
		}
		return true;
	}
}
