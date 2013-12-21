T5.extendInitializers({
	atmosphereContainer: function(options) {
		var pushTargets = options.pushTargets;
		var pushTargetsByTopic = {};

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
		
		var topics = "";
		var isFirst = true;
		for (var topic in pushTargetsByTopic) {
			if (isFirst) {
				isFirst = false;
			} else {
				topics += ",";
			}
			topics += topic;
			
			console.log("Topic " + topic + " = " + pushTargetsByTopic[topic].length);
		}
		
		var url = options.url + "?topics=" + topics;

		var request = {
			url: url,
		    contentType : options.contentTYpe,
		    logLevel : options.logLevel,
		    transport : options.transport,
		    fallbackTransport: options.fallbackTransport
		};

		request.onMessage = function (response) {
			var message = response.responseBody;
			var bits = message.split(',');
			var topic = bits[0];
			var message = bits[1];
			
			var list = pushTargetsByTopic[topic];
			console.log("Message received=" + message + ", list=" + list);
			if (list) {
				for (var i=0; i < list.length; ++i) {
					var pushTarget = list[i];
					console.log("Updating=" + pushTarget.id);
					document.getElementById(pushTarget.id).innerHTML = message;
				}
			}
		};

		var subsocket = atmosphere.subscribe(request);
	}
});
