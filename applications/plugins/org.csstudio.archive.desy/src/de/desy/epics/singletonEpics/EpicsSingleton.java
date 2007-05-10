package de.desy.epics.singletonEpics;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_String;

import com.cosylab.epics.caj.*;

/*
     * The singleton implementation is necessary to be able to use this service from several services
     * within a Tomcat instance.
     * The Tomcat instance supports several services which use their own class loaders.
     * This way all services have no relation with each other.
     * The only possible relation can be established between a 'sigleton' service directly in the Tomcat
     * engine and the individually loaded services.
     *
     * This implementation allows several services to access the EPICS channel access service.
     * This way only ONE channel access client is used to satisfy CA requests from all services of the Tomcat instance.
     * Beware!
     * This process may never crash!
     * If this (singleton) process crashes it will crash the whole Tomcat instance!
     *
     * (C) DESY Hamburg 2003
     *
     * @author Matthias Clausen DESY/MKS-2
     * @param 
     * @return
     * @version 1.1.0
     *
     * Initial implementation by Matthias Clausen DESY/MKS-2 September-2003
     *
     * 2003-09-09   MCL check for empty channel names (like "")
     *                  this was crashing Tomcat!
     * 
     */

public class EpicsSingleton

{
    
    private static EpicsSingleton EpicsSingletonInstance;
    private CaReseourceHashTable caReseourceHashTable;
    //public final static String EPICS_CA_ACCESS_VALUE = "KRISTA2884";
    private Context caContext;
    
    private EpicsSingleton () {
        
        caReseourceHashTable = new CaReseourceHashTable ( "caRecordName", "caRecordID");
        
        initializeCAContext();
    }

    // TODO implement better error handling, singleton should not be created
    private void initializeCAContext() {
        
        //
        // set EPICS_CA specific environment variables
        //
        // since the sigleton will be running on our Tomcat server we've to hard code this
        //
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "131.169.115.236 131.169.115.237");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list","false");
        
