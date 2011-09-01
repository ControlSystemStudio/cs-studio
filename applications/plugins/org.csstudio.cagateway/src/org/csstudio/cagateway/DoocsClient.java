package org.csstudio.cagateway;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Scanner;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ttf.doocs.clnt.EqAdr;

/**
 * 
 * This class should contain all DOOCS PVs in HashMap DPVs
 * 
 */


public class DoocsClient implements ControlSystemClient{	
    
    private static final Logger LOG = LoggerFactory.getLogger(DoocsClient.class);
	
	private static final String urlPrefix = "DOOCS";
	
	//File which DoocClient will read for translation of EPICS pv names to DOOCS pv names
	//Each line must have exactly two words separated with space - first word for EPICS pv name and second word for DOOCS pv name
	private static final String completeNamesFile = "EpicsToDoocsNames.def";
	
	private static final String initialBlackListconfigFile = "InitialBlackList.def";
	
	private static final String facilityNamesFile 	= "FACILITY.def";
	private static final String deviceNamesFile 	= "DEVICE.def";
	private static final String locationNamesFile 	= "LOCATION.def";
	private static final String propertyNamesFile 	= "PROPERTY.def";
	
	private static DoocsClient DoocsClientInstance = null;
	
	//Mock have to be replaced with real DOOCS PVs
	HashMap<String, Mock> DPVs = new HashMap<String, Mock>();
	
	//Data is loaded from a file
	HashMap<String, String> completeNames 		= new HashMap<String, String>();
	HashMap<String, String> initialBlackList	= new HashMap<String, String>();
	HashMap<String, String> facilities 			= new HashMap<String, String>();
	HashMap<String, String> locations 			= new HashMap<String, String>();
	HashMap<String, String> devices 			= new HashMap<String, String>();
	HashMap<String, String> properties 			= new HashMap<String, String>();
	HashMap<String, String> facilitiesReverse 	= new HashMap<String, String>();
	HashMap<String, String> locationsReverse 	= new HashMap<String, String>();
	HashMap<String, String> devicesReverse 		= new HashMap<String, String>();
	HashMap<String, String> propertiesReverse 	= new HashMap<String, String>();
	
	HashMap<String, GregorianCalendar> blackList		 	= new HashMap<String, GregorianCalendar>();
	
	
	public HashMap<String, GregorianCalendar> getBlackList() {
		return blackList;
	}

	public void setBlackList(HashMap<String, GregorianCalendar> blackList) {
		this.blackList = blackList;
	}
	
	public void addToBlackList ( String channel, GregorianCalendar timeStamp) {
		blackList.put(channel, timeStamp);
	}
	/**
	 * if the channel has been put onto the black list within the last checkBlackListPeriodHours
	 * just return if on the list
	 * else remove from the black list and give it another try
	 */
	public boolean checkBlacklist ( String channel) {
		// ToDo: MCL replace checkBlackListPeriodHours by preference
		final int checkBlackListPeriodHours = 12;
		final long oneHour = 1000*60*60;	// hours
//		long oneHour = 1000;	// seconds for testing
		boolean isOnBlackList = false;
		GregorianCalendar blackListTime = null;
		
		// check if on initialBlackList
		if ( initialBlackList.containsKey(channel)) {
			return true;
		}
		
		isOnBlackList = blackList.containsKey(channel);
		if ( isOnBlackList) {
			// check time
			blackListTime = blackList.get(channel);
			long timeDifference = new GregorianCalendar().getTimeInMillis() - blackListTime.getTimeInMillis();
			
			// check if timeDifference > checkBlackListPeriodHours
			if ( (timeDifference/oneHour) >  checkBlackListPeriodHours) {
				// remove from blackList
				blackList.remove(channel);
				isOnBlackList = false;
				
				LOG.warn("caGateway DOOCS: remove {} from blackList", channel);
			} else {
				isOnBlackList = true;
			}
		}
		return isOnBlackList;
	}

	private DoocsClient(){
		
		loadNamesWithMetadata(completeNames, completeNamesFile);
		
		loadSingleNames( initialBlackList, initialBlackListconfigFile);
		
		loadNames(facilities, facilitiesReverse, facilityNamesFile);
		loadNames(devices, devicesReverse, deviceNamesFile);
		loadNames(locations, locationsReverse, locationNamesFile);
		loadNames(properties, propertiesReverse, propertyNamesFile);
	}
	
