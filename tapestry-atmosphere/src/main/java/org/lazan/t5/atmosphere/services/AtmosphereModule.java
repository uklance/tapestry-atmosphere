package org.lazan.t5.atmosphere.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.LibraryMapping;
import org.lazan.t5.atmosphere.services.internal.BroadcasterManagerImpl;
import org.lazan.t5.atmosphere.services.internal.PageGlobalsComponentRequestFilter;
import org.lazan.t5.atmosphere.services.internal.PageGlobalsImpl;
import org.slf4j.Logger;

public class AtmosphereModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(BroadcasterManager.class, BroadcasterManagerImpl.class);
		binder.bind(PageGlobals.class, PageGlobalsImpl.class);
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

	public static void contributeComponentRequestHandler(OrderedConfiguration<ComponentRequestFilter> config) {
		config.addInstance("PageGlobals", PageGlobalsComponentRequestFilter.class, "before:*");
	}
}
