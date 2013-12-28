package org.lazan.t5.atmosphere.services.internal;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.HttpServletRequestHandler;

/**
 * Manages a HTTPServlet as a HttpServletRequestFilter under tapestry
 */
public class HttpServletHttpServletRequestFilter implements HttpServletRequestFilter {
	private final Pattern uriPattern;
	private final HttpServlet servlet;

	public HttpServletHttpServletRequestFilter(
			ApplicationGlobals applicationGlobals,
			RegistryShutdownHub registryShutdownHub,
			Class<? extends HttpServlet> servletType,
			String uri,
			Map<String, String> initParams) {
		
		this(applicationGlobals, registryShutdownHub, newInstanceQuietly(servletType), uri, initParams);
	}

	public HttpServletHttpServletRequestFilter(
			ApplicationGlobals applicationGlobals,
			RegistryShutdownHub registryShutdownHub,
			HttpServlet servlet,
			String uri,
			Map<String, String> initParams) {
		this.servlet = servlet;
		this.uriPattern = Pattern.compile(String.format("/%s/.*", uri), Pattern.CASE_INSENSITIVE);
		ServletConfig servletConfig = createServletConfig(initParams, applicationGlobals);
		try {
			servlet.init(servletConfig);
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
		registryShutdownHub.addRegistryShutdownListener(new Runnable() {
			public void run() {
				HttpServletHttpServletRequestFilter.this.servlet.destroy();
			}
		});		
	}

	@Override
	public boolean service(HttpServletRequest request, HttpServletResponse response, HttpServletRequestHandler handler)
			throws IOException {
		
		boolean matches = uriPattern.matcher(request.getServletPath()).matches();
		if (matches) {
			try {
				servlet.service(request, response);
			} catch (ServletException e) {
				throw new RuntimeException(e);
			}
			return true;
		}
		return handler.service(request, response);
	}

	protected ServletConfig createServletConfig(final Map<String, String> initParams, final ApplicationGlobals applicationGlobals) {
		return new ServletConfig() {
			public String getServletName() {
				return "atmosphere";
			}
			public ServletContext getServletContext() {
				return applicationGlobals.getServletContext();
			}
			public Enumeration<String> getInitParameterNames() {
				return createEnumeration(initParams.keySet());
			}
			public String getInitParameter(String name) {
				return initParams.get(name);
			}
		};
	}

	protected <T> Enumeration<T> createEnumeration(Iterable<T> collection) {
		final Iterator<T> it = collection.iterator();
		return new Enumeration<T>() {
			public boolean hasMoreElements() {
				return it.hasNext();
			}
			public T nextElement() {
				return it.next();
			}
		};
	}
	
	protected static HttpServlet newInstanceQuietly(Class<? extends HttpServlet> servletType) {
		try {
			return servletType.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public HttpServlet getHttpServlet() {
		return servlet;
	}
}
