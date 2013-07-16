/*
 * Copyright (c) 2009 CSIRO Australia Telescope National Facility (ATNF) Commonwealth
 * Scientific and Industrial Research Organisation (CSIRO) PO Box 76, Epping NSW 1710,
 * Australia atnf-enquiries@csiro.au
 *
 * This file is part of the ASKAP software distribution.
 *
 * The ASKAP software distribution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite
 * 330, Boston, MA 02111-1307 USA
 */

package org.csstudio.askap.utility.pv.ice;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import Ice.Communicator;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;
import IceStorm.NoSuchTopic;
import IceStorm.TopicManagerPrx;
import IceStorm.TopicManagerPrxHelper;
import IceStorm.TopicPrx;

/**
 * @author wu049
 * @created Jun 29, 2010
 *
 * Singleton class to manage ICE stuff
 */
public class IceManager {
	private static Logger logger = Logger.getLogger(IceManager.class.getName());
	private static Communicator ic = null;
	private static Ice.Properties props = null;

	private static ObjectAdapter adapter = null;
	
	public static String ICESTORM_TOPICMANAGER_NAME = "IceStorm/TopicManager";
	public static Properties ICE_PROPERTIES = new Properties();
	public static String ADAPTOR_NAME = "RealtimeLogSubscriber";
	
	private IceManager() {
		
	}
	
	private static void initialize() {
		logger.info("Initialising ICE");
		props = Ice.Util.createProperties();
//		for (Entry<Object, Object> entry : ICE_PROPERTIES.entrySet()) {
//			props.setProperty((String)entry.getKey(), (String)entry.getValue());
//			logger.info("ICE initialize - " + entry.getKey() + "=" + entry.getValue());
//		}

		props.setProperty("Ice.Default.Locator", "IceGrid/Locator:tcp -h localhost -p 4061");
		props.setProperty("RealtimeLogSubscriber.Endpoints", "tcp");

		// Initialize a communicator with these properties.
		Ice.InitializationData id = new Ice.InitializationData();
		id.properties = props;

		ic = Ice.Util.initialize(id);
		logger.info("ICE has been initialised");
		
    	adapter = ic.createObjectAdapter(ADAPTOR_NAME);
		logger.info("ICE adapter <" + ADAPTOR_NAME + "> has been created");
	}
	
	
	public static ObjectPrx setupSubscriber(String topicName, Ice.Object callbackObj) throws Exception {
		if (ic==null)
			initialize();

		ObjectPrx subscriber = null;
        TopicPrx topic = null;
       
         try {
            Ice.ObjectPrx proxy = ic.stringToProxy(ICESTORM_TOPICMANAGER_NAME);
            TopicManagerPrx topicManager = TopicManagerPrxHelper.checkedCast(proxy);
            
            subscriber = adapter.addWithUUID(callbackObj).ice_oneway();
            
            try {
                topic = topicManager.retrieve(topicName);
            } catch (NoSuchTopic e) {
            	topic = topicManager.create(topicName);
            }
            Map<String, String> qos = new HashMap<String, String>();
            topic.subscribeAndGetPublisher(qos, subscriber);
            
            adapter.activate();
                        
        } catch (Exception e) {
        	logger.log(Level.WARNING, "Error while trying to subscribe to " + topicName, e);
        	if (topic!=null && subscriber!=null)
        		topic.unsubscribe(subscriber);
        	
        	throw e;
        }
        
        return subscriber;

	}

	public static void unsubscribe(String topicName, ObjectPrx subscriber) throws Exception {
		if (ic==null)
			initialize();

        TopicPrx topic = null;
        
         try {
            Ice.ObjectPrx proxy = ic.stringToProxy(ICESTORM_TOPICMANAGER_NAME);
            TopicManagerPrx topicManager = TopicManagerPrxHelper.checkedCast(proxy);
            
            topic = topicManager.retrieve(topicName);
            if (subscriber!=null) {
            	topic.unsubscribe(subscriber);
            	if (adapter==null)
            		return;
            	
            	if (adapter.find(subscriber.ice_getIdentity())!=null)
            		adapter.remove(subscriber.ice_getIdentity());
            }
            
        } catch (Exception e) {
        	logger.log(Level.WARNING, "Error while trying to unsubscribe to " + topicName, e);
        	throw e;
        }
	}
	
	public static String getConnectionString() {
		String str = "ICE server=";
		String iceStr = "UNKNOWN";
		
		if (ic==null) {
			for (Entry<Object, Object> entry : ICE_PROPERTIES.entrySet()) {
				if (entry.getKey().equals("Ice.Default.Locator")) {
					iceStr = (String)entry.getValue();
					break;
				}
			}
		} else {
			iceStr = ic.getDefaultLocator().toString();
		}
		
		// just extract the host and port info
        String[] arr = iceStr.split("[ \t\n\r]+");
        String host = "default";
        String port = "default";
        
        // skip first element which is "IceGrid/Locator:tcp"
        int i = 1;
		while (i < (arr.length-1)) {
			if (arr[i].trim().length() == 0) {
				i++;
				continue;
			}

			String param = arr[i].trim();
			String value = arr[++i].trim();
			
			if (param.equals("-p"))
				port = value;
			if (param.equals("-h"))
				host = value;
		}

		str = str + host + ":" + port;
		logger.log(Level.INFO, "ICE = " + str);
		
		return str;
	}
}
