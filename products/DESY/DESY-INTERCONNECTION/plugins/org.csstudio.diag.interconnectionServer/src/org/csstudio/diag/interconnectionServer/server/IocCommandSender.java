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
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.naming.NamingException;

import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.TagList;
import org.csstudio.diag.interconnectionServer.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;

/**
 * The runnable which actually is sending the command.
 *
 * @author Matthias Clausen
 *
 */
public class IocCommandSender implements Runnable {

	private String hostName = "iocNodeName";
	private InetAddress iocInetAddress = null;
	private int port = 0;
	private String command = "NONE";
	private int id = 0;
	private static String IOC_NOT_REACHABLE = "IOC_NOT_REACHABLE";
	private final int statusMessageDelay = 0;
	private boolean retry = false;
	private final IocConnection iocConnection;

	/**
	 * Send a command to the IOC in an independent thread from command thread pool.
	 * @param hostName IOC name.
	 * @param port Port to be used.
	 * @param command One of the supported commands.
	 */
	public IocCommandSender ( final InetAddress iocInetAddress, final int port, final String command) {

		this.id = InterconnectionServer.getInstance().getSendCommandId();
		this.iocInetAddress = iocInetAddress;
		this.port = port;
		this.command = command;

		// XXX: This has to use the data port from the preferences because that
		// is (hopefully) the port from which the IOC sends messages and under
		// which it is stored in the IocConnectionManager. This should be
		// refactored and the IocConnection object passed to this object instead
		// of this object having to search in in this way.
		final int dataPort = Integer.parseInt(Platform.getPreferencesService().getString(Activator.getDefault().getPluginId(),
				PreferenceConstants.DATA_PORT_NUMBER, "", null));
		try {
            iocConnection = IocConnectionManager.INSTANCE.getIocConnection(iocInetAddress, dataPort);
        } catch (final NamingException e) {
            CentralLogger.getInstance().fatal(this, "LDAP name could not be composed");
            throw new IllegalArgumentException("LDAP name could not be composed", e);
        }

		if (this.hostName == null || this.hostName.equals(""))  {
			// FIXME: This is not sufficient for error handling. The command
			// will still be runnable! Throw an exception instead.
			CentralLogger.getInstance().fatal(this, "Wrong HostName! Host: " + hostName);
			throw new IllegalArgumentException("Wrong HostName! Host: " + hostName);
		}
	}

	/**
	 * Send a command to the IOC in an independent thread from command thread pool.
	 * @param connection the IOC connection
	 * @param command The command to be sent
	 * @param retry TRUE if it's a retry
	 */
	public IocCommandSender (final IocConnection connection, final String command, final boolean retry) {
		this.id = InterconnectionServer.getInstance().getSendCommandId();
		this.retry = retry;
		this.command = command;

		this.hostName = connection.getHost();
		this.iocInetAddress = connection.getIocInetAddress();
		this.port = connection.getPort();
		this.iocConnection = connection;
	}

