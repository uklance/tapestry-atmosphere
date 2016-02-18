define(["atmosphere", "t5/core/pageinit", "t5/core/dom"], function(atmosphere, pageInit, dom){
	return function(options) {
		var pushTargets = options.pushTargets;
		var pushTargetsById = {};
		var subsocket;

		// group pushTargets by topic
		for (var i = 0; i < pushTargets.length; ++i) {
			var pushTarget = pushTargets[i];
			pushTargetsById[pushTarget.id] = pushTarget;
		}
		
		var request = options.connectOptions;

		request.onOpen = function(response) {
			var data = {
				pushTargets: pushTargets, 
				ac: options.ac,
				activePageName: options.activePageName,
				containingPageName: options.containingPageName,
			};
			subsocket.push(atmosphere.util.stringifyJSON(data));
		};		

		request.onMessage = function (response) {

			var messageJson = response.responseBody;
			pageInit.handlePartialPageRenderResponse({json: atmosphere.util.parseJSON(messageJson)}, function(response){
				var message = response.json;
				for (var clientId in message) {
					var singleResponse = message[clientId];
					var content = singleResponse.content;
					var pushTarget = pushTargetsById[clientId];

					var element = dom(clientId);
					if (pushTarget.update == 'PREPEND') {
					    element.prepend(content);
					} else if (pushTarget.update == 'APPEND') {
					    element.append(content);
					} else {
					    element.update(content);
					}
				}

			});



		};

		subsocket = atmosphere.subscribe(request);
	}
});
