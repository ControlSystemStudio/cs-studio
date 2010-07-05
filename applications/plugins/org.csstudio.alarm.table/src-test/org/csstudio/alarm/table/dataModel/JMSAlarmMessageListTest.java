package org.csstudio.alarm.table.dataModel;

import java.util.Map;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;

public class JMSAlarmMessageListTest {

	String columnNames = "ACK,25;COUNT,52;TYPE,56;EVENTTIME,138;NAME,173;VALUE,100;HOST,100;FACILITY,100;TEXT,100;SEVERITY,100;STATUS,100;OVERWRITES,100;SEVERITY-MAX,100;SEVERITY-OLD,73;STATUS-OLD,178;USER,100;APPLICATION-ID,100;PROCESS-ID,100;CLASS,100;DOMAIN,100;LOCATION,100;DESTINATION,100";

	static Integer _incrementedMilliSeconds = 100;

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
		// add 5 minors with the same name but later time
		for(int i = 0; i < 5; i++) {
		    messageList.addMessage(createJMSMessage("NAME_" + i, "MINOR", "event", false, eventtimeMinor));
		}
		for(int i = 0; i < 5; i++) {
		    Assert.assertEquals(true, checkForAlarm("NAME_" + i, "MINOR", messageList));
		}
		for(int i = 0; i < 5; i++) {
		    messageList.addMessage(createJMSMessage("NAME_" + i, "MAJOR", "event", false, eventtimeMajor));
		}
		for(int i = 0; i < 5; i++) {
		    Assert.assertEquals(true, checkForAlarm("NAME_" + i, "MAJOR", messageList));
		    Assert.assertEquals(true, checkForAlarm("NAME_" + i, "MINOR", messageList));
		}

		Assert.assertEquals(10, messageList.getJMSMessageList().size());
		//Send acknowledges
		for(int i = 0; i < 5; i++) {
		    messageList.addMessage(createJMSMessage("NAME_" + i, "MINOR", "event", true, eventtimeMinor));
		}
		for(int i = 0; i < 5; i++) {
			Assert.assertEquals(true, checkForAlarm("NAME_" + i, "MINOR", messageList));
		}
		for(int i = 0; i < 5; i++) {
		    messageList.addMessage(createJMSMessage("NAME_" + i, "MAJOR", "event", true, eventtimeMajor));
		}
//		Assert.assertEquals(5, messageList.getJMSMessageList().size());
	}

	@Test
	public void testAddStatusDisconnected() {
		final AlarmMessageList messageList = new AlarmMessageList();
		messageList.addMessage(createJMSMessage("NAME", "MAJOR", "event", false, createAndIncrementDate()));
		messageList.addMessage(createJMSMessage("NAME", "MINOR", "event", false, createAndIncrementDate()));
		Assert.assertEquals(2, messageList.getJMSMessageList().size());
		messageList.addMessage(addJMSMessage("NAME", "MINOR", "status", false, createAndIncrementDate(), "DISCONNECTED"));
		Assert.assertEquals(1, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "MINOR", messageList, "DISCONNECTED"));
//		messageList.deleteAllMessages();

		messageList.addMessage(createJMSMessage("NAME", "INVALID", "event", false, createAndIncrementDate()));
		messageList.addMessage(addJMSMessage("NAME", "MINOR", "status", false, createAndIncrementDate(), "DISCONNECTED"));
		Assert.assertEquals(1, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "INVALID", messageList, "DISCONNECTED"));
