package org.lazan.t5.atmosphere.services.internal;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.lazan.t5.atmosphere.services.PageGlobals;

@Scope(ScopeConstants.PERTHREAD)
public class PageGlobalsImpl implements PageGlobals {
	private EventContext pageActivationContext;

	@Override
	public EventContext getPageActivationContext() {
		return pageActivationContext;
	}

	@Override
	public void storePageActivationContext(EventContext pageActivationContext) {
		this.pageActivationContext = pageActivationContext;
	}
}
