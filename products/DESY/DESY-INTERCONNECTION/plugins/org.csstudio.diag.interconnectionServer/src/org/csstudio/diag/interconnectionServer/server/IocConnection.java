/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.diag.interconnectionServer.server;

import java.net.InetAddress;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.csstudio.diag.interconnectionServer.internal.time.TimeSource;
import org.csstudio.diag.interconnectionServer.internal.time.TimeUtil;
import org.csstudio.platform.logging.CentralLogger;

/**
 * Information about a connection to an IOC. Instances of this class track the
 * state of an IOC connection.
 * 
 * @author Matthias Clausen, Joerg Rathlev
 */
public class IocConnection {
	
	private final String host;
	private final int port;
	private String logicalIocName = null;
	private String ldapIocName = null;
	private String _hostName = null;
	private InetAddress _iocInetAddress = null;
	
	private long timeReConnected = 0;
	private long timeLastBeaconReceived = 0;
	private long time2ndToLastBeaconReceived = 0;
	private long time3rdToLastBeaconReceived = 0;
	private long timeBetweenLastAnd3rdToLastBeacon = 0;
	private long _scheduledDowntimeUntil = 0;
	private boolean connectState = false;
	private boolean selectState = false;
	private boolean getAllAlarmsOnSelectChange = true;
	private boolean didWeSetAllChannelToDisconnect = false;

	// The following variables are used only for statistical output
	private int _statLastMessageSize = 0;
	private int _statAccumulatedMessageSize = 0;
	private int _statNumberOfIncomingMessages = 0;
	private int _statNumberOfOutgoingMessages = 0;
	private long _statTimeStarted = 0;
	private long _statTimeLastReceived = 0;
	private long _statTimeLastCommandSent = 0;
	private long _statTimeLastErrorOccured = 0;
	private int _statErrorCount = 0;
	private final TimeSource _timeSource;

	/**
	 * Creates a new IOC connection.
	 * 
	 * @param host
	 *            the host name of the IOC.
	 * @param port
	 *            the port from which messages are received.
	 * @param timeSource
	 *            the time source that will be used by this IOC connection for
	 *            timeout calculations, statistical information etc.
	 */
	public IocConnection(InetAddress iocInetAddress, int port, TimeSource timeSource) {

		this.port = port;
		_timeSource = timeSource;
		
		//  2009-07-06 MCL
		// a good chance to get the host name...
	    /*
	     * getHostName is a blocking activity 
	     * this may cause the process to wait for the answer from the name server
	     * if the primary name server is NOT online it will take several seconds to fail over to the next in line
	     * it seems that this will occur EACH time getHostName() is called! 
	     */
        String hostName = iocInetAddress.getHostName();
        /*
         * in case the host name is null
         * keep the IP address instead
         */
        if ( hostName == null) {
        	hostName = iocInetAddress.getHostAddress();
        }
		this.host = hostName;
		this._iocInetAddress = iocInetAddress;
		
		//
		// init time
		//
		this._statTimeStarted = _timeSource.now();
		this.timeReConnected = _timeSource.now();
		this._statTimeLastReceived = 0;
		this._statTimeLastCommandSent = 0;
		this.timeLastBeaconReceived = 0;
		this.time2ndToLastBeaconReceived = 0;
		this.time3rdToLastBeaconReceived = 0;
		this._statTimeLastErrorOccured = 0;
	}

	public boolean isGetAllAlarmsOnSelectChange() {
		return getAllAlarmsOnSelectChange;
	}

	public void setGetAllAlarmsOnSelectChange(
			boolean getAllAlarmsOnSelectChange) {
		this.getAllAlarmsOnSelectChange = getAllAlarmsOnSelectChange;
	}

	public void setTime(Boolean received) {
		//
		// init time
		//
		if (received) {
			this._statTimeLastReceived = _timeSource.now();
			_statNumberOfIncomingMessages++;
			IocConnectionManager.getInstance().totalNumberOfIncomingMessages++;
		} else {
			this._statTimeLastCommandSent = _timeSource.now();
			_statNumberOfOutgoingMessages++;
			IocConnectionManager.getInstance().totalNumberOfOutgoingMessages++;
		}

	}

