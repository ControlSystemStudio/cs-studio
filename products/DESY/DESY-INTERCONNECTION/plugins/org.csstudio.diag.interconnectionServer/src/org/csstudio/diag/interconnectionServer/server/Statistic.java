package org.csstudio.diag.interconnectionServer.server;

import java.util.GregorianCalendar;
import java.util.*;
import java.text.*;

public class Statistic {
	
	private static Statistic statisticInstance = null;
	Hashtable<String,StatisticContent>	connectionList	= null;
	int	totalNumberOfConnections	= 0;
	int totalNumberOfIncomingMessages	= 0;
	int totalNumberOfOutgoingMessages	= 0;
	int numberOfJmsServerFailover = -1;
	
	public Statistic () {
		//
		// initialize hash table
		//
		connectionList  = new Hashtable<String,StatisticContent>();
		
	}
	
	public static Statistic getInstance() {
		//
		// get an instance of our sigleton
		//
		if ( statisticInstance == null) {
			synchronized (Statistic.class) {
				if (statisticInstance == null) {
					statisticInstance = new Statistic();
				}
			}
		}
		return statisticInstance;
	}
	
	public int getTotalNumberOfIncomingMessages() {
		return this.totalNumberOfIncomingMessages;
	}
	
	public int getTotalNumberOfOutgoingMessages() {
		return this.totalNumberOfOutgoingMessages;
	}
	
	public void incrementNumberOfJmsServerFailover () {
		numberOfJmsServerFailover++;
	}
	
	public int getNumberOfJmsServerFailover () {
		return numberOfJmsServerFailover;
	}
	
	/*
	public Statistic ( String statisticID, String lastMessage, int lastMessageSize, String lastCommand ) {
	}
	*/
		
		public StatisticContent getContentObject ( String statisticId) {
			//
			// search for content object with id statisticId
			//
			StatisticContent	contentObject = null;
			if( connectionList.containsKey( statisticId)) {
				contentObject = (StatisticContent) connectionList.get( statisticId);
			} else {
				//
				// create new statistic object
				//
				contentObject = new StatisticContent();
				connectionList.put( statisticId, contentObject);
			}
			
			return contentObject;
		}
		

	
	public class StatisticContent {
		String host			= null;
		int	port			= 0;
		String	lastMessage	= null;
		int lastMessageSize = 0;
		int accumulatedMessageSize = 0;
		int numberOfIncomingMessages = 0;
		int numberOfOutgoingMessages = 0;
		String lastCommand = null;
		GregorianCalendar timeStarted = null;
		GregorianCalendar timeLastReceived = null;
		GregorianCalendar timeLastCommandSent = null;
		GregorianCalendar timeLastBeaconReceived = null;
		GregorianCalendar timeLastErrorOccured = null;
		int errorCounter = 0;
		boolean connectState = false;
		
		public StatisticContent () {
			//
			// init time
			//
			this.timeStarted = new GregorianCalendar();
			this.timeLastReceived = new GregorianCalendar(1970,1,1);
			this.timeLastCommandSent = new GregorianCalendar(1970,1,1);
			this.timeLastBeaconReceived = new GregorianCalendar(1970,1,1);
			this.timeLastErrorOccured = new GregorianCalendar(1970,1,1);
			
		}
		
		public void setTime (Boolean received) {
			//
			// init time
			//
			if ( received) {
				this.timeLastReceived = new GregorianCalendar();
				numberOfIncomingMessages++;
				totalNumberOfIncomingMessages++;
			} else {
				this.timeLastCommandSent = new GregorianCalendar();
				numberOfOutgoingMessages++;
				totalNumberOfOutgoingMessages++;
			}
			
		}
		
		public void setBeaconTime ( ) {
			//
			// init time
			//
			this.timeLastBeaconReceived = new GregorianCalendar();
		}
		
		public void setHost ( String host) {
			
			this.host = host;
		}
		
		public void setPort ( int port) {
			
			this.port = port;
		}
		
		public void setLastMessage ( String lastMessage) {
			
			this.lastMessage = lastMessage;
		}
		
		public void setLastMessageSize ( int lastMessageSize) {
			
			this.lastMessageSize = lastMessageSize;
			this.accumulatedMessageSize += lastMessageSize;
		}
		
		public void setConnectState ( boolean state) {
			this.connectState = state;
		}
		
		
		public String dateToString ( GregorianCalendar gregorsDate) {
			
			//
			// convert Gregorian date into string
			//
			//TODO: use other time format - actually : DD-MM-YYYY
			Date d = gregorsDate.getTime();
			SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.S" );
		    //DateFormat df = DateFormat.getDateInstance();
		    return df.format(d);
		}
		
		public long dateToLong ( GregorianCalendar gregorsDate) {
			
			//
			// convert Gregorian date into long
			//
			
		    return gregorsDate.getTime().getTime();
		}
		
		public int gregorianTimeDifference ( GregorianCalendar fromTime, GregorianCalendar toTime) {
			//
			// calculate time difference
			//
			Date fromDate = fromTime.getTime();
			Date toDate = toTime.getTime();
			long fromLong = fromDate.getTime();
			long toLong = toDate.getTime();
			long timeDifference = toLong - fromLong;
			int intDiff = (int)timeDifference;
			return intDiff;
		}
		
		public int gregorianTimeDifferenceFromNow ( GregorianCalendar fromTime) {
			//
			// time diff from now
			//
			return gregorianTimeDifference ( fromTime, new GregorianCalendar());
		}
		
		public void incrementErrorCounter () {
			this.errorCounter++;
			this.timeLastErrorOccured = new GregorianCalendar();
		}
		
