/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
	
	/** 
	 * for alarm table: false->no other message with the same pv name and an other
	 * severity is in the table. true->another OLDER message with same pv an other
	 * severity is in the table and the label provider change the color to gray.
	 */
	private boolean _backgroundColorGray = false;
	
	/**
	 * is this message already acknowledged?
	 */
	private boolean _ackknowledgement = false;
	
	/**
	 * How many times has this pv changed the alarm status
	 */
	private int _alarmChangeCount = 0;
	
	public boolean is_ackknowledgement() {
		return _ackknowledgement;
	}

	public void set_ackknowledgement(boolean _ackknowledgement) {
		this._ackknowledgement = _ackknowledgement;
	}

	/**
	 * Initialisation of HashMap with actual message properties.
	 *
	 */
	public JMSMessage(String[] propNames) {
		super();
//		propertyNames = JmsLogsPlugin.getDefault().getPluginPreferences().
//			getString(LogViewerPreferenceConstants.P_STRING).split(";");
		for(int i = 0; i < propNames.length; i++) {
			messageProperties.put(propNames[i].split(",")[0], ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Setting value of a message property
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
		if (property.equals("SEVERITY")) { //$NON-NLS-1$
			if(messageProperties.get("SEVERITY") != null) { //$NON-NLS-1$
				return findSeverityValue();
			}
		}
		
		//to get the severity key (the 'real' severity get from the map message)
		//we have to ask for 'SEVERITY_KEY'
		if (property.equals("SEVERITY_KEY")) { //$NON-NLS-1$
			if(messageProperties.get("SEVERITY") != null) { //$NON-NLS-1$
				return messageProperties.get("SEVERITY"); //$NON-NLS-1$
			}
		}
		
		//all other properties
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
	
	/**
	 * returns the severity value for the severity key of this message.
	 * 
	 * @return
	 */
	private String findSeverityValue() {
		
		String severityKey = messageProperties.get("SEVERITY");//$NON-NLS-1$
		if(severityKey.equals("")) {
			return "";
		}
		IPreferenceStore preferenceStore = JmsLogsPlugin.getDefault().getPreferenceStore();
		
		if (severityKey.equals(preferenceStore.getString("key 0"))) { //$NON-NLS-1$
			return preferenceStore.getString("value 0"); //$NON-NLS-1$
		}
		if (severityKey.equals(preferenceStore.getString("key 1"))) { //$NON-NLS-1$
			return preferenceStore.getString("value 1"); //$NON-NLS-1$
		}
		if (severityKey.equals(preferenceStore.getString("key 2"))) { //$NON-NLS-1$
			return preferenceStore.getString("value 2"); //$NON-NLS-1$
		}
		if (severityKey.equals(preferenceStore.getString("key 3"))) { //$NON-NLS-1$
			return preferenceStore.getString("value 3"); //$NON-NLS-1$
		}
		if (severityKey.equals(preferenceStore.getString("key 4"))) { //$NON-NLS-1$
			return preferenceStore.getString("value 4"); //$NON-NLS-1$
		}
		if (severityKey.equals(preferenceStore.getString("key 5"))) { //$NON-NLS-1$
			return preferenceStore.getString("value 5"); //$NON-NLS-1$
		}
		if (severityKey.equals(preferenceStore.getString("key 6"))) { //$NON-NLS-1$
			return preferenceStore.getString("value 6"); //$NON-NLS-1$
		}
		if (severityKey.equals(preferenceStore.getString("key 7"))) { //$NON-NLS-1$
			return preferenceStore.getString("value 7"); //$NON-NLS-1$
		}
		if (severityKey.equals(preferenceStore.getString("key 8"))) { //$NON-NLS-1$
			return preferenceStore.getString("value 8"); //$NON-NLS-1$
		}
		if (severityKey.equals(preferenceStore.getString("key 9"))) { //$NON-NLS-1$
			return preferenceStore.getString("value 9"); //$NON-NLS-1$
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
		String severityKey = messageProperties.get("SEVERITY"); //$NON-NLS-1$
		
		if (severityKey.equals(preferenceStore.getString("key 0"))) { //$NON-NLS-1$
			return 0;
		}
		if (severityKey.equals(preferenceStore.getString("key 1"))) { //$NON-NLS-1$
			return 1;
		}
		if (severityKey.equals(preferenceStore.getString("key 2"))) { //$NON-NLS-1$
			return 2;
		}
		if (severityKey.equals(preferenceStore.getString("key 3"))) { //$NON-NLS-1$
			return 3;
		}
		if (severityKey.equals(preferenceStore.getString("key 4"))) { //$NON-NLS-1$
			return 4;
		}
		if (severityKey.equals(preferenceStore.getString("key 5"))) { //$NON-NLS-1$
			return 5;
		}
		if (severityKey.equals(preferenceStore.getString("key 6"))) { //$NON-NLS-1$
			return 6;
		}
		if (severityKey.equals(preferenceStore.getString("key 7"))) { //$NON-NLS-1$
			return 7;
		}
		if (severityKey.equals(preferenceStore.getString("key 8"))) { //$NON-NLS-1$
			return 8;
		}
		if (severityKey.equals(preferenceStore.getString("key 9"))) { //$NON-NLS-1$
			return 9;
		}
		
		return -1;
		
	}


	public String getName() {
		// TODO Auto-generated method stub
		return messageProperties.get("NAME"); //$NON-NLS-1$
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

	public String getTypeId() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isBackgroundColorGray() {
		return _backgroundColorGray;
	}

	public void setBackgroundColorGray(boolean backgroundColorGray) {
		this._backgroundColorGray = backgroundColorGray;
	}

	public HashMap<String, String> getHashMap(){
	    return messageProperties;
    }

	public int get_alarmChangeCount() {
		return _alarmChangeCount;
	}

	public void set_alarmChangeCount(int changeCount) {
		_alarmChangeCount = changeCount;
	}
}