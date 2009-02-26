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

package org.csstudio.diag.interconnectionServer.internal.iocmessage;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;


/**
 * @author Joerg Rathlev
 *
 */
public class IocMessageTest {

	@Test
	public void emptyMessage() throws Exception {
		IocMessage message = new IocMessage();
		assertFalse(message.isValid());
		assertFalse(message.hasMessageId());
		assertEquals(-1, message.getMessageId());
		assertNull(message.getMessageTypeString());
	}
	
	@Test
	public void addItem() throws Exception {
		IocMessage message = new IocMessage();
		TagValuePair tv = new TagValuePair("TEST", "123");
		message.addItem(tv);
		assertTrue(message.contains("TEST"));
		assertEquals(tv, message.getItem("TEST"));
	}
	
	@Test
	public void messageWithIdAndTypeIsValid() throws Exception {
		IocMessage message = new IocMessage();
		message.addItem(new TagValuePair("ID", "1"));
		message.addItem(new TagValuePair("TYPE", "event"));
		assertTrue(message.isValid());
		assertTrue(message.hasMessageId());
	}
	
	@Test
	public void testGetItems() throws Exception {
		IocMessage message = new IocMessage();
		TagValuePair tv1 = new TagValuePair("TEST", "1");
		message.addItem(tv1);
		TagValuePair tv2 = new TagValuePair("TEST2", "2");
		message.addItem(tv2);
		Collection<TagValuePair> items = message.getItems();
		assertTrue(items.contains(tv1));
		assertTrue(items.contains(tv2));
		assertEquals(2, items.size());
	}
	
	@Test
	public void testGetMessageId() throws Exception {
		IocMessage message = new IocMessage();
		message.addItem(new TagValuePair("ID", "1"));
		assertTrue(message.hasMessageId());
		assertEquals(1, message.getMessageId());
	}
	
	@Test
	public void testGetMessageTypeString() throws Exception {
		IocMessage message = new IocMessage();
		message.addItem(new TagValuePair("TYPE", "event"));
		assertEquals("event", message.getMessageTypeString());
	}
	
	@Test
	public void testToString() throws Exception {
		IocMessage message = new IocMessage();
		message.addItem(new TagValuePair("ID", "1"));
		assertEquals("ID=1;", message.toString());
	}
	
	@Test
	public void testInvalidMessageIdIsIgnored() throws Exception {
		IocMessage message = new IocMessage();
		message.addItem(new TagValuePair("ID", "abc"));
		assertFalse(message.hasMessageId());
		assertEquals(-1, message.getMessageId());
	}
}
