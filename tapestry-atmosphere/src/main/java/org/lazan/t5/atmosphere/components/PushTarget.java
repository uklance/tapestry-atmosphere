package org.lazan.t5.atmosphere.components;

import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.components.Any;
import org.lazan.t5.atmosphere.model.ContainerModel;
import org.lazan.t5.atmosphere.model.PushTargetModel;
import org.lazan.t5.atmosphere.model.UpdateStrategy;

public class PushTarget extends Any {
	@Parameter(required=true, defaultPrefix=BindingConstants.LITERAL)
	private List<String> topics;
	
	@Parameter(defaultPrefix=BindingConstants.LITERAL, value="replace", required=true)
	private UpdateStrategy update;
	
	@Environmental
	private ContainerModel containerModel;
	
	void afterRender() {
		PushTargetModel targetModel = new PushTargetModel(getClientId(), update, topics);
		containerModel.addPushTarget(targetModel);
	}
}