//		messageList.deleteAllMessages();

		messageList.addMessage(createJMSMessage("NAME", "MINOR", "event", false, createAndIncrementDate()));
		messageList.addMessage(addJMSMessage("NAME", "NO_ALARM", "status", false, createAndIncrementDate(), "DISCONNECTED"));
		Assert.assertEquals(1, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "MINOR", messageList, "DISCONNECTED"));
		messageList.addMessage(addJMSMessage("NAME", "MAJOR", "status", false, createAndIncrementDate(), "CONNECTED"));
		Assert.assertEquals(true, checkForAlarm("NAME", "MAJOR", messageList, "CONNECTED"));

		messageList.addMessage(createJMSMessage("NAME", "MAJOR", "event", false, createAndIncrementDate()));
		messageList.addMessage(createJMSMessage("NAME_NEU", "MINOR", "status", false, createAndIncrementDate()));
		Assert.assertEquals(1, messageList.getJMSMessageList().size());
		messageList.addMessage(addJMSMessage("NAME_NEU", "MINOR", "status", false, createAndIncrementDate(), "DISCONNECTED"));
		Assert.assertEquals(1, messageList.getJMSMessageList().size());
//		messageList.deleteAllMessages();
	}


	@Test
	public void testSimpleMessageSequence() {
		final AlarmMessageList messageList = new AlarmMessageList();
		messageList.addMessage(createJMSMessage("NAME", "MINOR", "event", false, null));
		messageList.addMessage(createJMSMessage("NAME", "MINOR", "event", false, null));
		Assert.assertEquals(1, messageList.getJMSMessageList().size());

		// keep eventtime to create later an ack-message with the same eventtime
		// string.
		final String eventtime = createAndIncrementDate();
		messageList.addMessage(createJMSMessage("NAME", "MAJOR", "event", false, eventtime));
		Assert.assertEquals(2, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "MAJOR", messageList));
		Assert.assertEquals(true, checkForAlarm("NAME", "MINOR", messageList));
		messageList.addMessage(createJMSMessage("NAME", "MAJOR", "event", true, eventtime));
		Assert.assertEquals(2, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "MAJOR",
				messageList));
		Assert.assertEquals(true, checkForAlarm("NAME", "MINOR",
				messageList));
		messageList.addMessage(createJMSMessage("NAME", "NO_ALARM", "event", false, null));
		Assert.assertEquals(1, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "NO_ALARM", messageList));
	}

	@Test
	public void testInvalidMapMessages() {
	    final AlarmMessageList messageList = new AlarmMessageList();

	    // add empty message
	    messageList.addMessage(new BasicMessage());
	    Assert.assertEquals(0, messageList.getJMSMessageList().size());

		// property severity = null
	    messageList.addMessage(createJMSMessage("NAME", null, "type", Boolean.TRUE, null));

		// property type = null and ack is not set
		messageList.addMessage(createJMSMessage("NAME", "MAJOR", null, null, null));
		Assert.assertEquals(0, messageList.getJMSMessageList().size());

		// property type = null and ack is set to FALSE
		messageList.addMessage(createJMSMessage("NAME", "MINOR", null, Boolean.FALSE, null));
		Assert.assertEquals(0, messageList.getJMSMessageList().size());
	}

	private boolean checkForAlarm(final String name,
	                              final String severity,
	                              final AlarmMessageList messageList) {
		return checkForAlarm(name, severity, messageList, null);
	}


	private boolean checkForAlarm(final String name,
	                              final String severity,
	                              final AlarmMessageList inputList,
	                              final String status) {
		boolean isEqual = false;
		final Vector<? extends BasicMessage> messages = inputList.getJMSMessageList();
		for (final BasicMessage message : messages) {
			final Map<String, String> messageHashMap = message.getHashMap();
			if ((messageHashMap.get("NAME").equalsIgnoreCase(name)) &&
			    (messageHashMap.get("SEVERITY").equalsIgnoreCase(severity))) {
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

	private String createAndIncrementDate() {
		final String time = "2008-10-11 12:13:14." + _incrementedMilliSeconds.toString();
		_incrementedMilliSeconds++;
		return time;
	}

	private BasicMessage createJMSMessage(final String name,
	                                   final String severity,
	                                   final String type,
	                                   final Boolean acknowledged,
	                                   final String eventtime) {
	    return addJMSMessage(name, severity, type, acknowledged, eventtime, null);
	}

	private BasicMessage addJMSMessage(final String name,
	                                   final String severity,
	                                   final String type,
	                                   final Boolean acknowledged,
	                                   final String eventtime,
	                                   final String status) {

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
}
