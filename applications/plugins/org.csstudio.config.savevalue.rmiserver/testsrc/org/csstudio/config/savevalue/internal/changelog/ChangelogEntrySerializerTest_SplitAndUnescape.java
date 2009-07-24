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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


/**
 * Test for the method {@link ChangelogEntrySerializer#splitAndUnescape(String)}.
 * 
 * @author Joerg Rathlev
 */
@RunWith(Parameterized.class)
public class ChangelogEntrySerializerTest_SplitAndUnescape {

	private String _line;
	private String[] _expectedTokens;
	
	public ChangelogEntrySerializerTest_SplitAndUnescape(Object line, Object expectedTokens) {
		_line = (String) line;
		_expectedTokens = (String[]) expectedTokens;
	}
	
	@Parameters
	public static Collection<Object[]> parameters() {
		return Arrays.asList(new Object[][] {
				{"", new String[] {""}},
				{"foo", new String[] {"foo"}},
				{"foo bar", new String[] {"foo", "bar"}},
				{"foo\\ bar", new String[] {"foo bar"}},
				{"C:\\\\Program\\ Files\\\\ test", new String[] {"C:\\Program Files\\", "test"}},
				{"foo\\nbar x y\\ z", new String[] {"foo\nbar", "x", "y z"}},
				{"foo\\r\\ bar x", new String[] {"foo\r bar", "x"}},
		});
	}
	
	@Test
	public void testSplitAndUnescape() throws Exception {
		String[] tokens = ChangelogEntrySerializer.splitAndUnescape(_line);
		assertArrayEquals(_expectedTokens, tokens);
	}
}