	public static DoocsClient getInstance(){
		//
		// get an instance of our sigleton
		//
		if ( DoocsClientInstance == null) {
			synchronized (DoocsClient.class) {
				if (DoocsClientInstance == null) {
					DoocsClientInstance = new DoocsClient();
				}
			}
		}
		return DoocsClientInstance;	
	}
	
	private boolean loadSingleNames(HashMap<String, String> hashMap, String configFile) {
        final Bundle bundle = Activator.getDefault().getBundle();
        File loc = null;
        try {
            loc = FileLocator.getBundleFile(bundle);
        } catch (IOException e1) {
            LOG.warn("caGateway DOOCS: {} not found", configFile);
        }
        if (loc == null) {
            return false;
        }
        String definitionFilePath = new File(loc, "DefinitionFiles/" + configFile).toString();

		Scanner s = null;
		System.out.println("Open file " + definitionFilePath);
		LOG.info("caGateway DOOCS: load {}", configFile);
		try {
			s = new Scanner(new FileReader(definitionFilePath));
		} catch (FileNotFoundException e) {
			LOG.warn("caGateway DOOCS: {} not found", configFile);
			return false;
		}
		
		while(s.hasNextLine()){
			String line = s.nextLine();
			/*
			 * use white char instead!
			 */
			String[] words = line.split("(\\s)+");
			if (words.length > 1){
				System.out.println("File " +configFile +" is not written in right format @ >" + line + "<");
				return false;
			}
			if ( ! words[0].startsWith("#")) {
				// # is a comment
				hashMap.put(words[0], "invalid");
			}
		}
		return true;		
	}
	
	private boolean loadNames(HashMap<String, String> hashMap, HashMap<String, String> hashMapReverse, String configFile) {
        final Bundle bundle = Activator.getDefault().getBundle();
        File loc = null;
        try {
            loc = FileLocator.getBundleFile(bundle);
        } catch (IOException e1) {
            LOG.warn("caGateway DOOCS: {} not found", configFile);
        }
        if (loc == null) {
            return false;
        }
        String definitionFilePath = new File(loc, "DefinitionFiles/" + configFile).toString();

		Scanner s = null;
		System.out.println("Open file " + definitionFilePath);
		LOG.info("caGateway DOOCS: load {}", configFile);
		try {
			s = new Scanner(new FileReader(definitionFilePath));
		} catch (FileNotFoundException e) {
			LOG.warn("caGateway DOOCS: {} not found", configFile);
			return false;
		}
		
		while(s.hasNextLine()){
			String line = s.nextLine();
			/*
			 * use white char instead!
			 */
			String[] words = line.split("(\\s)+");
			if (words.length != 2){
				System.out.println("File " +configFile +" is not written in right format @ >" + line + "<");
				return false;
			}
			if ( ! words[0].startsWith("#")) {
				// # is a comment
				hashMap.put(words[1], words[0]);
				hashMapReverse.put(words[0], words[1]);
//				System.out.println(configFile +" add " + words[0] + " " + words[1]);
			} else {
				System.out.println(configFile +" comment " + line );
			}
		}
		return true;		
	}
	
	//ToDo: MCL
	// this method must be implemented to support also config files which include:
	/*
	 * precision
	 * lower/upper warning
	 * lower/upper alarm
	 * display limits
	 * ...
	 */
	private boolean loadNamesWithMetadata(HashMap<String, String> hashMap, String configFile) {
		HashMap<String, String> dummy 		= new HashMap<String, String>();
		return loadNames( hashMap, dummy, configFile);
	}
	
	//Instead off adding PVs DoocsClient should be able to find them
	public void addMock(Mock m){
		DPVs.put(m.getName(), m);
	}

	

