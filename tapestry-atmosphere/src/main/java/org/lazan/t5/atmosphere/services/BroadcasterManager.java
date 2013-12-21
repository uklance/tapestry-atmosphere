package org.lazan.t5.atmosphere.services;

import java.util.List;

import org.atmosphere.cpr.Broadcaster;

public interface BroadcasterManager {
	List<Broadcaster> getBroadcasters(String[] topics);
}
