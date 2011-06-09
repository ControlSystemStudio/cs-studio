package org.csstudio.alarm.table.dataModel;

import static org.mockito.Mockito.mock;

import java.util.Vector;

import org.csstudio.alarm.table.preferences.ISeverityMapping;
import org.junit.Before;

/**
 * Test for the abstract message list. An empty implementation of the message list is used for testing.
 * 
 * @author jpenning
 * @author $Author: jpenning $
 * @since 04.10.2010
 */
@SuppressWarnings("synthetic-access")
public class MessageListUnitTest extends AbstractMessageListUnitTest {

	@Before
	public void setUp() {
		// severity mapping is currently not interpreted in the basic message
		SeverityRegistry.setSeverityMapping(mock(ISeverityMapping.class));
	}

	@Override
	public AbstractMessageList createMessageListForTest() {
		return new TestMessageList();
	}
	
	/**
	 * Implementation of abstract message list for testing.
	 */
	private static class TestMessageList extends AbstractMessageList {

		@Override
		public Vector<? extends BasicMessage> getMessageList() {
			return null;
		}

	}

}