	public Object findChannelName(String channelName, HashMap<String, MetaData> availableRemoteDevices, CaServer thisGatewayServer) {
		String facility = null;
		String facility_2 = null;
		String remainingString = null;
		boolean foundFacility = false;
		String doocsFacility = null;
		String doocsDevice = null;
		String doocsLocation = null;
		String doocsProperty = null;
		String doocsCompleteName = null;
		String reverseCheck = null;
		
		String epicsDoocsFacility = null;
		String epicsDoocsDevice = null;
		String epicsDoocsLocation = null;
		String epicsDoocsProperty = null;
		EqAdr doocsEqAdr = null;
		
		// is channel on black list?
		
		if ( checkBlacklist(channelName)) {
			// ToDo: MCL
			// check here how long this channel is already on black list
			// maybe retry
//			LOG.debug( this, "caGateway DOOCS: findChannel: -sorry- channel is on black list : " + channelName);
			return null;
		}
		
		
		// already defined in HashMap??
		if (availableRemoteDevices.get(channelName) != null) {
//			System.out.println("findChannel:channel already defined in HashMap: " + channelName);
			return "found in HashMap";	//DOOCS name not available here!
		}
		
		/*
		 * only look for DOOCS channel of the requested channel starts with a string defined in
		 * the facility hash map
		 */
		
		// check for separator
		if ( channelName.contains(":")) {
			// case if facility only consists of TTF:device:loation:property
			facility = channelName.substring(0, channelName.indexOf(":"));
			remainingString = channelName.substring( channelName.indexOf(":")+1, channelName.length());
			if ( remainingString.contains(":")) {
				// case if facility consists of TTF:VAC:device:loation:property
				facility_2 = facility + ":" + remainingString.substring(0, remainingString.indexOf(":"));
			}
		} else {
//			System.out.println("findChannel: missing [:]");
			return null;
		}
		
		// ok separator is available
		// is it a DOOCS Facility?
		foundFacility = false;
//		System.out.println("findChannel: try to find: " + facility_2);
		doocsFacility = facilities.get(facility_2);
		if ( doocsFacility != null) {
			foundFacility = true;
//			System.out.println("findChannel: found facility " + facility_2);
		} else {
//			System.out.println("findChannel: try to find: " + facility);
			doocsFacility = facilities.get(facility);
			if ( doocsFacility != null) {
				foundFacility = true;
//				System.out.println("findChannel: found facility " + facility);
			}
		}
		
		if ( foundFacility) {
//			System.out.println("findChannel: found facility: " + doocsFacility);
			// check if channel defined in completeNames
			doocsCompleteName = completeNames.get(channelName);
			if ( doocsCompleteName != null) {
				// check if a 'real' DOOCS name
				doocsEqAdr = new EqAdr(doocsCompleteName);
				if ( doocsEqAdr != null) {
					// create/ fill meta data
					MetaData metaData = new MetaData(channelName, doocsCompleteName, urlPrefix);
					metaData.set_objectReference( doocsEqAdr);
					metaData.set_facility(doocsFacility);
					metaData.setDisplayValues(1e-8, 1.0);
					metaData.set_precision( (short)7);
					metaData.set_egu("mbar");
					metaData.set_descriptor("DOOCS device dynamically created");
					
					CaServer.getGatewayInstance().addAvailableRemoteDevices(channelName, metaData);
					/*
					 * precision
					 * lower/upper warning
					 * lower/upper alarm
					 */
					DoocsFloatingPV newChannel = new DoocsFloatingPV( channelName, doocsCompleteName, null, metaData);
					thisGatewayServer.getServer().registerProcessVaribale(newChannel);
					LOG.debug("caGateway DOOCS: Created DOOCS-C channel: {}", doocsCompleteName);
					return doocsCompleteName;
				} else {
//					System.out.println("Cannot connect to DOOCS-C channel: " + doocsCompleteName);
					return null;
				}

			} else {
				// now split the name into pieces
				String[] words = channelName.split(":");
				if ( words.length == 4) {
					epicsDoocsFacility = words[0];
					epicsDoocsDevice = words[1];
					epicsDoocsLocation = words[2];
					epicsDoocsProperty = words[3];
//					System.out.println("found: " + epicsDoocsFacility + ":" + epicsDoocsDevice + ":" + epicsDoocsLocation + ":" + epicsDoocsProperty);
				} else if ( words.length == 5) {
					epicsDoocsFacility = words[0] + ":" + words[1];
					epicsDoocsDevice = words[2];
					epicsDoocsLocation = words[3];
					epicsDoocsProperty = words[4];
//					System.out.println("found: " + epicsDoocsFacility + ":" + epicsDoocsDevice + ":" + epicsDoocsLocation + ":" + epicsDoocsProperty);
				} else {
					LOG.debug("caGateway DOOCS: incorrect number of separators - must be 4 or 5 ! in {}", channelName);
					return null;
				}
				doocsFacility = facilities.get(epicsDoocsFacility);
				if ( doocsFacility == null) {
					doocsFacility = epicsDoocsFacility;
					LOG.debug("caGateway DOOCS: error translating FACILITY: [{}]", epicsDoocsFacility);
					
					reverseCheck = facilitiesReverse.get(epicsDoocsFacility);
					if ( reverseCheck != null) {
						System.out.println("use : " + reverseCheck + " instead in EPICS channel name! " + channelName);
					}
					LOG.debug( "caGateway DOOCS: error translating FACILITY: [{}] in {} probably no property defined - wromg umber of separated (:) strings", epicsDoocsFacility, channelName);
					LOG.warn( "caGateway DOOCS: Error  put {} on blackList", channelName);
		        	addToBlackList(channelName, new GregorianCalendar());
					return null;
				} else {
//					System.out.println("translated FACILITY: " + epicsDoocsFacility + " into: " + doocsFacility);
				}
					
				doocsDevice = devices.get(epicsDoocsDevice);
				if ( doocsDevice == null) {
					doocsDevice = epicsDoocsDevice;
					LOG.debug("caGateway DOOCS: error translating DEVICE: [{}]", epicsDoocsDevice);
					reverseCheck = devicesReverse.get(epicsDoocsDevice);
					if ( reverseCheck != null) {
						LOG.debug("caGateway DOOCS: use : {} instead in EPICS channel name! {}", reverseCheck, channelName);
					}
				} else {
//					System.out.println("translated DEVICE: " + epicsDoocsDevice + " into: " + doocsDevice);
				}
				
				doocsLocation = locations.get(epicsDoocsLocation);
				if ( doocsLocation == null) {
					// use location 'as is' and give it a try
					doocsLocation = epicsDoocsLocation;
					LOG.debug("caGateway DOOCS: error translating LOCATION: [{}]", epicsDoocsLocation);
					reverseCheck = locationsReverse.get(epicsDoocsLocation);
					if ( reverseCheck != null) {
						LOG.debug("caGateway DOOCS: use : {} instead in EPICS channel name! {}",reverseCheck , channelName);
					}
				} else {
//					System.out.println("translated LOCATION: " + epicsDoocsLocation + " into: " + doocsLocation);
				}
					
				doocsProperty = properties.get(epicsDoocsProperty);
				reverseCheck = propertiesReverse.get(epicsDoocsProperty);
				// the property MUST be corret!
				// otherwise something like :P_ai will be translated into a DOOCS property called /P_ai which makes no sense
				// channel will be pu on blackList
				// for test purposes the invalid DOOCS property Px has been added
				if ( (doocsProperty == null) && ( reverseCheck == null)) {
					// ok the property was not entered in 'the EPICS way'
					// and not the DOOCS way
					LOG.debug("caGateway DOOCS: error translating PROPERTY: [{}] in {} do NOT create DOOCS channel", epicsDoocsProperty, channelName);
					addToBlackList(channelName, new GregorianCalendar());
					return null;
				} else if ( doocsProperty == null){
					// so the DOOCS variant must be correctly entered
					doocsProperty = reverseCheck;
				}
					
				doocsCompleteName = doocsFacility + "/" + doocsDevice + "/" + doocsLocation + "/" + doocsProperty;
//				System.out.println("found EPICS name:  " + channelName);
//				System.out.println("created DOOCS name: " + doocsCompleteName);
				doocsEqAdr = new EqAdr(doocsCompleteName);
				if ( doocsEqAdr != null) {
					
					// create/ fill meta data
					MetaData metaData = new MetaData(channelName, doocsCompleteName, urlPrefix);
					metaData.set_objectReference( doocsEqAdr);
					metaData.set_facility(doocsFacility);
					metaData.setDisplayValues(1e-8, 1.0);
					metaData.set_precision( (short)7);
					metaData.set_egu("mbar");
					metaData.set_descriptor("DOOCS device name from list");
					
					CaServer.getGatewayInstance().addAvailableRemoteDevices(channelName, metaData);
					/*
					 * precision
					 * lower/upper warning
					 * lower/upper alarm
					 */
					DoocsFloatingPV newChannel = new DoocsFloatingPV( channelName, doocsCompleteName, null, metaData) ;
					thisGatewayServer.getServer().registerProcessVaribale(newChannel);
					LOG.debug("caGateway DOOCS: Created DOOCS channel: {}", doocsCompleteName);
					return doocsCompleteName;
				} else {
					LOG.debug("Cannot connect to DOOCS channel: {}", doocsCompleteName);
					return null;
				}
			}
		}
		else {
			return null;
		}
	}


	@Override
    public Object findChannelName(String channelName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public void registerObjectInRemoteControlSystem(
			RemoteControlSystemCallback callback, Object object) {
		// TODO Auto-generated method stub	
	}
}