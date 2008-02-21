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
import java.net.*;
import java.util.Enumeration;

import org.csstudio.diag.interconnectionServer.server.Statistic.StatisticContent;

/**
 * Still used??
 * 
 * @author claus
 *
 */
public class ServerCommands extends Thread{
	
	private DatagramSocket socket;
	private DatagramPacket packet;
	private String attribute;
	private String value;
	private TagList.TagProperties tagProperties;
	
	private static String RETURN_MESSAGE_OK = "Ok";
	private static String RETURN_MESSAGE_ERROR = "Error";
	
	public ServerCommands ( String attribute, String value, TagList.TagProperties tagProperties, DatagramSocket socket, DatagramPacket packet) {
		this.attribute			= attribute;
		this.socket				= socket;
		this.packet				= packet;
		this.value				= value;
		this.tagProperties		= TagList.getInstance().getTagProperties( attribute);
		
		this.start();
	}
	
	public void run() {
		//
		// whenever we get a trigger -> execute statitic output
		//
		
		Statistic.getInstance().createStatisticPrintout();
		
	}
	
	public static String prepareMessage ( String attribute, String value, boolean status) {
		String message = "undefined tag type";
		String statusString = RETURN_MESSAGE_OK;
		
		if ( !status) {
			statusString = RETURN_MESSAGE_ERROR;
		}
		
		switch (TagList.getInstance().getTagProperties( attribute).getTagType())
		{
		case	PreferenceProperties.TAG_TYPE_IS_ID:
			
			message = attribute + "=" + value + ";" + "STATUS=" + statusString + ";";
			//message += ";IN=" + Statistic.getInstance().getTotalNumberOfIncomingMessages() + ";OUT=" + Statistic.getInstance().totalNumberOfOutgoingMessages;
			break;

		default:	
		}
		
		return message;
	}
	
	public static void sendMesssage ( String message, DatagramSocket socket, DatagramPacket packet) {
		
		if((message != null) && message.length() > 0)
        {
            message = message + "\0";
            
            byte[] answerData = message.getBytes();
            
            DatagramPacket newPacket = new DatagramPacket(answerData, answerData.length, packet.getAddress(), packet.getPort());
            
            try
            {
                // Dieser Thread nutzt den Socket des Servers.
                // Lieber einen neuen Socket erzeugen?
            	/*
            	 * just in case we want to test sleeping ...
            	 * 
            	try {
            		Thread.sleep(0);
            	}
            	catch (InterruptedException  e) {
            		
            	}
            	*/

            	socket.send(newPacket);
            	//TODO: remove sysprint
            	///System.out.println(message);
            	//
            	// write statistic
            	//
            	Statistic.getInstance().getContentObject( packet.getAddress().getHostName() + ":" + packet.getPort()).setTime( false); // false = sent
            }
            catch(IOException ioe)
            {
                // create jms message
            	Statistic.getInstance().getContentObject( packet.getAddress().getHostName() + ":" + packet.getPort()).incrementErrorCounter();
            }
        }
	}
	
	public static String getNodeNames () {
		return Statistic.getInstance().getNodeNames();
	}
	public static String getCommands () {
		String commandList;
		int length = PreferenceProperties.COMMAND_LIST.length;
		commandList = PreferenceProperties.COMMAND_LIST[0] + ",";
		for ( int i = 1; i < length; i++) {
			commandList += PreferenceProperties.COMMAND_LIST[i] + ",";
		}
		return commandList;
	}

}
