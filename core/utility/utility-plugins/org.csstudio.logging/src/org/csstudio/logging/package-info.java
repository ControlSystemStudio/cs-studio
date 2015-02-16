/** Helper for using <code>java.util.logging</code>
 *
 *  <p>In principle, CSS code can simply use the {@link java.util.logging.Logger} that is included
 *  in the JRE without any additions.
 *
 *  <p>This plugin provides:
 *  <ul>
 *  <li>{@link org.csstudio.logging.LogConfigurator} for configuring the logger based on Eclipse preferences.
 *  <li>{@link org.csstudio.logging.LogFormatter} for single-line logging, more compact than the default.
 *  <li>{@link org.csstudio.logging.JMSLogHandler} for logging to JMS
 *  <li>{@link org.csstudio.logging.PluginLogListener} for routing Eclipse log messages into the {@link java.util.logging.Logger}
 *  </ul>
 */
package org.csstudio.logging;