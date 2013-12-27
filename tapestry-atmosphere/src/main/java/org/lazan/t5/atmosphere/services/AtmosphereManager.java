package org.lazan.t5.atmosphere.services;

import org.apache.tapestry5.json.JSONObject;
import org.atmosphere.cpr.AtmosphereResource;

public interface AtmosphereManager {
	void initPushTargets(JSONObject data, AtmosphereResource suspendedResource);
}
