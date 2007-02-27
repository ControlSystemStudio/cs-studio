package org.csstudio.diag.interconnectionServer.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class SendCommands {
	
	private static SendCommands sendCommandsInstance = null;
	private DatagramSocket              sendSocket    	= null; 
	private boolean						quit			= false;
	private Hashtable<String,CommandInstance> commandList		= null;
	private int							currentId		= 0;
	
	
	
	public SendCommands()
    {
        DatagramPacket  packet      = null;
        ClientRequest   newClient   = null;
        int             result      = 0;
        byte 			buffer[]	=  new byte[ PreferenceProperties.BUFFER_ZIZE];
        commandList	= new Hashtable<String,CommandInstance>();
        currentId = PreferenceProperties.SENT_START_ID;

        
        System.out.println("Send Commands - start");

        try
        {
        	sendSocket = new DatagramSocket( PreferenceProperties.COMMAND_PORT_NUMBER);
            //TODO: create message - successfully up and running
        }
        catch(IOException ioe)
        {
        	System.out.println("Send Commands - start ** ERROR ** : Socket konnte nicht initialisiert werden. Port: " + PreferenceProperties.DATA_PORT_NUMBER);
        	System.out.println("Send Commands - start *** EXCEPTION *** : " + ioe.getMessage());
            
            return;
        }
        
        
    
        // TODO: Abbruchbedingung einfügen
        //       z.B. Receiver für Queue COMMAND einfügen
        while(!this.quit)
        {
            try
            {
                packet = new DatagramPacket( buffer, buffer.length);

                sendSocket.receive( packet);
                
                checkReply ( packet);
            }
            catch(IOException ioe)
            {
                System.out.println("Send Commands - start *** IOException *** : " + ioe.getMessage());         
            }
        }
                
        sendSocket.close();
        
        return;
    }
	
	public static SendCommands getInstance() {
		//
		// get an instance of our sigleton
		//
		if ( sendCommandsInstance == null) {
			synchronized (Statistic.class) {
				if (sendCommandsInstance == null) {
					sendCommandsInstance = new SendCommands();
				}
			}
		}
		return sendCommandsInstance;
	}
	
	private void checkReply ( DatagramPacket  packet) {
		boolean 	success 		= false;
		String[] 	attribute 		= null;
		boolean 	gotReply		= false;
    	boolean 	gotId 			= false;
    	TagList		tagList 	  	= TagList.getInstance();
    	String		id				= null;
    	String		reply			= null;
		
		String daten = new String( packet.getData(), 0, packet.getLength());
		
		//
		// just in case we should use another data format in the future
		// here's the place to implement anoher parser
		//

		StringTokenizer tok = new StringTokenizer(daten, PreferenceProperties.DATA_TOKENIZER);
        
		// TODO: make it a logMessage
        System.out.println("Anzahl der Token: " + tok.countTokens() + "\n");
        
        if(tok.countTokens() > 0)
        {
                while(tok.hasMoreTokens())
                {
                	String localTok = tok.nextToken();
                	//
                	// parsing Tag=value;Tag1=value1;
                	//
                	
                	//
                	// first make sure that it's a pair
                	// this requires a '=' and at least two more chares like a=b
                	//
                	
                	if ( (localTok !=null) && localTok.contains("=") && (localTok.length() > 2 )) {
                		
                		//
                		// ok seems to be ok to parse further
                		// now make sure that '=' is not the first and not the last char
                		// -> avoid ;=Value;Tag=; combinations
                		
                		if ( (!localTok.endsWith( "=")) && (!localTok.startsWith( "="))) {
	                		attribute = localTok.split("=");
	                		
	                		// TODO: make this a debug message
		                    System.out.println(" SendCommand - Reply : " + attribute[0] + " := "+ attribute[1]);
		                    
		                    if ( tagList.getTagType( attribute[0].toString()) == PreferenceProperties.TAG_TYPE_IS_ID) {		                    	
		                    	gotId = true;
		                    	id = attribute[1].toString();
		                    } else if ( tagList.getTagType( attribute[0].toString()) == PreferenceProperties.TAG_TYPE_IS_TYPE) {
		                    	gotReply = true;
		                    	reply = attribute[1].toString();
		                    }
	                	} //if
	                } // if
                } //while
        } // if tok
        if ( gotId && gotReply){
        	success = true;
        	checkCommandState( id, reply);
        	return;
        }
		return;
	}
	
	public void checkCommandState ( String id, String reply) {
		//
		// check for the ID and the reply that came with it
		//
		if (commandList.containsKey(id)) {
			//
			// check reply
			//
			if ( reply == null){
				System.out.println ("SendCommand - NO ID received");
			}
			switch (TagList.getInstance().getReplyType(reply)) {
			
			case TagList.REPLY_TYPE_DONE:
			case TagList.REPLY_TYPE_OK:
				//
				// ok - done - remove from list
				//
				commandList.remove(id);
				break;
			case TagList.REPLY_TYPE_ERROR:
				// send one more time
				sendCommand( id);
				break;
				//
				// not handled here
				//TODO: handle it!
				//
			case TagList.REPLY_TYPE_CMD_MISSING:
			case TagList.REPLY_TYPE_CMD_UNKNOWN:
			case TagList.REPLY_TYPE_NOT_SELECTED:
			case TagList.REPLY_TYPE_REFUSED:
			case TagList.REPLY_TYPE_SELECTED:
				default:
					System.out.println ("SendCommand - unknown ID received");
			}
		}
		
	}
	
	public void enterNewCommand( String command) {
		//
		// enter new command in hash table
		//
		String id = "" + this.currentId++;
		CommandInstance thisCommandInstance = new CommandInstance(command);
		commandList.put(id, thisCommandInstance);
	}
	
	public void sendCommand ( String id) {
		//
		// take whats stred in the commandInstance and send it
		//
		if (commandList.containsKey(id)) {
			CommandInstance thisCommandInstance = commandList.get(id);
			String message = PreferenceProperties.TAG_TYPE_IS_ID + "=" + id + ";" + PreferenceProperties.TAG_TYPE_IS_COMMAND + "=" + thisCommandInstance.getCommand() + ";";
		} else {
			System.out.println ("SendCommand - cannot issue command on unknown ID");
		}
	}
	
	public void quitSendCommands() {
		this.quit = true;
	}
	
	private class CommandInstance {
		String 	command = null;
		GregorianCalendar timeSent = null;
		
		public CommandInstance ( String lastCommand) {
			this.timeSent = new GregorianCalendar();
			this.command = lastCommand;
		}
		
		public String getCommand () {
			return this.command;
		}
		
		
	}
}
