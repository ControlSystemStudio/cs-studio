
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

package org.csstudio.ams.connector.jms;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.platform.utility.jms.JmsRedundantProducer;
import org.csstudio.platform.utility.jms.JmsRedundantReceiver;
import org.csstudio.platform.utility.jms.JmsRedundantProducer.ProducerId;
import org.eclipse.jface.preference.IPreferenceStore;

public class JMSConnectorWork extends Thread implements AmsConstants
{
	private static final int TIMEOUT_AFTER_JMS_INIT = 100;

	private static final String AMS_SUBSCRIBER_JMS_ID = "amsSubscriberJms";

	private JMSConnectorStart jcs = null;
	private JmsRedundantReceiver messageReceiver = null;
	private JmsRedundantProducer messageProducer = null;
	private ProducerId messageProducerId;
    private boolean bStop = false;
    private boolean bStoppedClean = false;

    public JMSConnectorWork(JMSConnectorStart app) {
		this.jcs = app;
	}

    /**
     * Sets the boolean variable that controlls the main loop to true
     */
    public synchronized void stopWorking()
    {
        bStop = true;
    }
    
    /**
     * Returns the shutdown state.
     * 
     * @return True, if the shutdown have occured clean otherwise false
     */
    public boolean stoppedClean()
    {
        return bStoppedClean;
    }

	private boolean acknowledge(Message msg) {
		try {
			msg.acknowledge();
			return true;
		} catch (Exception e) {
			Log.log(this, Log.FATAL, "could not acknowledge", e);
		}
		return false;
	}

	public void closeJms() {
		Log.log(this, Log.INFO, "exiting internal jms communication");

		if (messageReceiver != null) {
			messageReceiver.closeAll();
		}

		if (messageProducer != null) {
			messageProducer.closeAll();
		}

		Log.log(this, Log.INFO, "jms internal communication closed");
	}

	private boolean initJms() {

		boolean result = false;

		try {
			IPreferenceStore storeAct = AmsActivator.getDefault().getPreferenceStore();

			boolean durable = Boolean.parseBoolean(storeAct.getString(AmsPreferenceKey.P_JMS_AMS_CREATE_DURABLE));

			
			String url1 = storeAct.getString(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1);
			String url2 = storeAct.getString(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2);
			messageReceiver = new JmsRedundantReceiver(
					"JMSConnectorWorkReceiverInternal",
					url1,
					url2);

			if (!messageReceiver.isConnected()) {
				Log.log(this, Log.FATAL, "could not create amsReceiver on url1: " + url1 + " and url2: " + url2);
				return false;
			}

			result = messageReceiver
					.createRedundantSubscriber(
							AMS_SUBSCRIBER_JMS_ID,
							storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_JMS_CONNECTOR),
							storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TSUB_JMS_CONNECTOR),
							durable);
			if (result == false) {
				Log.log(this, Log.FATAL, "could not create "
						+ AMS_SUBSCRIBER_JMS_ID);
				return false;
			}

			String[] urls = new String[1];
			urls[0] = url1;
			try {
				messageProducer = new JmsRedundantProducer(
						"JMSConnectorWorkProducerInternal", urls);
			} catch(Exception e)
			{
				// fallback
				urls[0] = url2;
				messageProducer = new JmsRedundantProducer(
						"JMSConnectorWorkProducerInternal", urls);
			}
			

			messageProducerId = messageProducer.createProducer(null);

