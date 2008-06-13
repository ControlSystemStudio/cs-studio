Web Server How-To
=================
Eclipse includes the Jetty web server and servlet engine,
and this describes how one can use them.

Assume your plugin has a "/webroot" directory with HTML files
and maybe some Servlets derived from HttpServlet.
There are two basic ways to add a web server or servlet engine
to the application.

In any case, add these plugins to your application:
 org.eclipse.equinox.http.jetty,
 org.eclipse.equinox.http.registry,
 javax.servlet

* A. Configure Web Server Via Extension Points

Add something like this to plugin.xml:
 <extension point="org.eclipse.equinox.http.registry.resources">
    <resource
      alias="/root"
      base-name="/webroot"/>
  </extension>

  <extension point="org.eclipse.equinox.http.registry.servlets">
    <servlet
      alias="/server_test"
      class="org.csstudio.platform.httpd.servlets.ServerTest"/>
  </extension>

(Sometimes mapping the webroof folder to "/" seems to work,
 at other times a name that's not part of servlets like "/...."
 seems to be required?!)

A simple test is possible with just a "OSGI Framework" run config:
Include the plugin with webroot & the above plugin.xml,
add plugins *http.jetty, *http.registry and their dependencies.

Specify VM arg
  -Dorg.osgi.service.http.port=9001
because the default port 80 will likely result in an error message
 "WARNING: Failed to start: SocketListener0@0.0.0.0:80".

Run the OSGI Framework config and wait(!!) for messages to appear.
Try "help" and "ss" in case of plugin load problems, otherwise
use a web browser to access the /webroot files and servlets via URLs
  http://localhost:9001/root/test.html
  http://localhost:9001/server_info
  
Disadvantages:
- Web server starts at an unpredictable time.
- No way to control the web server port within the application.
- No way to run more than one web server with different ports.

If your Model/View/Controller type application loads one or more models,
and then wants to start web servers for a specific model at a specific
port (which might be part of the model config and unknown at command-line
time), this option doesn't work.


* B. Start Web Server Programmatically

The JettyConfigurator allows the application to start one or more web servers,
for example one for each model item, at a known time with
a specific port.

Don't define any resources or servlets in the plugin.xml file,
instead use the HttpServiceHelper to create HttpService instances,
then add the resources and servlets manually.

Might have to set command-line option
   -Dorg.eclipse.equinox.http.jetty.autostart=false
to prevent additional auto-started web server instances.
Exact mechanism is unclear. When debugging into 
org.eclipse.equinox.http.jetty.internal.Activator
where the autostart property is checked, the Archive Engine
happens to be in a state where the "StartLevel.isBundleActivationPolicyUsed"
test results in no auto-started HTTP server.
In other standalone test, I had to set the command-line option
to prevent a default HTTP instance.
