define(["atmosphere", "t5/core/pageinit"], function(atmosphere, pageInit){
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

					var element = document.getElementById(clientId);
					if (pushTarget.update == 'PREPEND') {
						var html = content + element.innerHTML;
						element.innerHTML = html;
					} else if (pushTarget.update == 'APPEND') {
						var html = element.innerHTML + content;
						element.innerHTML = html;
					} else {
						element.innerHTML = content;
					}
				}

			});



		};

		subsocket = atmosphere.subscribe(request);
	}
});
