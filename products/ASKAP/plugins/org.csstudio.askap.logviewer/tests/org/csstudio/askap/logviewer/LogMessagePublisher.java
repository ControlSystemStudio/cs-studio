package org.csstudio.askap.logviewer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import Ice.ObjectPrx;
import Ice.Properties;
import IceStorm.NoSuchTopic;
import IceStorm.TopicManagerPrx;
import IceStorm.TopicManagerPrxHelper;
import IceStorm.TopicPrx;
import askap.interfaces.TimeTaggedTypedValueMap;
import askap.interfaces.TypedValue;
import askap.interfaces.TypedValueString;
import askap.interfaces.TypedValueType;
import askap.interfaces.datapublisher.ITimeTaggedTypedValueMapPublisherPrx;
import askap.interfaces.logging.ILogEvent;
import askap.interfaces.logging.ILoggerPrx;
import askap.interfaces.logging.ILoggerPrxHelper;
import askap.interfaces.logging.LogLevel;

public class LogMessagePublisher {
	public static String SERVICE_NAMES[] = {"Executive", "Data", "TOM"};
	public static String ORIGINS[] = {"askap.executive", "askap.executive.xxx",
								"askap.yyy", 
								"askap.opl", "askap.opl.yyy", 
								"askap.epics", "askap.epics.zzz"};
	
	private static int i=0;
	private static final SimpleDateFormat dateformat = new SimpleDateFormat("ss");
	
	public static String LOG_LEVELS[] = {"TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL"};
	public static String ANTENNA_NAME_ARRAY[] = {"ant1", "ant2", "ant3"};

	public static void main(String[] args) throws Exception {
		String topicName = "logger";
		
		System.err.println("Publishing to " + topicName);
		
        Ice.Communicator ic = null;
        try {
        	System.err.println("Initialising ICE");
            Properties props = Ice.Util.createProperties();
            props.setProperty("Ice.Default.Locator", "IceGrid/Locator:tcp -h localhost -p 4061");
//            props.setProperty("Ice.Default.Locator", "IceGrid/Locator:tcp -h aktos02.atnf.csiro.au -p 4061");
            
            
            Ice.InitializationData id = new Ice.InitializationData();
            id.properties = props;

            ic = Ice.Util.initialize(id);
            System.err.println("ICE has been initialised");
        	
            Ice.ObjectPrx proxy = ic.stringToProxy("IceStorm/TopicManager@IceStorm.TopicManager");
            TopicManagerPrx topicManager = TopicManagerPrxHelper.checkedCast(proxy);
            
            TopicPrx topic = null;
            try {
                topic = topicManager.retrieve(topicName);
            } catch (NoSuchTopic e) {
            	topic = topicManager.create(topicName);
            }
            
            ObjectPrx pub = topic.getPublisher().ice_oneway();
            Object publisher = null;
 	        publisher = ILoggerPrxHelper.uncheckedCast(pub);

            try {
                topic = topicManager.retrieve("ioc_logger");
            } catch (NoSuchTopic e) {
            	topic = topicManager.create("ioc_logger");
            }
            ObjectPrx pub2 = topic.getPublisher().ice_oneway();
            Object publisher2 = null;
 	        publisher2 = ILoggerPrxHelper.uncheckedCast(pub2);
 	        
 	        
            while (true) {
        		publishLogMessage((ILoggerPrx) publisher, topicName);
        		
        		publishLogMessage((ILoggerPrx) publisher2, "ioc_logger");

        		Thread.sleep(1000);
        		
            	i++;
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
        	System.exit(0);
        }

	}
	
	private static void publishLogMessage(ILoggerPrx logger, String topicName) {
    	String s = "Hello world " + new Date();
    	
		String serviceName = SERVICE_NAMES[i%SERVICE_NAMES.length].replace(" ", "");
		String orign = ORIGINS[i%ORIGINS.length].replace(" ", "");
		String logLevel = LOG_LEVELS[i%LOG_LEVELS.length];
		String logMessage = logLevel + " Could not open configuration file" + i;

    	ILogEvent e = new ILogEvent();
    	e.origin = orign;
    	e.created = System.currentTimeMillis();
    	e.message = logMessage;
    	e.level = LogLevel.valueOf(logLevel);
    	e.tag = serviceName;
    	logger.send(e);
		
    	System.out.println("Publishing " + logMessage);
	}
	
	private static void publishDataMessage(ITimeTaggedTypedValueMapPublisherPrx publisher) {
		TimeTaggedTypedValueMap valueMap = new TimeTaggedTypedValueMap();
		valueMap.timestamp = System.currentTimeMillis();
		valueMap.data = new HashMap<String, TypedValue>();
		
		String s = dateformat.format(new Date());
		
		for (int j=0; j<ANTENNA_NAME_ARRAY.length; j++) {
			TypedValueString strValue = new TypedValueString(TypedValueType.TypeString, 
					"Scan " + j + "," + s);					
			valueMap.data.put(ANTENNA_NAME_ARRAY[j] + ".Scan", strValue);			
		}

		publisher.publish(valueMap);
    	System.out.println("Publishing valueMap " + s);

	}
}