			return true;
		} catch (Exception e) {
			Log.log(this, Log.FATAL, "could not init internal Jms", e);
			// XXX Exception should be thrown to caller...
		}
		return false;
	}

	@Override
    public void run() {
		boolean isJMSInitialized = false;
		int iErr = JMSConnectorStart.STAT_OK;
		Log.log(this, Log.INFO, "start jms connector work");
        bStop = false;
        
        while(bStop == false) {
			try {

				if (!isJMSInitialized) {
					isJMSInitialized = initJms();
					if (!isJMSInitialized) {
						iErr = JMSConnectorStart.STAT_ERR_JMS_CONNECTION_FAILED;
						jcs.setStatus(iErr); // set it for not overwriting
						// with next error
					}
				}

				sleep(TIMEOUT_AFTER_JMS_INIT);

				if (isJMSInitialized) {
					iErr = JMSConnectorStart.STAT_OK;
					if (jcs.getStatus() == JMSConnectorStart.STAT_INIT)
						jcs.setStatus(JMSConnectorStart.STAT_OK);

					Log.log(this, Log.DEBUG, "runs");

					Message message = null;
					try {
						message = messageReceiver
								.receive(AMS_SUBSCRIBER_JMS_ID);
					} catch (Exception e) {
						Log.log(this, Log.FATAL,
								"could not receive from internal jms", e);
						iErr = JMSConnectorStart.STAT_ERR_JMS_CONNECTION_FAILED;
					}
					if (message != null) {
						Log
								.log(Log.INFO,
										"JMSConnectorWork.run(): Recieved a message, now trying to send...");
						iErr = sendJMSMsg(message);
					}

					if (iErr == JMSConnectorStart.STAT_ERR_JMS_SEND) {
						closeJms(); // recover msg
						isJMSInitialized = false;
					}
					if (iErr == JMSConnectorStart.STAT_ERR_JMS_CONNECTION_FAILED) {
						closeJms();
						isJMSInitialized = false;
					}
				}

				// set status in every loop
				jcs.setStatus(iErr); // set error status, can be OK if no
				// error
			} catch (Exception e) {
				jcs.setStatus(JMSConnectorStart.STAT_ERR_UNKNOWN);
				Log.log(this, Log.FATAL, e);

				closeJms();
				isJMSInitialized = false;
			}
		} // while
        
        closeJms();
        bStoppedClean = true;
        
        Log.log(this, Log.INFO, "JMSConnectorWork exited");
	}

	/**
	 * Sends the message to the topic specified in Message-Property
	 * MSGPROP_RECEIVERADDR.
	 * 
	 * @param message
	 *            The Message, have to be a MapMessage, not null.
	 * @return true if successfully sent, false otherwise.
	 */
	private boolean sendJMSMessageIntern(Message message)
	{
		assert message != null : "Precondition unresolved: message != null";

		MapMessage mmsg = null;
		HashMap<String, String> map = null;
		boolean result = true;
		
		// The message have to be a MapMessage object
		if(!(message instanceof MapMessage))
		{
		    Log.log(Log.WARN, "The message is NOT a MapMessage object.");
		    
		    // We handle a non MapMessage object as NO ERROR
		    return result;
		}
		
		mmsg = (MapMessage)message;
		
		try
		{
		    // Get the name of the destination topic
	        String topicName = mmsg.getString(AmsConstants.MSGPROP_RECEIVERADDR);

	        // First we have to check if the message contains the "raw" alarm message
		    if(mmsg.itemExists(MSGPROP_EXTENDED_MESSAGE))
		    {
		        // Get the complete content
		        map = this.getMessageContent(mmsg);
	            
		        if(map != null)
		        {
		            if(!map.isEmpty())
		            {
		                mmsg = null;
		                mmsg = messageProducer.createMapMessage();
		                
		                // We have to delete all AMS related keys
		                String key;
		                Iterator<String> keys = map.keySet().iterator();
		                while(keys.hasNext())
		                {
		                    key = keys.next().trim();
		                    
		                    if(!key.startsWith(AMS_PREFIX))
		                    {
		                        mmsg.setString(key, map.get(key));
		                    }
		                }
		                
		                key = null;
		                keys = null;
		            }
		        }
		    }
		    
			messageProducer.send(messageProducerId, topicName, mmsg);
			
			mmsg = null;
			
			Log.log(Log.INFO, "JMSConnectorWork.sendJMSMessageIntern(): Message succesfully sent to topic \""
							+ topicName + "\"");
		}
		catch(ClassCastException ce)
		{
			Log.log(Log.INFO, "Probably invalid message type", ce);
			result = false;
		}
		catch(JMSException je)
		{
			Log.log(Log.INFO, "JMS-Failure", je);
			result = false;
		}
		catch(RuntimeException e)
		{
			Log.log(Log.INFO, "JMS-Failure", e);
			result = false;
		}
		
		return result;
	}

	private int sendJMSMsg(Message message) throws Exception
	{
		int iErr = JMSConnectorStart.STAT_ERR_UNKNOWN;

		for (int j = 1; j <= 5; j++) // only for short net breaks
		{
			Log.log(Log.INFO, "JMSConnectorWork.sendJMSMsg(): Try to send message, attemp number: " + j);
			if(sendJMSMessageIntern(message))
			{
                // deletes all received messages of
                // the session
				if(acknowledge(message))
				{
					return JMSConnectorStart.STAT_OK;
				}
				
				iErr = JMSConnectorStart.STAT_ERR_JMS_CONNECTION_FAILED;
			}
			else
			{
				iErr = JMSConnectorStart.STAT_ERR_JMS_SEND;
			}

			sleep(2000);
		}

		return iErr;
	}
	
    private HashMap<String, String> getMessageContent(MapMessage message)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        String key = null;
        
        try
        {
            Enumeration<?> list = message.getMapNames();
            while(list.hasMoreElements())
            {
                key = (String)list.nextElement();
                map.put(key, message.getString(key));
            }
        }
        catch(JMSException jmse)
        {
            map.clear();
        }
        
        return map;
    }
}
