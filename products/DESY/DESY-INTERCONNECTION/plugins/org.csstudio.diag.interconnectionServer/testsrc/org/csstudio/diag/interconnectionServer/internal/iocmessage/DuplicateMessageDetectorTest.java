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

import org.junit.Test;
import org.mockito.Mockito;


/**
 * @author Joerg Rathlev
 */
public class DuplicateMessageDetectorTest {

	@Test
	public void simpleDuplicateMessage() throws Exception {
		IDuplicateMessageHandler handler = Mockito.mock(IDuplicateMessageHandler.class);
		DuplicateMessageDetector detector = new DuplicateMessageDetector(handler);
		
		IocMessage message1 = new IocMessage();
		message1.addItem(new TagValuePair("ID", "1"));
		detector.checkAndRemember(message1);
		Mockito.verifyZeroInteractions(handler);
		
		IocMessage message2 = new IocMessage();
		message2.addItem(new TagValuePair("ID", "1"));
		detector.checkAndRemember(message2);
		Mockito.verify(handler).duplicateMessageDetected(message1, message2);
		
		Mockito.verifyNoMoreInteractions(handler);
	}
	
	@Test
	public void duplicateMessageAfterAnotherMessage() throws Exception {
		IDuplicateMessageHandler handler = Mockito.mock(IDuplicateMessageHandler.class);
		DuplicateMessageDetector detector = new DuplicateMessageDetector(handler);
		
		IocMessage message1 = new IocMessage();
		message1.addItem(new TagValuePair("ID", "1"));
		detector.checkAndRemember(message1);
		Mockito.verifyZeroInteractions(handler);

		IocMessage message2 = new IocMessage();
		message2.addItem(new TagValuePair("ID", "2"));
		detector.checkAndRemember(message2);
		Mockito.verifyZeroInteractions(handler);
		
		IocMessage message3 = new IocMessage();
		message3.addItem(new TagValuePair("ID", "1"));
		detector.checkAndRemember(message3);
		Mockito.verify(handler).duplicateMessageDetected(message1, message3);
		
		Mockito.verifyNoMoreInteractions(handler);
	}
	
	@Test
	public void differentMessagesWithSameIdDetectedAsDuplicates() throws Exception {
		IDuplicateMessageHandler handler = Mockito.mock(IDuplicateMessageHandler.class);
		DuplicateMessageDetector detector = new DuplicateMessageDetector(handler);
		
		IocMessage message1 = new IocMessage();
		message1.addItem(new TagValuePair("TEXT", "some text"));
		message1.addItem(new TagValuePair("ID", "1"));
		detector.checkAndRemember(message1);
		Mockito.verifyZeroInteractions(handler);

		IocMessage message2 = new IocMessage();
		message2.addItem(new TagValuePair("TEXT", "different text"));
		message2.addItem(new TagValuePair("TEST", "an additional tag"));
		message2.addItem(new TagValuePair("ID", "1"));
		detector.checkAndRemember(message2);
		Mockito.verify(handler).duplicateMessageDetected(message1, message2);
		
		Mockito.verifyNoMoreInteractions(handler);
	}
	
	@Test
	public void highNumberOfMessages() throws Exception {
		IDuplicateMessageHandler handler = Mockito.mock(IDuplicateMessageHandler.class);
		DuplicateMessageDetector detector = new DuplicateMessageDetector(handler);
		
		for (int i = 0; i < 1000; i++) {
			IocMessage message = new IocMessage();
			message.addItem(new TagValuePair("ID", Integer.toString(i)));
			detector.checkAndRemember(message);
		}
		
		Mockito.verifyNoMoreInteractions(handler);
	}
	
	@Test
	public void messagesWithoutIdAreIgnored() throws Exception {
		IDuplicateMessageHandler handler = Mockito.mock(IDuplicateMessageHandler.class);
		DuplicateMessageDetector detector = new DuplicateMessageDetector(handler);
		
		IocMessage message1 = new IocMessage();
		message1.addItem(new TagValuePair("TEST", "foo"));
		detector.checkAndRemember(message1);
		Mockito.verifyZeroInteractions(handler);

		IocMessage message2 = new IocMessage();
		message2.addItem(new TagValuePair("TEST", "foo"));
		detector.checkAndRemember(message2);
		Mockito.verifyZeroInteractions(handler);
		
		Mockito.verifyNoMoreInteractions(handler);
	}
}
