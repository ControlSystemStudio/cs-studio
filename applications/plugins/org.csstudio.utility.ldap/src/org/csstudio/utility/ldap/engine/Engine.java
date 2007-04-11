package org.csstudio.utility.ldap.engine;

import java.text.SimpleDateFormat;

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.csstudio.utility.ldap.Activator;
import org.csstudio.utility.ldap.preference.PreferenceConstants;
import org.csstudio.utility.ldap.reader.LDAPReader;

public class Engine extends Thread {

	private static 		Engine thisEngine = null;
	private boolean 	doWork = false;
	private DirContext 	ctx;
	/**
	 * @param args
	 */
	public void run () {
		Integer intSleepTimer = null;
		
		//
		// initialize LDAP connection (dir context
		//
		ctx = LDAPReader.initial();
		
		while (true) {
			//
			// do the work actually prepared
			//
			if (doWork) {
				
			}
			/*
        	 * sleep before we check for work again
        	 */
        	try {
        		if(Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.SECURITY_PROTOCOL).trim().length()>0) {
        			intSleepTimer = new Integer(Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.SECURITY_PROTOCOL));
        		} else {
        			intSleepTimer = 100; //default
        		}
        		Thread.sleep( (long)intSleepTimer );
        	}
        	catch (InterruptedException  e) {
        		
        	}
		}
	}
	
	private Engine() {
    	// absicherung
    }
    
    public static Engine getInstance() {
		//
		// get an instance of our sigleton
		//
		if ( thisEngine == null) {
			synchronized (Engine.class) {
				if (thisEngine == null) {
					thisEngine = new Engine();
				}
			}
		}
		return thisEngine;
	}
    
    synchronized public void setSeverityStatusTimeStamp ( String channel, String severity, String status, String timeStamp) {
		ModificationItem epicsStatus, epicsSeverity, epicsTimeStamp, epicsAcknowledgeTimeStamp ;
		ModificationItem[] modItem = null;
		int i = 0;

		String channelName = "eren=" + channel;
		
		//
		// change severity if value is entered
		//
		if ((severity != null)&& (severity.length() > 0)) {
			epicsSeverity = new ModificationItem( DirContext.REPLACE_ATTRIBUTE,	new BasicAttribute(	"epicsAlarmSeverity", severity));
			modItem[i++] = epicsSeverity;
		}

		//
		// change status if value is entered
		//
		if ((status != null) && (status.length() > 0)) {
			epicsStatus = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("epicsAlarmStatus", status));
		}
		
		//
		// change alarm time stamp
		//
		if ((timeStamp != null) && (timeStamp.length() > 0)) {
			epicsTimeStamp = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("epicsAlarmTimeStamp", timeStamp));
		}
		
		//
		// change time stamp acknowledged time
		//
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S");
        java.util.Date currentDate = new java.util.Date();
        String eventTime = sdf.format(currentDate);
        
        epicsAcknowledgeTimeStamp = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("epicsAlarmAcknTimeStamp", eventTime));
        
        ctx.modifyAttributes(channelName, modItem);
        
        
    }

}
