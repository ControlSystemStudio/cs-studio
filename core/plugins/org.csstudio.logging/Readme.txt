org.csstudio.logging: CSS Setup of java.util.logging
====================================================

User code can simply use java.util.logging.
No additional libraries or plugins are required.

In comparison to the CentralLogger, code like this:

   // Need o.c.platform and Log4j
   import org.csstudio.platform.logging.CentralLogger;
   import org.apache.log4j.Logger;
   
   Logger logger = CentralLogger.getInstance().getLogger(this);
   
   logger.info("This is a message");
   logger.debug("Hello?");
   
   // Check level to prevent unnecessary StringBuilder calls
   if (logger.isDebugEnabled())
       logger.debug("Value is " + value)
   
changes into this:

   // Nothing required beyond JRE
   import java.util.logging.Logger;
   
   // Logger based on a name. Use current class name or plugin ID.
   Logger logger = Logger.getLogger(getClass().getName());
   
   // Instead of Log4j's fatal, error, warning, info, debug, trace
   // there's severe, warning, info, config, fine, finer, finest.
   logger.info("This is a message");
   logger.fine("Hello?");
   
   // Can use a formatter for lazy message generation
   logger.log(Level.DEBUG, "Value is {0}", value);

java.util.logging can be configured in many ways, for example via *.ini files in the JRE.
This plugin offers a LogConfigurator that can be called from the product's startup code:
LogConfigurator.configureFromPreferences() reads Eclipse preferences to configure logging
for the console, files and JMS. For definition and defaults see preferences.ini.
In addition, the LogConfigurator adds the PluginLogListener to the Eclipse Platform.

