T5.extendInitializers({
	atmosphereContainer: function(options) {
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
			console.log("onOpen: " + response);
			var data = {
				pushTargets: pushTargets, 
				ac: options.ac,
				activePageName: options.activePageName,
				containingPageName: options.containingPageName,
			};
			subsocket.push(Object.toJSON(data));
		};		

		request.onMessage = function (response) {
			var messageJson = response.responseBody;
			// prototype specific
			var message = messageJson.evalJSON();
			
			for (var clientId in message) {
				var singleResponse = message[clientId];
				var content = singleResponse.content;
				var pushTarget = pushTargetsById[clientId];
				document.getElementById(clientId).innerHTML = content;
			}
		};

		subsocket = atmosphere.subscribe(request);
	}
});
