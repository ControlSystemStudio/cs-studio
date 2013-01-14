package org.csstudio.alarm.syslog2jms;


import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.csstudio.platform.utility.jms.JmsSimpleProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

	/**
	 * send a JMS message every beaconInterval
	 *
	 * @author Matthias Clausen
	 *
	 */

	public class JmsHeartBeat extends Thread {
		private int	beaconInterval = 30000; // ms - 30 seconds
		private boolean isRunning = true;
	    /** The JMS producer / publisher */
	    private JmsSimpleProducer jmsProducer;
	    private String localHostName = "";
	    
	    /** The class logger */
	    private static final Logger LOG = LoggerFactory.getLogger(Syslog2JmsApplication.class);

	    
		/**
		 * @param checkInterval
		 *            The time between two checks for IOCs which are offline.
		 */
		JmsHeartBeat(final int beaconInterval, JmsSimpleProducer jmsProducer, String localHostName) {
			this.beaconInterval = beaconInterval;
			this.jmsProducer = jmsProducer;
			this.localHostName = localHostName;
			LOG.info("Starting JmsBeacon for @" + beaconInterval + " ms");
			this.start();
		}

		@Override
	    public void run() {
			int counter = 0;
			
			MapMessage message = null;

			while ( isRunning) {

				// Simple example to show how to create and send a JMS message
			    
				try {
					message = jmsProducer.createMapMessage();
				
				    if (message != null) {
				        message.setString("TYPE", "beacon");
				        message.setString("EVENTTIME", jmsProducer.getCurrentDateAsString());
				        message.setString("STATUS", "NO_ALARM");
				        message.setString("SEVERITY", "NO_ALARM");
				        message.setString("TEXT", "Syslog2Jms ALIVE");
				        message.setString("APPLICATION-ID", "Syslog2Jms");
				        message.setString("HOST", localHostName);
				        message.setString("VALUE", ""+counter++);
				        
				        jmsProducer.sendMessage(message);
				        
				    }
				} catch (javax.jms.IllegalStateException e1) {
					// session is invalid or closed - create a new one!
					e1.printStackTrace();
					LOG.info("JMS session invalid or closed");
					// this.jmsProducer = Syslog2JmsApplication.initJmsBeacon();
				}
				catch (JMSException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				/*
				 * wait
				 */
				try {
					Thread.sleep( this.beaconInterval);

				} catch (final InterruptedException e) {
					// Ok, if interrupted it will take place more early
				}
				finally {
					//clean up
				}
			}
			
			try {
				message = jmsProducer.createMapMessage();
			
			    if (message != null) {
			        message.setString("TYPE", "beacon");
			        message.setString("EVENTTIME", jmsProducer.getCurrentDateAsString());
			        message.setString("STATUS", "NO_ALARM");
			        message.setString("SEVERITY", "MINOR");
			        message.setString("TEXT", "Syslog2Jms STOP");
			        message.setString("APPLICATION-ID", "Syslog2Jms");
			        message.setString("HOST", localHostName);
			        message.setString("VALUE", ""+counter++);
			        
			        jmsProducer.sendMessage(message);
			        
			    }
			} catch (JMSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		/**
		 *
		 * @param isRunning
		 */
		public void setRunning(final boolean isRunning) {
			this.isRunning = isRunning;
		}
	
	
}
