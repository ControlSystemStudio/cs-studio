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

import java.io.StringWriter;
import java.io.Writer;

import org.csstudio.config.savevalue.service.ChangelogEntry;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * @author Joerg Rathlev
 */
public class ChangelogAppenderTest {

	@Test
	public void testAppend() throws Exception {
		StringWriter sw = new StringWriter();
		ChangelogAppender appender = new ChangelogAppender(sw);
		appender.append(new ChangelogEntry("pv", "1.0", "user", "host", "20090101T000000"));
		assertEquals("pv 1.0 user host 20090101T000000\n", sw.toString());
	}
	
	@Test
	public void testAppendWithValueThatRequiresEscaping() throws Exception {
		StringWriter sw = new StringWriter();
		ChangelogAppender appender = new ChangelogAppender(sw);
		appender.append(new ChangelogEntry("pv", "test test", "user", "host", "20090101T000000"));
		assertEquals("pv test\\ test user host 20090101T000000\n", sw.toString());
	}
	
	@Test
	public void testClose() throws Exception {
		Writer w = Mockito.mock(Writer.class);
		ChangelogAppender appender = new ChangelogAppender(w);
		appender.close();
		Mockito.verify(w).close();
		Mockito.verifyNoMoreInteractions(w);
	}
}
