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
import java.util.concurrent.TimeUnit;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.diag.icsiocmonitor.service.IocConnectionState;
import org.csstudio.diag.interconnectionServer.internal.time.TimeSource;
import org.csstudio.diag.interconnectionServer.internal.time.TimeUtil;
import org.csstudio.servicelocator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Information about a connection to an IOC. Instances of this class track the
 * state of an IOC connection.
 *
 * @author Matthias Clausen, Joerg Rathlev
 */
public class IocConnection {
    
    private static final Logger LOG = LoggerFactory.getLogger(IocConnection.class);

	private IocNameDefinitions _iocNameDefinitions = null;

	private long _timeReConnected = 0;
	private long _timeLastBeaconReceived = 0;
	private long _time2ndToLastBeaconReceived = 0;
	private long _time3rdToLastBeaconReceived = 0;
	private long _timeBetweenLastAnd3rdToLastBeacon = 0;
	private long _scheduledDowntimeUntil = 0;
	private boolean _connectState = false;
	private boolean _selectState = false;
	private boolean _disabled = false;
	private boolean _getAllAlarmsOnSelectChange = true;
	private boolean _didWeSetAllChannelToDisconnect = false;

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
	 * Do not use directly, @see {@link org.csstudio.diag.interconnectionServer.server.IocConnectionManager#getIocConnection(InetAddress, int)}
	 *
	 * @param iocNameDefinitions
	 *            the container for the names of the IOC.
	 * @param timeSource
	 *            the time source that will be used by this IOC connection for
	 *            timeout calculations, statistical information etc.
	 */
    IocConnection(final IocNameDefinitions iocNameDefinitions,
                  final TimeSource timeSource) {
        _timeSource = timeSource;
        _iocNameDefinitions = iocNameDefinitions;
        initTime();
    }
	
    private void initTime() {
        this._statTimeStarted = _timeSource.now();
		this._timeReConnected = _timeSource.now();
		this._statTimeLastReceived = 0;
		this._statTimeLastCommandSent = 0;
		this._timeLastBeaconReceived = 0;
		this._time2ndToLastBeaconReceived = 0;
		this._time3rdToLastBeaconReceived = 0;
		this._statTimeLastErrorOccured = 0;
    }

	/**
	 * Enables or disables handling of messages from the IOC. When this IOC
	 * connection is disabled, all messages received from this IOC will be
	 * ignored by the Interconnection Server.
	 *
	 * @param disabled
	 *            <code>true</code> to ignore messages from the IOC,
	 *            <code>false</code> to enable message handling.
	 */
	public void setDisabled(final boolean disabled) {
		_disabled = disabled;
	}

	/**
	 * Returns whether this connection is disabled.
	 *
	 * @return <code>true</code> if the connection is disabled,
	 *         <code>false</code> otherwise.
	 */
	public boolean isDisabled() {
		return _disabled;
	}

	public boolean isGetAllAlarmsOnSelectChange() {
		return _getAllAlarmsOnSelectChange;
	}

	public void setGetAllAlarmsOnSelectChange(
			final boolean getAllAlarmsOnSelectChange) {
		this._getAllAlarmsOnSelectChange = getAllAlarmsOnSelectChange;
	}

	public void setTime(final Boolean received) {
		//
		// init time
		//
		if (received) {
			this._statTimeLastReceived = _timeSource.now();
			_statNumberOfIncomingMessages++;
			ServiceLocator.getService(IIocConnectionManager.class).incNoOfIncomingMessages();
		} else {
			this._statTimeLastCommandSent = _timeSource.now();
			_statNumberOfOutgoingMessages++;
			ServiceLocator.getService(IIocConnectionManager.class).incNoOfOutgoingMessages();
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
		final long now = _timeSource.now();
		_timeBetweenLastAnd3rdToLastBeacon = now - _time3rdToLastBeaconReceived;
		_time3rdToLastBeaconReceived = _time2ndToLastBeaconReceived;
		_time2ndToLastBeaconReceived = _timeLastBeaconReceived;
		_timeLastBeaconReceived = now;
	}

	@Nonnull
	public IocNameDefinitions getNames() {
	    return _iocNameDefinitions;
	}
	
	public InetAddress getInetAddress() {
		return _iocNameDefinitions.getInetAddress();
	}

	public void setLastMessageSize(final int lastMessageSize) {
		this._statLastMessageSize = lastMessageSize;
		this._statAccumulatedMessageSize += lastMessageSize;
	}

	public void setConnectState(final boolean state) {
		this._connectState = state;
	}

	/**
	 * Returns the IOC connection state as required for the ICS/IOC monitor
	 * service.
	 */
	public IocConnectionState getIocConnectionState() {
		if (isDisabled()) {
			return IocConnectionState.DISABLED;
		} else if (isScheduledDowntime()) {
			// Note: Scheduled downtime cannot be reported only when the IOC is
			// actually disconnected, because during a scheduled downtime, the
			// state is never set to disconnected.
			return IocConnectionState.SCHEDULED_DOWNTIME;
		} else if (getConnectState()) {
			return isSelectState() ? IocConnectionState.CONNECTED_SELECTED
					: IocConnectionState.CONNECTED;
		} else {
			return IocConnectionState.DISCONNECTED;
		}
	}

	public boolean getConnectState() {
		return _connectState;
	}

	public void incrementErrorCounter() {
		this._statErrorCount++;
		this._statTimeLastErrorOccured = _timeSource.now();
	}

	public String getCurrentConnectState() {
		if (this._connectState) {
			return "connected";
		} else {
			return "disconnected";
		}
	}

	public boolean isSelectState() {
		return _selectState;
	}

	public void setSelectState(final boolean selectState) {
		this._selectState = selectState;
	}

	public String getCurrentSelectState() {
		if (this._selectState) {
			return "selected";
		} else {
			return "NOT selected";
		}
	}

    /**
     * After a change of hardware the ioc connection has to be told about the new names.
     * 
     * @param iocNameDefinitions
     */
    public void refreshIocNameDefinitions(@Nonnull final IocNameDefinitions iocNameDefinitions) {
        _iocNameDefinitions = iocNameDefinitions;
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
		if (_timeBetweenLastAnd3rdToLastBeacon > 3 * PreferenceProperties.BEACON_TIMEOUT) {
			LOG.info(getNames().getLogicalIocName()
							+ ": Previous beacon timeout: "
							+ _timeBetweenLastAnd3rdToLastBeacon
							+ " [ms]");
			return false;
		} else {
			LOG.info(getNames().getLogicalIocName()
							+ ": Previous beacon within timeout period: "
							+ _timeBetweenLastAnd3rdToLastBeacon
							+ " [ms] < " + 3
							* PreferenceProperties.BEACON_TIMEOUT);
			return true;
		}
	}

	public boolean areWeConnectedLongerThenThreeBeaconTimeouts() {
		return _timeSource.millisecondsSince(_timeReConnected) > 3 * PreferenceProperties.BEACON_TIMEOUT;
	}

	public void setTimeReConnected() {
		this._timeReConnected = _timeSource.now();
	}

	public boolean isDidWeSetAllChannelToDisconnect() {
		return _didWeSetAllChannelToDisconnect;
	}

	public void setDidWeSetAllChannelToDisconnect(
			final boolean didWeSetAllChannelToDisconnect) {
		this._didWeSetAllChannelToDisconnect = didWeSetAllChannelToDisconnect;
	}

	/**
	 * Appends statistical information about this IOC connection to the
	 * specified <code>output</code>.
	 *
	 * @param output
	 *            the output to which the information is appended.
	 */
	public void appendStatisticInformationTo(final StringBuilder output) {
		output.append("Host:                        ")
				.append(_iocNameDefinitions.getHostName()).append("\n");
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
				.append(TimeUtil.formatTime(_timeLastBeaconReceived)).append("\n");
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
		return _timeSource.millisecondsSince(_timeLastBeaconReceived) >
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
	public void scheduleDowntime(final long duration, final TimeUnit unit) {
		_scheduledDowntimeUntil = _timeSource.now() + unit.toMillis(duration);
	}

}
