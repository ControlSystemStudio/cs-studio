package org.csstudio.diag.interconnectionServer.server;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.csstudio.platform.logging.CentralLogger;

public class SendCommandToIoc extends Thread {
	
	private String hostName = "locahost";
	private int port = 0;
	private String command = "NONE";
	private int id = 0;
	
	public SendCommandToIoc ( String hostName, int port, String command) {
		
		this.id = InterconnectionServer.getInstance().getSendCommandId();
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
		byte 			buffer[]	=  new byte[1024];
		DatagramSocket socket = null;
		DatagramPacket packet = null;
        
        preparedMessage = prepareMessage ( command, id);

        try
        {
        	socket = new DatagramSocket( port);
            
            // DatagramPacket newPacket = new DatagramPacket(preparedMessage, preparedMessage.length, packet.getAddress(), packet.getPort());
            DatagramPacket newPacket = new DatagramPacket(preparedMessage, preparedMessage.length, InetAddress.getByName( hostName), port);
            
            socket.send(newPacket);
            
            
			try {
				/*
	        	 * set timeout period to 1000ms
	        	 */
				socket.setSoTimeout(1000);

				packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
			} catch (InterruptedIOException ioe) {
				// TODO: handle exception
				ioe.printStackTrace();
			}            
			/*
             * check answer
             * for now we only check for the string 'OK'
             */
            if ( packet.toString().toUpperCase().contains("OK")) {
            	/*
            	 * nothing to do
            	 */
            	CentralLogger.getInstance().info(this, "Command accepted by IOC: " + hostName + " command: " + command);
            } else {
            	CentralLogger.getInstance().info(this, "Command not accepted by IOC: " + hostName + " command: " + command);
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
