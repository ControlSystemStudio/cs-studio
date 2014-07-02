package org.csstudio.askap.utility.icemanager;

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


import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import Ice.Communicator;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;
import IceStorm.NoSuchTopic;
import IceStorm.TopicManagerPrx;
import IceStorm.TopicManagerPrxHelper;
import IceStorm.TopicPrx;
import askap.interfaces.ephem.ISourceSearchPrx;
import askap.interfaces.ephem.ISourceSearchPrxHelper;
import askap.interfaces.executive.IExecutiveServicePrx;
import askap.interfaces.executive.IExecutiveServicePrxHelper;
import askap.interfaces.logging.ILogQueryPrx;
import askap.interfaces.logging.ILogQueryPrxHelper;
import askap.interfaces.logging.ILoggerPrx;
import askap.interfaces.logging.ILoggerPrxHelper;
import askap.interfaces.monitoring.MonitoringProviderPrx;
import askap.interfaces.monitoring.MonitoringProviderPrxHelper;
import askap.interfaces.pksdatacapture.IPksDataCaptureServicePrx;
import askap.interfaces.pksdatacapture.IPksDataCaptureServicePrxHelper;
import askap.interfaces.schedblock.IObsProgramServicePrx;
import askap.interfaces.schedblock.IObsProgramServicePrxHelper;
import askap.interfaces.schedblock.ISBTemplateServicePrx;
import askap.interfaces.schedblock.ISBTemplateServicePrxHelper;
import askap.interfaces.schedblock.ISchedulingBlockServicePrx;
import askap.interfaces.schedblock.ISchedulingBlockServicePrxHelper;
import atnf.atoms.mon.comms.MoniCAIcePrx;
import atnf.atoms.mon.comms.MoniCAIcePrxHelper;

/**
 * @author wu049
 * @created Jun 29, 2010
 *
 * Singleton class to manage ICE stuff
 */
public class IceManager {
	private static Logger logger = Logger.getLogger(IceManager.class.getName());
	private static Communicator ic = null;
	
	// I can't seem to get ICE to tell me if an ObjectAdaptor has already been registered.
	// So I'll remember it myself.
	private static Map<String, ObjectAdapter> ADAPTOR_MAP = new HashMap<String, ObjectAdapter>();
	

	/*
	 * This maps is MonitoringProvider interface name to a MonitoringPointManager
	 * So for each MonitoringProvider interface, we only need to create one MonitoringProviderPrx.
	 * We'll update all the listener of monitoring points listeners
	 */
	private static Map<String, MonitoringPointManager> MONITORING_MAP 
										= new HashMap<String, MonitoringPointManager>(); 

	private IceManager() {
		
	}
	
	private static void initialize() {
		logger.info("Initialising ICE");
		
		Ice.Properties props = Ice.Util.createProperties();
		// getIceProperties() returns a Map<String,String>
        for (Map.Entry<String, String> entry : Preferences.getIceProperties().entrySet()) {
                        props.setProperty(entry.getKey(), entry.getValue());
        }
        // Initialize a communicator with these properties.
        Ice.InitializationData id = new Ice.InitializationData();
        id.properties = props;
        ic = Ice.Util.initialize(id);		
		            
		logger.info("ICE has been initialised");
	}
		
	public static boolean icePing(String iceName) {
		if (ic==null)
			initialize();
		
		//long currentTime = 0;
		try {			
			Ice.ObjectPrx proxy = ic.stringToProxy(iceName);
			proxy.ice_ping();
			return true;			
		} catch (RuntimeException e) {
			logger.log(Level.WARNING, "Could not ice_ping() on " + iceName + ": " + e.getMessage());
		}
		
		return false;
	}
	
