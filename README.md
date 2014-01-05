tapestry-atmosphere
===================

Asynchronous WebSocket/Comet Support in [Tapestry](http://tapestry.apache.org) using [Atmosphere](https://github.com/Atmosphere/atmosphere)

The aim of this project is to support push applications in Apache Tapestry without requiring developers
to write a single line of javascript or concern themselves with the underlying Atmosphere implementation.

See a live demo running [here](http://tapestry-atmosphere.uklance.cloudbees.net)

### Topics

- [Container component](https://github.com/uklance/tapestry-atmosphere/wiki/Container-Component)
- [PushTarget component](https://github.com/uklance/tapestry-atmosphere/wiki/PushTarget-Component)
- [Atmosphere Broadcaster](https://github.com/uklance/tapestry-atmosphere/wiki/Atmosphere-Broadcaster)
- [Supported Browsers / Servers](https://github.com/Atmosphere/atmosphere/wiki/Supported-WebServers-and-Browsers)
- [Topic Authorizer](https://github.com/uklance/tapestry-atmosphere/wiki/Topic-Authorizer)
- [Topic Listener](https://github.com/uklance/tapestry-atmosphere/wiki/Topic-Listener)
- Servlet Configuration
- HttpSession
- TapestryAtmosphereObjectFactory
- web.xml
- Tomcat / JBOSS
- Apache

### Maven

```xml
<dependencies>
   <dependency>
      <groupId>org.lazan</groupId>
      <artifactId>tapestry-atmosphere</artifactId>
      <!-- 
         lookup latest version at 
         https://github.com/uklance/releases/tree/master/org/lazan/tapestry-atmosphere
      -->
      <version>...</version> 
   </dependency>
</dependencies>

...

<repositories>
   <repository>
      <id>lazan-releases</id>
      <url>https://github.com/uklance/releases/raw/master</url>
   </repository>
</repositories>
```


