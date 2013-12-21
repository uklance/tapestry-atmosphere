package org.lazan.t5.atmosphere.demo.services;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.cron.IntervalSchedule;
import org.apache.tapestry5.ioc.services.cron.PeriodicExecutor;
import org.apache.tapestry5.ioc.services.cron.Schedule;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.lazan.t5.atmosphere.services.AtmosphereModule;
import org.slf4j.Logger;

@SubModule(AtmosphereModule.class)
public class AppModule {
	public static void bind(ServiceBinder binder) {
	}
	
	public static void contributeFactoryDefaults(MappedConfiguration<String, Object> configuration) {
		configuration.override(SymbolConstants.APPLICATION_VERSION, "1.0-SNAPSHOT");
	}

	public static void contributeApplicationDefaults(MappedConfiguration<String, Object> configuration) {
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
	}
	
	
	@Startup
	public static void startRandoms(PeriodicExecutor executor, final Logger log) {
		final AtomicInteger count = new AtomicInteger(0);
		final String[] topics = new String[] { "topic1", "topic2", "topic3" };
		Schedule schedule = new IntervalSchedule(5000);
		executor.addJob(schedule, "garbage", new Runnable() {
			public void run() {
				for (String topic : topics) {
					Broadcaster broadcaster = BroadcasterFactory.getDefault().lookup(topic);
					System.out.println(String.format("Attempting broadcast topic=%s, broadcaster=%s", topic, broadcaster));
					if (broadcaster != null) {
						String message = String.format("%s,%s : %s", topic, topic, count.incrementAndGet());
						broadcaster.broadcast(message);
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		});
	}
	
}
