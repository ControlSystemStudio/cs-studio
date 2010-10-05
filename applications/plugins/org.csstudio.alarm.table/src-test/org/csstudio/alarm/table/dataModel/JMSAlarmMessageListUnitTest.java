package org.csstudio.alarm.table.dataModel;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.table.preferences.ISeverityMapping;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the message list. The message list is the model for the alarm table views (log, ams, alarm).
 * 
 * @author jpenning
 * @author $Author: bknerr $
 * @since 04.10.2010
 */
public class JMSAlarmMessageListUnitTest {

	private static Integer INCREMENTED_MSEC = 100;

	
	@Before
	public void setUp() {
		ISeverityMapping severityMapping = new SeverityMappingForTest();
		SeverityRegistry.setSeverityMapping(severityMapping);
	}
	
	
	// Test case to reproduce an error 2008-01-06:
	// more than one message that should be deleted for a corresponding
	// acknowledge message are in the table (NO_ALARM or grayed out).
	// -> receive acknowledge message for all messages.
	// -> only one message is removed.
	@Test
	public void testRemoveMessages() {
		final AlarmMessageList messageList = new AlarmMessageList();
		final String eventtimeMajor = createAndIncrementDate();
		final String eventtimeMinor = createAndIncrementDate();

		for (int i = 0; i < 5; i++) {
			messageList.addMessage(createJMSMessage("NAME_" + i, "MINOR",
					"event", false, eventtimeMinor));
		}
		for (int i = 0; i < 5; i++) {
			Assert.assertEquals(true,
					checkForAlarm("NAME_" + i, "MINOR", messageList));
		}
		for (int i = 0; i < 5; i++) {
			messageList.addMessage(createJMSMessage("NAME_" + i, "MAJOR",
					"event", false, eventtimeMajor));
		}
		Assert.assertEquals(10, messageList.getMessageListSize());
		for (int i = 0; i < 5; i++) {
			Assert.assertEquals(true,
					checkForAlarm("NAME_" + i, "MAJOR", messageList));
			Assert.assertEquals(true,
					checkForAlarm("NAME_" + i, "MINOR", messageList));
		}

		// Send acknowledges to minor
		for (int i = 0; i < 5; i++) {
			messageList.addMessage(createJMSMessage("NAME_" + i, "MINOR",
					"event", true, eventtimeMinor));
		}
		for (int i = 0; i < 5; i++) {
			Assert.assertEquals(false,
					checkForAlarm("NAME_" + i, "MINOR", messageList));
		}
		for (int i = 0; i < 5; i++) {
			messageList.addMessage(createJMSMessage("NAME_" + i, "MAJOR",
					"event", true, eventtimeMajor));
		}
		for (int i = 0; i < 5; i++) {
			Assert.assertEquals(true,
					checkForAlarm("NAME_" + i, "MAJOR", messageList));
		}
	}