        try
        {
            //caContext = JCALibrary.getInstance().createContext(JCALibrary.JNI_THREAD_SAFE);
            caContext = JCALibrary.getInstance().createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
            //caContext.JCALibrary.aAddr_list = "131.169.115.236 131.169.115.237";
        } catch (CAException e)
        {
            System.out.println("de.desy.epics.singleton.EpicsSingleton.initializeCAContext: failed to initialize JCA context");
            e.printStackTrace();
        }
    }
    
    public static EpicsSingleton getInstance () {
        
        if ( EpicsSingletonInstance == null) {
            System.out.println ( "de.desy.epics.singleton.EpicsSingleton: try to initialize");
            //
            // a hint from JavaMagazin to avoid that EpicsSingleton might be never instantiated
            //
            synchronized ( EpicsSingleton.class) {
                if ( EpicsSingletonInstance == null) {
                    
                    System.out.println ( "de.desy.epics.singleton.EpicsSingleton: " + property.EPICS_SINGLETON_VERSION );
                    System.out.println ( "de.desy.epics.singleton.EpicsSingleton: compiled with " + property.CHANNEL_ACCESS_JCA_VERSION_NUMBER);
                    System.out.println ( "de.desy.epics.singleton.EpicsSingleton: compiled with " + property.CHANNEL_ACCESS_CAJ_VERSION_NUMBER);
                    
                    EpicsSingletonInstance = new  EpicsSingleton();
                    System.out.println ( "de.desy.epics.singleton.EpicsSingleton: initialized");
                }
            }
        }
        return EpicsSingletonInstance;
    }
    
    
    public String get ( String recordName) {
        
        return getValueRoutine ( recordName);
    }

    public String getValueRoutine ( String recordName) {
        if ( (recordName != null) && !(recordName.equals( "")) ) {
		        String caValue = getValue (recordName);
		        return caValue;
		    } else {
		        return "ERROR";
		    }
    }
    
    
    public String set ( String putRecordName, String putRecordValue, String putWriteAccess) {
        
        return putValueRoutine ( putRecordName, putRecordValue, putWriteAccess);
    }
                        
    public String putValueRoutine ( String putRecordName, String putRecordValue, 
                    String putWriteAccess) {
        if ( (putRecordName != null) && !(putRecordName.equals( "")) ) {
		        if ( ( putWriteAccess != null) && ( putWriteAccess.equals( property.EPICS_CA_ACCESS_VALUE))) {

		            if ( putRecordValue == null) {
		                return "error: no value specified";
		            } else {
		                String caValue = setValue (putRecordName, putRecordValue);
    		            return caValue;
    		        }
    		    } else {
    		        return "error: no access";
    		    }
		  } else {
		        return "ERROR";
          }
    }

	//===============================
	//
	// EPICS specific things go here
	//
	//===============================
	
	
	public String getValue ( String deviceName)
    {
        
        Channel channel;
        
        //
        // save PV's in a hashtable in order to avoid instatiating the same PV over and over again
        //
        if ( caReseourceHashTable.getSetPVs.containsKey(deviceName)) {
            channel = (Channel)caReseourceHashTable.getSetPVs.get( deviceName);
        } else {
            try {
                System.out.println("de.desy.epics.singleton.EpicsSingleton.getValue: caSearch for: " + deviceName);
                // do the connect here !!!
                channel = caContext.createChannel( deviceName);
                caContext.pendIO(1.0);
            }
            catch (Exception e) 
                    {
    		            //System.out.println(e.getMessage());
    		            System.out.println("de.desy.epics.singleton.EpicsSingleton.getValue: error resolving PV name: " + deviceName);
    		            return "record unknown";
    		        }
    		        caReseourceHashTable.getSetPVs.put( deviceName, channel);
    	}
    	
    	//
    	// if we deal with a single value - use single string from EPICS ca
    	// else use a double array and convert it later to string
    	//
    	
    	int elementCount = channel.getElementCount();
    	
    	if ( elementCount < 2) {

	        DBR_String stringVal = null;

	        try {

	            Channel.ConnectionState state = channel.getConnectionState();
    	        // System.out.println ("EPICSSingleTon.getValue: record: " + deviceName + " state: " + state);
    	        // remove record from hash table if connect state not CONNECTETED or DISCONNECTED (old. PREV_CONN) 
    	        if ( ( state != Channel.CONNECTED) && ( state != Channel.DISCONNECTED)) {
    	            // remove entry 
    	            caReseourceHashTable.getSetPVs.remove( deviceName);
    	            return "CST!=2";
    	        }

    	        stringVal = (DBR_String)channel.get(DBR_String.TYPE, elementCount);
    	        caContext.pendIO(1.0);
    	    }
    	    catch (Exception e) 
                    {
    		            System.out.println(e.getMessage());
    		            System.out.println("de.desy.epics.singleton.EpicsSingleton.getValue: error getting value for" + deviceName);
    		            //registry.updateConnectStateInRegistry( "EPICS|"+deviceName, "invalid");
    		            return "invalid";
    		        }
        	
    	    // set the value
    	    //registry.updateValueInRegistry ( "EPICS|"+deviceName, caPV_value.value());
    	    if (stringVal == null)
    	        return "invalid";
    	    else
    	        return stringVal.getStringValue()[0];
            // set the connect state "" means >ok<
            //registry.updateConnectStateInRegistry( "EPICS|"+deviceName, "");
        } else {
            
            //System.out.println("EpicsDataSource: getValue elementCount = " + elementCount);
            DBR_Double doubleVal = null;

    	    try {
	            Channel.ConnectionState state = channel.getConnectionState();
    	        // System.out.println ("EPICSSingleTon.getValue: record: " + deviceName + " state: " + state);
    	        // remove record from hash table if connect state not CONNECTETED or DISCONNECTED (old. PREV_CONN) 
    	        if ( ( state != Channel.CONNECTED) && ( state != Channel.DISCONNECTED)) {
    	            // remove entry 
    	            caReseourceHashTable.getSetPVs.remove( deviceName);
    	            return "CST!=2";
    	        }

    	        doubleVal = (DBR_Double)channel.get(DBR_Double.TYPE, elementCount);
    	        caContext.pendIO(1.0);

    	    }
    	    catch (Exception e) 
                    {
    		            System.out.println(e.getMessage());
    		            System.out.println("de.desy.epics.singleton.EpicsSingleton.getValue: error getting double vector for" + deviceName);
    		            //registry.updateConnectStateInRegistry( "EPICS|"+deviceName, "invalid");
    		            return "invalid";
    		        }
        	
    	    // convert double to string
    	    String result = doubleVal.getDoubleValue()[0] + " ";
    	    for ( int i=1; i<elementCount; i++) {
    	        result += doubleVal.getDoubleValue()[i] + " ";
    	    }
    	    //registry.updateValueInRegistry ( "EPICS|"+deviceName, result);
    	    return result;
            // set the connect state "" means >ok<
            //registry.updateConnectStateInRegistry( "EPICS|"+deviceName, "");
        }

        // return "invalid Exit"; can never be reached due to previous return statements
    }
    
    public String setValue ( String deviceName, String newValue)
    {
        //
        // add later: if elementCount >1 we have to handle this ourselves
        // jca1 does not support String[]
        //
        
        Channel channel;
        
        //
        // save PV's in a hashtable in order to avoid instatiating the same PV over and over again
        //
        if ( caReseourceHashTable.getSetPVs.containsKey(deviceName)) {
            channel = (Channel)caReseourceHashTable.getSetPVs.get( deviceName);
        } else {
            try {
                channel = caContext.createChannel( deviceName);
                caContext.pendIO(1.0);
            }
            catch (Exception e) 
                    {
    		            System.out.println(e.getMessage());
    		            System.out.println("de.desy.epics.singleton.EpicsSingleton.setValue: error resolving PV name: " + deviceName);
    		            //registry.updateConnectStateInRegistry( "EPICS|"+deviceName, "unknown");
    		            return "unknown";
    		        }
    		        caReseourceHashTable.getSetPVs.put( deviceName, channel);
    	}
    	
    	try {
    	    channel.put( newValue);
    	    caContext.flushIO();
    	}
    	catch (Exception e) 
                {
    		        System.out.println(e.getMessage());
    		        System.out.println("de.desy.epics.singleton.EpicsSingleton.setValue: error setting value for" + deviceName);
    		        //registry.updateConnectStateInRegistry( "EPICS|"+deviceName, "put fail");
    		        return "put fail";
    		    }
    	
    	// nothing to do for the display ...

        return "OK" ; // bad error handling
    }
    

}

