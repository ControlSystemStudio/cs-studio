package org.csstudio.utility.ldap.engine;

import java.text.SimpleDateFormat;

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.csstudio.utility.ldap.Activator;
import org.csstudio.utility.ldap.preference.PreferenceConstants;

public class Engine extends Thread {

	private static 		Engine thisEngine = null;
	private boolean 	doWork = false;
	/**
	 * @param args
	 */
	public void run () {
		Integer intSleepTimer = null;
		DirContext ctx;
		
		//
		// initialize LDAP connection (dir context
		//
		
		
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
    
    public void setSeverityStatusTimestamp ( String severity, String status, ) {
		ModificationItem epicsStatus, epicsSeverity, epicsTimeStamp;

		//
		// change severity if value is entered
		//
		if ((recordSeverity.getText() != null)
				&& (recordSeverity.getText().length() > 0)) {
			epicsSeverity = new ModificationItem(
					DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute(
							"epicsAlarmSeverity",
							recordSeverity.getText()));
			//
			// create modification item 'array' with single
			// entry
			//
			ModificationItem[] modItems = new ModificationItem[] { epicsSeverity };
			ctx.modifyAttributes(result.getName(),modItems);
			message += "New: epicsAlarmSeverity: "
					+ recordSeverity.getText() + "\n";
		}

		//
		// change status if value is entered
		//
		if ((recordStatus.getText() != null)
				&& (recordStatus.getText().length() > 0)) {
			epicsStatus = new ModificationItem(
					DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("epicsAlarmStatus",
							recordStatus.getText()));
			//
			// create modification item 'array' with single
			// entry
			//
			ModificationItem[] modItems = new ModificationItem[] { epicsStatus };
			ctx.modifyAttributes(result.getName(),modItems);
			message += "New: epicsAlarmStatus: "
					+ recordStatus.getText() + "\n";
		}
		
		//
		// change time stamp in any case
		//
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S");
        java.util.Date currentDate = new java.util.Date();
        String eventTime = sdf.format(currentDate);
        
		epicsTimeStamp = new ModificationItem(
				DirContext.REPLACE_ATTRIBUTE,
				new BasicAttribute("epicsAlarmTimeStamp",
						eventTime));
		//
		// create modification item 'array' with single
		// entry
		//
		ModificationItem[] modItems = new ModificationItem[] { epicsTimeStamp };
		ctx.modifyAttributes(result.getName(),modItems);
		message += "New: epicsAlarmTimeStamp: "
				+ eventTime + "\n";
    }

}
