package org.csstudio.diag.interconnectionServer.server;

import org.csstudio.servicelocator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Thread which generates alarm messages
 * By default it's running @ 1000ms (or 1 sec)
 * The name of the Channel is defines in channelPrefix.
 *
 * @author Matthias Clausen
 *
 */
public class AlarmSimulatorThread extends Thread{

    private static final Logger LOG = LoggerFactory.getLogger(AlarmSimulatorThread.class);

	private int	scanTime = 100;
	private boolean isRunning = true;
	private static String channelPrefix = "AlarmSimulator";

	/**
	 * Stating the AlarmSimulatorThread with the default scanTime of 1000ms.
	 */
	AlarmSimulatorThread(){
		this.start();
	}

	/**
	 * Stating the AlarmSimulatorThread with your own scanTime.
	 * @param scanTime
	 * If scanTime is < 100ms it will be set to 100ms.
	 */
	AlarmSimulatorThread ( int scanTime) {
	    this.scanTime = scanTime;
		if (this.scanTime < 100) {
		    this.scanTime = 100;
		}
		this.start();
	}

	/**
	 * get started
	 * stop the loop by setting isRunning to false.
	 */
	@Override
    public void run() {
		int counter5 = 0;
		int counter10 = 0;
		int counter100 = 0;

		String severity1 = JmsMessage.SEVERITY_NO_ALARM;
		String severity5 = JmsMessage.SEVERITY_NO_ALARM;
		String severity10 = JmsMessage.SEVERITY_NO_ALARM;
		String severity100 = JmsMessage.SEVERITY_NO_ALARM;

		//get local host name

		final String localHostName = ServiceLocator.getService(IInterconnectionServer.class).getLocalHostName();

//		try {
//			java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
//			localHostName = localMachine.getHostName();
//		}
//		catch (java.net.UnknownHostException uhe) {
//		}

		// tell logging - we are running
		LOG.info("AlarmSimulator started on: " + localHostName);
		LOG.info("The AlarmSimulator does NOT write to LDAP! -> avoid traffic!");

		while (isRunning()) {
			/*
			 * wait
			 */
			try {
				Thread.sleep( this.scanTime);
			} catch (final InterruptedException e) {
				// TODO: handle exception
			}
			/*
			 * do it every scanTime time here
			 */
			if ( severity1.equals(JmsMessage.SEVERITY_NO_ALARM)) {
				severity1 = JmsMessage.SEVERITY_MINOR;
			} else if ( severity1.equals(JmsMessage.SEVERITY_MINOR)) {
				severity1 = JmsMessage.SEVERITY_MAJOR;
			} else {
				severity1 = JmsMessage.SEVERITY_NO_ALARM;
			}
			JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
					JmsMessage.MESSAGE_TYPE_SIMULATOR, 						// type
					channelPrefix + ":1",											// name
					localHostName, 											// value
					severity1, 												// severity
					"SIMULATED", 											// status
					localHostName, 											// host
					"Alarm-Simulator", 										// facility
					"virtual channel");
			/*
			 * counter10 handling
			 */
			if (counter5++ > 5){
				counter5 = 0;
				/*
				 * do things @ a rate of scanTime/5
				 */
				if ( severity5.equals(JmsMessage.SEVERITY_NO_ALARM)) {
					severity5 = JmsMessage.SEVERITY_MINOR;
				} else if ( severity5.equals(JmsMessage.SEVERITY_MINOR)) {
					severity5 = JmsMessage.SEVERITY_MAJOR;
				} else {
					severity5 = JmsMessage.SEVERITY_NO_ALARM;
				}
				JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
						JmsMessage.MESSAGE_TYPE_SIMULATOR, 						// type
						channelPrefix + ":5",											// name
						localHostName, 											// value
						severity5, 											// severity
						"SIMULATED", 											// status
						localHostName, 											// host
						"Alarm-Simulator", 										// facility
						"virtual channel");
			}

			/*
			 * counter10 handling
			 */
			if (counter10++ > 10){
				counter10 = 0;
				/*
				 * do things @ a rate of scanTime/10
				 */
				if ( severity10.equals(JmsMessage.SEVERITY_NO_ALARM)) {
					severity10 = JmsMessage.SEVERITY_MINOR;
				} else if ( severity10.equals(JmsMessage.SEVERITY_MINOR)) {
					severity10 = JmsMessage.SEVERITY_MAJOR;
				} else {
					severity10 = JmsMessage.SEVERITY_NO_ALARM;
				}
				JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
						JmsMessage.MESSAGE_TYPE_SIMULATOR, 						// type
						channelPrefix + ":10",											// name
						localHostName, 											// value
						severity10, 											// severity
						"SIMULATED", 											// status
						localHostName, 											// host
						"Alarm-Simulator", 										// facility
						"virtual channel");
			}

			/*
			 * counter100 handling
			 */
			if (counter100++ > 100){
				counter100 = 0;
				/*
				 * do things @ a rate of scanTime/100
				 */
				if ( severity100.equals(JmsMessage.SEVERITY_NO_ALARM)) {
					severity100 = JmsMessage.SEVERITY_MINOR;
				} else if ( severity100.equals(JmsMessage.SEVERITY_MINOR)) {
					severity100 = JmsMessage.SEVERITY_MAJOR;
				} else {
					severity100 = JmsMessage.SEVERITY_NO_ALARM;
				}
				JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
						JmsMessage.MESSAGE_TYPE_SIMULATOR, 						// type
						channelPrefix + ":100",											// name
						localHostName, 											// value
						severity100, 											// severity
						"SIMULATED", 											// status
						localHostName, 											// host
						"Alarm-Simulator", 										// facility
						"virtual channel");
			}

		}
		// tell logging - we stopped
		LOG.info("AlarmSimulator stopped on: " + localHostName);
	}

	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * change the running state.
	 * @param isRunning
	 */
	public void setRunning(final boolean isRunning) {
		this.isRunning = isRunning;
	}

	/**
	 * what does the channel prefix look like.
	 * @return current channel prefix.
	 */
	public static String getChannelPrefix() {
		return channelPrefix;
	}

	/**
	 * set channel prefix for simulated alarms.
	 * @param channelPrefix
	 */
	public static void setChannelPrefix(final String channelPrefix) {
		AlarmSimulatorThread.channelPrefix = channelPrefix;
	}

}
