package org.lazan.t5.atmosphere.demo.services;

import org.apache.tapestry5.ComponentParameterConstants;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.lazan.t5.atmosphere.services.AtmosphereModule;
import org.lazan.t5.atmosphere.services.TopicAuthorizer;
import org.lazan.t5.atmosphere.services.TopicListener;

@SubModule(AtmosphereModule.class)
public class AppModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(ChatManager.class, ChatManagerImpl.class);
	}
	
	public static void contributeFactoryDefaults(MappedConfiguration<String, Object> config) {
		config.override(SymbolConstants.APPLICATION_VERSION, "1.0-SNAPSHOT");
	}
	
	public static void contributeApplicationDefaults(MappedConfiguration<String, Object> config) {
		config.add(SymbolConstants.PRODUCTION_MODE, "false");
		config.add(SymbolConstants.SUPPORTED_LOCALES, "en");
		config.add(ComponentParameterConstants.ZONE_UPDATE_METHOD, "show");
	}

	public static TopicAuthorizer decorateTopicAuthorizer(@Autobuild ChatTopicAuthorizer chatTopicAuthorizer) {
		return chatTopicAuthorizer;
	}
	
	public static TopicListener decorateTopicListener(@Autobuild ChatTopicListener chatTopicListener) {
		return chatTopicListener;
	}
}
