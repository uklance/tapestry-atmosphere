package org.lazan.t5.atmosphere.services;

import org.apache.tapestry5.EventContext;

public interface PageGlobals {
	EventContext getPageActivationContext();
	void storePageActivationContext(EventContext eventContext);
}