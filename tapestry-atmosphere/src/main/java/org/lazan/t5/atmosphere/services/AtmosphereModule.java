package org.lazan.t5.atmosphere.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.LibraryMapping;
import org.slf4j.Logger;

public class AtmosphereModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(BroadcasterManager.class, BroadcasterManagerImpl.class);
	}
	
	public void contributeFactoryDefaults(MappedConfiguration<String, String> config) {
		config.add("atmosphere.contentType", "application/json");
		config.add("atmosphere.logLevel", "debug");
		config.add("atmosphere.transport", "websocket");
		config.add("atmosphere.fallbackTransport", "long-polling");
		config.add("atmosphere.uri", "atmosphere");
		config.add("atmosphere.secure", "false");
	}
	
	public static void contributeIgnoredPathsFilter(Configuration<String> configuration, @Symbol("atmosphere.uri") String atmosphereUri) {
		String pattern = String.format("/%s/.*", atmosphereUri);
		configuration.add(pattern);
	}
	
	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration, Logger log) {
		log.info("Registering atmosphere component library");
		configuration.add(new LibraryMapping("atmos", "org.lazan.t5.atmosphere"));
	}
}
