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

import org.junit.Test;


/**
 * @author Joerg Rathlev
 */
public class TagListTest {
	
	@Test
	public void testGetTagType() throws Exception {
		assertEquals(TagList.TAG_TYPE_ID, TagList.getTagType("ID"));
		assertEquals(TagList.TAG_TYPE_UNKNOWN, TagList.getTagType("-x-test-invalid"));
	}
	
	@Test
	public void testGetMessageType() throws Exception {
		assertEquals(TagList.ALARM_MESSAGE, TagList.getMessageType("alarm"));
		assertEquals(TagList.SYSTEM_LOG_MESSAGE, TagList.getMessageType("sysLog"));
		assertEquals(TagList.IOC_SYSTEM_MESSAGE, TagList.getMessageType("sysMsg"));
		assertEquals(TagList.UNKNOWN_MESSAGE, TagList.getMessageType("-x-test-invalid"));
	}
	
	@Test
	public void testGetReplyType() throws Exception {
		assertEquals(TagList.REPLY_TYPE_ERROR, TagList.getReplyType("error"));
		assertEquals(TagList.REPLY_TYPE_OK, TagList.getReplyType("ok"));
		assertEquals(TagList.REPLY_TYPE_CMD_UNKNOWN, TagList.getReplyType("-x-test-invalid"));
	}

}
