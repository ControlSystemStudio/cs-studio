package org.csstudio.alarm.table.dataModel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.*;

import javax.annotation.Nonnull;

import org.junit.Test;

/**
 * Test for the abstract message list. The message list is the model for the
 * alarm table views (log, ams, alarm).
 * 
 * Subclasses should register a severity mapping, because this is used by the
 * BasicMessage. i.e.: SeverityRegistry.setSeverityMapping(mock(ISeverityMapping.class));
 * 
 * @author jpenning
 * @author $Author: jpenning $
 * @since 04.10.2010
 */
public abstract class AbstractMessageListUnitTest {

	/**
	 * @return the object under test
	 */
	@Nonnull
	protected abstract AbstractMessageList createMessageListForTest();

	/**
	 * Subclasses may override this to define a suitable message
	 * 
	 * @return the message to be added / removed to the list under test
	 */
	@Nonnull
	protected BasicMessage createMessage() {
		return new BasicMessage();
	}

	@Test
	public void testAddRemoveListener() throws Exception {
		IMessageViewer messageViewer0 = mock(IMessageViewer.class);
		AbstractMessageList messageList = createMessageListForTest();

		// not called if not registered
		BasicMessage message0 = createMessage();
		messageList.addMessage(message0);
		verify(messageViewer0, never()).addJMSMessage(message0);

		// now registered, so called once
		messageList.addChangeListener(messageViewer0);
		messageList.addMessage(message0);
		
		verify(messageViewer0, times(1)).addJMSMessage(message0);

		// called once more
		messageList.addMessage(message0);
		verify(messageViewer0, times(2)).addJMSMessage(message0);
		
		// no longer registered, not called again
		messageList.removeChangeListener(messageViewer0);
		messageList.addMessage(message0);
		verify(messageViewer0, times(2)).addJMSMessage(message0);
	}

	@Test
	public void testAddRemoveMessageWithListeners() throws Exception {
		IMessageViewer messageViewer0 = mock(IMessageViewer.class);
		IMessageViewer messageViewer1 = mock(IMessageViewer.class);

		AbstractMessageList messageList = createMessageListForTest();
		messageList.addChangeListener(messageViewer0);
		messageList.addChangeListener(messageViewer1);

		// the added message is given to all listeners
		// the alarm message list internally creates a new message from the given one, so the
		// test only checks for the type
		BasicMessage message0 = createMessage();
		messageList.addMessage(message0);
		verify(messageViewer0).addJMSMessage(any(BasicMessage.class));
		verify(messageViewer1).addJMSMessage(any(BasicMessage.class));

		// the removed message is given to all listeners
		messageList.removeMessage(message0);
		verify(messageViewer0).removeJMSMessage(message0);
		verify(messageViewer1).removeJMSMessage(message0);
	}

	@Test
	public void testAddRemoveMessagesWithListeners() throws Exception {
		IMessageViewer messageViewer0 = mock(IMessageViewer.class);
		IMessageViewer messageViewer1 = mock(IMessageViewer.class);

		AbstractMessageList messageList = createMessageListForTest();
		messageList.addChangeListener(messageViewer0);
		messageList.addChangeListener(messageViewer1);

		// the added messages are given to all listeners
		BasicMessage[] messages = new BasicMessage[0];
		messageList.addMessages(messages);
		verify(messageViewer0).addJMSMessages(messages);
		verify(messageViewer1).addJMSMessages(messages);

		// the removed messages are given to all listeners
		messageList.removeMessages(messages);
		verify(messageViewer0).removeJMSMessage(messages);
		verify(messageViewer1).removeJMSMessage(messages);
	}

}
