package org.lazan.t5.atmosphere.services;

import javax.servlet.ServletContext;

import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.Registry;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereFramework.DefaultAtmosphereObjectFactory;
import org.atmosphere.cpr.AtmosphereObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TapestryAtmosphereObjectFactory implements AtmosphereObjectFactory {
	private static final AtmosphereObjectFactory FALLBACK_OBJECT_FACTORY = new DefaultAtmosphereObjectFactory();
	private static final Logger logger = LoggerFactory.getLogger(TapestryAtmosphereObjectFactory.class);
	
	@Override
	public <T> T newClassInstance(AtmosphereFramework framework, Class<T> type) throws InstantiationException,
			IllegalAccessException
	{
		Registry registry = getRegistry(framework);
		try {
			T service = registry.getService(type);
			logger.debug("Found {} in tapestry registry", type.getSimpleName());
			return service;
		} catch (RuntimeException e) {
			logger.debug("Falling back to default lookup for {}", type.getSimpleName());
			return FALLBACK_OBJECT_FACTORY.newClassInstance(framework, type);
		}
	}
	
	protected Registry getRegistry(AtmosphereFramework framework) {
		ServletContext servletContext = framework.getServletContext();
		Registry registry = (Registry) servletContext.getAttribute(TapestryFilter.REGISTRY_CONTEXT_NAME);
		if (registry == null) {
			throw new IllegalStateException(
					"Tapestry registry does not exist. Try configuring the atmosphere servlet to have a load-on-startup value greater than tapestry's in web.xml");
		}
		return registry;
	}	
}
