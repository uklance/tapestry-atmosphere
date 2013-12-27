T5.extendInitializers({
	atmosphereContainer: function(options) {
		var pushTargets = options.pushTargets;
		var pushTargetsByTopic = {};
		var subsocket;

		// group pushTargets by topic
		for (var i = 0; i < pushTargets.length; ++i) {
			var pushTarget = pushTargets[i];
			for (var j = 0; j < pushTarget.topics.length; ++ j) {
				var topic = pushTarget.topics[j];
				console.log("  " + topic);
				var group = pushTargetsByTopic[topic];
				if (group == null) {
					group = [];
					pushTargetsByTopic[topic] = group;
				}
				group.push(pushTarget);
			}
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
			var topic = message.topic;
			var content = message.content;
			var list = pushTargetsByTopic[topic];
			console.log("Message received=" + messageJson + ", listSize=" + list.length);
			if (list) {
				for (var i=0; i < list.length; ++i) {
					var pushTarget = list[i];
					console.log("Updating=" + pushTarget.id);
					document.getElementById(pushTarget.id).innerHTML = content;
				}
			}
		};

		subsocket = atmosphere.subscribe(request);
	}
});
