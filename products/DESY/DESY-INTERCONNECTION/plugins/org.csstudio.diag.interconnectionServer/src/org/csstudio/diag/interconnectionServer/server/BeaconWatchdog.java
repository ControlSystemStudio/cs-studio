package org.csstudio.diag.interconnectionServer.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import org.csstudio.diag.interconnectionServer.server.Statistic.StatisticContent;
import org.csstudio.platform.logging.CentralLogger;


public class BeaconWatchdog extends Thread{
	private int	timeout	= 1000;	// 1mS
	private boolean isRunning = true;
	
	BeaconWatchdog ( int timeout) {
		this.timeout = timeout;
		
		this.start();
	}
	
	public void run() {
		
		while ( isRunning()) {
			
			checkBeaconTimeout();
			
			/*
			 * wait
			 */
			try {
				Thread.sleep( this.timeout);
	
			} catch (InterruptedException e) {
				// TODO: handle exception
			}
			finally {
				//clean up
			}
		}
		
	}
	

	private void checkBeaconTimeout () {
		
		Enumeration connections = Statistic.getInstance().connectionList.elements();
		 while (connections.hasMoreElements()) {
			 StatisticContent thisContent = (StatisticContent)connections.nextElement();
			 //CentralLogger.getInstance().debug(this,"checking : " + thisContent.getStatisticId() + " " + thisContent.getCurrentConnectState() + " " + thisContent.getCurrentSelectState());
			 
			 if ( thisContent.gregorianTimeDifferenceFromNow( thisContent.getTimeLastBeaconReceived()) > PreferenceProperties.BEACON_TIMEOUT) {
				 
				 /*
				  * if we come here the first time...
				  */
				 if ( thisContent.getConnectState()) {
					 /*
					  * ok we're disconnected - remember
					  */
					 thisContent.setConnectState( false);	// not connected
					 
					 /*
					  * send log message
					  */
					 CentralLogger.getInstance().warn(this, "InterconnectionServer: Beacon timeout for Host: " + thisContent.host);
					 /*
					  * do the changed state stuff in a new thread
					  * ... but only if this InterconnectionServer is the selected one (from IOC point of view)
					  */
					  if ( thisContent.isSelectState()) {
						  CentralLogger.getInstance().debug(this, "InterconnectionServer: trigger timeout actions");
						  new IocChangedState (thisContent.getHost(), thisContent.getIpAddress(), false);
					  }
					  
					  /*
					   * change also the select state - the IOC can't tell us any more ...
					   */
					  thisContent.setSelectState( false);	// not selected
					  
					 System.out.println("---------- Client disconnected ---------------");
					 System.out.println("Host:" +  thisContent.host + "  Port: " + thisContent.port +"\n");
					 System.out.println("Last beacon: " +  dateToString( thisContent.getTimeLastBeaconReceived()) + " Actual time: " + dateToString(new GregorianCalendar()));
				 }
			 }
		 }
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
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

}