	@Test
	public void testAddStatusDisconnected() {
		final AlarmMessageList messageList = new AlarmMessageList();
		messageList.addMessage(createJMSMessage("NAME", "MAJOR", "event",
				false, createAndIncrementDate()));
		messageList.addMessage(createJMSMessage("NAME", "MINOR", "event",
				false, createAndIncrementDate()));
		Assert.assertEquals(2, messageList.getMessageListSize());
		messageList.addMessage(addJMSMessage("NAME", "MINOR", "status", false,
				createAndIncrementDate(), "DISCONNECTED"));
		Assert.assertEquals(1, messageList.getMessageListSize());
		Assert.assertEquals(true,
				checkForAlarm("NAME", "MINOR", messageList, "DISCONNECTED"));
		// messageList.deleteAllMessages();

		messageList.addMessage(createJMSMessage("NAME", "INVALID", "event",
				false, createAndIncrementDate()));
		messageList.addMessage(addJMSMessage("NAME", "MINOR", "status", false,
				createAndIncrementDate(), "DISCONNECTED"));
		Assert.assertEquals(1, messageList.getMessageListSize());
		Assert.assertEquals(true,
				checkForAlarm("NAME", "INVALID", messageList, "DISCONNECTED"));
		// messageList.deleteAllMessages();

		messageList.addMessage(createJMSMessage("NAME", "MINOR", "event",
				false, createAndIncrementDate()));
		messageList.addMessage(addJMSMessage("NAME", "NO_ALARM", "status",
				false, createAndIncrementDate(), "DISCONNECTED"));
		Assert.assertEquals(1, messageList.getMessageListSize());
		Assert.assertEquals(true,
				checkForAlarm("NAME", "MINOR", messageList, "DISCONNECTED"));
		messageList.addMessage(addJMSMessage("NAME", "MAJOR", "status", false,
				createAndIncrementDate(), "CONNECTED"));
		Assert.assertEquals(true,
				checkForAlarm("NAME", "MAJOR", messageList, "CONNECTED"));

		messageList.addMessage(createJMSMessage("NAME", "MAJOR", "event",
				false, createAndIncrementDate()));
		messageList.addMessage(createJMSMessage("NAME_NEU", "MINOR", "status",
				false, createAndIncrementDate()));
		Assert.assertEquals(1, messageList.getMessageListSize());
		messageList.addMessage(addJMSMessage("NAME_NEU", "MINOR", "status",
				false, createAndIncrementDate(), "DISCONNECTED"));
		Assert.assertEquals(1, messageList.getMessageListSize());
		// messageList.deleteAllMessages();
	}

	@Test
	public void testSimpleMessageSequence() {
		final AlarmMessageList messageList = new AlarmMessageList();
		messageList.addMessage(createJMSMessage("NAME", "MINOR", "event",
				false, null));
		messageList.addMessage(createJMSMessage("NAME", "MINOR", "event",
				false, null));
		Assert.assertEquals(1, messageList.getMessageListSize());

		// keep eventtime to create later an ack-message with the same eventtime
		// string.
		final String eventtime = createAndIncrementDate();
		messageList.addMessage(createJMSMessage("NAME", "MAJOR", "event",
				false, eventtime));
		Assert.assertEquals(2, messageList.getMessageListSize());
		Assert.assertEquals(true, checkForAlarm("NAME", "MAJOR", messageList));
		Assert.assertEquals(true, checkForAlarm("NAME", "MINOR", messageList));
		messageList.addMessage(createJMSMessage("NAME", "MAJOR", "event", true,
				eventtime));
		Assert.assertEquals(2, messageList.getMessageListSize());
		Assert.assertEquals(true, checkForAlarm("NAME", "MAJOR", messageList));
		Assert.assertEquals(true, checkForAlarm("NAME", "MINOR", messageList));
		messageList.addMessage(createJMSMessage("NAME", "NO_ALARM", "event",
				false, null));
		Assert.assertEquals(1, messageList.getMessageListSize());
		Assert.assertEquals(true,
				checkForAlarm("NAME", "NO_ALARM", messageList));
	}

	@Test
	public void testInvalidMapMessages() {
		// This test ensures that only useful messages are added to the table.

		final AlarmMessageList messageList = new AlarmMessageList();

		// add empty message
		messageList.addMessage(new BasicMessage());
		Assert.assertEquals(0, messageList.getMessageListSize());

		// property severity = null
		messageList.addMessage(createJMSMessage("NAME", null, "type",
				Boolean.TRUE, null));

		// if no type is given, only messages which ack TRUE will be processed
		// property type = null and ack is not set
		messageList.addMessage(createJMSMessage("NAME", "MAJOR", null, null,
				null));
		Assert.assertEquals(0, messageList.getMessageListSize());

		// property type = null and ack is set to FALSE
		messageList.addMessage(createJMSMessage("NAME", "MINOR", null,
				Boolean.FALSE, null));
		Assert.assertEquals(0, messageList.getMessageListSize());
	}

	private boolean checkForAlarm(@Nonnull final String name, @Nonnull final String severity,
			@Nonnull final AlarmMessageList messageList) {
		return checkForAlarm(name, severity, messageList, null);
	}

