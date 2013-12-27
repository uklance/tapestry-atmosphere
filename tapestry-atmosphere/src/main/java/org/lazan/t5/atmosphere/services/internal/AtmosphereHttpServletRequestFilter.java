package org.lazan.t5.atmosphere.services.internal;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
	private final Pattern uriPattern;
	private final ExtendedAtmosphereServlet atmosphereServlet;

	/**
	 * Provides access to the protected AtmosphereFramework instance
	 */
	static class ExtendedAtmosphereServlet extends AtmosphereServlet {
		public ExtendedAtmosphereServlet(boolean isFilter, boolean autoDetectHandlers) {
			super(isFilter, autoDetectHandlers);
		}

		public AtmosphereFramework getAtmosphereFramework() {
			return framework;
		}
	}

	public AtmosphereHttpServletRequestFilter(
			Map<String, String> initParams,
			ApplicationGlobals applicationGlobals,
			RegistryShutdownHub registryShutdownHub,
			@Symbol("atmosphere.uri") String uri) {
		boolean isFilter = false;
		boolean autoDetectHandlers = false;
		uriPattern = Pattern.compile(String.format("/%s/", uri));
		atmosphereServlet = new ExtendedAtmosphereServlet(isFilter, autoDetectHandlers);
		ServletConfig servletConfig = createServletConfig(initParams, applicationGlobals);
		try {
			atmosphereServlet.init(servletConfig);
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
		registryShutdownHub.addRegistryShutdownListener(new Runnable() {
			public void run() {
				atmosphereServlet.destroy();
			}
		});		
	}

	public AtmosphereFramework getAtmosphereFramework() {
		return atmosphereServlet.getAtmosphereFramework();
	}

	@Override
	public boolean service(HttpServletRequest request, HttpServletResponse response, HttpServletRequestHandler handler)
			throws IOException {
		
		boolean matches = uriPattern.matcher(request.getServletPath()).matches();
		if (matches) {
			try {
				atmosphereServlet.service(request, response);
			} catch (ServletException e) {
				throw new RuntimeException(e);
			}
			return true;
		}
		return handler.service(request, response);
	}

	protected ServletConfig createServletConfig(final Map<String, String> initParams, final ApplicationGlobals applicationGlobals) {
		ServletConfig servletConfig = new ServletConfig() {
			public String getServletName() {
				return "atmosphere";
			}

			@Override
			public ServletContext getServletContext() {
				return applicationGlobals.getServletContext();
			}

			@Override
			public Enumeration getInitParameterNames() {
				return createEnumeration(initParams.keySet());
			}

			@Override
			public String getInitParameter(String name) {
				return initParams.get(name);
			}
		};
		return servletConfig;
	}

	protected Enumeration createEnumeration(Iterable<String> collection) {
		final Iterator<String> it = collection.iterator();
		return new Enumeration<String>() {
			@Override
			public boolean hasMoreElements() {
				return it.hasNext();
			}

			@Override
			public String nextElement() {
				return it.next();
			}
		};
	}
}