	public void setBeaconTime() {
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
		// XXX: I don't understand this.
		long now = _timeSource.now();
		timeBetweenLastAnd3rdToLastBeacon = now - time3rdToLastBeaconReceived;
		time3rdToLastBeaconReceived = time2ndToLastBeaconReceived;
		time2ndToLastBeaconReceived = timeLastBeaconReceived;
		timeLastBeaconReceived = now;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
	
	public InetAddress getIocInetAddress() {
		return _iocInetAddress;
	}

	public void setLastMessageSize(int lastMessageSize) {
		this._statLastMessageSize = lastMessageSize;
		this._statAccumulatedMessageSize += lastMessageSize;
	}
	
	public void setConnectState(boolean state) {
		this.connectState = state;
	}

	public boolean getConnectState() {
		return connectState;
	}

	public void incrementErrorCounter() {
		this._statErrorCount++;
		this._statTimeLastErrorOccured = _timeSource.now();
	}

	public String getCurrentConnectState() {
		if (this.connectState) {
			return "connected";
		} else {
			return "disconnected";
		}
	}

	public boolean isSelectState() {
		return selectState;
	}

	public void setSelectState(boolean selectState) {
		this.selectState = selectState;
	}

	public String getCurrentSelectState() {
		if (this.selectState) {
			return "selected";
		} else {
			return "NOT selected";
		}
	}

	public String getLogicalIocName() {
		return logicalIocName;
	}

	// TODO: This is currently called by the ClientRequest#run method, which
	// basically is responsible in part for the initialization of this object.
	// Refactor and move responsibility here, or into a builder class.
	public void setLogicalIocName(String logicalIocName) {
		this.logicalIocName = logicalIocName;
	}

	public String getLdapIocName() {
		return ldapIocName;
	}

	// TODO: This is currently called by the ClientRequest#run method, which
	// basically is responsible in part for the initialization of this object.
	// Refactor and move responsibility here, or into a builder class.
	public void setLdapIocName(String ldapIocName) {
		this.ldapIocName = ldapIocName;
	}

	/*
	 * XXX: This method has a confusing name (and an unclear purpose). In
	 * particular,
	 * 
	 * - it does NOT check whether the last beacon occured at most 3*timeout
	 *   ago,
	 * - it does NOT check whether the third to last beacon occured at most
	 *   3*timeout ago,
	 *   
	 * but it only checks whether the time difference between the last and
	 * the third to last beacon is greater than 3*timeout.
	 */
	public boolean wasPreviousBeaconWithinThreeBeaconTimeouts() {
		if (timeBetweenLastAnd3rdToLastBeacon > 3 * PreferenceProperties.BEACON_TIMEOUT) {
			CentralLogger.getInstance().info(
					this,
					getLogicalIocName()
							+ ": Previous beacon timeout: "
							+ timeBetweenLastAnd3rdToLastBeacon
							+ " [ms]");
			return false;
		} else {
			CentralLogger.getInstance().info(
					this,
					getLogicalIocName()
							+ ": Previous beacon within timeout period: "
							+ timeBetweenLastAnd3rdToLastBeacon
							+ " [ms] < " + 3
							* PreferenceProperties.BEACON_TIMEOUT);
			return true;
		}
	}

	public boolean areWeConnectedLongerThenThreeBeaconTimeouts() {
		return _timeSource.millisecondsSince(timeReConnected) > 3 * PreferenceProperties.BEACON_TIMEOUT;
	}

	public void setTimeReConnected() {
		this.timeReConnected = _timeSource.now();
	}

	public boolean isDidWeSetAllChannelToDisconnect() {
		return didWeSetAllChannelToDisconnect;
	}

	public void setDidWeSetAllChannelToDisconnect(
			boolean didWeSetAllChannelToDisconnect) {
		this.didWeSetAllChannelToDisconnect = didWeSetAllChannelToDisconnect;
	}

	/**
	 * Appends statistical information about this IOC connection to the
	 * specified <code>output</code>.
	 * 
	 * @param output
	 *            the output to which the information is appended.
	 */
	public void appendStatisticInformationTo(StringBuilder output) {
		output.append("Host:                        ")
				.append(host).append(":")
				.append(Integer.toString(port)).append("\n");
		output.append("Current connect state:       ")
				.append(getCurrentConnectState()).append("\n");
		output.append("Number of incoming messages: ")
				.append(_statNumberOfIncomingMessages).append("\n");
		output.append("Number of outgoing messages: ")
				.append(_statNumberOfOutgoingMessages).append("\n");
		output.append("Number of errors:            ")
				.append(_statErrorCount).append("\n");
		output.append("Last message size:           ")
				.append(_statLastMessageSize).append("\n");
		output.append("Accumulated message size:    ")
				.append(_statAccumulatedMessageSize).append("\n");
		output.append("Start time:                  ")
				.append(TimeUtil.formatTime(_statTimeStarted)).append("\n");
		output.append("Last beacon time:            ")
				.append(TimeUtil.formatTime(timeLastBeaconReceived)).append("\n");
		output.append("Last message received:       ")
				.append(TimeUtil.formatTime(_statTimeLastReceived)).append("\n");
		output.append("Last command sent time:      ")
				.append(TimeUtil.formatTime(_statTimeLastCommandSent)).append("\n");
		output.append("Last error occured:          ")
				.append(TimeUtil.formatTime(_statTimeLastErrorOccured)).append("\n");
	}

	/**
	 * Returns whether this connection is timed out with an error. A connection
	 * is timed out if the time since the last beacon was received is longer
	 * than the configured limit and there is no scheduled downtime.
	 * 
	 * @return <code>true</code> if this connection is timed out with an error,
	 *         <code>false</code> otherwise.
	 */
	public boolean isTimeoutError() {
		return isTimeout() && !isScheduledDowntime();
	}

	/**
	 * Returns whether this connection is currently in a scheduled downtime.
	 * 
	 * @return <code>true</code> if there is currently a scheduled downtime for
	 *         this connection, <code>false</code> otherwise.
	 */
	private boolean isScheduledDowntime() {
		return _timeSource.now() <= _scheduledDowntimeUntil;
	}

	/**
	 * Returns whether this connection has timed out.
	 * 
	 * @return <code>true</code> if this connection is timed out,
	 *         <code>false</code> otherwise.
	 * 
	 * @see #isTimeoutError()
	 */
	private boolean isTimeout() {
		return _timeSource.millisecondsSince(timeLastBeaconReceived) >
				PreferenceProperties.BEACON_TIMEOUT;
	}

	/**
	 * Schedules a downtime for this IOC connection. This connection will not
	 * report a timeout error for the duration of the scheduled downtime.
	 * 
	 * @param duration
	 *            the duration of the downtime.
	 * @param unit
	 *            the time unit of the <code>duration</code> argument.
	 */
	public void scheduleDowntime(long duration, TimeUnit unit) {
		_scheduledDowntimeUntil = _timeSource.now() + unit.toMillis(duration);
	}
}
