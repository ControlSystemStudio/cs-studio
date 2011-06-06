
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.tine2jms;

import java.util.Observable;
import java.util.Observer;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.tine2jms.management.Restart;
import org.csstudio.tine2jms.management.Stop;
import org.csstudio.tine2jms.preferences.PreferenceKeys;
import org.csstudio.tine2jms.util.JmsProducer;
import org.csstudio.tine2jms.util.JmsProducerException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 *
 */
public class TineToJmsApplication implements IApplication, Stoppable, Observer, IGenericServiceListener<ISessionService>
{
    /** Common logger of CSS */
    private static final Logger LOG = LoggerFactory.getLogger(TineToJmsApplication.class);
    
    /** Alarm monitor */
    private TineAlarmMonitor[] alarmMonitor = null;
    
    /** JMS producer */
    private JmsProducer producer = null;
    
    /** Array of alarm systems we want to monitor */
    private String[] facilities = null;
    
    /** Flag that indicates wheather or not the application should be stopped */
    private boolean working;

    /** Flag that indicates wheather or not the application should be restarted */
    private boolean restart;

    public TineToJmsApplication()
    {
        String jmsUrl = null;
        String jmsClientId = null;
        String jmsTopics = null;
        
        IPreferencesService preference = Platform.getPreferencesService();
        
        jmsUrl = preference.getString(TineToJmsActivator.PLUGIN_ID, PreferenceKeys.JMS_PROVIDER_URL, "", null);
        jmsClientId = preference.getString(TineToJmsActivator.PLUGIN_ID, PreferenceKeys.JMS_CLIENT_ID, "", null);
        jmsTopics = preference.getString(TineToJmsActivator.PLUGIN_ID, PreferenceKeys.JMS_TOPICS_ALARM, "", null);

        working = true;
        restart = false;
        
        try
        {
            producer = new JmsProducer(jmsClientId, jmsUrl, jmsTopics);
        }
        catch(JmsProducerException jpe)
        {
            LOG.error("Cannot instantiate class JmsProducer.", jpe);
            producer = null;
            working = false;
        }
    }
    
    /**
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    @Override
    public Object start(IApplicationContext context) throws Exception
    {
        IPreferencesService preference = Platform.getPreferencesService();

        int result = IApplication.EXIT_OK;
        
        PreferenceKeys.showPreferences();

        // Prepare the stop and restart action objects
        Stop.staticInject(this);
        Restart.staticInject(this);
        
        // Wait until some time for XMPP login
        synchronized(this)
        {
            try
            {
                wait(5000);
            }
            catch(InterruptedException ie) {}
        }
        
        facilities = preference.getString(TineToJmsActivator.PLUGIN_ID, PreferenceKeys.TINE_FACILITY_NAMES, "", null).split(",");
        if(facilities == null)
        {
            LOG.error("No alarm system / facility is defined.");
            
            return IApplication.EXIT_OK;
        }
        
        alarmMonitor = new TineAlarmMonitor[facilities.length];
        
        for(int i = 0;i < facilities.length;i++)
        {
            alarmMonitor[i] = new TineAlarmMonitor(this, facilities[i]);
        }

        while(working)
        {
            synchronized(this)
            {
                LOG.debug("Waiting for alarms...\n");
                
                try
                {
                    this.wait();
                }
                catch(InterruptedException e) {}                
            }
        }
        
        for(int i = 0;i < facilities.length;i++)
        {
            alarmMonitor[i].close();
        }
        
        if(restart)
        {
            result = IApplication.EXIT_RESTART;
        }
        
        return result;
    }


    @Override
    public synchronized void update(Observable messageCreator, Object obj)
    {
        MapMessage msg = null;
        
        AlarmMessage alarm = (AlarmMessage)obj;
        
        try
        {
            msg = producer.createMapMessages(alarm);
            producer.sendMessage(msg);
        }
        catch(JMSException jmse)
        {
            LOG.error("Cannot create MapMessage object.");
        }
        catch(JmsProducerException jpe)
        {
            LOG.error("Cannot send MapMessage object.");
        }
    }

    @Override
    public synchronized void stopWorking()
    {
        LOG.info("Tine2Jms gets a stop request.");
        working = false;
        restart = false;
        this.notify();
    }

    @Override
    public synchronized void setRestart()
    {
        LOG.info("Tine2Jms gets a restart request.");
        working = false;
        restart = true;
        this.notify();
    }

    /**
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    @Override
    public void stop()
    {
        LOG.info("Method stop() was called...");
    } 
    
    @Override
    public void bindService(ISessionService sessionService) {
        IPreferencesService preference = Platform.getPreferencesService();

        String xmppUser = preference.getString(TineToJmsActivator.PLUGIN_ID, PreferenceKeys.XMPP_USER, "", null);
        String xmppPassword = preference.getString(TineToJmsActivator.PLUGIN_ID, PreferenceKeys.XMPP_PASSWORD, "", null);
        String xmppServer = preference.getString(TineToJmsActivator.PLUGIN_ID, PreferenceKeys.XMPP_SERVER, "", null);
    	
    	try {
			sessionService.connect(xmppUser, xmppPassword, xmppServer);
		} catch (Exception e) {
			CentralLogger.getInstance().warn("XMPP connection is not available, " + e.toString());
		}
    }
    
    @Override
    public void unbindService(ISessionService service) {
    	service.disconnect();
    }
}
