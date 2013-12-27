package org.lazan.t5.atmosphere.services.internal;

import java.io.IOException;

import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.lazan.t5.atmosphere.services.PageGlobals;

public class PageGlobalsComponentRequestFilter implements ComponentRequestFilter {
	private final PageGlobals pageGlobals;
	
	public PageGlobalsComponentRequestFilter(PageGlobals pageGlobals) {
		super();
		this.pageGlobals = pageGlobals;
	}
	
	@Override
	public void handleComponentEvent(ComponentEventRequestParameters parameters, ComponentRequestHandler handler) throws IOException {
		pageGlobals.storePageActivationContext(parameters.getPageActivationContext());
		handler.handleComponentEvent(parameters);
	}
	
	@Override
	public void handlePageRender(PageRenderRequestParameters parameters, ComponentRequestHandler handler) throws IOException {
		pageGlobals.storePageActivationContext(parameters.getActivationContext());
		handler.handlePageRender(parameters);
	}
}