	private boolean checkForAlarm(@Nonnull final String name, @Nonnull final String severity,
			@Nonnull final AlarmMessageList inputList, @CheckForNull final String status) {
		boolean isEqual = false;
		for (final BasicMessage message : inputList.getMessageList()) {
			final Map<String, String> messageHashMap = message.getHashMap();
			if ((messageHashMap.get("NAME").equalsIgnoreCase(name))
					&& (messageHashMap.get("SEVERITY")
							.equalsIgnoreCase(severity))) {
				isEqual = true;
				if (status != null) {
					if (!messageHashMap.get("STATUS").equalsIgnoreCase(status)) {
						isEqual = false;
					}
				}
				return isEqual;
			}
		}
		return isEqual;
	}

	@Nonnull
	private String createAndIncrementDate() {
		final String time = "2008-10-11 12:13:14."
				+ INCREMENTED_MSEC.toString();
		INCREMENTED_MSEC++;
		return time;
	}

	@Nonnull
	private BasicMessage createJMSMessage(@Nonnull final String name,
			@Nonnull final String severity, @Nonnull final String type,
			@CheckForNull final Boolean acknowledged,
			@Nonnull final String eventtime) {
		return addJMSMessage(name, severity, type, acknowledged, eventtime,
				null);
	}

	@Nonnull
	private BasicMessage addJMSMessage(@Nonnull final String name,
			@Nonnull final String severity, @Nonnull final String type,
			@CheckForNull final Boolean acknowledged,
			@Nonnull final String eventtime, @CheckForNull final String status) {

		final BasicMessage message = new BasicMessage();
		message.setProperty("TYPE", type);
		message.setProperty("NAME", name);
		message.setProperty("SEVERITY", severity);
		message.setProperty("EVENTTIME", eventtime);
		if (acknowledged == null) {
			message.setProperty("ACK", null);
		} else {
			message.setProperty("ACK", acknowledged.toString());
		}
		if (status != null) {
			message.setProperty("STATUS", status);
		}
		return message;
	}
	
	/**
	 * Mapping for test
	 */
	private static class SeverityMappingForTest implements ISeverityMapping {

		private final HashMap<String, String> _severityKeyValueMapping = new HashMap<String, String>();
		private final HashMap<String, Integer> _severityKeyNumberMapping = new HashMap<String, Integer>();

		public SeverityMappingForTest() {
			enterValueAndNumberForKey("MAJOR", "MAJOR", 0);
			enterValueAndNumberForKey("MINOR", "MINOR", 1);
			enterValueAndNumberForKey("NO_ALARM", "NO_ALARM", 2);
			enterValueAndNumberForKey("INVALID", "INVALID", 3);
			enterValueAndNumberForKey("4", "NOT DEFINED", 4);
			enterValueAndNumberForKey("FATAL", "FATAL", 5);
			enterValueAndNumberForKey("ERROR", "ERROR", 6);
			enterValueAndNumberForKey("WARN", "WARN", 7);
			enterValueAndNumberForKey("INFO", "INFO", 8);
			enterValueAndNumberForKey("DEBUG", "DEBUG", 9);
		}

		private void enterValueAndNumberForKey(
				@Nonnull String key, @Nonnull String value,
				int number) {
			_severityKeyValueMapping.put(key, value);
			_severityKeyNumberMapping.put(key, number);
		}

		@Override
	    public String findSeverityValue(@Nonnull final String severityKey) {
	    	String severityValue = _severityKeyValueMapping.get(severityKey);
	        if (severityValue == null) {
	            return "invalid severity";
	        } else {
	        	return severityValue;
	        }
	    }

		@Override
	    public int getSeverityNumber(@Nonnull final String severityKey) {
	    	Integer severityNumber = _severityKeyNumberMapping.get(severityKey);
	    	//if there is no mapping return 10, that means the lowest severity
	    	if (severityNumber == null) {
	    		return 10;
	    	} else {
	    		return severityNumber;
	    	}
	    }
		
	}
	
	

	
}