		public int getErrorCounter () {
			return this.errorCounter;
		}
		
		public GregorianCalendar getTimeStarted () {
			return this.timeStarted;
		}
		
		public GregorianCalendar getTimeLastReceived () {
			return this.timeLastReceived;
		}
		
		public GregorianCalendar getTimeLastCommandSent () {
			return this.timeLastCommandSent;
		}
		
		public GregorianCalendar getTimeLastBeaconReceived () {
			return this.timeLastBeaconReceived;
		}
		
		public GregorianCalendar getTimeLastErrorOccured () {
			return this.timeLastErrorOccured;
		}
		
		public String getCurrentConnectState () {
			if( this.connectState) {
				return "connected";
			} else {
				return "disconnected";
			}
		}
		
		public String getStatisticId () {
			return host + ":" + port;
		}
		

	}
	
	public void createStatisticPrintout () {
		 System.out.println("Total incomin messages     	= " + this.totalNumberOfIncomingMessages);
		 System.out.println("Total outgoing messages     	= " + this.totalNumberOfOutgoingMessages);
		 System.out.println("");
		 
		 Enumeration connections = this.connectionList.elements();
		 while (connections.hasMoreElements()) {
			 StatisticContent thisContent = (StatisticContent)connections.nextElement();
			 System.out.println("---------- statistische Auswertung ---------------");
			 System.out.println("Host:Port: " +  thisContent.host + ":" + thisContent.port);
			 System.out.println("Current ConnectState			: " + thisContent.getCurrentConnectState());
			 System.out.println("Number of Incoming Messages 	: " + thisContent.numberOfIncomingMessages);
			 System.out.println("Number of Outgoing Messages 	: " + thisContent.numberOfOutgoingMessages);
			 System.out.println("Number of Errors			 	: " + thisContent.errorCounter);
			 System.out.println("Last Message				 	: " + thisContent.lastMessage);
			 System.out.println("Last Message Size			 	: " + thisContent.lastMessageSize);
			 System.out.println("Accumulated Message Size		: " + thisContent.accumulatedMessageSize);
			 System.out.println("Start Time         	: " + thisContent.dateToString(thisContent.timeStarted));
			 System.out.println("Last Beacon Time		: " + thisContent.dateToString(thisContent.timeLastBeaconReceived));
			 System.out.println("Last Message Received  : " + thisContent.dateToString(thisContent.timeLastReceived));
			 System.out.println("Last Command Sent Time : " + thisContent.dateToString(thisContent.timeLastCommandSent));
			 System.out.println("Last Error Occured     : " + thisContent.dateToString(thisContent.timeLastErrorOccured));
			 System.out.println("");
		 }
		
		
	}
	
	public String getStatisticAsString () {
		String result = "";
		result += "\nTotal incomin messages     	= " + this.totalNumberOfIncomingMessages ;
		result += "\nTotal outgoing messages     	= " + this.totalNumberOfOutgoingMessages ;
		result += "\n" ;
		 
		 Enumeration connections = this.connectionList.elements( );
		 while (connections.hasMoreElements()) {
			 StatisticContent thisContent = (StatisticContent)connections.nextElement();
			result += "\n---------- statistische Auswertung ---------------" ;
			result += "\nHost:Port: " +  thisContent.host + ":" + thisContent.port ;
			result += "\nCurrent ConnectState			: " + thisContent.getCurrentConnectState() ;
			result += "\nNumber of Incoming Messages 	: " + thisContent.numberOfIncomingMessages ;
			result += "\nNumber of Outgoing Messages 	: " + thisContent.numberOfOutgoingMessages ;
			result += "\nNumber of Errors			 	: " + thisContent.errorCounter ;
			result += "\nLast Message				 	: " + thisContent.lastMessage ;
			result += "\nLast Message Size			 	: " + thisContent.lastMessageSize ;
			result += "\nAccumulated Message Size		: " + thisContent.accumulatedMessageSize ;
			result += "\nStart Time         	: " + thisContent.dateToString(thisContent.timeStarted) ;
			result += "\nLast Beacon Time		: " + thisContent.dateToString(thisContent.timeLastBeaconReceived) ;
			result += "\nLast Message Received  : " + thisContent.dateToString(thisContent.timeLastReceived) ;
			result += "\nLast Command Sent Time : " + thisContent.dateToString(thisContent.timeLastCommandSent) ;
			result += "\nLast Error Occured     : " + thisContent.dateToString(thisContent.timeLastErrorOccured) ;
			result += "\n" ;
			
		 }
		 return result;
	}
	
	public String getNodeNames () {
		 String nodeNames = null;
		 boolean first = true;
		 
		 try {
			 // just in case no enum is possible
			 Enumeration connections = this.connectionList.elements();
			 while (connections.hasMoreElements()) {
				 StatisticContent thisContent = (StatisticContent)connections.nextElement();
				 if ( first) {
					 nodeNames = thisContent.host + ",";
				 } else {
					 nodeNames += thisContent.host + ","; 
				 }
				 first = false;
			 }
		 } catch (Exception e) {
			 nodeNames = "NONE";
		 }
		 return nodeNames;
		 
	}
	
	public String[] getNodeNameArray () {
		 String[] nodeNames = null;
		 boolean first = true;
		 int index = 0;
		 
		 try {
			 // just in case no enum is possible
			 Enumeration connections = this.connectionList.elements();
			 while (connections.hasMoreElements()) {
				 StatisticContent thisContent = (StatisticContent)connections.nextElement();
				 nodeNames[index] = thisContent.host;
				 index++;
			 }
		 } catch (Exception e) {
			 nodeNames[0] = "NONE";
		 }
		 return nodeNames;
		 
	}

}
