package org.lazan.t5.atmosphere.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.LibraryMapping;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.atmosphere.cpr.BroadcasterFactory;
import org.lazan.t5.atmosphere.services.internal.AtmosphereBroadcasterImpl;
import org.lazan.t5.atmosphere.services.internal.AtmosphereHandlerImpl;
import org.lazan.t5.atmosphere.services.internal.AtmosphereHttpServletRequestFilter;
import org.lazan.t5.atmosphere.services.internal.AtmosphereManagerImpl;
import org.lazan.t5.atmosphere.services.internal.AtmosphereResourceEventListenerImpl;
import org.lazan.t5.atmosphere.services.internal.AtmosphereSessionManagerImpl;
import org.lazan.t5.atmosphere.services.internal.PageGlobalsComponentRequestFilter;
import org.lazan.t5.atmosphere.services.internal.PageGlobalsImpl;
import org.lazan.t5.atmosphere.services.internal.PerRequestBroadcastFilterImpl;
import org.slf4j.Logger;

public class AtmosphereModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(AtmosphereManager.class, AtmosphereManagerImpl.class);
		binder.bind(PageGlobals.class, PageGlobalsImpl.class);
		binder.bind(AtmosphereHandler.class, AtmosphereHandlerImpl.class);
		binder.bind(AtmosphereHttpServletRequestFilter.class, AtmosphereHttpServletRequestFilter.class);
		binder.bind(AtmosphereResourceEventListener.class, AtmosphereResourceEventListenerImpl.class);
		binder.bind(AtmosphereSessionManager.class, AtmosphereSessionManagerImpl.class);
		binder.bind(AtmosphereBroadcaster.class, AtmosphereBroadcasterImpl.class);

		// note that the concrete class is required here
		binder.bind(PerRequestBroadcastFilterImpl.class, PerRequestBroadcastFilterImpl.class);
	}
	
	public static void contributeFactoryDefaults(MappedConfiguration<String, String> config) {
		config.add("atmosphere.logLevel", "debug");
		config.add("atmosphere.transport", "websocket");
		config.add("atmosphere.fallbackTransport", "long-polling");
		config.add("atmosphere.uri", "atmosphere");
		config.add("atmosphere.secure", "false");
	}
	
	public static AtmosphereFramework buildAtmosphereFramework(AtmosphereHttpServletRequestFilter atmosphereFilter) {
		return atmosphereFilter.getAtmosphereFramework();
	}
	
	public static BroadcasterFactory buildBroadcasterFactory() {
		return BroadcasterFactory.getDefault();
	}
	
	public static AtmosphereResourceFactory buildAtmosphereResourceFactory() {
		return AtmosphereResourceFactory.getDefault();
	}
	
	public static void contributeHttpServletRequestHandler(
			OrderedConfiguration<HttpServletRequestFilter> configuration,
			AtmosphereHttpServletRequestFilter atmosphereFilter)
	{
		configuration.add("atmosphere", atmosphereFilter);
	}	

	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration, Logger log) {
		log.info("Registering atmosphere component library");
		configuration.add(new LibraryMapping("atmos", "org.lazan.t5.atmosphere"));
	}

	public static void contributeComponentRequestHandler(OrderedConfiguration<ComponentRequestFilter> config) {
		config.addInstance("PageGlobals", PageGlobalsComponentRequestFilter.class, "before:*");
	}
	
	public static void contributeAtmosphereHttpServletRequestFilter(MappedConfiguration<String, String> config) {
		config.add(ApplicationConfig.OBJECT_FACTORY, TapestryAtmosphereObjectFactory.class.getName());
		config.add(ApplicationConfig.BROADCAST_FILTER_CLASSES, PerRequestBroadcastFilterImpl.class.getName());
	}
	
	@Startup
	public static void configureAtmosphereFramework(AtmosphereFramework framework, AtmosphereHandler handler) {
		framework.addAtmosphereHandler("/", handler);
	}
}
