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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.Vector;

import javax.jms.MapMessage;

import org.csstudio.diag.interconnectionServer.internal.iocmessage.DuplicateMessageDetector;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.IocMessage;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.IocMessageParser;
import org.csstudio.diag.interconnectionServer.internal.iocmessage.TagValuePair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Joerg Rathlev
 *
 */
public class ClientRequestTest {
	
	private Hashtable<String, String> _tagsValue;
	private Vector<TagValuePair> _tagValuePairs;

	@Before
	public void setUp() {
		_tagsValue = new Hashtable<String, String>();
		_tagValuePairs = new Vector<TagValuePair>();
	}
	
	private IocMessage makeMessage(String unparsedMessage) {
		return new IocMessageParser().parse(unparsedMessage);
	}

	@Test
	public void parseMessageWithIdAndType() throws Exception {
		IocMessage message = makeMessage("ID=123;TYPE=event;EVENTTIME=2010-11-12 01:23:45.678;");
		ClientRequest.putIocMessageDataIntoLegacyDataStructures(
				message, _tagsValue, _tagValuePairs);
		
		assertEquals(3, _tagsValue.size());
		assertEquals(1, _tagValuePairs.size());
		assertTrue(_tagsValue.containsKey("ID"));
		assertTrue(_tagsValue.containsKey("TYPE"));
		assertTrue(_tagsValue.containsKey("EVENTTIME"));
		assertEquals("123", _tagsValue.get("ID"));
		assertEquals("event", _tagsValue.get("TYPE"));
		assertEquals("2010-11-12 01:23:45.678", _tagsValue.get("EVENTTIME"));
		assertEquals("EVENTTIME", _tagValuePairs.get(0).getTag());
		assertEquals("2010-11-12 01:23:45.678", _tagValuePairs.get(0).getValue());
	}
	
	@Test
	public void parseMessageUsesCreatetimeAsEventtime() throws Exception {
		IocMessage message = makeMessage("ID=1;CREATETIME=2010-11-12 01:23:45.678;");
		ClientRequest.putIocMessageDataIntoLegacyDataStructures(
				message, _tagsValue, _tagValuePairs);
		
		assertEquals(2, _tagsValue.size());
		assertEquals(2, _tagValuePairs.size());
		// XXX: This test is quite fragile (order dependency)
		assertEquals("CREATETIME", _tagValuePairs.get(0).getTag());
		assertEquals("2010-11-12 01:23:45.678", _tagValuePairs.get(0).getValue());
		assertEquals("EVENTTIME", _tagValuePairs.get(1).getTag());
		assertEquals("2010-11-12 01:23:45.678", _tagValuePairs.get(1).getValue());
	}
	
	@Test
	public void parseMessageUsesSystemTimeIfNoTimeTagInMessage() throws Exception {
		IocMessage message = makeMessage("ID=1;");
		ClientRequest.putIocMessageDataIntoLegacyDataStructures(
				message, _tagsValue, _tagValuePairs);

		assertEquals(1, _tagsValue.size());
		assertEquals(1, _tagValuePairs.size());
		// XXX: This test is quite fragile (order dependency)
		assertEquals("EVENTTIME", _tagValuePairs.get(0).getTag());
		// TODO: check that time is correct (need mockable time provider)
	}
	
	@Test
	public void parseMessageWithoutIdOrType() {
		IocMessage message = makeMessage("TEST=1;");
		ClientRequest.putIocMessageDataIntoLegacyDataStructures(
				message, _tagsValue, _tagValuePairs);
		
		assertEquals(1, _tagsValue.size());
		assertEquals(2, _tagValuePairs.size());
		assertTrue(_tagsValue.containsKey("TEST"));
		assertEquals("1", _tagsValue.get("TEST"));
		// XXX: This test is quite fragile (order dependency)
		assertEquals("TEST", _tagValuePairs.get(0).getTag());
		assertEquals("1", _tagValuePairs.get(0).getValue());
		// message did not have an event time, should be generated
		assertEquals("EVENTTIME", _tagValuePairs.get(1).getTag());
		// TODO: check that time is correct (need mockable time provider)
	}
	
	@Test
	public void parseEmptyMessage() throws Exception {
		IocMessage message = makeMessage("");
		ClientRequest.putIocMessageDataIntoLegacyDataStructures(
				message, _tagsValue, _tagValuePairs);
		
		assertEquals(0, _tagsValue.size());
		assertEquals(1, _tagValuePairs.size());
		// EVENTTIME is generated
		assertEquals("EVENTTIME", _tagValuePairs.get(0).getTag());
		// TODO: check that time is correct (need mockable time provider)
	}
	
	@Test
	public void testGetDuplicateMessageDetectorForIoc() throws Exception {
		DuplicateMessageDetector d1 = ClientRequest.getDuplicateMessageDetectorForIoc("ioc");
		DuplicateMessageDetector d2 = ClientRequest.getDuplicateMessageDetectorForIoc("ioc");
		DuplicateMessageDetector d3 = ClientRequest.getDuplicateMessageDetectorForIoc("ioc2");
		
		assertSame(d1, d2);
		assertNotSame(d1, d3);
		assertNotSame(d2, d3);
	}
	
	@Test
	public void testPrepareTypedJmsMessage() throws Exception {
		Vector<TagValuePair> pairs = new Vector<TagValuePair>();
		pairs.add(new TagValuePair("TEST", "foo"));
		pairs.add(new TagValuePair("BAR", "baz"));
		MapMessage message = Mockito.mock(MapMessage.class);
		
		ClientRequest.prepareTypedJmsMessage(message, pairs, "quux");
		Mockito.verify(message).setString("TYPE", "quux");
		Mockito.verify(message).setString("TEST", "foo");
		Mockito.verify(message).setString("BAR", "baz");
		Mockito.verifyNoMoreInteractions(message);
	}
	
	@Test
	public void testPrepareJmsMessageLogNewClientConnected() throws Exception {
		MapMessage message = Mockito.mock(MapMessage.class);
		
		ClientRequest.prepareJmsMessageLogNewClientConnected(message, "foo");
		Mockito.verify(message).setString("TYPE", "SysLog");
		Mockito.verify(message).setString(Mockito.eq("EVENTTIME"), Mockito.anyString());
		Mockito.verify(message).setString("TEXT", "new log client connected");
		Mockito.verify(message).setString("HOST", "foo");
		Mockito.verify(message).setString("STATUS", "on");
		Mockito.verify(message).setString("SEVERITY", "NO_ALARM");
		Mockito.verifyNoMoreInteractions(message);
	}
}
