package org.lazan.t5.atmosphere.services;

import org.apache.tapestry5.json.JSONObject;
import org.atmosphere.cpr.AtmosphereResource;
import org.lazan.t5.atmosphere.model.ContainerClientModel;

public interface AtmosphereManager {
	ContainerClientModel initContainerClientModel(AtmosphereResource resource, JSONObject data);
	ContainerClientModel getContainerClientModel(AtmosphereResource resource);
}
