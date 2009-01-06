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
	public void testRemoveMessages() throws JMSException {
		JMSAlarmMessageList messageList = new JMSAlarmMessageList(columnNames
				.split(";"));
		messageList.setSound(false);
		String eventtimeMajor = createAndIncrementDate();
		String eventtimeMinor = createAndIncrementDate();
		for(int i = 0; i < 5; i++) {
			addJMSMessage("NAME_" + i, "MAJOR", "event", false, eventtimeMajor, messageList);
		}
		for(int i = 0; i < 5; i++) {
			addJMSMessage("NAME_" + i, "MINOR", "event", false, eventtimeMinor, messageList);
		}
		for(int i = 0; i < 5; i++) {
			Assert.assertEquals(true, checkForAlarm("NAME_" + i, "MAJOR", true,
					false, messageList));
		}
		Assert.assertEquals(10, messageList.getJMSMessageList().size());
		//Send acknowledges
		for(int i = 0; i < 5; i++) {
			addJMSMessage("NAME_" + i, "MINOR", "event", true, eventtimeMinor, messageList);
		}
		for(int i = 0; i < 5; i++) {
			Assert.assertEquals(true, checkForAlarm("NAME_" + i, "MINOR", false,
					true, messageList));
		}
		for(int i = 0; i < 5; i++) {
			addJMSMessage("NAME_" + i, "MAJOR", "event", true, eventtimeMajor, messageList);
		}
//		Assert.assertEquals(5, messageList.getJMSMessageList().size());
	}

	@Test
	public void testSimpleMessageSequence() throws JMSException {
		JMSAlarmMessageList messageList = new JMSAlarmMessageList(columnNames
				.split(";"));
		messageList.setSound(false);
		addJMSMessage("NAME", "MINOR", "event", false, null, messageList);
		addJMSMessage("NAME", "MINOR", "event", false, null, messageList);
		Assert.assertEquals(1, messageList.getJMSMessageList().size());

		// keep eventtime to create later an ack-message with the same eventtime
		// string.
		String eventtime = createAndIncrementDate();
		addJMSMessage("NAME", "MAJOR", "event", false, eventtime, messageList);
		Assert.assertEquals(2, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "MAJOR", false, false,
				messageList));
		Assert.assertEquals(true, checkForAlarm("NAME", "MINOR", true, false,
				messageList));
		addJMSMessage("NAME", "MAJOR", "event", true, eventtime, messageList);
		Assert.assertEquals(2, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "MAJOR", false, true,
				messageList));
		Assert.assertEquals(true, checkForAlarm("NAME", "MINOR", true, false,
				messageList));
		addJMSMessage("NAME", "NO_ALARM", "event", false, null, messageList);
		Assert.assertEquals(1, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "NO_ALARM", false,
				false, messageList));
	}

	@Test
	public void testInvalidMapMessages() throws JMSException {
		// property TYPE = null
		JMSAlarmMessageList messageList = new JMSAlarmMessageList(columnNames
				.split(";"));
		messageList.setSound(false);
		addJMSMessage("NAME", "MAJOR", null, false, null, messageList);
		Assert.assertEquals(0, messageList.getJMSMessageList().size());

		addJMSMessage("NAME", "MAJOR", "status", false, null, messageList);
		Assert.assertEquals(0, messageList.getJMSMessageList().size());

		// property SEVERITY = null
		addJMSMessage("NAME", null, "event", false, null, messageList);
		Assert.assertEquals(0, messageList.getJMSMessageList().size());

		ActiveMQMapMessage message2 = null;
		messageList.addJMSMessage(message2);
		Assert.assertEquals(0, messageList.getJMSMessageList().size());
	}

	private boolean checkForAlarm(String name, String severity, boolean gray,
			boolean acknowledged, JMSAlarmMessageList messageList) {
		Vector<JMSMessage> messageList2 = messageList.getJMSMessageList();
		for (JMSMessage message : messageList2) {
			HashMap<String, String> messageHashMap = message.getHashMap();
			if ((messageHashMap.get("NAME").equalsIgnoreCase(name))
					&& (messageHashMap.get("SEVERITY")
							.equalsIgnoreCase(severity))
					&& (message.isBackgroundColorGray() == gray)
					&& (message.isAcknowledged() == acknowledged)) {
				return true;
			}
		}
		return false;
	}

	private String createAndIncrementDate() {
		String time = "2008-10-11 12:13:14."
				+ incrementedMilliSeconds.toString();
		incrementedMilliSeconds++;
		return time;
	}

	private void addJMSMessage(String name, String severity, String type,
			boolean acknowledged, String eventtime,
			JMSAlarmMessageList messageList) throws JMSException {
		ActiveMQMapMessage message = new ActiveMQMapMessage();
		message.setString("TYPE", type);
		message.setString("NAME", name);
		message.setString("SEVERITY", severity);
		if (eventtime != null) {
			message.setString("EVENTTIME", eventtime);
		} else {
			message.setString("EVENTTIME", createAndIncrementDate());
		}
		if (acknowledged == true) {
			message.setString("ACK", "TRUE");
		}
		messageList.addJMSMessage(message);
	}
}
