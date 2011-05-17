package org.csstudio.diag.interconnectionServer.server;
/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton,
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

import java.util.Enumeration;

import org.csstudio.platform.logging.CentralLogger;

/**
 * Checking beacon frequency for each IOC connection
 *
 * @author Matthias Clausen
 *
 */
public class BeaconWatchdog extends Thread {
	private int checkDisabledIntervalMultiplier = 10;
	private int	checkInterval = 1000; // ms
	private boolean isRunning = true;

	/**
	 * @param checkInterval
	 *            The time between two checks for IOCs which are offline.
	 * @param checkDisabledIntervalMultiplier
	 *            The multiplier of checkInterval for checks of disabled IOCs.
	 */
	BeaconWatchdog(final int checkInterval, final int checkDisabledIntervalMultiplier) {
		this.checkInterval = checkInterval;
		this.checkDisabledIntervalMultiplier = checkDisabledIntervalMultiplier;
		CentralLogger.getInstance().info(this, "Starting new beaconWatchdog @ " + checkInterval + " ms");
		this.start();
	}

	@Override
    public void run() {

		int count = 0;
		while ( isRunning) {

			checkBeaconTimeout();

			if (++count % checkDisabledIntervalMultiplier == 0) {
				count = 0;
				checkForDisabledIOCs();
			}

			/*
			 * wait
			 */
			try {
				Thread.sleep( this.checkInterval);

			} catch (final InterruptedException e) {
				// Ok, if interrupted it will take place more early
			}
			finally {
				//clean up
			}
		}

	}

	/**
	 *
	 */
	private void checkForDisabledIOCs() {
		final Enumeration<IocConnection> connections = IocConnectionManager.INSTANCE._connectionList.elements();
		while (connections.hasMoreElements()) {
			final IocConnection connection = connections.nextElement();
			if (connection.isDisabled()) {
				CentralLogger.getInstance().warn(this, "IOC is disabled: " + connection.getHost());
				JmsMessage.INSTANCE.sendMessage(
						JmsMessage.JMS_MESSAGE_TYPE_LOG,					// topic
						JmsMessage.MESSAGE_TYPE_LOG,     					// type
						connection.getLogicalIocName() + ":disabledState",	// name
						"",						 							// value
						JmsMessage.SEVERITY_NO_ALARM,			 			// severity
						"", 												// status
						connection.getHost(),		 						// host
						null,			 									// facility
						"IOC is disabled: " + connection.getLogicalIocName());  // text
			}
		}
	}

	/**
	 * Processing actions if a timeout occurred.
	 */
	private void checkBeaconTimeout () {

		final Enumeration<IocConnection> connections = IocConnectionManager.INSTANCE._connectionList.elements();
		 while (connections.hasMoreElements()) {
			final IocConnection connection = connections.nextElement();
			if (connection.isTimeoutError()) {

				 /*
				  * if we come here the first time...
				  */
				 if ( connection.getConnectState()) {
					 /*
					  * ok we're disconnected - remember
					  */
					 connection.setConnectState( false);	// not connected

					 CentralLogger.getInstance().warn(this, "InterconnectionServer: Beacon timeout for Host: " + connection.getHost() + "|" + connection.getLogicalIocName());
					 /*
					  * do the changed state stuff in a new thread
					  * ... but only if this InterconnectionServer is the selected one (from IOC point of view)
					  */
					  if ( connection.isSelectState()) {
						  CentralLogger.getInstance().warn(this, "InterconnectionServer: trigger IOC timeout actions");
						  new IocChangedState (connection, false);
					  }

					  /*
					   * change also the select state - the IOC can't tell us any more ...
					   */
					  connection.setSelectState( false);	// not selected
					  connection.setGetAllAlarmsOnSelectChange(true);	// the next time we get connected
				 }
			}
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
