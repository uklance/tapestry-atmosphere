package org.lazan.t5.atmosphere.services.internal;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.annotations.UsesMappedConfiguration;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.HttpServletRequestHandler;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereServlet;

@UsesMappedConfiguration(key = String.class, value = String.class)
public class AtmosphereHttpServletRequestFilter implements HttpServletRequestFilter {
	private final ExtendedAtmosphereServlet atmosphereServlet;
	private final HttpServletHttpServletRequestFilter delegate;

	/**
	 * Provides access to the protected AtmosphereFramework instance
	 */
	@SuppressWarnings("serial")
	static class ExtendedAtmosphereServlet extends AtmosphereServlet {
		public ExtendedAtmosphereServlet(boolean isFilter, boolean autoDetectHandlers) {
			super(isFilter, autoDetectHandlers);
		}

		public AtmosphereFramework getAtmosphereFramework() {
			return this.framework();
		}
	}

	public AtmosphereHttpServletRequestFilter(
			Map<String, String> initParams,
			ApplicationGlobals applicationGlobals,
			RegistryShutdownHub registryShutdownHub,
			@Symbol("atmosphere.uri") String uri) {
		boolean isFilter = false;
		boolean autoDetectHandlers = false;
		atmosphereServlet = new ExtendedAtmosphereServlet(isFilter, autoDetectHandlers);
		delegate = new HttpServletHttpServletRequestFilter(applicationGlobals, registryShutdownHub, atmosphereServlet, uri, initParams);
	}
	
	@Override
	public boolean service(HttpServletRequest request, HttpServletResponse response, HttpServletRequestHandler handler) throws IOException {
		return delegate.service(request, response, handler);
	}

	public AtmosphereFramework getAtmosphereFramework() {
		return atmosphereServlet.getAtmosphereFramework();
	}
}
