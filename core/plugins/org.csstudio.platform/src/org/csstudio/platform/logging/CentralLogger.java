/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.platform.logging;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.csstudio.platform.CSSPlatformPlugin;

/**
 * The central logging service of the CSS platform. The service is implemented
 * as singleton. A reference can be obtained by
 * 
 * <p/>
 * 
 * <code>
 * 	CentralLogger.getInstance()
 * </code>
 * 
 * <p/>
 * 
 * Logging is straight forward. For example, you can use the Logger this way:
 * 
 * <p/>
 * 
 * <code>
 * 	CentralLogger.getInstance().debug(this, "test log message"); <br/>
 * 	CentralLogger.getInstance().info(this, "test log message");
 * </code>
 * <p>
 * In addition, <code>getLogger(Object o)</code> offers access to
 * a plain <code>Log4j</code> logger that can be passed to libraries
 * which understand Log4j but weren't specifically written for CSS.
 * @author Alexander Will, Sven Wende
 */
public final class CentralLogger {

	/**
	 * Holds the only one instance of this class.
	 */
	private static CentralLogger _instance = null;

	/**
	 * Log4j property for the usage of the css core console logger.
	 */
	public static final String PROP_LOG4J_CONSOLE = "css_console"; //$NON-NLS-1$

	/**
	 * Log4j property for the usage of the css core file logger.
	 */
	public static final String PROP_LOG4J_FILE = "css_file"; //$NON-NLS-1$

	/**
	 * Log4j property for the usage of the css core JMS logger.
	 */
	public static final String PROP_LOG4J_JMS = "css_jms"; //$NON-NLS-1$

	/**
	 * Log4j property for the css console appender.
	 */
	public static final String PROP_LOG4J_CONSOLE_APPENDER = "log4j.appender.css_console"; //$NON-NLS-1$

	/**
	 * Log4j property for the css console appender layout.
	 */
	public static final String PROP_LOG4J_CONSOLE_LAYOUT = "log4j.appender.css_console.layout"; //$NON-NLS-1$

	/**
	 * Log4j property for the css console appender pattern.
	 */
	public static final String PROP_LOG4J_CONSOLE_PATTERN = "log4j.appender.css_console.layout.ConversionPattern"; //$NON-NLS-1$

	/**
	 * Log4j property for the css console appender threshold.
	 */
	public static final String PROP_LOG4J_CONSOLE_THRESHOLD = "log4j.appender.css_console.Threshold"; //$NON-NLS-1$

	/**
	 * Log4j property that indicates that the console appender should be aware
	 * of changing standard system output channels.
	 */
	public static final String PROP_LOG4J_CONSOLE_FOLLOW = "log4j.appender.css_console.Follow"; //$NON-NLS-1$

	/**
	 * Log4j property for the css file appender.
	 */
	public static final String PROP_LOG4J_FILE_APPENDER = "log4j.appender.css_file"; //$NON-NLS-1$

	/**
	 * Log4j property for the css file appender layout.
	 */
	public static final String PROP_LOG4J_FILE_LAYOUT = "log4j.appender.css_file.layout"; //$NON-NLS-1$

	/**
	 * Log4j property for the css file appender pattern.
	 */
	public static final String PROP_LOG4J_FILE_PATTERN = "log4j.appender.css_file.layout.ConversionPattern"; //$NON-NLS-1$

	/**
	 * Log4j property for the css file appender log file destination.
	 */
	public static final String PROP_LOG4J_FILE_DESTINATION = "log4j.appender.css_file.File"; //$NON-NLS-1$

	/**
	 * Log4j property for the css file appender threshold.
	 */
	public static final String PROP_LOG4J_FILE_THRESHOLD = "log4j.appender.css_file.Threshold"; //$NON-NLS-1$

	/**
	 * Log4j property for the css file appender append property.
	 */
	public static final String PROP_LOG4J_FILE_APPEND = "log4j.appender.css_file.Append"; //$NON-NLS-1$

	/**
	 * Log4j property for the css file appender maximum size property.
	 */
	public static final String PROP_LOG4J_FILE_MAX_SIZE = "log4j.appender.css_file.MaxFileSize"; //$NON-NLS-1$

	/**
	 * Log4j property for the css file appender maximum backup index property.
	 */
	public static final String PROP_LOG4J_FILE_MAX_INDEX = "log4j.appender.css_file.MaxBackupIndex"; //$NON-NLS-1$

