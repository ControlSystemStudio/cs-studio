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

import static org.junit.Assert.*;

import org.csstudio.config.savevalue.service.ChangelogEntry;
import org.junit.Test;


/**
 * @author Joerg Rathlev
 *
 */
public class ChangelogEntrySerializerTest {

	@Test
	public void testSerialize() throws Exception {
		ChangelogEntry entry = new ChangelogEntry("pv", "value", "user", "host", "2009-01-01T00:00:00");
		assertEquals("pv value user host 2009-01-01T00:00:00", ChangelogEntrySerializer.serialize(entry));
	}
	
	@Test
	public void testSerializeWithExamplesFromJavadocClassDocumentation() throws Exception {
		ChangelogEntry entry = new ChangelogEntry("EXAMPLE:counter", "0", "alice", "host123", "2009-07-01T15:00:00");
		assertEquals("EXAMPLE:counter 0 alice host123 2009-07-01T15:00:00", ChangelogEntrySerializer.serialize(entry));
		
		// In the next example, note that backslashes are escaped in the code
		// as required for Java string literals
		entry = new ChangelogEntry("EXAMPLE:path", "C:\\Program Files\\", "alice", "host123", "2009-07-01T15:00:00");
		assertEquals("EXAMPLE:path C:\\\\Program\\ Files\\\\ alice host123 2009-07-01T15:00:00", ChangelogEntrySerializer.serialize(entry));
	}
	
	@Test
	public void testDeserialize() throws Exception {
		ChangelogEntry entry = ChangelogEntrySerializer.deserialize("pv value user host 2009-01-01T00:00:00");
		assertEquals("pv", entry.getPvName());
		assertEquals("value", entry.getValue());
		assertEquals("user", entry.getUsername());
		assertEquals("host", entry.getHostname());
		assertEquals("2009-01-01T00:00:00", entry.getLastModified());
	}
	
	@Test
	public void testDeserializeWithExamplesFromJavadocClassDocumentation() throws Exception {
		ChangelogEntry entry = ChangelogEntrySerializer.deserialize("EXAMPLE:counter 0 alice host123 2009-07-01T15:00:00");
		assertEquals("EXAMPLE:counter", entry.getPvName());
		assertEquals("0", entry.getValue());
		assertEquals("alice", entry.getUsername());
		assertEquals("host123", entry.getHostname());
		assertEquals("2009-07-01T15:00:00", entry.getLastModified());
		
		// In the next example, note that backslashes are escaped in the code
		// as required for Java string literals
		entry = ChangelogEntrySerializer.deserialize("EXAMPLE:path C:\\\\Program\\ Files\\\\ alice host123 2009-07-01T15:00:00");
		assertEquals("EXAMPLE:path", entry.getPvName());
		assertEquals("C:\\Program Files\\", entry.getValue());
		assertEquals("alice", entry.getUsername());
		assertEquals("host123", entry.getHostname());
		assertEquals("2009-07-01T15:00:00", entry.getLastModified());
	}
	
	@Test
	public void testRoundTrip() throws Exception {
		ChangelogEntry in = new ChangelogEntry("pv", "value", "user", "host", "2009-01-01T00:00:00");
		ChangelogEntry out = ChangelogEntrySerializer.deserialize(ChangelogEntrySerializer.serialize(in));
		assertEquals(in.getPvName(), out.getPvName());
		assertEquals(in.getValue(), out.getValue());
		assertEquals(in.getUsername(), out.getUsername());
		assertEquals(in.getHostname(), out.getHostname());
		assertEquals(in.getLastModified(), out.getLastModified());
	}
}
