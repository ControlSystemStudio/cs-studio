package org.csstudio.diag.interconnectionServer;

import java.util.Map;

import org.csstudio.diag.interconnectionServer.server.PreferenceProperties;
import org.csstudio.diag.interconnectionServer.server.SendCommandToIoc;
import org.csstudio.platform.libs.dcf.actions.IAction;
import org.csstudio.platform.store.XMLStore;

public class ExecuteRemoteCommandDyn implements IAction {

	public Object run(Object param) {
		
		//get properties from xml store.
		XMLStore store = XMLStore.getInstance();
		String commandPortNumber = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
				"commandPortNumber", false);

		int commandPortNum = Integer.parseInt(commandPortNumber);
		SendCommandToIoc sendCommandToIoc = null;
		
		if(!(param instanceof Map))
			return null;
		
		Map m = (Map)param;
		String command 	= m.get("Command").toString();
		String client 	= m.get("IOC").toString();
		System.out.println("received param: " + command + " " + client);
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