	/**
	 * Log4j property for the css JMS appender.
	 */
	public static final String PROP_LOG4J_JMS_APPENDER = "log4j.appender.css_jms"; //$NON-NLS-1$

	/**
	 * Log4j property for the css JMS appender threshold.
	 */
	public static final String PROP_LOG4J_JMS_THRESHOLD = "log4j.appender.css_jms.Threshold"; //$NON-NLS-1$

	/**
	 * Log4j property for the css JMS appender layout.
	 */
	public static final String PROP_LOG4J_JMS_LAYOUT = "log4j.appender.css_jms.layout"; //$NON-NLS-1$

	/**
	 * Log4j property for the css JMS appender pattern.
	 */
	public static final String PROP_LOG4J_JMS_PATTERN = "log4j.appender.css_jms.layout.ConversionPattern"; //$NON-NLS-1$

	/**
	 * Log4j property for the css JMS appender provider url.
	 */
	public static final String PROP_LOG4J_JMS_URL = "log4j.appender.css_jms.providerURL"; //$NON-NLS-1$

	/**
	 * Log4j property for the css JMS appender context factory name.
	 */
	public static final String PROP_LOG4J_JMS_ICFN = "log4j.appender.css_jms.initialContextFactoryName"; //$NON-NLS-1$

	/**
	 * Log4j property fot the css JMS appender connection factory name.
	 */
	public static final String PROP_LOG4J_JMS_TCFBN = "log4j.appender.css_jms.topicConnectionFactoryBindingName"; //$NON-NLS-1$

	/**
	 * Log4j property for the css JMS appender topic binding name.
	 */
	public static final String PROP_LOG4J_JMS_TOPIC = "log4j.appender.css_jms.topicBindingName"; //$NON-NLS-1$

	/**
	 * Log4j property for the css JMS appender user name.
	 */
	public static final String PROP_LOG4J_JMS_USER = "log4j.appender.css_jms.userName"; //$NON-NLS-1$

	/**
	 * Log4j property for the css JMS appender password.
	 */
	public static final String PROP_LOG4J_JMS_PASSWORD = "log4j.appender.css_jms.password"; //$NON-NLS-1$

	/**
	 * Private constructor due to singleton pattern.
	 */
	private CentralLogger() {
		configure();
	}

	/**
	 * Return the only one instance of this class.
	 * 
	 * @return The only one instance of this class.
	 */
	public static CentralLogger getInstance() {
		if (_instance == null) {
			_instance = new CentralLogger();
		}

		return _instance;
	}

	/**
	 * Configure the log4j library.
	 */
	public void configure() {
		final CSSPlatformPlugin plugin = CSSPlatformPlugin.getDefault();
		if (plugin == null)
		{
		    // Not running in full Eclipse environment, probably because
		    // this is called from a JUnit test.
		    // Setup basic console log.
		    BasicConfigurator.configure();
		    return;
		}
		// Else: Configure Log4j from plugin properties
        final Properties p = createLog4jProperties(
                plugin.getPluginPreferences());
		PropertyConfigurator.configure(p);
	}

	/**
	 * Obtain a logger for the given class.
	 * @param caller Calling class, may be <code>null</code>.
	 * @return A Log4j <code>Logger</code>.
	 */
    public Logger getLogger(final Object caller) {
        if (caller == null)
            return Logger.getRootLogger();
        return Logger.getLogger(caller.getClass());
    }
	
	/**
	 * Log a message with log level <i>info</i>. The reference to the calling
	 * object is used to automatically generate more detailled log4j messages.
	 * 
	 * @param caller
	 *            The calling object.
	 * @param message
	 *            The log message.
	 */
	public void info(final Object caller, final String message) {
	    getLogger(caller).info(message);
	}

	/**
	 * Log a throwable with log level <i>info</i>. The reference to the calling
	 * object is used to automatically generate more detailled log4j messages.
	 * 
	 * @param caller
	 *            The calling object.
	 * @param throwable
	 *            The throwable.
	 */
	public void info(final Object caller, final Throwable throwable) {
		info(caller, null, throwable);
	}

	/**
	 * Log a message together with a throwable with log level <i>info</i>. The
	 * reference to the calling object is used to automatically generate more
	 * detailled log4j messages.
	 * 
	 * @param caller
	 *            The calling object.
	 * @param message
	 *            The log message.
	 * @param throwable
	 *            The throwable.
	 */
	public void info(final Object caller, final String message,
			final Throwable throwable) {
	    getLogger(caller).info(message, throwable);
	}

