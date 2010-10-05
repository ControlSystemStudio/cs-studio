package org.csstudio.alarm.table.dataModel;

import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;
import static org.junit.Assert.*;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.table.preferences.ISeverityMapping;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the abstract message list. The message list is the model for the
 * alarm table views (log, ams, alarm).
 * 
 * @author jpenning
 * @author $Author: jpenning $
 * @since 04.10.2010
 */
public class AlarmMessageListUnitTest extends AbstractMessageListUnitTest {

	
	private static final String SEVERITY_PROPERTY_VALUE = "Severity property value";
	private static final String SEVERITY_AS_MAPPED = "Severity as mapped";

	@Override
	public AbstractMessageList createMessageListForTest() {
		return new AlarmMessageList();
	}
	
	@Override
	protected BasicMessage createMessage() {
		// Define a message that will pass the checkValidity-Test in AlarmMessageList
		BasicMessage result = new AlarmMessage();
		result.setProperty(AlarmMessageKey.NAME.getDefiningName(), "PV for test");
		result.setProperty(AlarmMessageKey.SEVERITY.getDefiningName(), SEVERITY_PROPERTY_VALUE);
		result.setProperty(AlarmMessageKey.TYPE.getDefiningName(), "Type for test");
		return result;
	}
	
	@Nonnull
	private BasicMessage createNamedMessage(@Nonnull final String name) {
		BasicMessage result = createMessage();
		result.setProperty(AlarmMessageKey.NAME.getDefiningName(), name);
		return result;
	}
	
	@Before
	public void setUp() {
		// severity mapping is used to retrieve a value matching the severity property
		ISeverityMapping severityMapping = mock(ISeverityMapping.class);
		when(severityMapping.findSeverityValue(SEVERITY_PROPERTY_VALUE)).thenReturn(SEVERITY_AS_MAPPED);
		SeverityRegistry.setSeverityMapping(severityMapping);
	}

	
	@Override
	@Test
	public void testAddRemoveListener() throws Exception {
		// Adding messages has a different semantics compared to the abstract implementation, so the test is overridden here.
		IMessageViewer messageViewer0 = mock(IMessageViewer.class);
		AbstractMessageList messageList = createMessageListForTest();
		assertEquals(0, messageList.getMessageListSize());

		// listener is not called if not registered, but the message is already stored
		BasicMessage message0 = createNamedMessage("PV0");
		messageList.addMessage(message0);
		verify(messageViewer0, never()).addJMSMessage(any(BasicMessage.class));
		assertEquals(1, messageList.getMessageListSize());

		// now registered, so listener is called once. message is stored too.
		BasicMessage message1 = createNamedMessage("PV1");
		messageList.addChangeListener(messageViewer0);
		messageList.addMessage(message1);
		verify(messageViewer0, times(1)).addJMSMessage(any(BasicMessage.class));
		assertEquals(2, messageList.getMessageListSize());

		// no longer registered, listener not called again, but one more message is stored
		BasicMessage message2 = createNamedMessage("PV2");
		messageList.removeChangeListener(messageViewer0);
		messageList.addMessage(message2);
		verify(messageViewer0, times(1)).addJMSMessage(any(BasicMessage.class));
		assertEquals(3, messageList.getMessageListSize());
	}


	
}
