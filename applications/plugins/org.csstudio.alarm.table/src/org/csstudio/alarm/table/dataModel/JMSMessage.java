package org.csstudio.alarm.table.dataModel;

import java.util.HashMap;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.alarm.table.preferences.LogViewerPreferenceConstants;
import org.csstudio.platform.model.IProcessVariable;

//import org.csstudio.platform.model.IProcessVariableName;
//import org.csstudio.data.exchange.ProcessVariableName;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;


public class JMSMessage implements IProcessVariable {//,
//		org.csstudio.data.exchange.IFrontEndControllerName{

	private HashMap<String, String> messageProperties = new HashMap<String, String>();
//	private String[] propertyNames;
	
	//for alarm table: false->no other message with the same pv name and an other
	//severity is in the table. true->another OLDER message with same pv an other
	// severity is in the table and the label provider change the color to gray.
	private boolean backgroundColorGray = false;
	
	/**
	 * Initialisation of HashMap with actual message properties.
	 *
	 */
	public JMSMessage(String[] propNames) {
		super();
//		propertyNames = JmsLogsPlugin.getDefault().getPluginPreferences().
//			getString(LogViewerPreferenceConstants.P_STRING).split(";");
		for(int i = 0; i < propNames.length; i++) {
			messageProperties.put(propNames[i].split(",")[0], "");
		}
	}
	
	/**
	 * Setting value of message a message property
	 * 
	 * @param property 
	 * @param value
	 */
	public void setProperty(String property, String value) {
		if (messageProperties.containsKey(property)) {
			messageProperties.put(property, value);
		}
	}
	
	
	/**
	 * Returns value of the requested property 
	 * 
	 * @param property
	 * @return
	 */
	public String getProperty(String property) {
		
		//if the table asks for the severity we return the severity value
		//set in the preferences
		if (property.equals("SEVERITY")) {
			if(messageProperties.get("SEVERITY") != null) {
				return findSeverityValue();
//				try {
//					// Matthias 05-04-2007
//					switch( severityToNumber(messageProperties.get("SEVERITY"))) {
//					case 0 : return JmsLogsPlugin.getDefault().getPluginPreferences().getString(JmsLogPreferenceConstants.VALUE0);
//					case 1 : return JmsLogsPlugin.getDefault().getPluginPreferences().getString(JmsLogPreferenceConstants.VALUE1);
//					case 2 : return JmsLogsPlugin.getDefault().getPluginPreferences().getString(JmsLogPreferenceConstants.VALUE2);
//					case 3 : return JmsLogsPlugin.getDefault().getPluginPreferences().getString(JmsLogPreferenceConstants.VALUE3);
//					case 4 : return JmsLogsPlugin.getDefault().getPluginPreferences().getString(JmsLogPreferenceConstants.VALUE4);
//					case 5 : return JmsLogsPlugin.getDefault().getPluginPreferences().getString(JmsLogPreferenceConstants.VALUE5);
//					default : return "INVALID_SEVERITY";
//					}
//				} catch (NumberFormatException e) {
//					return "INVALID_SEVERITY";
//				}
			}
		}
		
		//to get the severity key (the 'real' severity get from the map message)
		//we have to ask for 'SEVERITY_KEY'
		if (property.equals("SEVERITY_KEY")) {
			if(messageProperties.get("SEVERITY") != null) {
				return messageProperties.get("SEVERITY");
			}
		}
		
		//all other properties
		if (messageProperties.containsKey(property)) {
			String s = messageProperties.get(property);
			if (s != null) {
				return s;
			} else {
				return "";
			}
		} else {
			return "";
		}
	}
	
