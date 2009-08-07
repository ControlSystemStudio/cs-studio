
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
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.tine2jms.management.Restart;
import org.csstudio.tine2jms.management.Stop;
import org.csstudio.tine2jms.util.JmsProducer;
import org.csstudio.tine2jms.util.JmsProducerException;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.servicelauncher.ServiceLauncher;
import org.remotercp.ecf.ECFConstants;
import org.remotercp.login.connection.HeadlessConnection;

/**
 * @author Markus Moeller
 *
 */
public class TineToJmsApplication implements IApplication, Stoppable, Observer
{
    /** Configuration for the application */
    private PropertiesConfiguration configuration = null;
    
    /** Common logger of CSS */
    private Logger logger = null;
    
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
        logger = CentralLogger.getInstance().getLogger(TineToJmsApplication.class);

        configuration = TineToJmsActivator.getDefault().getConfiguration();
        logger.debug("provider.url: " + configuration.getString("provider.url"));
        logger.debug("client.id: " + configuration.getString("client.id"));
        logger.debug("topic.alarm: " + configuration.getString("topic.alarm"));
        logger.debug("facility.name: " + configuration.getString("facility.name"));
        logger.debug("xmpp.user: " + configuration.getString("xmpp.user"));

        working = true;
        restart = false;
        
        try
        {
            producer = new JmsProducer(configuration.getString("client.id"), configuration.getString("provider.url"), configuration.getString("topic.alarm"));
        }
        catch(JmsProducerException jpe)
        {
            logger.error("Cannot instantiate class JmsProducer.", jpe);
            producer = null;
            working = false;
        }
    }
    
    /**
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    public Object start(IApplicationContext context) throws Exception
    {
        int result = IApplication.EXIT_OK;
        
        logger.info("Starting...");
        
        // Prepare the stop and restart action objects
        Stop.staticInject(this);
        Restart.staticInject(this);
        
        connectToXMPPServer();
        
        // Wait until some time for XMPP login
        synchronized(this)
        {
            try
            {
                wait(5000);
            }
            catch(InterruptedException ie) {}
        }
        
        facilities = configuration.getStringArray("facility.name");
        if(facilities == null)
        {
            logger.error("No alarm system / facility is defined.");
            
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
                logger.debug("Waiting for alarms...\n");
                
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

    public void connectToXMPPServer()
    {
        String xmppUserName = TineToJmsActivator.getDefault().getConfiguration().getString("xmpp.user", "tine2jms");
        String xmppPassword = TineToJmsActivator.getDefault().getConfiguration().getString("xmpp.password", "tine2jms");
        String xmppServer = TineToJmsActivator.getDefault().getConfiguration().getString("xmpp.server", "krykxmpp.desy.de");

        try
        {
            HeadlessConnection.connect(xmppUserName, xmppPassword, xmppServer, ECFConstants.XMPP);
            ServiceLauncher.startRemoteServices();     
        }
        catch(Exception e)
        {
            CentralLogger.getInstance().warn(this, "Could not connect to XMPP server: " + e.getMessage());
        }
    }

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
            logger.error("Cannot create MapMessage object.");
        }
        catch(JmsProducerException jpe)
        {
            logger.error("Cannot send MapMessage object.");
        }
    }

    public synchronized void stopWorking()
    {
        logger.info("Tine2Jms gets a stop request.");
        working = false;
        restart = false;
        this.notify();
    }

    public synchronized void setRestart()
    {
        logger.info("Tine2Jms gets a stop request.");
        working = false;
        restart = true;
        this.notify();
    }

    /**
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    public void stop()
    {
        logger.info("Method stop() was called...");
    }
}
