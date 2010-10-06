package org.csstudio.alarm.table.dataModel;

import static org.mockito.Mockito.mock;

import org.csstudio.alarm.table.preferences.ISeverityMapping;
import org.junit.Before;

/**
 * Test for the abstract message list. The message list is the model for the
 * alarm table views (log, ams, alarm).
 * 
 * @author jpenning
 * @author $Author: jpenning $
 * @since 04.10.2010
 */
public class ArchiveMessageListUnitTest extends AbstractMessageListUnitTest {

	
	@Override
	public AbstractMessageList createMessageListForTest() {
		return new ArchiveMessageList();
	}
	
	@Before
	public void setUp() {
		// severity mapping is currently not interpreted in the basic message
		SeverityRegistry.setSeverityMapping(mock(ISeverityMapping.class));
	}

}
