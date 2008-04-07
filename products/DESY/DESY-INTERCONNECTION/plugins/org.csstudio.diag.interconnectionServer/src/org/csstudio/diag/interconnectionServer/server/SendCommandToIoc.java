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
import java.util.GregorianCalendar;
import java.util.Random;

import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.diag.interconnectionServer.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;
import org.eclipse.core.runtime.Platform;

/**
 * The thread which actually is sending the command.
 * 
 * @author Matthias Clausen
 *
 */
public class SendCommandToIoc extends Thread {
	
	private String hostName = "locahost";
	private int port = 0;
	private String command = "NONE";
	private volatile String statisticId = "NONE";
	private int id = 0;
	private static String IOC_NOT_REACHABLE = "IOC_NOT_REACHABLE";
	private int statusMessageDelay = 0;
	
	/**
	 * Send a command to the IOC in an independent thread.
	 * @param hostName IOC name.
	 * @param port Port to be used.
	 * @param command One of the supported commands.
	 */
	public SendCommandToIoc ( String hostName, int port, String command) {
		
		this.id = InterconnectionServer.getInstance().getSendCommandId();
		this.hostName = hostName;
		this.port = port;
		this.command = command;
		this.statisticId = hostName + ":" + Integer.parseInt(Platform.getPreferencesService().getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.DATA_PORT_NUMBER, "", null));
		if ( (this.hostName == null || this.statisticId == null ) || (this.hostName.equals("") || this.statisticId.equals("") ))  {
			CentralLogger.getInstance().fatal(this, "Wrong StatiscitcID or HostName! ID: " + this.statisticId + "Host: " + hostName);
			// do NOT start!
		} else {
			this.start();
		}
	}
	
	/**
	 * Send a command to the IOC in an independent thread.
	 * @param statisticId
	 * @param command
	 */
	public SendCommandToIoc ( String statisticId, String command) {
		
		this.id = InterconnectionServer.getInstance().getSendCommandId();
		this.hostName = Statistic.getInstance().getContentObject(statisticId).getHost();
		this.port = Statistic.getInstance().getContentObject(statisticId).getPort();
		this.command = command;
		this.statisticId = statisticId;
		if ( (this.hostName == null || this.statisticId == null ) || (this.hostName.equals("") || this.statisticId.equals("") ))  {
			CentralLogger.getInstance().fatal(this, "Wrong StatiscitcID or HostName! ID: " + this.statisticId + "Host: " + hostName);
			// do NOT start!
		} else {
			this.start();
		}
	}
	
	public void run() {
		
		/*
		 * 
		 */
		byte[] preparedMessage = null; 
		byte[] buffer	=  new byte[1024];
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
        
        if ( (this.command != null) && this.command.equals(PreferenceProperties.COMMAND_SEND_ALL_ALARMS)) {
        	
        	if ( (Engine.getInstance().getWriteVector().size() >=0) && (100*Engine.getInstance().getWriteVector().size() < PreferenceProperties.MAX_WAIT_UNTIL_SEND_ALL_ALARMS)) {
    			statusMessageDelay = 100*Engine.getInstance().getWriteVector().size() + (int)((new GregorianCalendar().getTimeInMillis())%10000);
    		} else {
    			statusMessageDelay = PreferenceProperties.MAX_WAIT_UNTIL_SEND_ALL_ALARMS + (int)((new GregorianCalendar().getTimeInMillis())%10000);	// ~ 5 minutes + random
    		}

        	CentralLogger.getInstance().info(this, "Waiting " + statusMessageDelay + " until sending " + PreferenceProperties.COMMAND_SEND_ALL_ALARMS + " to the IOC " + Statistic.getInstance().getContentObject(statisticId).getLogicalIocName() + " (" + hostName+ ")");
        	try {
				Thread.sleep( statusMessageDelay);
			} catch (InterruptedException e) {
				// TODO: handle exception
			}
			/*
			 * never execute this command
			 */
//        	return;
        }

        try
        {
        	socket = new DatagramSocket( );	// do NOT specify the port
            
            // DatagramPacket newPacket = new DatagramPacket(preparedMessage, preparedMessage.length, packet.getAddress(), packet.getPort());
        	/*
        	 * it happened that the host name looked like: ipName|logicalIocName - but Why?? and from where?
        	 * in any case: be prepared!
        	 */
        	if ( hostName.contains("|")) {
        		System.out.println ("SendCommandToIoc: hostname contains >|< ! hostname = " + hostName);
        		hostName = hostName.substring(0, hostName.indexOf("|"));
    		}
            DatagramPacket newPacket = new DatagramPacket(preparedMessage, preparedMessage.length, InetAddress.getByName( hostName), PreferenceProperties.COMMAND_PORT_NUMBER);
            
            socket.send(newPacket);
            
            
			try {
				/*
	        	 * set timeout period to 10 seconds
	        	 */
				socket.setSoTimeout( PreferenceProperties.TIME_TO_GET_ANSWER_FROM_IOC_AFTER_COMMAND);

				packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
			} catch (InterruptedIOException ioe) {
				// TODO: handle exception
				ioe.printStackTrace();
				/*
				 * error handling:
				 * set answer message and use normal handling of state
				 */
				answerMessage = IOC_NOT_REACHABLE;
			}
			
			if ( answerMessage == null) {
				/*
	             * check answer
	             * for now we only check for the string 'DONE'
	             */
				answerMessage = new String(packet.getData(), 0, packet.getLength());
				
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
			
			
			
			//System.out.println ("IOC: " + hostName + " is " + answer);
			
            if ( TagList.getInstance().getReplyType(answer) == TagList.REPLY_TYPE_DONE) {
            	/*
            	 * nothing to do
            	 */
            	CentralLogger.getInstance().info(this, "Command accepted by IOC: " + Statistic.getInstance().getContentObject(statisticId).getLogicalIocName() + " (" + hostName+ ")" + " command: " + command);
            } else if (TagList.getInstance().getReplyType(answer) == TagList.REPLY_TYPE_SELECTED) {
            	/*
            	 * did the select state change?
            	 */
            	if (!Statistic.getInstance().getContentObject(statisticId).isSelectState()) {
        			//remember we're selected
        			Statistic.getInstance().getContentObject(statisticId).setSelectState(true);
        			//create log message
        			CentralLogger.getInstance().warn(this, "IOC SELECTED this InterConnectionServer: " + Statistic.getInstance().getContentObject(statisticId).getLogicalIocName() + " (" + hostName+ ")");
        			/*
        			 * send JMS message - we are selected
        			 */
        			/*
        			 * get host name of interconnection server
        			 */
        			String localHostName = null;
        			try {
        				java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
        				localHostName = localMachine.getHostName();
        			}
        			catch (java.net.UnknownHostException uhe) { 
        			}
        			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
        					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
        					localHostName + ":" + Statistic.getInstance().getContentObject(statisticId).getLogicalIocName() + ":selectState",					// name
        					localHostName, 														// value
        					JmsMessage.SEVERITY_NO_ALARM, 										// severity
        					"SELECTED", 														// status
        					hostName, 															// host
        					null, 																// facility
        					"virtual channel", 											// text
        					null);	
        			// send command to IOC - get ALL alarm states
        			
        			/*
        			 * if we received beacons within the last two beacon timeout periods we 'probably' did not loose any messages
        			 * this is a switch over from one IC-Server to another and thus
        			 * we DO NOT have to ask for an update on all alarms! 
        			 */
        			if ( ! Statistic.getInstance().getContentObject(statisticId).wasLastBeaconWithinTwoBeaconTimeouts()) {
        				new SendCommandToIoc( hostName, port, PreferenceProperties.COMMAND_SEND_ALL_ALARMS);
        			}
        		}           	
            } else if (TagList.getInstance().getReplyType(answer) == TagList.REPLY_TYPE_NOT_SELECTED) {
            	/*
        		 * we are not selected any more
        		 * in case we were selected before - we'll have to create a JMS message
        		 */
            	
        		if ( Statistic.getInstance().getContentObject(statisticId).isSelectState()) {
        			//create log message
        			CentralLogger.getInstance().warn(this, "IOC DE-selected this InterConnectionServer: " + Statistic.getInstance().getContentObject(statisticId).getLogicalIocName() + " (" + hostName+ ")");
        			/*
        			 * send JMS message - we are NOT selected
        			 */
        			/*
        			 * get host name of interconnection server
        			 */
        			String localHostName = null;
        			try {
        				java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
        				localHostName = localMachine.getHostName();
        			}
        			catch (java.net.UnknownHostException uhe) { 
        			}
        			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
        					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
        					localHostName + ":" + Statistic.getInstance().getContentObject(statisticId).getLogicalIocName() + ":selectState",					// name
        					localHostName, 														// value
        					JmsMessage.SEVERITY_MINOR, 											// severity
        					"NOT-SELECTED", 													// status
        					hostName, 															// host
        					null, 																// facility
        					"virtual channel", 											// text
        					null);	
        		}
        		//remember we're not selected any more
    			Statistic.getInstance().getContentObject(statisticId).setSelectState(false);
    			
            } else if ( (answerMessage != null) && answerMessage.equals(IOC_NOT_REACHABLE)) {
            	/*
        		 * we cannot reach the IOC
        		 * in case we were selected before - we'll have to create a JMS message
        		 */
            	
        		if ( Statistic.getInstance().getContentObject(statisticId).isSelectState()) {
        			//create log message
        			CentralLogger.getInstance().warn(this, "IOC not reachable by this InterConnectionServer: " + Statistic.getInstance().getContentObject(statisticId).getLogicalIocName() + " (" + hostName+ ")");
        			/*
        			 * send JMS message - we are NOT selected
        			 */
        			/*
        			 * get host name of interconnection server
        			 */
        			String localHostName = null;
        			try {
        				java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
        				localHostName = localMachine.getHostName();
        			}
        			catch (java.net.UnknownHostException uhe) { 
        			}
        			JmsMessage.getInstance().sendMessage ( JmsMessage.JMS_MESSAGE_TYPE_ALARM, 
        					JmsMessage.MESSAGE_TYPE_IOC_ALARM, 									// type
        					localHostName + ":" + Statistic.getInstance().getContentObject(statisticId).getLogicalIocName() + ":selectState",					// name
        					localHostName, 														// value
        					JmsMessage.SEVERITY_MINOR, 											// severity
        					"NOT-SELECTED", 													// status
        					hostName, 															// host
        					null, 																// facility
        					"virtual channel", 											// text
        					null);	
        		}
        		//remember we're not selected any more
    			Statistic.getInstance().getContentObject(statisticId).setSelectState(false);
            } else {
            	CentralLogger.getInstance().info(this, "Command not accepted by IOC: " + Statistic.getInstance().getContentObject(statisticId).getLogicalIocName() + " (" + hostName+ ")" + " command: " + command + " answer: " + answer);
            }

        }
        catch ( /* UnknownHostException is a */ IOException e )
        {
          e.printStackTrace();
        }
        finally
        {
          if ( socket != null )
        	  socket.close(); 
        } 
		
	}
	
	public byte[] prepareMessage ( String command, int id) {
		String message = null;
		
		message = "COMMAND=" + command + ";" + "ID=" + id + ";";
		message = message + "\0";
        
        return message.getBytes();
	}

}
