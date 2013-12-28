package org.lazan.t5.atmosphere.services;

import org.apache.tapestry5.ioc.annotations.UsesMappedConfiguration;

@UsesMappedConfiguration(key=Class.class, value=Class.class)
public interface AtmosphereObjectFactoryOverrideProvider {
	<T> Class<? extends T> getOverride(Class<T> type);
}
