
/*
 * Copyright 1999-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.csstudio.platform.logging;

import java.util.Calendar;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.csstudio.platform.CSSPlatformInfo;
import org.csstudio.platform.internal.PlatformPreferencesInitializer;

/** Log4j appender that publishes events to a JMS Topic as described
 *  in <code>JMSLogMessage</code>.
 *
 *  The configuration of the appender is somewhat convoluted:
 *  Eclipse Preferences are read on startup, or later
 *  modified from preference pages.
 *  CentralLogger.configure() is invoked on startup or from the CSS
 *  JMS preference page onOK(), which in in turn creates properties
 *  with the following names for the Log4j PropertyConfigurator,
 *  which then uses Java Bean get/set calls to configure this class:
 *  <pre>
 *    log4j.appender.css_jms.Threshold
 *    log4j.appender.css_jms.layout
 *    log4j.appender.css_jms.layout.ConversionPattern
 *    log4j.appender.css_jms.providerURL
 *    log4j.appender.css_jms.topicBindingName
 *    log4j.appender.css_jms.userName
 *    log4j.appender.css_jms.password
 *  </pre>
 *
 *  @author Ceki G&uuml;lc&uuml;: Original version
 *  @author Markus Moeller: Changes for CSS
 *  @author Kay Kasemir: Using JMSLogThread
 *
 *  @see PlatformPreferencesInitializer for defaults
 *  @see JMSLogMessage for message format
 */
public class CSSJmsAppender extends AppenderSkeleton
{
    /** JMS server URL */
    private String url;

    /** JMS queue topic */
    private String topic;

    /** Unused user name */
    private String user_name;

    /** Unused password */
    private String password;

    /** */
    private String tcfBindingName;

    /** Thread that performs the actual logging.
     *  When parameters change, a new/different thread will be created.
     *  <p>
     *  NOTE: Synchronize on <code>this</code> when accessing!
     */
    private JMSLogThread log_thread = null;

    /** @return JMS server URL */
    public String getProviderURL()
    {
        return url;
    }

    /** @param url JMS server URL */
    public void setProviderURL(final String url)
    {
        this.url = url.trim();
    }

    /**
     * The <b>TopicConnectionFactoryBindingName</b> option takes a
     * string value. Its value will be used to lookup the appropriate
     * <code>TopicConnectionFactory</code> from the JNDI context.
     */
    public void setTopicConnectionFactoryBindingName(String tcfBindingName) {
        this.tcfBindingName = tcfBindingName;
    }
  
    /**
     * Returns the value of the <b>TopicConnectionFactoryBindingName</b> option.
     */
    public String getTopicConnectionFactoryBindingName() {
        return tcfBindingName;
    }

    /** @returns JMS topic used for logging */
    public String getTopicBindingName()
    {
        return topic;
    }

    /** @param topic JMS topic used for logging */
    public void setTopicBindingName(final String topic)
    {
        this.topic = topic.trim();
    }

    /** @returns JMS user name */
    public String getUserName()
    {
        return user_name;
    }

    /** @param user JMS user name */
    public void setUserName(final String user)
    {
        this.user_name = user.trim();
    }

    /** @returns JMS password */
    public String getPassword()
    {
        return password;
    }

    /** @param password JMS user name */
    public void setPassword(final String password)
    {
        this.password = password.trim();
    }

    /** Options are activated and become effective only after calling
     *  this method.
     */
    @SuppressWarnings("nls")
    @Override
    public void activateOptions()
    {
        if (url == null)
        {
            LogLog.error(name + " no URL");
            return;
        }
        if (topic == null)
        {
            LogLog.error(name + " no topic");
            return;
        }
        synchronized (this)
        {
            if (log_thread != null)
            {   // Ask to cancel, but can't wait for that to actually happen
                log_thread.cancel();
                log_thread = null;
            }
            log_thread = new JMSLogThread(url, topic, user_name, password);
            log_thread.start();
        }
        LogLog.debug(name + " activated for '" + topic
                + "' on " + url);
    }

    /**
     * Close this JMSAppender. Closing releases all resources used by the
     * appender. A closed appender cannot be re-opened.
     */
    @SuppressWarnings("nls")
    public void close()
    {
        closed = true;
        synchronized (this)
        {
            if (log_thread != null)
            {
                log_thread.cancel();
                log_thread = null;
            }
        }
        LogLog.debug(name + " closed.");
    }

    /** This method called by {@link AppenderSkeleton#doAppend} method to
     *  do most of the real appending work.
     */
    @Override
    public void append(final LoggingEvent event)
    {
        final String text = layout.format(event).trim();

        final String severity = event.getLevel().toString();

        final Calendar event_time = Calendar.getInstance();
        event_time.setTimeInMillis(event.timeStamp);

        final Calendar create_time = Calendar.getInstance();

        String clazz = null;
        String method = null;
        String file = null;
        final LocationInfo location = event.getLocationInformation();
        if(location != null)
        {
            clazz = location.getClassName();
            method = location.getMethodName();
            file = location.getFileName();
        }

        String app = null;
        String host = null;
        String user = null;
        final CSSPlatformInfo pinfo = CSSPlatformInfo.getInstance();
        if(pinfo != null)
        {
            app = pinfo.getApplicationId();
            host = pinfo.getHostId();
            user = pinfo.getUserId();
        }

        final JMSLogMessage log_msg = new JMSLogMessage(text, severity,
                create_time, event_time,
                clazz, method, file, app, host, user);
        synchronized (this)
        {
            if (log_thread == null) {
                errorHandler.error(name + " not configured."); //$NON-NLS-1$
            } else {
                log_thread.addMessage(log_msg);
            }
        }
    }

    /** The JMSAppender for CSS sends requires a layout. */
    public boolean requiresLayout()
    {
        return true;
    }

    /** String representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return String.format("Log4j appender '%s': '%s' @ '%s'\n",
                name, topic, url);
    }
}
