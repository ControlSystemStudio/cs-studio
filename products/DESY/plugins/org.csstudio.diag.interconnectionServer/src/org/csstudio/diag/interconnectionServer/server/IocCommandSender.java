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

import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.TagList;
import org.csstudio.diag.interconnectionServer.preferences.PreferenceConstants;
import org.csstudio.servicelocator.ServiceLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The runnable which actually is sending the command.
 *
 * @author Matthias Clausen
 *
 */
public class IocCommandSender implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(IocCommandSender.class);

	private String command = "NONE";
	private int id = 0;
	private static String IOC_NOT_REACHABLE = "IOC_NOT_REACHABLE";
	private final int statusMessageDelay = 0;
	private boolean retry = false;
	private final IocConnection iocConnection;

	/**
	 * Send a command to the IOC in an independent thread from command thread pool.
	 * This is marked as a retry and only used internally.
	 * @param connection the IOC connection
	 * @param command The command to be sent
	 * @param retry true if this is a retry
	 */
	private IocCommandSender (final IocConnection connection, final String command, final boolean retry) {
		this.id = ServiceLocator.getService(IInterconnectionServer.class).nextSendCommandId();
		this.retry = retry;
		this.command = command;
		this.iocConnection = connection;
	}
	
	
	private String getHostname() {
	    return iocConnection.getNames().getHostName();
	}

    /**
     * Send a command to the IOC in an independent thread from command thread pool.
     * @param connection the IOC connection (used to maintain its state)
     * @param command the command to be sent
     */
    public IocCommandSender(final IocConnection connection, final String command) {
        this(connection, command, false);
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

            LOG.info("Waiting " + statusMessageDelay + " until sending "
                    + PreferenceProperties.COMMAND_SEND_ALL_ALARMS + " to the IOC "
                    + iocConnection.getNames().getLogicalIocName() + " (" + getHostname() + ")");
			try {
				Thread.sleep( statusMessageDelay);
			} catch (final InterruptedException e) {
			    // do not care
			}
		}

		try
		{
			socket = new DatagramSocket( );	// do NOT specify the port

			final DatagramPacket newPacket = new DatagramPacket(preparedMessage, preparedMessage.length, iocConnection.getInetAddress(), commandPort());

			LOG.debug("Sending packet to host: " + getHostname() + "; packet: " + new String(preparedMessage));
			socket.send(newPacket);

			IInterconnectionServer interconnectionServer = ServiceLocator.getService(IInterconnectionServer.class);
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
                if (this.retry) {
                    LOG.warn("Retry-Timeout ("
                            + PreferenceProperties.TIME_TO_GET_ANSWER_FROM_IOC_AFTER_COMMAND
                            + ") mS  sending command: " + command + " to IOC: "
                            + iocConnection.getNames().getLogicalIocName());
                } else {
                    LOG.info("Timeout ("
                            + PreferenceProperties.TIME_TO_GET_ANSWER_FROM_IOC_AFTER_COMMAND
                            + ") mS  sending command: " + command + " to IOC: "
                            + iocConnection.getNames().getLogicalIocName());
                }

				/*
				 * retry to send the command just ONCE!
				 */
				if (!this.retry) {
					final IocCommandSender sendCommandToIoc = new IocCommandSender(iocConnection, this.command, true);
					interconnectionServer.getCommandExecutor().execute(sendCommandToIoc);
					LOG.info("Retry to send command: " + command + " to IOC: " +
							iocConnection.getNames().getLogicalIocName());
				}
			}

			if ( answerMessage == null) {
				/*
				 * check answer
				 * for now we only check for the string 'DONE'
				 */
				answerMessage = new String(packet.getData(), 0, packet.getLength());
				LOG.debug("Received answer from host: " + getHostname() + "; packet: " + answerMessage);

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
				LOG.info("Command accepted by IOC: " + iocConnection.getNames().getLogicalIocName() + " (" + getHostname()+ ")" + " command: " + command);
			} else if (TagList.getReplyType(answer) == TagList.REPLY_TYPE_SELECTED) {
				/*
				 * did the select state change?
				 */
				if (!iocConnection.isSelectState()) {
					//remember we're selected
					iocConnection.setSelectState(true);
					//create log message
					LOG.warn("IOC SELECTED this InterConnectionServer: " + iocConnection.getNames().getLogicalIocName() + " (" + getHostname()+ ")");
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
					        final IocCommandSender sendCommandToIoc = new IocCommandSender(iocConnection, PreferenceProperties.COMMAND_SEND_ALL_ALARMS);
					        interconnectionServer.getCommandExecutor().execute(sendCommandToIoc);
					        iocConnection.setGetAllAlarmsOnSelectChange(false);	// we set the trigger to get the alarms...
					        iocConnection.setDidWeSetAllChannelToDisconnect(false);
					        LOG.info("IOC Connected and selected again - previously channels were set to disconnect - get an update on all alarms!");
					    } catch (final IllegalArgumentException e) {
					        LOG.error("Creation of command sender failed:\n" + e.getMessage());
					    }
					}
					/*
					 * send JMS message - we are selected
					 */
					final String localHostName = interconnectionServer.getLocalHostName();
					String selectMessage = "SELECTED";
					if (iocConnection.wasPreviousBeaconWithinThreeBeaconTimeouts()) {
						selectMessage = "SELECTED - switch over";
					}
					JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
							JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
							localHostName + ":" + iocConnection.getNames().getLogicalIocName() + ":selectState",					// name
							localHostName, 														// value
							JmsMessage.SEVERITY_NO_ALARM, 										// severity
							selectMessage, 														// status
							getHostname(), 															// host
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
						final IocCommandSender sendCommandToIoc = new IocCommandSender(iocConnection, PreferenceProperties.COMMAND_SEND_ALL_ALARMS);
						interconnectionServer.getCommandExecutor().execute(sendCommandToIoc);
						iocConnection.setGetAllAlarmsOnSelectChange(false); // one time is enough
						LOG.info("This is a fail over from one IC-Server to this one - get an update on all alarms!");
					} else {
						LOG.info("Just a switch over from one IC-Server to this one - no need to get an update on all alarms!");
					}
				}
			} else if (TagList.getReplyType(answer) == TagList.REPLY_TYPE_NOT_SELECTED) {
				/*
				 * we are not selected any more
				 * in case we were selected before - we'll have to create a JMS message
				 */

				if (iocConnection.isSelectState()) {
					//create log message
					LOG.warn("IOC DE-selected this InterConnectionServer: " + iocConnection.getNames().getLogicalIocName() + " (" + getHostname()+ ")");
					/*
					 * send JMS message - we are NOT selected
					 */
					final String localHostName = interconnectionServer.getLocalHostName();
					JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
							JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
							localHostName + ":" + iocConnection.getNames().getLogicalIocName() + ":selectState",					// name
							localHostName, 														// value
							JmsMessage.SEVERITY_MINOR, 											// severity
							"NOT-SELECTED", 													// status
							getHostname(), 															// host
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
					LOG.warn("IOC not reachable by this InterConnectionServer: " + iocConnection.getNames().getLogicalIocName() + " (" + getHostname()+ ")");
					/*
					 * send JMS message - we are NOT selected
					 */
					final String localHostName = interconnectionServer.getLocalHostName();
					JmsMessage.INSTANCE.sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM,
							JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
							localHostName + ":" + iocConnection.getNames().getLogicalIocName() + ":selectState",					// name
							localHostName, 														// value
							JmsMessage.SEVERITY_MINOR, 											// severity
							"NOT-SELECTED", 													// status
							getHostname(), 															// host
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
				LOG.info("Command not accepted by IOC: " + iocConnection.getNames().getLogicalIocName() + " (" + getHostname()+ ")" + " command: " + command + " answer: " + answer);
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

	private byte[] prepareMessage ( final String cmd, final int ident) {
		String message = null;

		message = "COMMAND=" + cmd + ";" + "ID=" + ident + ";";
		message = message + "\0";
		return message.getBytes();
	}
	
    /**
     * Returns the command port from the preferences.
     *
     * @return the port.
     */
    private int commandPort() {
        final IPreferencesService prefs = Platform.getPreferencesService();
        return prefs.getInt(Activator.PLUGIN_ID,
                PreferenceConstants.IOC_COMMAND_PORT_NUMBER, 0, null);
    }


}
