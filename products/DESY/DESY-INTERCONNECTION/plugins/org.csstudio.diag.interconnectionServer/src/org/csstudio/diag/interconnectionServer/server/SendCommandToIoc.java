package org.csstudio.diag.interconnectionServer.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendCommandToIoc extends Thread {
	
	private String hostName = "locahost";
	private int port = 0;
	private String command = "NONE";
	private int id = 0;
	
	public SendCommandToIoc (int id, String hostName, int port, String command) {
		
		this.id = id;
		this.hostName = hostName;
		this.port = port;
		this.command = command;
		this.start();
	}
	
	public void run() {
		
		/*
		 * 
		 */
		byte[] preparedMessage = null; 
		DatagramSocket outPutSocket = null;
        
        preparedMessage = prepareMessage ( command, id);

        try
        {
        	outPutSocket = new DatagramSocket( port);
            
            // DatagramPacket newPacket = new DatagramPacket(preparedMessage, preparedMessage.length, packet.getAddress(), packet.getPort());
            DatagramPacket newPacket = new DatagramPacket(preparedMessage, preparedMessage.length, InetAddress.getByName( hostName), port);
            
            outPutSocket.send(newPacket);

        }
        catch ( /* UnknownHostException is a */ IOException e )
        {
          e.printStackTrace();
        }
        finally
        {
          if ( outPutSocket != null )
        	  outPutSocket.close(); 
        } 
		
	}
	
	public byte[] prepareMessage ( String command, int id) {
		String message = null;
		
		message = "COMMAND=" + command + ";" + "ID=" + id + ";";
		message = message + "\0";
        
        return message.getBytes();
	}

}
