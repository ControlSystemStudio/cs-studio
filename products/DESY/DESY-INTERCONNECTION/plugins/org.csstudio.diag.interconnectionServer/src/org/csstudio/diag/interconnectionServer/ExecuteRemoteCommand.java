package org.csstudio.diag.interconnectionServer;

import java.util.Map;

import org.csstudio.diag.interconnectionServer.server.PreferenceProperties;
import org.csstudio.platform.libs.dcf.actions.IAction;

public class ExecuteRemoteCommand implements IAction {

	public Object run(Object param) {
		if(!(param instanceof Map))
			return null;
		
		Map m = (Map)param;
		String command 	= m.get("param1").toString();
		String client 	= m.get("param2").toString();
		int commandId = -1;
		
		for ( int i=0; i < PreferenceProperties.COMMAND_LIST.length; i++) {
			if ( PreferenceProperties.COMMAND_LIST[i].equalsIgnoreCase(command)) {
				commandId = i;
			}
		}
		
		switch ( commandId) {
		case PreferenceProperties.COMMAND_SEND_ALL_ALARMS_I:
			// all records
			
			break;
		case PreferenceProperties.COMMAND_SEND_ALARM_I:
			// individual record
			
			break;
			
		case PreferenceProperties.COMMAND_DISCONNECT_I:
		case PreferenceProperties.COMMAND_SEND_STATUS_I:
		case PreferenceProperties.COMMAND_TAKE_OVER_I:
			default:
				
		
		}
		
		return command + ":" + client + " - success";
	}

}
