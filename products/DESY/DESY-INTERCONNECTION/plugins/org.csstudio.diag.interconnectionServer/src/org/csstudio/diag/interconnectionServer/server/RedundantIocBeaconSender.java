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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.diag.interconnectionServer.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;

/**
 * The runnable which actually is sending the command.
 *
 * @author Matthias Clausen
 * @version 1.0
 *
 */
public class RedundantIocBeaconSender implements Runnable {

	private int iocBroadcastPortNumber = 0;
	private String iocBroadcastAddressString = null;
	private InetAddress iocBroadcastAddress = null;
	private boolean running = true;
	private String IcsHostName = null;
	private int broadcastCycleTime = 500;

	/**
	 * Send a command to the IOC in an independent thread from command thread pool.
	 * @param hostName IOC name.
	 * @param port Port to be used.
	 * @param command One of the supported commands.
	 */
	public RedundantIocBeaconSender () {

		iocBroadcastPortNumber = Integer.parseInt(Platform.getPreferencesService().getString(Activator.getDefault().getPluginId(),
				PreferenceConstants.IOC_BROADCAST_PORT_NUMBER, "", null));

		iocBroadcastAddressString = Platform.getPreferencesService().getString(Activator.getDefault().getPluginId(),
				PreferenceConstants.IOC_BROADCAST_ADDRESS, "", null);

		broadcastCycleTime = Integer.parseInt(Platform.getPreferencesService().getString(Activator.getDefault().getPluginId(),
				PreferenceConstants.IOC_BROADCAST_CYCLE_TIME, "", null));

		try {
			iocBroadcastAddress = InetAddress.getByName( iocBroadcastAddressString);
		} catch (final UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IcsHostName = InterconnectionServer.getInstance().getLocalHostName();

//		this.run();
	}



	public void run() {
		byte[] preparedMessage = null;
		DatagramPacket newPacket = null;
		DatagramSocket socket = null;

		preparedMessage = prepareMessage ();

		CentralLogger.getInstance().debug(this, "Start RedundantIocBeaconSender on " + IcsHostName + " with " + iocBroadcastAddressString + " on " + iocBroadcastPortNumber + " @ " + broadcastCycleTime);

		try
		{
			socket = new DatagramSocket( );	// do NOT specify the port
				newPacket = new DatagramPacket(preparedMessage, preparedMessage.length, iocBroadcastAddress, iocBroadcastPortNumber);

		}
		catch ( /* UnknownHostException is a */ final IOException e )
		{
			e.printStackTrace();
			CentralLogger.getInstance().debug(this, "RedundantIocBeaconSender on " + IcsHostName + " could not create socket");
		}

		while (running) {

			try
			{
				socket.send(newPacket);
			}
			catch ( /* UnknownHostException is a */ final IOException e )
			{
				e.printStackTrace();
			}
//			finally
//			{
//				if ( socket != null ) {
//	                socket.close();
//	            }
//			}
			try {
				Thread.sleep( broadcastCycleTime);
			} catch (final InterruptedException e) {
			}
		}
		if ( socket != null ) {
            socket.close();
        }
		CentralLogger.getInstance().debug(this, "RedundantIocBeaconSender stopped on " + IcsHostName);
	}

	private byte[] prepareMessage () {
		String message = null;

		message = "BEACON ICS " + IcsHostName + " hello IOC";
		message = message + "\0";
		return message.getBytes();
	}

	public void stopRedundanIocBeaconServer() {
		CentralLogger.getInstance().debug(this, "Stop RedundantIocBeaconSender on " + IcsHostName);
		running = false;
		return;
	}
}
