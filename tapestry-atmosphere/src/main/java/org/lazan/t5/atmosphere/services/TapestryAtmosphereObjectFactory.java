package org.lazan.t5.atmosphere.services;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.Registry;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereFramework.DefaultAtmosphereObjectFactory;
import org.atmosphere.cpr.AtmosphereObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Note: This is not a tapestry managed service. It is instantiated by Atmosphere
 */
public class TapestryAtmosphereObjectFactory implements AtmosphereObjectFactory {
	private static final AtmosphereObjectFactory FALLBACK_OBJECT_FACTORY = new DefaultAtmosphereObjectFactory();
	private static final Logger logger = LoggerFactory.getLogger(TapestryAtmosphereObjectFactory.class);
	private final Set<Class<?>> registryMisses = Collections.newSetFromMap(new ConcurrentHashMap<Class<?>, Boolean>());

	@Override
	public <T> T newClassInstance(AtmosphereFramework framework, Class<T> type) throws InstantiationException,
			IllegalAccessException
	{
		if (!registryMisses.contains(type)) {
			Registry registry = getRegistry(framework);
		
			try {
				// attempt service lookup
				T service = registry.getService(type);
				logger.debug("Found {} in tapestry registry", type.getSimpleName());
				return service;
			} catch (RuntimeException e) {
				// never try this type again
				registryMisses.add(type);
			}
		}
		// fallback to default
		logger.debug("Falling back to default lookup for {}", type.getSimpleName());
		return FALLBACK_OBJECT_FACTORY.newClassInstance(framework, type);
	}
	
	protected Registry getRegistry(AtmosphereFramework framework) {
		ServletContext servletContext = framework.getServletContext();
		Registry registry = (Registry) servletContext.getAttribute(TapestryFilter.REGISTRY_CONTEXT_NAME);
		if (registry == null) {
			throw new IllegalStateException("Tapestry registry not found in ServletContext");
		}
		return registry;
	}	
}
