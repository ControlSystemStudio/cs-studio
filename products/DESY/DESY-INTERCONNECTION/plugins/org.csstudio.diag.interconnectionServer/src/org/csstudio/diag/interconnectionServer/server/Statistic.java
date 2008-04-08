package org.csstudio.diag.interconnectionServer.server;
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
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

import java.util.*;
import java.text.*;

import org.csstudio.platform.logging.CentralLogger;

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
		// get an instance of our singleton
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
		String ipAddress	= null;
		String logicalIocName = null;
		String ldapIocName = null;
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
		GregorianCalendar timePreviousBeaconReceived = null;
		GregorianCalendar time2ndPreviousBeaconReceived = null;
		int deltaTimeLastBeaconReceived = 0;
		int deltaTimePreviousBeaconReceived = 0;
		int deltaTime2ndPreviousBeaconReceived = 0;
		GregorianCalendar timeLastErrorOccured = null;
		int errorCounter = 0;
		boolean connectState = false;
		boolean selectState = false;
		int selectStateCounter = 0;
		
		public int getSelectStateCounter() {
			return selectStateCounter;
		}

		public void setSelectStateCounter(int selectStateCounter) {
			this.selectStateCounter = selectStateCounter;
		}
		
		public void incrementSelectStateCounter() {
			this.selectStateCounter++;
		}

		public StatisticContent () {
			//
			// init time
			//
			this.timeStarted = new GregorianCalendar();
			this.timeLastReceived = new GregorianCalendar(1970,1,1);
			this.timeLastCommandSent = new GregorianCalendar(1970,1,1);
			this.timeLastBeaconReceived = new GregorianCalendar(1970,1,1);
			this.timePreviousBeaconReceived = new GregorianCalendar(1970,1,1);
			this.time2ndPreviousBeaconReceived = new GregorianCalendar(1970,1,1);
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
			/*
			 * Why is it so complicated?
			 * Order:
			 * IOC new connected:
			 * (1)Beacon  ->setBeaconTime()
			 * (2)Message -> setBeaconTime() - process message - find out we are selected - check for beacon time
			 * In this case:
			 * - 2ndprevious == old
			 * - previous    == set by (1) -> most recent ~ as old as the beacon update time
			 * - last        == set by (2) -> the current time
			 * ==> we have to check against the 2ndprevious time
			 */
			GregorianCalendar newTime = new GregorianCalendar();
			setDeltaTimeLastBeaconReceived( gregorianTimeDifference ( this.timeLastBeaconReceived, newTime));
			setDeltaTimePreviousBeaconReceived( gregorianTimeDifference ( this.timePreviousBeaconReceived, newTime));
			setDeltaTime2ndPreviousBeaconReceived( gregorianTimeDifference ( this.time2ndPreviousBeaconReceived, newTime));
			this.time2ndPreviousBeaconReceived = this.timePreviousBeaconReceived;
			this.timePreviousBeaconReceived = this.timeLastBeaconReceived;
			this.timeLastBeaconReceived = newTime;
		}
		
		public void setHost ( String host) {
			
			this.host = host;
		}
		
		public String getHost () {
			return host;
		}
		
		public void setPort ( int port) {
			
			this.port = port;
		}
		
		public int getPort () {
			return port;
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
		
		public boolean getConnectState () {
			return connectState;
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

		public String getIpAddress() {
			return ipAddress;
		}

		public void setIpAddress(String ipAddress) {
			this.ipAddress = ipAddress;
		}

		public boolean isSelectState() {
			return selectState;
		}

		public void setSelectState(boolean selectState) {
			this.selectState = selectState;
		}
		
		public String getCurrentSelectState () {
			if( this.selectState) {
				return "selected";
			} else {
				return "NOT selected";
			}
		}

		public String getLogicalIocName() {
			return logicalIocName;
		}

		public void setLogicalIocName(String logicalIocName) {
			this.logicalIocName = logicalIocName;
		}

		public String getLdapIocName() {
			return ldapIocName;
		}

		public void setLdapIocName(String ldapIocName) {
			this.ldapIocName = ldapIocName;
		}

		public int getDeltaTimeLastBeaconReceived() {
			return deltaTimeLastBeaconReceived;
		}

		public void setDeltaTimeLastBeaconReceived(
				int deltaTimeLastBeaconReceived) {
			this.deltaTimeLastBeaconReceived = deltaTimeLastBeaconReceived;
		}
		
		public boolean wasPreviousBeaconWithinThreeBeaconTimeouts() {
			if ( getDeltaTime2ndPreviousBeaconReceived() > 3*PreferenceProperties.BEACON_TIMEOUT) {
				CentralLogger.getInstance().info(this, "Previous beacon timeout: " + getDeltaTime2ndPreviousBeaconReceived() + " [ms]");
				return false;
			} else {
				CentralLogger.getInstance().info(this, "Previous beacon within timeout period: " + getDeltaTime2ndPreviousBeaconReceived() + " [ms] < " + 3*PreferenceProperties.BEACON_TIMEOUT);
				CentralLogger.getInstance().info(this, "LastBeacon " + getDeltaTimePreviousBeaconReceived() + " [ms]");
				CentralLogger.getInstance().info(this, "LastBeacon " + getDeltaTimeLastBeaconReceived() + " [ms]");
				return true;
			}
		}

		public int getDeltaTimePreviousBeaconReceived() {
			return deltaTimePreviousBeaconReceived;
		}

		public void setDeltaTimePreviousBeaconReceived(
				int deltaTimePreviousBeaconReceived) {
			this.deltaTimePreviousBeaconReceived = deltaTimePreviousBeaconReceived;
		}

		public GregorianCalendar getTimePreviousBeaconReceived() {
			return timePreviousBeaconReceived;
		}

		public void setTimePreviousBeaconReceived(
				GregorianCalendar timePreviousBeaconReceived) {
			this.timePreviousBeaconReceived = timePreviousBeaconReceived;
		}

		public GregorianCalendar getTime2ndPreviousBeaconReceived() {
			return time2ndPreviousBeaconReceived;
		}

		public void setTime2ndPreviousBeaconReceived(
				GregorianCalendar time2ndPreviousBeaconReceived) {
			this.time2ndPreviousBeaconReceived = time2ndPreviousBeaconReceived;
		}

		public int getDeltaTime2ndPreviousBeaconReceived() {
			return deltaTime2ndPreviousBeaconReceived;
		}

		public void setDeltaTime2ndPreviousBeaconReceived(
				int deltaTime2ndPreviousBeaconReceived) {
			this.deltaTime2ndPreviousBeaconReceived = deltaTime2ndPreviousBeaconReceived;
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
		 List<String> nodeNames = new ArrayList<String>();
		 boolean first = true;
		 
		 try {
			 // just in case no enum is possible
			 Enumeration connections = this.connectionList.elements();
			 while (connections.hasMoreElements()) {
				 StatisticContent thisContent = (StatisticContent)connections.nextElement();
				 nodeNames.add(thisContent.getHost());
			 }
		 } catch (Exception e) {
			 nodeNames.add("NONE");
		 }
		 return nodeNames.toArray(new String[0]);
		 
	}
	
	public String[] getNodeNameArrayWithLogicalName () {
		 List<String> nodeNames = new ArrayList<String>();
		 boolean first = true;
		 
		 try {
			 // just in case no enum is possible
			 Enumeration connections = this.connectionList.elements();
			 while (connections.hasMoreElements()) {
				 StatisticContent thisContent = (StatisticContent)connections.nextElement();
				 nodeNames.add(thisContent.getHost() + "|" + thisContent.getLogicalIocName());
			 }
		 } catch (Exception e) {
			 nodeNames.add("NONE");
		 }
		 return nodeNames.toArray(new String[0]);
		 
	}
	
	public String[] getNodeNameStatusArray () {
		 List<String> nodeNames = new ArrayList<String>();
		 boolean first = true;
		 
		 try {
			 // just in case no enum is possible
			 Enumeration connections = this.connectionList.elements();
			 while (connections.hasMoreElements()) {
				 StatisticContent thisContent = (StatisticContent)connections.nextElement();
				 nodeNames.add(thisContent.getHost() + " | " + thisContent.getLogicalIocName() + "  " + thisContent.getCurrentConnectState() + "  " + thisContent.getCurrentSelectState());
			 }
		 } catch (Exception e) {
			 nodeNames.add("NONE");
		 }
		 return nodeNames.toArray(new String[0]);
		 
	}

}
