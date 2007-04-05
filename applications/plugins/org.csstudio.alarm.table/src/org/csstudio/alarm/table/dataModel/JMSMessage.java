package org.csstudio.alarm.table.dataModel;

import java.util.HashMap;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.alarm.table.preferences.LogViewerPreferenceConstants;
import org.csstudio.platform.model.IProcessVariable;

//import org.csstudio.platform.model.IProcessVariableName;
//import org.csstudio.data.exchange.ProcessVariableName;

import org.eclipse.core.runtime.IAdaptable;


public class JMSMessage implements IProcessVariable {//,
//		org.csstudio.data.exchange.IFrontEndControllerName{

	private static final String SEVERITY = "SEVERITY"; //$NON-NLS-1$
	private static final String SEVERITY_NUMBER = "SEVERITY_NUMBER"; //$NON-NLS-1$

	private HashMap<String, String> messageProperties = new HashMap<String, String>();
	private String[] propertyNames;
//	private IProcessVariableName ipvn;

	/**
	 * Initialisation of HashMap with actual message properties.
	 *
	 */
	public JMSMessage(String[] propNames) {
		super();
		propertyNames = JmsLogsPlugin.getDefault().getPluginPreferences().
			getString(LogViewerPreferenceConstants.P_STRING).split(";"); //$NON-NLS-1$
		for(int i = 0; i < propertyNames.length; i++) {
			messageProperties.put(propertyNames[i], ""); //$NON-NLS-1$
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
		if (property.equals(SEVERITY)) {
			if(messageProperties.get(SEVERITY) != null) {
				try {
					switch(new Integer(messageProperties.get(SEVERITY))) {
					case 0 : return JmsLogsPlugin.getDefault().getPluginPreferences().getString(JmsLogPreferenceConstants.VALUE0);
					case 1 : return JmsLogsPlugin.getDefault().getPluginPreferences().getString(JmsLogPreferenceConstants.VALUE1);
					case 2 : return JmsLogsPlugin.getDefault().getPluginPreferences().getString(JmsLogPreferenceConstants.VALUE2);
					case 3 : return JmsLogsPlugin.getDefault().getPluginPreferences().getString(JmsLogPreferenceConstants.VALUE3);
					case 4 : return JmsLogsPlugin.getDefault().getPluginPreferences().getString(JmsLogPreferenceConstants.VALUE4);
					case 5 : return JmsLogsPlugin.getDefault().getPluginPreferences().getString(JmsLogPreferenceConstants.VALUE5);
					default : return "INVALID_SEVERITY";
					}
				} catch (NumberFormatException e) {
					return "INVALID_SEVERITY";
				}
			}
		}
		if (property.equals(SEVERITY_NUMBER)) {
			if(messageProperties.get(SEVERITY) != null) {
				return messageProperties.get(SEVERITY);
			}
		}
		if (messageProperties.containsKey(property)) {
			String s = messageProperties.get(property);
			if (s != null) {
				return s;
			} else {
				return ""; //$NON-NLS-1$
			}
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	public String getName() {
		// TODO Auto-generated method stub
		return messageProperties.get("NAME");
	}

	public Object getAdapter(Class adapter) {
		System.out.println("hallo get adapter: " + adapter);


//	    if (adapter.equals(IProcessVariableName.class)) {
//	    	System.out.println(adapter);
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
		return "url jan";
	}

	public String getTypeId() {
		// TODO Auto-generated method stub
		return null;
	}


}