package org.lazan.t5.atmosphere.services;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.Registry;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereFramework.DefaultAtmosphereObjectFactory;
import org.atmosphere.cpr.AtmosphereObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Note: This is not a tapestry managed service. It is instantiated by Atmosphere
 */
public class TapestryAtmosphereObjectFactory implements AtmosphereObjectFactory<Object> {
	private static final AtmosphereObjectFactory<Object> FALLBACK_OBJECT_FACTORY = new DefaultAtmosphereObjectFactory();
	private static final Logger logger = LoggerFactory.getLogger(TapestryAtmosphereObjectFactory.class);
	private final Set<Class<?>> registryMisses = Collections.newSetFromMap(new ConcurrentHashMap<Class<?>, Boolean>());
	private AtmosphereFramework framework;

	@Override
	public <T, U extends T> T newClassInstance(Class<T> classType, Class<U> defaultType) throws InstantiationException, IllegalAccessException
	{
		if (!registryMisses.contains(classType)) {
			Registry registry = getRegistry();
		
			try {
				// attempt service lookup
				T service = registry.getService(classType);
				logger.debug("Found {} in tapestry registry", classType.getSimpleName());
				return service;
			} catch (RuntimeException e) {
				// never try this type again
				registryMisses.add(classType);
			}
		}
		// fallback to default
		logger.debug("Falling back to default lookup for {}", classType.getSimpleName());
		return FALLBACK_OBJECT_FACTORY.newClassInstance(classType, defaultType);
	}
	
	protected Registry getRegistry() {
		ServletContext servletContext = framework.getServletContext();
		Registry registry = (Registry) servletContext.getAttribute(TapestryFilter.REGISTRY_CONTEXT_NAME);
		if (registry == null) {
			throw new IllegalStateException("Tapestry registry not found in ServletContext");
		}
		return registry;
	}

	@Override
	public void configure(AtmosphereConfig config) {
		this.framework = config.framework();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public AtmosphereObjectFactory allowInjectionOf(Object z) {
		return this;
  }
}
