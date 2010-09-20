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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import org.csstudio.diag.interconnectionServer.internal.time.StubTimeSource;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Joerg Rathlev
 */
public class IocConnectionTest {

	private IocConnection _conn;
	private StubTimeSource _timeSource;

	@Before
	public void setUp() throws Exception {
		_timeSource = new StubTimeSource(10);
		final InetAddress ipAddress = InetAddress.getByName("127.0.0.1");

		_conn = new IocConnection(ipAddress, 1234, _timeSource);
	}

	@Test
	public void testInitialState() throws Exception {
		assertEquals("localhost", _conn.getHost());
		assertEquals(1234, _conn.getPort());
		assertFalse(_conn.getConnectState());
		assertFalse(_conn.isSelectState());
		assertTrue(_conn.isGetAllAlarmsOnSelectChange());
		assertFalse(_conn.isDidWeSetAllChannelToDisconnect());
	}

	@Test
	public void testGetAllAlarmsOnSelectChange() throws Exception {
		_conn.setGetAllAlarmsOnSelectChange(false);
		assertFalse(_conn.isGetAllAlarmsOnSelectChange());
		_conn.setGetAllAlarmsOnSelectChange(true);
		assertTrue(_conn.isGetAllAlarmsOnSelectChange());
	}

	@Test
	public void testDidWeSetAllChannelsToDisconnect() throws Exception {
		_conn.setDidWeSetAllChannelToDisconnect(true);
		assertTrue(_conn.isDidWeSetAllChannelToDisconnect());
		_conn.setDidWeSetAllChannelToDisconnect(false);
		assertFalse(_conn.isDidWeSetAllChannelToDisconnect());
	}

	@Test
	public void testLogicalIocName() throws Exception {
		_conn.setLogicalIocName("ioc-name-test");
		assertEquals("ioc-name-test", _conn.getLogicalIocName());
	}

	@Test
	public void testLdapIocName() throws Exception {
		_conn.setLdapIocName("ldap-ioc-name-test");
		assertEquals("ldap-ioc-name-test", _conn.getLdapIocName());
	}

	@Test
	public void testConnectState() throws Exception {
		_conn.setConnectState(true);
		assertTrue(_conn.getConnectState());
		assertEquals("connected", _conn.getCurrentConnectState());
		_conn.setConnectState(false);
		assertFalse(_conn.getConnectState());
		assertEquals("disconnected", _conn.getCurrentConnectState());
	}

	@Test
	public void testSelectState() throws Exception {
		_conn.setSelectState(true);
		assertTrue(_conn.isSelectState());
		assertEquals("selected", _conn.getCurrentSelectState());
		_conn.setSelectState(false);
		assertFalse(_conn.isSelectState());
		assertEquals("NOT selected", _conn.getCurrentSelectState());
	}

	@Test
	public void testWasPreviousBeaconWithinThreeBeaconTimeouts() throws Exception {
		final long beaconTimeout = PreferenceProperties.BEACON_TIMEOUT;
		_timeSource.setTime(1000);
		_conn.setBeaconTime();
		_timeSource.setTime(1000 + 1 * beaconTimeout);
		_conn.setBeaconTime();
		_timeSource.setTime(1000 + 2 * beaconTimeout);
		_conn.setBeaconTime();

		assertTrue(_conn.wasPreviousBeaconWithinThreeBeaconTimeouts());

		// Method does *not* depend on the current time!
		_timeSource.setTime(1000 + 1000 * beaconTimeout);
		assertTrue(_conn.wasPreviousBeaconWithinThreeBeaconTimeouts());

		_timeSource.setTime(1000 + 3 * beaconTimeout + 1);
		_conn.setBeaconTime();
		assertFalse(_conn.wasPreviousBeaconWithinThreeBeaconTimeouts());
	}

	@Test
	public void tesetAreWeConnectedLongerThenThreeBeaconTimeouts() throws Exception {
		final long beaconTimeout = PreferenceProperties.BEACON_TIMEOUT;
		_timeSource.setTime(1000);
		_conn.setConnectState(true);
		_conn.setTimeReConnected();

		assertFalse(_conn.areWeConnectedLongerThenThreeBeaconTimeouts());
		_timeSource.setTime(1000 + 3 * beaconTimeout);
		assertFalse(_conn.areWeConnectedLongerThenThreeBeaconTimeouts());
		_timeSource.setTime(1000 + 3 * beaconTimeout + 1);
		assertTrue(_conn.areWeConnectedLongerThenThreeBeaconTimeouts());
	}

	@Test
	public void testIsTimeoutError() throws Exception {
		final long beaconTimeout = PreferenceProperties.BEACON_TIMEOUT;
		_timeSource.setTime(1000);
		_conn.setBeaconTime();

		_timeSource.setTime(1000 + beaconTimeout);
		assertFalse(_conn.isTimeoutError());
		_timeSource.setTime(1000+ beaconTimeout + 1);
		assertTrue(_conn.isTimeoutError());
	}

	@Test
	public void testScheduledDowntime() throws Exception {
		final long beaconTimeout = PreferenceProperties.BEACON_TIMEOUT;
		_timeSource.setTime(1000);
		_conn.setBeaconTime();

		_conn.scheduleDowntime(600, TimeUnit.SECONDS);
		_timeSource.setTime(1000 + beaconTimeout + 1);
		assertFalse(_conn.isTimeoutError());
		_timeSource.setTime(1000 + 600000);
		assertFalse(_conn.isTimeoutError());
		_timeSource.setTime(1000 + 600000 + 1);
		assertTrue(_conn.isTimeoutError());
	}
}