	/**
	 * Log a message with log level <i>debug</i>. The reference to the calling
	 * object is used to automatically generate more detailled log4j messages.
	 * 
	 * @param caller
	 *            The calling object.
	 * @param message
	 *            The log message.
	 */
	public void debug(final Object caller, final String message) {
	    getLogger(caller).debug(message);
	}

	/**
	 * Log a throwable with log level <i>debug</i>. The reference to the
	 * calling object is used to automatically generate more detailled log4j
	 * messages.
	 * 
	 * @param caller
	 *            The calling object.
	 * @param throwable
	 *            The throwable.
	 */
	public void debug(final Object caller, final Throwable throwable) {
		debug(caller, null, throwable);
	}

	/**
	 * Log a message together with a throwable with log level <i>debug</i>. The
	 * reference to the calling object is used to automatically generate more
	 * detailled log4j messages.
	 * 
	 * @param caller
	 *            The calling object.
	 * @param message
	 *            The log message.
	 * @param throwable
	 *            The throwable.
	 */
	public void debug(final Object caller, final String message,
			final Throwable throwable) {
	    getLogger(caller).debug(message, throwable);
	}

	/**
	 * Log a message with log level <i>warn</i>. The reference to the calling
	 * object is used to automatically generate more detailled log4j messages.
	 * 
	 * @param caller
	 *            The calling object.
	 * @param message
	 *            The log message.
	 */
	public void warn(final Object caller, final String message) {
	    getLogger(caller).warn(message);
	}

	/**
	 * Log a throwable with log level <i>warn</i>. The reference to the calling
	 * object is used to automatically generate more detailled log4j messages.
	 * 
	 * @param caller
	 *            The calling object.
	 * @param throwable
	 *            The throwable.
	 */
	public void warn(final Object caller, final Throwable throwable) {
		warn(caller, null, throwable);
	}

	/**
	 * Log a message together with a throwable with log level <i>warn</i>. The
	 * reference to the calling object is used to automatically generate more
	 * detailled log4j messages.
	 * 
	 * @param caller
	 *            The calling object.
	 * @param message
	 *            The log message.
	 * @param throwable
	 *            The throwable.
	 */
	public void warn(final Object caller, final String message,
			final Throwable throwable) {
	    getLogger(caller).warn(message, throwable);
	}

	/**
	 * Log a message with log level <i>error</i>. The reference to the calling
	 * object is used to automatically generate more detailled log4j messages.
	 * 
	 * @param caller
	 *            The calling object.
	 * @param message
	 *            The log message.
	 */
	public void error(final Object caller, final String message) {
	    getLogger(caller).error(message);
	}

	/**
	 * Log a throwable with log level <i>error</i>. The reference to the
	 * calling object is used to automatically generate more detailled log4j
	 * messages.
	 * 
	 * @param caller
	 *            The calling object.
	 * @param throwable
	 *            The throwable.
	 */
	public void error(final Object caller, final Throwable throwable) {
		error(caller, null, throwable);
	}

	/**
	 * Log a message together with a throwable with log level <i>error</i>. The
	 * reference to the calling object is used to automatically generate more
	 * detailled log4j messages.
	 * 
	 * @param caller
	 *            The calling object.
	 * @param message
	 *            The log message.
	 * @param throwable
	 *            The throwable.
	 */
	public void error(final Object caller, final String message,
			final Throwable throwable) {
        getLogger(caller).error(message, throwable);
	}

	/**
	 * Log a message with log level <i>fatal</i>. The reference to the calling
	 * object is used to automatically generate more detailled log4j messages.
	 * 
	 * @param caller
	 *            The calling object.
	 * @param message
	 *            The log message.
	 */
	public void fatal(final Object caller, final String message) {
	    getLogger(caller).fatal(message);
	}

	/**
	 * Log a throwable with log level <i>fatal</i>. The reference to the
	 * calling object is used to automatically generate more detailled log4j
	 * messages.
	 * 
	 * @param caller
	 *            The calling object.
	 * @param throwable
	 *            The throwable.
	 */
	public void fatal(final Object caller, final Throwable throwable) {
		fatal(caller, null, throwable);
	}

	/**
	 * Log a message together with a throwable with log level <i>fatal</i>. The
	 * reference to the calling object is used to automatically generate more
	 * detailled log4j messages.
	 * 
	 * @param caller
	 *            The calling object.
	 * @param message
	 *            The log message.
	 * @param throwable
	 *            The throwable.
	 */
	public void fatal(final Object caller, final String message,
			final Throwable throwable) {
	    getLogger(caller).fatal(message, throwable);
	}

