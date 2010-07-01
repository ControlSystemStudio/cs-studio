package org.csstudio.alarm.table.dataModel;

import java.util.HashMap;
import java.util.Vector;

import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.junit.Assert;
import org.junit.Test;

public class JMSAlarmMessageListTest {

	String columnNames = "ACK,25;COUNT,52;TYPE,56;EVENTTIME,138;NAME,173;VALUE,100;HOST,100;FACILITY,100;TEXT,100;SEVERITY,100;STATUS,100;OVERWRITES,100;SEVERITY-MAX,100;SEVERITY-OLD,73;STATUS-OLD,178;USER,100;APPLICATION-ID,100;PROCESS-ID,100;CLASS,100;DOMAIN,100;LOCATION,100;DESTINATION,100";

	static Integer incrementedMilliSeconds = 100;

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
		for(int i = 0; i < 5; i++) {
			addJMSMessage("NAME_" + i, "MAJOR", "event", false, eventtimeMajor, messageList);
		}
		for(int i = 0; i < 5; i++) {
			addJMSMessage("NAME_" + i, "MINOR", "event", false, eventtimeMinor, messageList);
		}
		for(int i = 0; i < 5; i++) {
			Assert.assertEquals(true, checkForAlarm("NAME_" + i, "MAJOR", messageList));
		}
		Assert.assertEquals(10, messageList.getJMSMessageList().size());
		//Send acknowledges
		for(int i = 0; i < 5; i++) {
			addJMSMessage("NAME_" + i, "MINOR", "event", true, eventtimeMinor, messageList);
		}
		for(int i = 0; i < 5; i++) {
			Assert.assertEquals(true, checkForAlarm("NAME_" + i, "MINOR", messageList));
		}
		for(int i = 0; i < 5; i++) {
			addJMSMessage("NAME_" + i, "MAJOR", "event", true, eventtimeMajor, messageList);
		}
//		Assert.assertEquals(5, messageList.getJMSMessageList().size());
	}

	@Test
	public void testAddStatusDisconnected() {
		final AlarmMessageList messageList = new AlarmMessageList();
		addJMSMessage("NAME", "MAJOR", "event", false, null, messageList);
		addJMSMessage("NAME", "MINOR", "event", false, null, messageList);
		Assert.assertEquals(2, messageList.getJMSMessageList().size());
		addJMSMessage("NAME", "MINOR", "status", false, null, messageList, "DISCONNECTED");
		Assert.assertEquals(1, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "MINOR", messageList, "DISCONNECTED"));
//		messageList.deleteAllMessages();

		addJMSMessage("NAME", "INVALID", "event", false, null, messageList);
		addJMSMessage("NAME", "MINOR", "status", false, null, messageList, "DISCONNECTED");
		Assert.assertEquals(1, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "INVALID", messageList, "DISCONNECTED"));
//		messageList.deleteAllMessages();

		addJMSMessage("NAME", "MINOR", "event", false, null, messageList);
		addJMSMessage("NAME", "NO_ALARM", "status", false, null, messageList, "DISCONNECTED");
		Assert.assertEquals(1, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "MINOR", messageList, "DISCONNECTED"));
		addJMSMessage("NAME", "MAJOR", "status", false, null, messageList, "CONNECTED");
		Assert.assertEquals(true, checkForAlarm("NAME", "MAJOR", messageList, "CONNECTED"));

		addJMSMessage("NAME", "MAJOR", "event", false, null, messageList);
		addJMSMessage("NAME_NEU", "MINOR", "status", false, null, messageList);
		Assert.assertEquals(1, messageList.getJMSMessageList().size());
		addJMSMessage("NAME_NEU", "MINOR", "status", false, null, messageList, "DISCONNECTED");
		Assert.assertEquals(1, messageList.getJMSMessageList().size());
//		messageList.deleteAllMessages();
	}


	@Test
	public void testSimpleMessageSequence() {
		final AlarmMessageList messageList = new AlarmMessageList();
		addJMSMessage("NAME", "MINOR", "event", false, null, messageList);
		addJMSMessage("NAME", "MINOR", "event", false, null, messageList);
		Assert.assertEquals(1, messageList.getJMSMessageList().size());

		// keep eventtime to create later an ack-message with the same eventtime
		// string.
		final String eventtime = createAndIncrementDate();
		addJMSMessage("NAME", "MAJOR", "event", false, eventtime, messageList);
		Assert.assertEquals(2, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "MAJOR",
				messageList));
		Assert.assertEquals(true, checkForAlarm("NAME", "MINOR",
				messageList));
		addJMSMessage("NAME", "MAJOR", "event", true, eventtime, messageList);
		Assert.assertEquals(2, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "MAJOR",
				messageList));
		Assert.assertEquals(true, checkForAlarm("NAME", "MINOR",
				messageList));
		addJMSMessage("NAME", "NO_ALARM", "event", false, null, messageList);
		Assert.assertEquals(1, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "NO_ALARM", messageList));
	}

	@Test
	public void testInvalidMapMessages() throws JMSException {
		// property TYPE = null
		final AlarmMessageList messageList = new AlarmMessageList();
		addJMSMessage("NAME", "MAJOR", null, false, null, messageList);
		Assert.assertEquals(0, messageList.getJMSMessageList().size());

		addJMSMessage("NAME", "MAJOR", "status", false, null, messageList);
		Assert.assertEquals(0, messageList.getJMSMessageList().size());

		// property SEVERITY = null
		addJMSMessage("NAME", null, "event", false, null, messageList);
		Assert.assertEquals(0, messageList.getJMSMessageList().size());

		final ActiveMQMapMessage message2 = null;
//		messageList.addMessage(message2);
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
			final HashMap<String, String> messageHashMap = message.getHashMap();
			if ((messageHashMap.get("NAME").equalsIgnoreCase(name)) &&
			    (messageHashMap.get("SEVERITY").equalsIgnoreCase(severity))) {
				isEqual = true;
				if (status != null) {
					if (!messageHashMap.get("STATUS").equalsIgnoreCase(status)) {
						isEqual = false;
					}
				}
			}
		}
		return isEqual;
	}

	private String createAndIncrementDate() {
		final String time = "2008-10-11 12:13:14."
				+ incrementedMilliSeconds.toString();
		incrementedMilliSeconds++;
		return time;
	}


	private void addJMSMessage(final String name,
	                           final String severity,
	                           final String type,
	                           final boolean acknowledged,
	                           final String eventtime,
	                           final AlarmMessageList messageList) {
		addJMSMessage(name, severity, type, acknowledged, eventtime, messageList, null);
	}

	private void addJMSMessage(final String name,
	                           final String severity,
	                           final String type,
	                           final boolean acknowledged,
	                           final String eventtime,
	                           final AlarmMessageList messageList,
	                           final String status) {

		final BasicMessage message = new BasicMessage();
		message.setProperty("TYPE", type);
		message.setProperty("NAME", name);
		message.setProperty("SEVERITY", severity);
		if (eventtime != null) {
			message.setProperty("EVENTTIME", eventtime);
		} else {
			message.setProperty("EVENTTIME", createAndIncrementDate());
		}
		if (acknowledged) {
			message.setProperty("ACK", "TRUE");
		}
		if (status != null) {
			message.setProperty("STATUS", status);
		}
		messageList.addMessage(message);
	}
}
