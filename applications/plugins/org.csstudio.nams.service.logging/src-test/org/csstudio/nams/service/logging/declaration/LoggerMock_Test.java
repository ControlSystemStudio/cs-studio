package org.csstudio.nams.service.logging.declaration;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.nams.service.logging.declaration.LoggerMock.LogEntry;
import org.junit.Test;

public class LoggerMock_Test extends TestCase {

	@Test
	public void testCallAllMethodAndCheckLoggingEntry() {
		final Object dummy = new Object();
		final LoggerMock logger = new LoggerMock();

		Assert.assertNotNull(logger.mockGetCurrentLogEntries());
		Assert.assertEquals(0, logger.mockGetCurrentLogEntries().length);

		logger.mockClearCurrentLogEntries();

		Assert.assertNotNull(logger.mockGetCurrentLogEntries());
		Assert.assertEquals(0, logger.mockGetCurrentLogEntries().length);

		logger.logDebugMessage(dummy, "Test-Message-1");
		final LogEntry[] mockCurrentLogEntries = logger
				.mockGetCurrentLogEntries();
		Assert.assertNotNull(mockCurrentLogEntries);
		Assert.assertEquals(1, mockCurrentLogEntries.length);
		Assert.assertEquals("Test-Message-1", mockCurrentLogEntries[0]
				.getMessage());
		Assert.assertEquals(LoggerMock.LogType.DEBUG, mockCurrentLogEntries[0]
				.getLogType());
		Assert.assertEquals(dummy, mockCurrentLogEntries[0].getCaller());
		Assert.assertEquals(null, mockCurrentLogEntries[0].getThrowable());

		// TODO call all other methods to fully check behaviour.
	}

}
