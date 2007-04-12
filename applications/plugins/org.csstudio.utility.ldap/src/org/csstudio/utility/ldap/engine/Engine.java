package org.csstudio.utility.ldap.engine;

import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Vector;

import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.csstudio.utility.ldap.Activator;
import org.csstudio.utility.ldap.connection.LDAPConnector;
import org.csstudio.utility.ldap.preference.PreferenceConstants;

public class Engine extends Thread {

	private static 		Engine thisEngine = null;
	private boolean 	doWrite = false;
	private DirContext 	ctx;
	private Vector<WriteRequest>	writeVector;
	/**
	 * @param args
	 */
	public void run () {
		Integer intSleepTimer = null;

		//
		// initialize LDAP connection (dir context
		//
//		ctx = LDAPReader.initial();
		ctx = new LDAPConnector().getDirContext();

		while (true) {
			//
			// do the work actually prepared
			//
			if (doWrite) {
				performLdapWrite();
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
    
    synchronized public void addLdapWriteRequest(String attribute, String channel, String value) {
    	
		WriteRequest writeRequest = new WriteRequest( attribute, channel, value);
		//
		// add request to vector
		//
		writeVector.add(writeRequest);
		doWrite = true;
	}
    
    private void performLdapWrite() {
    	WriteRequest writeRequest;
    	ModificationItem[] modItem = null;
    	int i = 0;
    	String channel;
    	
    	channel = null;
    	
    	for ( Enumeration eWriteVector = writeVector.elements(); eWriteVector.hasMoreElements(); ){ 
    		Object tempObject = eWriteVector.nextElement();
    		writeRequest = (WriteRequest)tempObject;
    		
    		//
    		// prepare LDAP request for all entries matching the same channel
    		//
    		String channel = writeRequest.getChannel();
    		
    		modItem[i++] = new ModificationItem( DirContext.REPLACE_ATTRIBUTE,	
    				new BasicAttribute(	writeRequest.getAttribute(), writeRequest.getValue()));
    		
    		//setLdapValue (writeRequest.getAttribute(), writeRequest.getChannel(), writeRequest.getValue(), writeRequest.getTimeStamp());
    		writeVector.remove(tempObject);
    	}
    	
    	doWrite = false;
    }

    public void setLdapValue ( String channel, String severity, String status, String timeStamp) {
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

        try {
			ctx.modifyAttributes(channelName, modItem);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    }
    private class WriteRequest {
    	private String 	attribute = null;
    	private String 	channel	= null;
    	private String	value = null;
    	
    	private WriteRequest ( String attribute, String channel, String value) {
    		
    		this.attribute = attribute;
    		this.channel = channel;
    		this.value = value;
    	}
    	
    	public String getAttribute () {
    		return this.attribute;
    	}
    	
    	public String getChannel () {
    		return this.channel;
    	}
    	
    	public String getValue () {
    		return this.value;
    	}

    }

}
