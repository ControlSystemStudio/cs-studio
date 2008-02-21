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
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Helper class to send commands to the IOC
 * Use a separate thread to decouple processing.
 * 
 * @author Matthias Clausen
 *
 */
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
//        currentId = PreferenceProperties.SENT_START_ID;

//		//get properties from xml store.
//		XMLStore store = XMLStore.getInstance();
//		String commandPortNumber = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
//				"commandPortNumber", false);
//		String dataPortNumber = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
//				"dataPortNumber", false);
//		String sentStartID = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
//				"sentStartID", false);

        IPreferencesService prefs = Platform.getPreferencesService();
	    String commandPortNumber = prefs.getString(Activator.getDefault().getPluginId(),
	    		"commandPortNumber", "", null);  
	    String dataPortNumber = prefs.getString(Activator.getDefault().getPluginId(),
	    		"dataPortNumber", "", null);  
	    String sentStartID = prefs.getString(Activator.getDefault().getPluginId(),
	    		"sentStartID", "", null);  

		int commandPortNum = Integer.parseInt(commandPortNumber);
		int dataPortNum = Integer.parseInt(dataPortNumber);

		currentId = Integer.parseInt(sentStartID);
        
//        System.out.println("Send Commands - start");

        try
        {
        	sendSocket = new DatagramSocket( commandPortNum);
            //TODO: create message - successfully up and running
        }
        catch(IOException ioe)
        {
        	System.out.println("Send Commands - start ** ERROR ** : Socket konnte nicht initialisiert werden. Port: " + dataPortNum);
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
		CentralLogger.getInstance().info(this, "Anzahl der Token: " + tok.countTokens() + "\n");
//      System.out.println("Anzahl der Token: " + tok.countTokens() + "\n");
        
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
	                		CentralLogger.getInstance().info(this, " SendCommand - Reply : " + attribute[0] + " := "+ attribute[1]);
//		                    System.out.println(" SendCommand - Reply : " + attribute[0] + " := "+ attribute[1]);
		                    
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
				CentralLogger.getInstance().warn(this, "SendCommand - NO ID received");
//				System.out.println ("SendCommand - NO ID received");
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
					CentralLogger.getInstance().warn(this, "SendCommand - unknown ID received");
//					System.out.println ("SendCommand - unknown ID received");
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
			CentralLogger.getInstance().warn(this, "SendCommand - cannot issue command on unknown ID");
//			System.out.println ("SendCommand - cannot issue command on unknown ID");
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
