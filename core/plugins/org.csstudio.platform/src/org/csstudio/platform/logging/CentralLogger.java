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

import java.util.Enumeration;
import java.util.Properties;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggerRepository;
import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.security.SecureStorage;

/**
 * The central logging service of the CSS platform, based on Log4j.
 * The service is implemented as singleton. A reference can be obtained by
 * <pre>
 *  CentralLogger.getInstance()
 * </pre>
 *
 * Logging is straight forward:
 * <pre>
 *  final Logger log = CentralLogger.getInstance().getLogger(this);
 *  log.debug("test log message");
 *  log.info("test log message");
 * </pre>
 * Eclipse plugin log messages will also be forwarded to this Log4j log,
 * so existing code using the plugin log will continue to function.
 * <p>
 * <u>General Idea for using the log levels:</u>
 * <ul>
 * <li><b>Fatal:</b> Error that allows nothing more than exiting the application.
 *     No way to bring up a dialog box or other means to tell the user
 *     what is happening.
 * <li><b>Error:</b> Ran into error like "Cannot open file"
 *     which certainly impacts the user, but it can be handled
 *     by for example displaying the error in a dialog box
 *     without stopping all of CSS.
 * <li><b>Warn:</b> For example an exception in <code>close()</code>.
 *     A system expert should look at this, but the user
 *     won't really notice anything.
 * <li><b>Info:</b> Application "start" messages from CSS, Interconnection Server,
 *     maybe "User Fred authenticated".
 * <li><b>Debug:</b> Plugin start/stop, PV connected, PV received sample,
 *     SDS display "abc" opened, ...
 * </ul>
 * It would be nice to have multiple debug levels, but there's
 * only one. Log4j itself allows fine-grained configuration based on
 * the message creator (name of class behind the <code>this</code>
 * in the above <code>....getLogger(this)</code>), but the CSS
 * logging configuration via Eclipse preferences currently only
 * allows a global log level selection.
 * <p>
 * Alternatively, the following style is still supported, but with the drawback
 * that the source file info of the log calls will not reflect where your code
 * actually invoked the logger. Instead, they will show where 'debug', 'info'
 * etc. are defined inside the CentralLogger.
 * <pre>
 *  CentralLogger.getInstance().debug(this, "test log message");
 *  CentralLogger.getInstance().info(this, "test log message");
 * </pre>
 * @author Alexander Will, Sven Wende, Kay Kasemir
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
		
	    // TODO: Just a small hack to avoid that the CentralLogger
	    //       overwrites an existing logging configuration
	    
	    // Check if there are some configured logger
	    LoggerRepository repo = LogManager.getLoggerRepository();
	    Enumeration<?> l = repo.getCurrentLoggers();
	    Enumeration<?> c = repo.getCurrentCategories();
	    if (l.hasMoreElements() || c.hasMoreElements()) {
	        // If we find some logger, DO NOT configure the CentralLogger
	        return;
	    }
	    
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
        if (caller == null) {
            return Logger.getRootLogger();
        }
        return Logger.getLogger(caller.getClass());
    }

    /**
     * Obtain a logger for the given class name (for static classes).
     * @param className Calling className, must not be <code>null</code>.
     * @return A Log4j <code>Logger</code>.
     */
    public Logger getLogger(final String className) {
        return Logger.getLogger(className);
    }

    public Logger getLogger(final Class<?> clazz) {
        return Logger.getLogger(clazz.getCanonicalName());
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

	/** Log levels, ordered from 'almost all' to 'only severe errors' */
	final private static String LOG_LEVELS[] = new String[] { "debug", "info", "warn", "error", "fatal"};

	/**
	 * Create the log4j properts object from the given preference store.
	 *
	 * @param prefs
	 *            Source preference store.
	 * @return The log4j properts object from the given preference store.
	 */
	@SuppressWarnings("deprecation")
    private Properties createLog4jProperties(
			final org.eclipse.core.runtime.Preferences prefs) {
		final Properties result = new Properties();
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
		fillFromStore(result, prefs, PROP_LOG4J_JMS_TOPIC);
		fillFromSecureStorage(result, PROP_LOG4J_JMS_USER);
		fillFromSecureStorage(result, PROP_LOG4J_JMS_PASSWORD);

		// Maximize the 'threshold' of console, file and JMS appender
		// and use that as the root logger level.
		// This way, if nobody uses "debug", the debug level
		// is suppressed at the root and logger.isDebugEnabled()
		// can be used as intended to avoid debug message formatting
        String rootProperty = LOG_LEVELS[LOG_LEVELS.length-1];
        final boolean use_console = prefs.getBoolean(PROP_LOG4J_CONSOLE);
        final boolean use_file = prefs.getBoolean(PROP_LOG4J_FILE);
        final boolean use_jms = prefs.getBoolean(PROP_LOG4J_JMS);
		final String console_threshold = prefs.getString(PROP_LOG4J_CONSOLE_THRESHOLD);
        final String file_threshold = prefs.getString(PROP_LOG4J_FILE_THRESHOLD);
        final String jms_threshold = prefs.getString(PROP_LOG4J_JMS_THRESHOLD);
        for (int i=0; i < LOG_LEVELS.length; ++i)
        {
            if ((use_console && LOG_LEVELS[i].equalsIgnoreCase(console_threshold)) ||
                (use_file    && LOG_LEVELS[i].equalsIgnoreCase(file_threshold))    ||
                (use_jms     && LOG_LEVELS[i].equalsIgnoreCase(jms_threshold)))
            {
                rootProperty = LOG_LEVELS[i];
                break;
            }
        }
        // create the log4j root property:
        // level-of-root-logger, appender1, appender2, appender3
        if (use_console) {
            rootProperty += "," + "css_console"; //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (use_file) {
            rootProperty += "," + "css_file"; //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (use_jms) {
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
	 * Fill the given properties object (java.util) with a certain property that
	 * is read from secure storage
	 * (org.eclipse.core.runtime.Preferences).
	 *
	 * @param p
	 *            Properties object to fill.

	 * @param propertyID
	 *            The ID of the certain property.
	 */
	private void fillFromSecureStorage(final Properties p,
			final String propertyID) {
		p.setProperty(propertyID, SecureStorage.retrieveSecureStorage(CSSPlatformPlugin.getDefault().getBundle()
							.getSymbolicName(), propertyID));
	}
}