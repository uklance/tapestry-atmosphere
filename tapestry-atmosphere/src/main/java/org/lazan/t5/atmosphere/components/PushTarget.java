package org.lazan.t5.atmosphere.components;

import java.util.List;

import javax.inject.Inject;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.components.Any;
import org.lazan.t5.atmosphere.model.ContainerModel;
import org.lazan.t5.atmosphere.model.PushTargetModel;
import org.lazan.t5.atmosphere.model.UpdateStrategy;

public class PushTarget extends Any {
    @Inject
    private ComponentResources resources;

	@Parameter(required=true, defaultPrefix=BindingConstants.LITERAL)
	private List<String> topics;
	
	@Parameter(defaultPrefix=BindingConstants.LITERAL, value="replace", required=true)
	private UpdateStrategy update;
	
	@Parameter(defaultPrefix=BindingConstants.LITERAL, required=true)
	private String event;
	
	@Environmental
	private ContainerModel containerModel;

	void afterRender() {
		PushTargetModel targetModel = new PushTargetModel(topics, event, update, getClientId(), resources.getNestedId());
		containerModel.addPushTarget(targetModel);
	}
}