	public static ILogQueryPrx getLogQueryProxy(String name) throws Exception{
		if (ic==null)
			initialize();

		Ice.ObjectPrx proxy = ic.stringToProxy(name);
		ILogQueryPrx logQueryProxy = null;
		
		try {
			logQueryProxy = ILogQueryPrxHelper.checkedCast(proxy);
	        
			if (logQueryProxy == null) {
				logger.log(Level.WARNING, "Invalid proxy for " + name); 
				throw new Exception("Invalid proxy for " + name);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not creat proxy for " + name, e);
			throw new Exception("Could not creat proxy for " + name, e);
		}			

		return logQueryProxy;
	}
	
	public static IExecutiveServicePrx getExecutiveProxy(String name) throws Exception {
		if (ic==null)
			initialize();

		Ice.ObjectPrx proxy = ic.stringToProxy(name);
		IExecutiveServicePrx executiveProxy = null;
		
		try {
			executiveProxy = IExecutiveServicePrxHelper.checkedCast(proxy);
	        
			if (executiveProxy == null) {
				logger.log(Level.WARNING, "Invalid proxy for " + name); 
				throw new Exception("Invalid proxy for " + name);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not creat proxy for " + name, e);
			throw new Exception("Could not creat proxy for " + name, e);
		}			

		return executiveProxy;
		
	}
	
	/**
	 * @return
	 */
	public static MoniCAIcePrx getMoniCAIce(String name) throws Exception{
		if (ic==null)
			initialize();

		Ice.ObjectPrx proxy = ic.stringToProxy(name);
		MoniCAIcePrx monitorProxy = null;
		
		try {
			monitorProxy = MoniCAIcePrxHelper.checkedCast(proxy);
	        
			if (monitorProxy == null) {
				logger.log(Level.WARNING, "Invalid proxy for " + name); 
				throw new Exception("Invalid proxy for " + name);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not creat proxy for " + name + ": " + e.getMessage());
			throw new Exception("Could not creat proxy for " + name, e);
		}			

		return monitorProxy;
	}
	
	public static ObjectPrx setupSubscriber(String topicName, String adaptorName, Ice.Object callbackObj) throws Exception {
		if (ic==null)
			initialize();

		ObjectPrx subscriber = null;
        TopicPrx topic = null;
       
         try {
            Ice.ObjectPrx proxy = ic.stringToProxy(Preferences.getIceStormTopicManagerName());
            TopicManagerPrx topicManager = TopicManagerPrxHelper.checkedCast(proxy);
            
            ObjectAdapter adapter = ADAPTOR_MAP.get(adaptorName);
            if (adapter==null) {
            	adapter = ic.createObjectAdapter(adaptorName);
            	ADAPTOR_MAP.put(adaptorName, adapter);
            }
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

	public static void unsubscribe(String topicName, String adaptorName, ObjectPrx subscriber) throws Exception {
		if (ic==null)
			initialize();

        TopicPrx topic = null;
        
         try {
            Ice.ObjectPrx proxy = ic.stringToProxy(Preferences.getIceStormTopicManagerName());
            TopicManagerPrx topicManager = TopicManagerPrxHelper.checkedCast(proxy);
            
            topic = topicManager.retrieve(topicName);
            if (subscriber!=null) {
            	topic.unsubscribe(subscriber);
            	ObjectAdapter adapter = ADAPTOR_MAP.get(adaptorName);
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
	
	public static ISBTemplateServicePrx getSBTemplateProxy(String name) throws Exception{
		if (ic==null)
			initialize();

		Ice.ObjectPrx proxy = ic.stringToProxy(name);
		ISBTemplateServicePrx sbTemplateProxy = null;
		
		try {
			sbTemplateProxy = ISBTemplateServicePrxHelper.checkedCast(proxy);
	        
			if (sbTemplateProxy == null) {
				logger.log(Level.WARNING, "Invalid proxy for " + name); 
				throw new Exception("Invalid proxy for " + name);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not creat proxy for " + name + ": " + e.getMessage());
			throw new Exception("Could not creat proxy for " + name, e);
		}			

		return sbTemplateProxy;
	}

	
	public static ISchedulingBlockServicePrx getSBServiceProxy(String name) throws Exception{
		if (ic==null)
			initialize();

		Ice.ObjectPrx proxy = ic.stringToProxy(name);
		ISchedulingBlockServicePrx sbServiceProxy = null;
		
		try {
			sbServiceProxy = ISchedulingBlockServicePrxHelper.checkedCast(proxy);
	        
			if (sbServiceProxy == null) {
				logger.log(Level.WARNING, "Invalid proxy for " + name); 
				throw new Exception("Invalid proxy for " + name);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not creat proxy for " + name + ": " + e.getMessage());
			throw new Exception("Could not creat proxy for " + name, e);
		}			

		return sbServiceProxy;
	}

	/**
	 * @param name
	 * @return
	 */
	public static IObsProgramServicePrx getObsProgramServiceProxy(String name) throws Exception {
		if (ic==null)
			initialize();

		Ice.ObjectPrx proxy = ic.stringToProxy(name);
		IObsProgramServicePrx obsProgramProxy = null;
		
		try {
			obsProgramProxy = IObsProgramServicePrxHelper.checkedCast(proxy);
	        
			if (obsProgramProxy == null) {
				logger.log(Level.WARNING, "Invalid proxy for " + name); 
				throw new Exception("Invalid proxy for " + name);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not creat proxy for " + name + ": " + e.getMessage());
			throw new Exception("Could not creat proxy for " + name, e);
		}			

		return obsProgramProxy;
	}
	
	public static ILoggerPrx getLogMessagePublisher(String topicName) throws Exception {
		if (ic==null)
			initialize();

        TopicPrx topic = null;
       
         try {
            Ice.ObjectPrx proxy = ic.stringToProxy(Preferences.getIceStormTopicManagerName());
            TopicManagerPrx topicManager = TopicManagerPrxHelper.checkedCast(proxy);
            
            try {
                topic = topicManager.retrieve(topicName);
            } catch (NoSuchTopic e) {
            	topic = topicManager.create(topicName);
            }
            
            ObjectPrx pub = topic.getPublisher().ice_oneway();
            Object publisher = ILoggerPrxHelper.uncheckedCast(pub);
                
            return (ILoggerPrx) publisher;
            
        } catch (Exception e) {
        	logger.log(Level.WARNING, "Error while trying to subscribe to " + topicName, e);
        	throw e;
        }        
	}

	/**
	 * @param eMPHEMERIS_ICE_NAME
	 * @return
	 */
	public static ISourceSearchPrx getEphemerisProxy(String name) throws Exception {
		if (ic==null)
			initialize();

		Ice.ObjectPrx proxy = ic.stringToProxy(name);
		ISourceSearchPrx sourceSearchProxy = null;
		
		try {
			sourceSearchProxy = ISourceSearchPrxHelper.checkedCast(proxy);
	        
			if (sourceSearchProxy == null) {
				logger.log(Level.WARNING, "Invalid proxy for " + name); 
				throw new Exception("Invalid proxy for " + name);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not creat proxy for " + name + ": " + e.getMessage());
			throw new Exception("Could not creat proxy for " + name, e);
		}			

		return sourceSearchProxy;
	}

	protected static MonitoringProviderPrx getMonitoringProvider(String name) throws Exception {
		if (ic==null)
			initialize();

		Ice.ObjectPrx proxy = ic.stringToProxy(name);
		
		MonitoringProviderPrx monitoringProxy = MonitoringProviderPrxHelper.checkedCast(proxy);
		try {
	        
			if (monitoringProxy == null) {
				logger.log(Level.WARNING, "Invalid proxy for " + name); 
				throw new Exception("Invalid proxy for " + name);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not creat proxy for " + name + ": " + e.getMessage());
			throw new Exception("Could not creat proxy for " + name, e);
		}			

		return monitoringProxy;
	}

	public static void addPointListener(String pointNames[], MonitorPointListener listener, String adaptorName) 
		throws Exception {
		MonitoringPointManager manager = MONITORING_MAP.get(adaptorName);
		
		if (manager==null) {
			manager = new MonitoringPointManager(adaptorName);
			MONITORING_MAP.put(adaptorName, manager);
		}
		
		manager.addListener(pointNames, listener);
	}
	
	public static void removePointListener(String pointNames[], MonitorPointListener listener, String adaptorName) {
		MonitoringPointManager manager = MONITORING_MAP.get(adaptorName);
		
		if (manager!=null) {
			manager.remove(pointNames, listener);
		}
		
	}
	
	public static void removeMonitoringPointManager(String adaptorName) {
		MONITORING_MAP.remove(adaptorName);
	}
	
	public static IPksDataCaptureServicePrx getDataCaptureProxy(String name) throws Exception {
		if (ic==null)
			initialize();

		Ice.ObjectPrx proxy = ic.stringToProxy(name);
		IPksDataCaptureServicePrx dataCaptureProxy = null;
		
		try {
			dataCaptureProxy = IPksDataCaptureServicePrxHelper.checkedCast(proxy);
	        
			if (dataCaptureProxy == null) {
				logger.log(Level.WARNING, "Invalid proxy for " + name); 
				throw new Exception("Invalid proxy for " + name);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not creat proxy for " + name + ": " + e.getMessage());
			throw new Exception("Could not creat proxy for " + name, e);
		}			

		return dataCaptureProxy;
		
	}

	public static String getConnectionString() {
		String str = "ICE server=";
		String iceStr = "UNKNOWN";
		
		if (ic==null) {
			iceStr = "DISCONNECTED";
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