	/**
	 * returns the severity value for the severity key of this message.
	 * 
	 * @return
	 */
	private String findSeverityValue() {
		
		String severityKey = messageProperties.get("SEVERITY");
		IPreferenceStore preferenceStore = JmsLogsPlugin.getDefault().getPreferenceStore();
		
		if (severityKey.equals(preferenceStore.getString("key 0"))) {
			return preferenceStore.getString("value 0");
		}
		if (severityKey.equals(preferenceStore.getString("key 1"))) {
			return preferenceStore.getString("value 1");
		}
		if (severityKey.equals(preferenceStore.getString("key 2"))) {
			return preferenceStore.getString("value 2");
		}
		if (severityKey.equals(preferenceStore.getString("key 3"))) {
			return preferenceStore.getString("value 3");
		}
		if (severityKey.equals(preferenceStore.getString("key 4"))) {
			return preferenceStore.getString("value 4");
		}
		if (severityKey.equals(preferenceStore.getString("key 5"))) {
			return preferenceStore.getString("value 5");
		}
		if (severityKey.equals(preferenceStore.getString("key 6"))) {
			return preferenceStore.getString("value 6");
		}
		if (severityKey.equals(preferenceStore.getString("key 7"))) {
			return preferenceStore.getString("value 7");
		}
		if (severityKey.equals(preferenceStore.getString("key 8"))) {
			return preferenceStore.getString("value 8");
		}
		if (severityKey.equals(preferenceStore.getString("key 9"))) {
			return preferenceStore.getString("value 9");
		}
		
		return "invalid severity";
	}
	
	
	/**
	 * Returns the number of the severity. The number represents the
	 * level of the severity.
	 * 
	 * @return
	 */
	public int getSeverityNumber() {
		IPreferenceStore preferenceStore = JmsLogsPlugin.getDefault().getPreferenceStore();
		String severityKey = messageProperties.get("SEVERITY");
		
		if (severityKey.equals(preferenceStore.getString("key 0"))) {
			return 0;
		}
		if (severityKey.equals(preferenceStore.getString("key 1"))) {
			return 1;
		}
		if (severityKey.equals(preferenceStore.getString("key 2"))) {
			return 2;
		}
		if (severityKey.equals(preferenceStore.getString("key 3"))) {
			return 3;
		}
		if (severityKey.equals(preferenceStore.getString("key 4"))) {
			return 4;
		}
		if (severityKey.equals(preferenceStore.getString("key 5"))) {
			return 5;
		}
		if (severityKey.equals(preferenceStore.getString("key 6"))) {
			return 6;
		}
		if (severityKey.equals(preferenceStore.getString("key 7"))) {
			return 7;
		}
		if (severityKey.equals(preferenceStore.getString("key 8"))) {
			return 8;
		}
		if (severityKey.equals(preferenceStore.getString("key 9"))) {
			return 9;
		}
		
		return -1;
		
	}

	public static Integer severityToNumber ( String severity) {
		Integer severityAsNumber = null;
		//
		// this is a really dumb implementation to convert severity strings into an Integer
		// This only works for EPICS severity strings...
		// TODO - make this more generic
		// Matthias 05-04-2007
		if ( severity == null) {
			//no string compares with null string !
			return -1;
		}
		if ( severity.toUpperCase().equals("NO_ALARM")){
			severityAsNumber = 0;
		} else if ( severity.toUpperCase().equals("MINOR")){
			severityAsNumber = 1;
		} else if ( severity.toUpperCase().equals("MAJOR")){
			severityAsNumber = 2;
		} else if ( severity.toUpperCase().equals("INVALID")){
			severityAsNumber = 3;
		} else if ( severity.toUpperCase().equals("das wars")){
			severityAsNumber = 4;
		} else {
			//
			// maybe it's already a number?
			//
			try {
				severityAsNumber = new Integer( severity);
				
			} catch (NumberFormatException e) {
				return -1;
			}
			
		}
		
		return severityAsNumber;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return messageProperties.get("NAME");
	}

	public Object getAdapter(Class adapter) {
		JmsLogsPlugin.logInfo("get adapter: " + adapter);
		
		
//	    if (adapter.equals(IProcessVariableName.class)) {
//	    	IProcessVariableName pvn = new ProcessVariableName("hallo jan");
//	    	return pvn;
//	    } 
        return null;
		
//		if (adapter.isInstance(ipvn)) {
//			IProcessVariableName pvn = new ProcessVariableName("hallo jan");
//			return pvn;
//		}
//		// TODO Auto-generated method stub
//		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public int getKey() {
		// TODO Auto-generated method stub
		return 9;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		return "url";
	}

	public String getTypeId() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isBackgroundColorGray() {
		return backgroundColorGray;
	}

	public void setBackgroundColorGray(boolean backgroundColorGray) {
		this.backgroundColorGray = backgroundColorGray;
	}

	public HashMap<String, String> getHashMap(){
	    return messageProperties;
    }
}