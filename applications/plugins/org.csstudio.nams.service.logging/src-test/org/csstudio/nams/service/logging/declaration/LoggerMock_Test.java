package org.csstudio.nams.service.logging.declaration;

import junit.framework.TestCase;

import org.csstudio.nams.service.logging.declaration.LoggerMock.LogEntry;
import org.junit.Test;

public class LoggerMock_Test extends TestCase {

	@Test
	public void testCallAllMethodAndCheckLoggingEntry() {
		Object dummy = new Object();
		LoggerMock logger = new LoggerMock();
		
		assertNotNull(logger.mockGetCurrentLogEntries());
		assertEquals(0, logger.mockGetCurrentLogEntries().length);
		
		logger.mockClearCurrentLogEntries();
	
		assertNotNull(logger.mockGetCurrentLogEntries());
		assertEquals(0, logger.mockGetCurrentLogEntries().length);
		
		logger.logDebugMessage(dummy, "Test-Message-1");
		LogEntry[] mockCurrentLogEntries = logger.mockGetCurrentLogEntries();
		assertNotNull(mockCurrentLogEntries);
		assertEquals(1, mockCurrentLogEntries.length);
		assertEquals("Test-Message-1", mockCurrentLogEntries[0].getMessage());
		assertEquals(LoggerMock.LogType.DEBUG, mockCurrentLogEntries[0].getLogType());
		assertEquals(dummy, mockCurrentLogEntries[0].getCaller());
		assertEquals(null, mockCurrentLogEntries[0].getThrowable());
		
		// TODO call all other methods to fully check behaviour.
	}

}
