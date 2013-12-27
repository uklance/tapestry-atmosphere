package org.lazan.t5.atmosphere.services.internal;

import java.util.List;

import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.lazan.t5.atmosphere.services.BroadcasterManager;

public class BroadcasterManagerImpl implements BroadcasterManager {
	@Override
	public List<Broadcaster> getBroadcasters(String[] topics) {
		List<Broadcaster> broadcasters = CollectionFactory.newList();
		BroadcasterFactory factory = BroadcasterFactory.getDefault();
		for (String topic : topics) {
			
			// TODO: make this thread-safe
			Broadcaster broadcaster = factory.lookup(topic);
			if (broadcaster == null) {
				System.out.println("### Creating broadcaster for " + topic);
				broadcaster = factory.get(topic);
			}
			broadcasters.add(broadcaster);
		}
		return broadcasters;
	}
}
