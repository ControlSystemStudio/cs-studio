package org.csstudio.diag.interconnectionServer;
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

import java.util.Map;

import org.csstudio.diag.interconnectionServer.preferences.PreferenceConstants;
import org.csstudio.diag.interconnectionServer.server.PreferenceProperties;
import org.csstudio.diag.interconnectionServer.server.SendCommandToIoc;
import org.csstudio.platform.libs.dcf.actions.IAction;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

public class ExecuteRemoteCommandDyn implements IAction {

	public Object run(Object param) {
		
//		//get properties from xml store.
//		XMLStore store = XMLStore.getInstance();
//		String commandPortNumber = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
//				"commandPortNumber", false);

	    IPreferencesService prefs = Platform.getPreferencesService();
	    String commandPortNumber = prefs.getString(Activator.getDefault().getPluginId(),
	    		PreferenceConstants.COMMAND_PORT_NUMBER, "", null);  

		
		int commandPortNum = Integer.parseInt(commandPortNumber);
		SendCommandToIoc sendCommandToIoc = null;
		
		if(!(param instanceof Map))
			return null;
		
		Map m = (Map)param;
		String command 	= m.get("Command").toString();
		String client 	= m.get("IOC").toString();
		/*
		 * if the string consists of ipName|logicalName
		 * -> we only take the ipName!
		 */
		if ( client.contains("|")) {
			client = client.substring(0, client.indexOf("|"));
		}
		
		if ( (client != null) && (client.length()>0)) {
			// must be valid
		} else {
			return command + ":" + client + " - invalid IOC name";
		}
		
//		System.out.println("received param: " + command + " " + client);
		int commandId = -1;
		
		for ( int i=0; i < PreferenceProperties.COMMAND_LIST.length; i++) {
			if ( PreferenceProperties.COMMAND_LIST[i].equalsIgnoreCase(command)) {
				commandId = i;
			}
		}
		
		switch ( commandId) {
		case PreferenceProperties.COMMAND_SEND_ALL_ALARMS_I:
			// all records
			sendCommandToIoc = new SendCommandToIoc( client, commandPortNum, command);
			break;
			
		case PreferenceProperties.COMMAND_SEND_ALARM_I:
			// individual record
			sendCommandToIoc= new SendCommandToIoc( client, commandPortNum, command);
			break;
			
		case PreferenceProperties.COMMAND_DISCONNECT_I:
			sendCommandToIoc= new SendCommandToIoc( client, commandPortNum, command);
			break;
		case PreferenceProperties.COMMAND_SEND_STATUS_I:
			sendCommandToIoc= new SendCommandToIoc( client, commandPortNum, command);
			break;
		case PreferenceProperties.COMMAND_TAKE_OVER_I:
			sendCommandToIoc= new SendCommandToIoc( client, commandPortNum, command);
			break;
			default:
				
		
		}
		
		return command + ":" + client + " - success";
	}
	
}
