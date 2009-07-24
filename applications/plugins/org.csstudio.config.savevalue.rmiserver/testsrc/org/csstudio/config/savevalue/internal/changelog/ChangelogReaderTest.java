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

package org.csstudio.config.savevalue.internal.changelog;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;

import org.csstudio.config.savevalue.service.ChangelogEntry;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * @author Joerg Rathlev
 *
 */
public class ChangelogReaderTest {

	@Test
	public void testSingleEntry() throws Exception {
		StringReader sr = new StringReader(
				"pv test\\ test user host 20090101T000000\n");
		ChangelogReader reader = new ChangelogReader(sr);
		Collection<ChangelogEntry> entries = reader.readEntries();
		assertEquals(1, entries.size());
		ChangelogEntry entry = (ChangelogEntry) entries.toArray()[0];
		assertEquals("pv", entry.getPvName());
		assertEquals("test test", entry.getValue());
		assertEquals("user", entry.getUsername());
		assertEquals("host", entry.getHostname());
		assertEquals("20090101T000000", entry.getLastModified());
	}
	
	@Test
	public void testTwoEntries() throws Exception {
		StringReader sr = new StringReader(
				"pv test\\ test user host 20090101T000000\n" +
				"pv2 test_value user host 20090101T000000\n");
		ChangelogReader reader = new ChangelogReader(sr);
		Collection<ChangelogEntry> entries = reader.readEntries();
		assertEquals(2, entries.size());
	}
	
	@Test
	public void testLaterEntryOverwritesPreviousEntries() throws Exception {
		StringReader sr = new StringReader(
				"pv value1 user host 20090101T000000\n" +
				"pv value2 user host 20090101T000000\n");
		ChangelogReader reader = new ChangelogReader(sr);
		Collection<ChangelogEntry> entries = reader.readEntries();
		assertEquals(1, entries.size());
		ChangelogEntry entry = (ChangelogEntry) entries.toArray()[0];
		assertEquals("pv", entry.getPvName());
		assertEquals("value2", entry.getValue());
		assertEquals("user", entry.getUsername());
		assertEquals("host", entry.getHostname());
		assertEquals("20090101T000000", entry.getLastModified());
	}
	
	@Test
	public void testClose() throws Exception {
		Reader r = Mockito.mock(Reader.class);
		ChangelogReader reader = new ChangelogReader(r);
		reader.close();
		Mockito.verify(r).close();
		Mockito.verifyNoMoreInteractions(r);
	}
	
	@Test(expected=IOException.class)
	public void testInvalidLineTooManyTokens() throws Exception {
		StringReader sr = new StringReader(
				"pv test test user host 20090101T000000\n");
		ChangelogReader reader = new ChangelogReader(sr);
		reader.readEntries();
	}

	@Test(expected=IOException.class)
	public void testInvalidLineNotEnoughTokens() throws Exception {
		StringReader sr = new StringReader(
				"pv user host 20090101T000000\n");
		ChangelogReader reader = new ChangelogReader(sr);
		reader.readEntries();
	}
}