	/**
	 * Create the log4j properts object from the given preference store.
	 * 
	 * @param prefs
	 *            Source preference store.
	 * @return The log4j properts object from the given preference store.
	 */
	private Properties createLog4jProperties(
			final org.eclipse.core.runtime.Preferences prefs) {
		Properties result = new Properties();

		// console logger
		fillFromStore(result, prefs, PROP_LOG4J_CONSOLE_APPENDER);
		fillFromStore(result, prefs, PROP_LOG4J_CONSOLE_LAYOUT);
		fillFromStore(result, prefs, PROP_LOG4J_CONSOLE_PATTERN);
		fillFromStore(result, prefs, PROP_LOG4J_CONSOLE_THRESHOLD);

		// file logger
		fillFromStore(result, prefs, PROP_LOG4J_FILE_APPENDER);
		fillFromStore(result, prefs, PROP_LOG4J_FILE_LAYOUT);
		fillFromStore(result, prefs, PROP_LOG4J_FILE_PATTERN);
		fillFromStore(result, prefs, PROP_LOG4J_FILE_THRESHOLD);
		fillFromStore(result, prefs, PROP_LOG4J_FILE_DESTINATION);
		fillFromStore(result, prefs, PROP_LOG4J_FILE_APPEND);
		fillFromStore(result, prefs, PROP_LOG4J_FILE_MAX_SIZE);
		fillFromStore(result, prefs, PROP_LOG4J_FILE_MAX_INDEX);

		// JMS logger
		fillFromStore(result, prefs, PROP_LOG4J_JMS_APPENDER);
		fillFromStore(result, prefs, PROP_LOG4J_JMS_THRESHOLD);
		fillFromStore(result, prefs, PROP_LOG4J_JMS_LAYOUT);
		fillFromStore(result, prefs, PROP_LOG4J_JMS_PATTERN);
		fillFromStore(result, prefs, PROP_LOG4J_JMS_URL);
		fillFromStore(result, prefs, PROP_LOG4J_JMS_ICFN);
		fillFromStore(result, prefs, PROP_LOG4J_JMS_TOPIC);
		fillFromStore(result, prefs, PROP_LOG4J_JMS_USER);
		fillFromStore(result, prefs, PROP_LOG4J_JMS_PASSWORD);
		fillFromStore(result, prefs, PROP_LOG4J_JMS_TCFBN);

		// create the log4j root property
		String rootProperty = "debug"; //$NON-NLS-1$
		if (prefs.getBoolean(PROP_LOG4J_CONSOLE)) {
			rootProperty += "," + "css_console"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (prefs.getBoolean(PROP_LOG4J_FILE)) {
			rootProperty += "," + "css_file"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		if ((prefs.getBoolean(PROP_LOG4J_JMS) && checkJmsSettings(result))) {
			rootProperty += "," + "css_jms"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		result.setProperty("log4j.rootLogger", rootProperty); //$NON-NLS-1$

		return result;
	}

	/**
	 * Fill the given properties object (java.util) with a certain property that
	 * is read from the given plugin preference store
	 * (org.eclipse.core.runtime.Preferences).
	 * 
	 * @param p
	 *            Properties object to fill.
	 * @param prefs
	 *            Plugin preference store.
	 * @param propertyID
	 *            The ID of the certain property.
	 */
	private void fillFromStore(final Properties p,
			final org.eclipse.core.runtime.Preferences prefs,
			final String propertyID) {
		p.setProperty(propertyID, prefs.getString(propertyID));
	}

	/**
	 * Check if the given JMS settings are valid.
	 * 
	 * @param p
	 *            System properties.
	 * @return True, if the JMS settings are valid.
	 */
	private boolean checkJmsSettings(final Properties p) {
		boolean result = true;

		try {
			p.put(Context.INITIAL_CONTEXT_FACTORY, p
					.getProperty(PROP_LOG4J_JMS_ICFN));
			p.put(Context.PROVIDER_URL, p.getProperty(PROP_LOG4J_JMS_URL));

			Context context = new InitialContext(p);
			ConnectionFactory factory = (ConnectionFactory) context
					.lookup("ConnectionFactory"); //$NON-NLS-1$
			factory.createConnection();
		} catch (NamingException e) {
			result = false;
		} catch (JMSException e) {
			result = false;
		} finally {
			p.remove(p.getProperty(PROP_LOG4J_JMS_ICFN));
			p.remove(p.getProperty(PROP_LOG4J_JMS_URL));
		}

		return result;
	}
}
