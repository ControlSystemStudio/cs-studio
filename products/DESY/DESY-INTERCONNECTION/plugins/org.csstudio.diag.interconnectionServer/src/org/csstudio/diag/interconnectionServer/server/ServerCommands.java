package org.csstudio.diag.interconnectionServer.server;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

import org.csstudio.diag.interconnectionServer.server.Statistic.StatisticContent;

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