	public void run() {
		byte[] preparedMessage = null;
		final byte[] buffer	=  new byte[1024];
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		String answer = null;
		String answerMessage = null;

		preparedMessage = prepareMessage ( command, id);

		/*
		 * in case we have to send the 'get all alarms' to the IOC
		 * give us some time for the IOC to send the 'real' alarms first
		 * wait some time and send the status messages afterwards
		 */

		if ( this.command != null && this.command.equals(PreferenceProperties.COMMAND_SEND_ALL_ALARMS)) {

            // FIXME (jpenning, bknerr) : obsolete LDAP access stuff

//			if ( (Engine.getInstance().getWriteVector().size() >=0) && (100*Engine.getInstance().getWriteVector().size() < PreferenceProperties.MAX_WAIT_UNTIL_SEND_ALL_ALARMS)) {
//				statusMessageDelay = 100*Engine.getInstance().getWriteVector().size() + (int)((new GregorianCalendar().getTimeInMillis())%10000);
//			} else {
//				statusMessageDelay = PreferenceProperties.MAX_WAIT_UNTIL_SEND_ALL_ALARMS + (int)((new GregorianCalendar().getTimeInMillis())%10000);	// ~ 5 minutes + random
//			}

			CentralLogger.getInstance().info(this, "Waiting " + statusMessageDelay + " until sending " + PreferenceProperties.COMMAND_SEND_ALL_ALARMS + " to the IOC " + iocConnection.getLogicalIocName() + " (" + hostName+ ")");
			try {
				Thread.sleep( statusMessageDelay);
			} catch (final InterruptedException e) {
			}
		}

		try
		{
			socket = new DatagramSocket( );	// do NOT specify the port

			final DatagramPacket newPacket = new DatagramPacket(preparedMessage, preparedMessage.length, iocInetAddress, PreferenceProperties.COMMAND_PORT_NUMBER);

			CentralLogger.getInstance().debug(this, "Sending packet to host: " + hostName + "; packet: " + new String(preparedMessage));
			socket.send(newPacket);

			try {
				/*
				 * set timeout period to TIME_TO_GET_ANSWER_FROM_IOC_AFTER_COMMAND seconds
				 */
				socket.setSoTimeout( PreferenceProperties.TIME_TO_GET_ANSWER_FROM_IOC_AFTER_COMMAND);

				packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
			} catch (final InterruptedIOException ioe) {
				/*
				 * error handling:
				 * set answer message and use normal handling of state
				 */
				answerMessage = IOC_NOT_REACHABLE;
				/*
				 * create log message
				 * depending on whether this is a retry - or not
				 */
				if ( this.retry) {
					CentralLogger.getInstance().warn(this, "Retry-Timeout (" + PreferenceProperties.TIME_TO_GET_ANSWER_FROM_IOC_AFTER_COMMAND + ") mS  sending command: " + command + " to IOC: " +
							iocConnection.getLogicalIocName());
				} else {
					CentralLogger.getInstance().info(this, "Timeout (" + PreferenceProperties.TIME_TO_GET_ANSWER_FROM_IOC_AFTER_COMMAND + ") mS  sending command: " + command + " to IOC: " +
							iocConnection.getLogicalIocName());
				}

				/*
				 * retry to send the command just ONCE!
				 */
				if (!this.retry) {
					final IocCommandSender sendCommandToIoc = new IocCommandSender(iocConnection, this.command, true);
					InterconnectionServer.getInstance().getCommandExecutor().execute(sendCommandToIoc);
					CentralLogger.getInstance().info(this, "Retry to send command: " + command + " to IOC: " +
							iocConnection.getLogicalIocName());
				}
			}

			if ( answerMessage == null) {
				/*
				 * check answer
				 * for now we only check for the string 'DONE'
				 */
				answerMessage = new String(packet.getData(), 0, packet.getLength());
				CentralLogger.getInstance().debug(this, "Received answer from host: " + hostName + "; packet: " + answerMessage);

				/*
				 * check whether this message contains the mandatory string: REPLY
				 * if not it's not a valid answer
				 */
				if ( answerMessage.contains("REPLY")) {
					answer = answerMessage.substring( answerMessage.indexOf("REPLY=")+6, answerMessage.length()-2);
				} else {
					answer = "invalid answer from IOC";
				}
			}

			if ( TagList.getReplyType(answer) == TagList.REPLY_TYPE_DONE) {
				CentralLogger.getInstance().info(this, "Command accepted by IOC: " + iocConnection.getLogicalIocName() + " (" + hostName+ ")" + " command: " + command);
			} else if (TagList.getReplyType(answer) == TagList.REPLY_TYPE_SELECTED) {
				/*
				 * did the select state change?
				 */
				if (!iocConnection.isSelectState()) {
					//remember we're selected
					iocConnection.setSelectState(true);
					//create log message
					CentralLogger.getInstance().warn(this, "IOC SELECTED this InterConnectionServer: " + iocConnection.getLogicalIocName() + " (" + hostName+ ")");
					/*
					 * OK - we are selected - so:
					 * just in case we previously set the channel to disconnected - we'll have to update all alarm states
					 * -> trigger the IOC to send all alarms!
					 */
					if (iocConnection.isDidWeSetAllChannelToDisconnect()) {
						/*
						 * yes - did set all channel to disconnect
						 * we'll have to get all alarm-states from the IOC
						 */
					    try {
					        final IocCommandSender sendCommandToIoc = new IocCommandSender( iocInetAddress, port, PreferenceProperties.COMMAND_SEND_ALL_ALARMS);
					        InterconnectionServer.getInstance().getCommandExecutor().execute(sendCommandToIoc);
					        iocConnection.setGetAllAlarmsOnSelectChange(false);	// we set the trigger to get the alarms...
					        iocConnection.setDidWeSetAllChannelToDisconnect(false);
					        CentralLogger.getInstance().info(this, "IOC Connected and selected again - previously channels were set to disconnect - get an update on all alarms!");
					    } catch (final IllegalArgumentException e) {
					        CentralLogger.getInstance().fatal(this, "Creation of command sender failed:\n" + e.getMessage());
					    }
					}
					/*
					 * send JMS message - we are selected
					 */
					/*
					 * get host name of interconnection server
					 */
					final String localHostName = InterconnectionServer.getInstance().getLocalHostName();
//					try {
//						java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
//						localHostName = localMachine.getHostName();
//					}
//					catch (java.net.UnknownHostException uhe) {
//					}
					String selectMessage = "SELECTED";
					if (iocConnection.wasPreviousBeaconWithinThreeBeaconTimeouts()) {
						selectMessage = "SELECTED - switch over";
					}
					JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
							JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
							localHostName + ":" + iocConnection.getLogicalIocName() + ":selectState",					// name
							localHostName, 														// value
							JmsMessage.SEVERITY_NO_ALARM, 										// severity
							selectMessage, 														// status
							hostName, 															// host
							null, 																// facility
							"virtual channel");
					// send command to IOC - get ALL alarm states

					/*
					 * if we received beacons within the last three beacon timeout periods we 'probably' did not loose any messages
					 * this is a switch over from one IC-Server to another and thus
					 * we DO NOT have to ask for an update on all alarms!
					 *
					 * Else if:
					 * - wasPreviousBeaconWithinThreeBeaconTimeouts is FALSE  (enough is the new beacon type is used)
					 * - or (areWeConnectedLongerThenThreeBeaconTimeouts is FALSE) AND (isGetAllAlarmsOnSelectChange() is TRUE)
					 * ==> take action -> send all alarms from IOC
					 */
					if ( !iocConnection.wasPreviousBeaconWithinThreeBeaconTimeouts() ||
							!iocConnection.areWeConnectedLongerThenThreeBeaconTimeouts() &&
									iocConnection.isGetAllAlarmsOnSelectChange() )  {
						final IocCommandSender sendCommandToIoc = new IocCommandSender( iocInetAddress, port, PreferenceProperties.COMMAND_SEND_ALL_ALARMS);
						InterconnectionServer.getInstance().getCommandExecutor().execute(sendCommandToIoc);
						iocConnection.setGetAllAlarmsOnSelectChange(false); // one time is enough
						CentralLogger.getInstance().info(this, "This is a fail over from one IC-Server to this one - get an update on all alarms!");
					} else {
						CentralLogger.getInstance().info(this, "Just a switch over from one IC-Server to this one - no need to get an update on all alarms!");
					}
				}
			} else if (TagList.getReplyType(answer) == TagList.REPLY_TYPE_NOT_SELECTED) {
				/*
				 * we are not selected any more
				 * in case we were selected before - we'll have to create a JMS message
				 */

				if (iocConnection.isSelectState()) {
					//create log message
					CentralLogger.getInstance().warn(this, "IOC DE-selected this InterConnectionServer: " + iocConnection.getLogicalIocName() + " (" + hostName+ ")");
					/*
					 * send JMS message - we are NOT selected
					 */
					/*
					 * get host name of interconnection server
					 */
					final String localHostName = InterconnectionServer.getInstance().getLocalHostName();
//					try {
//						java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
//						localHostName = localMachine.getHostName();
//					}
//					catch (java.net.UnknownHostException uhe) {
//					}
					JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
							JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
							localHostName + ":" + iocConnection.getLogicalIocName() + ":selectState",					// name
							localHostName, 														// value
							JmsMessage.SEVERITY_MINOR, 											// severity
							"NOT-SELECTED", 													// status
							hostName, 															// host
							null, 																// facility
							"virtual channel");
					}
					//remember we're not selected any more
					iocConnection.setSelectState(false);
			} else if ( answerMessage != null && answerMessage.equals(IOC_NOT_REACHABLE)) {
				/*
				 * we cannot reach the IOC
				 * in case we were selected before - we'll have to create a JMS message
				 */

				if (iocConnection.isSelectState()) {
					//create log message
					CentralLogger.getInstance().warn(this, "IOC not reachable by this InterConnectionServer: " + iocConnection.getLogicalIocName() + " (" + hostName+ ")");
					/*
					 * send JMS message - we are NOT selected
					 */
					/*
					 * get host name of interconnection server
					 */
					final String localHostName = InterconnectionServer.getInstance().getLocalHostName();
//					try {
//						java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
//						localHostName = localMachine.getHostName();
//					}
//					catch (java.net.UnknownHostException uhe) {
//					}
					JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
							JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
							localHostName + ":" + iocConnection.getLogicalIocName() + ":selectState",					// name
							localHostName, 														// value
							JmsMessage.SEVERITY_MINOR, 											// severity
							"NOT-SELECTED", 													// status
							hostName, 															// host
							null, 																// facility
							"virtual channel");
				}
				//remember we're not selected any more
				iocConnection.setSelectState(false);
			} else {
				/*
				 * FIXME: The execution also goes here if the REPLY is "ok",
				 * which means that the server didn't have to do anything
				 * because it already was in the requested state. In that case,
				 * a different log message should be generated.
				 */
				CentralLogger.getInstance().info(this, "Command not accepted by IOC: " + iocConnection.getLogicalIocName() + " (" + hostName+ ")" + " command: " + command + " answer: " + answer);
			}
		}
		catch ( /* UnknownHostException is a */ final IOException e )
		{
			e.printStackTrace();
		}
		finally
		{
			if ( socket != null ) {
                socket.close();
            }
		}
	}

	private byte[] prepareMessage ( final String command, final int id) {
		String message = null;

		message = "COMMAND=" + command + ";" + "ID=" + id + ";";
		message = message + "\0";
		return message.getBytes();
	}
}
