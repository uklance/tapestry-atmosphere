package org.lazan.t5.atmosphere.components;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.lazan.t5.atmosphere.model.ContainerModel;
import org.lazan.t5.atmosphere.model.PushTargetModel;
import org.lazan.t5.atmosphere.services.PageGlobals;

@Import(library = { "atmosphere.js", "tapestry-atmosphere.js" })
public class Container {
	@Parameter(name="options")
	private JSONObject options;
	
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
	
	@Inject
	private PageGlobals pageGlobals;
	
	@Inject
    private ComponentResources resources;	
	
	@Inject
	private ApplicationGlobals applicationGlobals;

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
			JSONObject config = createConfig();
			javascriptSupport.addInitializerCall("atmosphereContainer", config);
			environment.pop(ContainerModel.class);
		}
	}
	
	JSONObject createConfig() {
		String baseUrl = baseUrlSource.getBaseURL(secure);
		String contextPath = applicationGlobals.getServletContext().getContextPath();
		String url = String.format("%s%s/%s/", baseUrl, contextPath, uri);

		JSONObject connectOptions = options == null ? new JSONObject() : new JSONObject(options.toString(true));
		connectOptions.put("url", url);
		connectOptions.put("transport", transport);
		putIfNotNull(connectOptions, "contentType", "application/json");
		putIfNotNull(connectOptions, "logLevel", logLevel);
		putIfNotNull(connectOptions, "fallbackTransport", fallbackTransport);
		
		JSONObject config = new JSONObject();
		config.put("connectOptions", connectOptions);
		EventContext ac = pageGlobals.getPageActivationContext();
		JSONArray acJson = new JSONArray();
		for (int i = 0; i < ac.getCount(); ++i) {
			acJson.put(ac.get(String.class, i));
		}
		config.put("ac", acJson);
		config.put("activePageName", resources.getPageName());
		config.put("containingPageName", resources.getPageName());

		for (PushTargetModel pushTarget : pushTargets) {
			JSONObject targetConfig = new JSONObject();
			targetConfig.put("topics", new JSONArray(pushTarget.getTopics().toArray()));
			targetConfig.put("id", pushTarget.getClientId());
			targetConfig.put("nestedComponentId", pushTarget.getNestedId());
			targetConfig.put("event", pushTarget.getEvent());
			config.append("pushTargets", targetConfig);
		}
		
		return config;
	}

	void putIfNotNull(JSONObject json, String key, Object value) {
		if (value != null) {
			json.put(key, value);
		}
	}
}
