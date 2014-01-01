package org.lazan.t5.atmosphere.services;

import org.apache.tapestry5.json.JSONObject;
import org.atmosphere.cpr.AtmosphereResource;
import org.lazan.t5.atmosphere.model.ContainerClientModel;

public interface AtmosphereManager {
	void initialize(AtmosphereResource resource, JSONObject data);
	ContainerClientModel getContainerClientModel(AtmosphereResource resource);
	void initializeIfSubsequentRequest(AtmosphereResource resource);
}
