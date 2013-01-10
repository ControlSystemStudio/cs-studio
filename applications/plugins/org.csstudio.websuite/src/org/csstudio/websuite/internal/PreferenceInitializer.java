
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.websuite.internal;

import org.csstudio.websuite.WebSuiteActivator;
import org.csstudio.websuite.internal.PreferenceConstants;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Markus Moeller
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences()
    {
        IEclipsePreferences prefs = new DefaultScope().getNode(WebSuiteActivator.PLUGIN_ID);

        prefs.put(PreferenceConstants.XMPP_USER_NAME, "anonymous");
        prefs.put(PreferenceConstants.XMPP_PASSWORD, "anonymous");
        prefs.put(PreferenceConstants.XMPP_SERVER, "xmppserver.where.ever");
        prefs.put(PreferenceConstants.JETTY_PORT, "8080");

        // Database settings
        prefs.put(PreferenceConstants.DATABASE_URL, "NONE");
        prefs.put(PreferenceConstants.DATABASE_USER, "NONE");
        prefs.put(PreferenceConstants.DATABASE_PASSWORD, "NONE");
        
        // AMS database settings
        prefs.put(PreferenceConstants.AMS_DATABASE_TYPE, "");
        prefs.put(PreferenceConstants.AMS_DATABASE_URL, "");
        prefs.put(PreferenceConstants.AMS_DATABASE_USER, "");
        prefs.put(PreferenceConstants.AMS_DATABASE_PASSWORD, "");

        prefs.put(PreferenceConstants.ENABLE_AMS_SERVLET, "false");
        
        prefs.put(PreferenceConstants.DEFAULT_TOPIC_SET, "ALARM");
        prefs.put(PreferenceConstants.HTML_SERVLET_ADDRESS, "/AlarmViewer");
        prefs.put(PreferenceConstants.ACTIVATE_HTML_SERVLET, "true");
        prefs.put(PreferenceConstants.XML_SERVLET_ADDRESS, "/AlarmViewerXml");
        prefs.put(PreferenceConstants.ACTIVATE_XML_SERVLET, "true");
        prefs.put(PreferenceConstants.XML_CHANNEL_SERVLET_ADDRESS, "/RecordViewerXml");
        prefs.put(PreferenceConstants.ACTIVATE_CHANNEL_XML_SERVLET, "true");
        prefs.put(PreferenceConstants.HTML_CHANNEL_SERVLET_ADDRESS, "/RecordViewer");
        prefs.put(PreferenceConstants.ACTIVATE_CHANNEL_HTML_SERVLET, "true");
        
        prefs.put(PreferenceConstants.HOST_NAME, "localhost");
        prefs.put(PreferenceConstants.EXTERN_HOST_NAME, "localhost");
        prefs.put(PreferenceConstants.EXTERN_HOST_PORT, "8080");
        
        prefs.put(PreferenceConstants.EPICS_WEB_APP, "http://localhost:8080/epics/EpicsCa");
        prefs.put(PreferenceConstants.AAPI_WEB_APP, "http://localhost:8080/AAPI-web/archivereader.jsp");

        prefs.put(PreferenceConstants.COLUMN_0, "0");
        prefs.put(PreferenceConstants.CHANNEL_COLUMN_0, "0");
        prefs.put(PreferenceConstants.KEY_0, "MAJOR");
        prefs.put(PreferenceConstants.KEY_1, "MINOR");
        prefs.put(PreferenceConstants.KEY_2, "NO_ALARM");
        prefs.put(PreferenceConstants.KEY_3, "INVALID");
        prefs.put(PreferenceConstants.KEY_4, "4");
        prefs.put(PreferenceConstants.KEY_5, "FATAL");
        prefs.put(PreferenceConstants.KEY_6, "ERROR");
        prefs.put(PreferenceConstants.KEY_7, "WARN");
        prefs.put(PreferenceConstants.KEY_8, "INFO");
        prefs.put(PreferenceConstants.KEY_9, "DEBUG");
        prefs.put(PreferenceConstants.VALUE_0, "MAJOR");
        prefs.put(PreferenceConstants.VALUE_1, "MINOR");
        prefs.put(PreferenceConstants.VALUE_2, "NO_ALARM");
        prefs.put(PreferenceConstants.VALUE_3, "INVALID");
        prefs.put(PreferenceConstants.VALUE_4, "NOT DEFINED");
        prefs.put(PreferenceConstants.VALUE_5, "FATAL");
        prefs.put(PreferenceConstants.VALUE_6, "ERROR");
        prefs.put(PreferenceConstants.VALUE_7, "WARNING");
        prefs.put(PreferenceConstants.VALUE_8, "INFO");
        prefs.put(PreferenceConstants.VALUE_9, "DEBUG");
    }
}
