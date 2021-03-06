tapestry-atmosphere
===================

Asynchronous WebSocket/Comet Support in [Tapestry](http://tapestry.apache.org) using [Atmosphere](https://github.com/Atmosphere/atmosphere)

The aim of this project is to support push applications in Apache Tapestry without requiring developers
to write a single line of javascript or concern themselves with the underlying Atmosphere implementation.

See a live demo running [here](http://t5atmosphere-lazan.rhcloud.com)

### Wiki Topics

- [Container component](https://github.com/uklance/tapestry-atmosphere/wiki/Container-Component)
- [PushTarget component](https://github.com/uklance/tapestry-atmosphere/wiki/PushTarget-Component)
- [Atmosphere Broadcaster](https://github.com/uklance/tapestry-atmosphere/wiki/Atmosphere-Broadcaster)
- [Supported Browsers / Servers](https://github.com/Atmosphere/atmosphere/wiki/Supported-WebServers-and-Browsers)
- [Topic Authorizer](https://github.com/uklance/tapestry-atmosphere/wiki/Topic-Authorizer)
- [Topic Listener](https://github.com/uklance/tapestry-atmosphere/wiki/Topic-Listener)
- [Configuring the Atmosphere Servlet](https://github.com/uklance/tapestry-atmosphere/wiki/Configuring-the-Atmosphere-Servlet)
- [HttpSession Support](https://github.com/uklance/tapestry-atmosphere/wiki/HTTPSession-Support)
- [Apache Proxy](https://github.com/uklance/tapestry-atmosphere/wiki/Apache-Proxy)

### Maven

```xml
<dependencies>
   <dependency>
      <groupId>org.lazan</groupId>
      <artifactId>tapestry-atmosphere</artifactId>
      <!--  lookup latest version at https://github.com/uklance/releases/tree/master/org/lazan/tapestry-atmosphere -->
      <version>x.y.z</version> 
   </dependency>
</dependencies>
<repositories>
   <repository>
      <id>lazan-releases</id>
      <url>https://raw.github.com/uklance/releases/master</url>
   </repository>
</repositories>
```
See the [quick dependency reference](https://github.com/Atmosphere/atmosphere/wiki/Installing-AtmosphereServlet-with-or-without-native-support#quick-dependency-reference) to see which atmosphere jars are required for your web container.

### web.xml

The `AtmosphereServlet` is running inside the `TapestryFilter` so there is no need to configure the `AtmosphereServlet` in `web.xml`. You must set the `async-supported` element to `true` to allow async communication.

eg:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE web-app
      PUBLIC "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
      "http://java.sun.com/dtd/web-app_2_3.dtd">
<web-app>
	<context-param>
		<param-name>tapestry.app-package</param-name>
		<param-value>my.app.package</param-value>
	</context-param>
	<filter>
		<filter-name>app</filter-name>
		<filter-class>org.apache.tapestry5.TapestryFilter</filter-class>
		<async-supported>true</async-supported>
	</filter>
	<filter-mapping>
		<filter-name>app</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
</web-app>
```

### Version History

**0.0.6** Upggrade to tapestry 5.4 and Atmosphere 2.4.2. A big thanks to [jochenberger](https://github.com/jochenberger) for his [pull request](https://github.com/uklance/tapestry-atmosphere/pull/29)
