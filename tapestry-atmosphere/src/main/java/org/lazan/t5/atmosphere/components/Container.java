package org.lazan.t5.atmosphere.components;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.lazan.t5.atmosphere.model.ContainerModel;
import org.lazan.t5.atmosphere.model.PushTargetModel;

@Import(library = { "atmosphere.js", "tapestry-atmosphere.js", "tapestry-atmosphere.js" })
public class Container {
	@Parameter(name="options")
	private JSONObject optionsParam;
	
	@Parameter(defaultPrefix=BindingConstants.LITERAL, value="symbol:atmosphere.contentType")
	private String contentType;
	
	@Parameter(defaultPrefix=BindingConstants.LITERAL, value="symbol:atmosphere.logLevel")
	private String logLevel;
	
	@Parameter(defaultPrefix=BindingConstants.LITERAL, value="symbol:atmosphere.transport", required=true)
	private String transport;
	
	@Parameter(defaultPrefix=BindingConstants.LITERAL, value="symbol:atmosphere.fallbackTransport")
	private String fallbackTransport;

	@Inject @Symbol("atmosphere.uri")
	private String uri;
	
	@Parameter(value="symbol:atmosphere.secure")
	private boolean secure;

	@Inject
	private Environment environment;
	
	@Inject
	private JavaScriptSupport javascriptSupport;
	
	@Inject
	private BaseURLSource baseUrlSource;
	
	private List<PushTargetModel> pushTargets;
	private Set<String> topics;
	
	void setupRender() {
		pushTargets = CollectionFactory.newList();
		topics = new LinkedHashSet<String>();
		environment.push(ContainerModel.class, new ContainerModel() {
			public void addPushTarget(PushTargetModel pushTarget) {
				pushTargets.add(pushTarget);
				topics.addAll(pushTarget.getTopics());
			}
		});
	}
	
	void afterRenderBody() {
		if (!pushTargets.isEmpty()) {
			JSONObject options = createOptions();
			javascriptSupport.addInitializerCall("atmosphereContainer", options);
			environment.pop(ContainerModel.class);
		}
	}
	
	JSONObject createOptions() {
		JSONObject options = optionsParam == null ? new JSONObject() : new JSONObject(optionsParam.toString());
		
		//String url = String.format("%s/%s/", baseUrlSource.getBaseURL(secure), uri);
		String url = "http://localhost:8080/tapestry-atmosphere/atmosphere/";
		options.put("url", url);
		options.put("transport", transport);

		putIfNotNull(options, "contentType", contentType);
		putIfNotNull(options, "logLevel", logLevel);
		putIfNotNull(options, "fallbackTransport", fallbackTransport);
		
		for (PushTargetModel pushTarget : pushTargets) {
			JSONObject targetConfig = new JSONObject();
			targetConfig.put("topics", new JSONArray(pushTarget.getTopics().toArray()));
			targetConfig.put("id", pushTarget.getClientId());
			
			options.append("pushTargets", targetConfig);
		}
		
		return options;
	}

	void putIfNotNull(JSONObject json, String key, Object value) {
		if (value != null) {
			json.put(key, value);
		}
	}
}
