package org.csstudio.alarm.table.dataModel;

import java.util.HashMap;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.junit.Assert;
import org.junit.Test;

public class JMSAlarmMessageListTest {

	String columnNames = "ACK,25;COUNT,52;TYPE,56;EVENTTIME,138;NAME,173;VALUE,100;HOST,100;FACILITY,100;TEXT,100;SEVERITY,100;STATUS,100;OVERWRITES,100;SEVERITY-MAX,100;SEVERITY-OLD,73;STATUS-OLD,178;USER,100;APPLICATION-ID,100;PROCESS-ID,100;CLASS,100;DOMAIN,100;LOCATION,100;DESTINATION,100";

	static Integer incrementedMilliSeconds = 100;

	public String createAndIncrementDate() {
		String time = "yyyy-MM-dd HH:mm:ss"
				+ incrementedMilliSeconds.toString();
		incrementedMilliSeconds++;
		return time;
	}


	public MapMessage createMapMessage(String name, String severity, String type)
		throws JMSException {
		ActiveMQMapMessage message = new ActiveMQMapMessage();
		message.setString("TYPE", type);
		message.setString("NAME", name);
		message.setString("SEVERITY", severity);
		message.setString("EVENTTIME", createAndIncrementDate());
		return message;
	}	

	public MapMessage addJMSMessage(String name, String severity, String type, boolean gray, boolean acknowledged, JMSAlarmMessageList messageList)
	throws JMSException {
		ActiveMQMapMessage message = new ActiveMQMapMessage();
		message.setString("TYPE", type);
		message.setString("NAME", name);
		message.setString("SEVERITY", severity);
		message.setString("EVENTTIME", createAndIncrementDate());
		
		return message;
	}	
	
	@Test
	public void testSimpleMessageSequence() throws JMSException {
		JMSAlarmMessageList messageList = new JMSAlarmMessageList(columnNames
				.split(";"));
		messageList.setSound(false);
		messageList.setSound(false);
		MapMessage message1 = createMapMessage("NAME", "MINOR", "event");
		messageList.addJMSMessage(message1);
		MapMessage message2 = createMapMessage("NAME", "MINOR", "event");
		messageList.addJMSMessage(message2);
		Assert.assertEquals(1, messageList.getJMSMessageList().size());
		MapMessage message3 = createMapMessage("NAME", "MAJOR", "event");
		messageList.addJMSMessage(message3);
		Assert.assertEquals(2, messageList.getJMSMessageList().size());
		Assert.assertEquals(true, checkForAlarm("NAME", "MAJOR", false, false,
				messageList));
		Assert.assertEquals(true, checkForAlarm("NAME", "MINOR", true, false,
				messageList));
		
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

	@Test
	public void testInvalidMapMessages() throws JMSException {
		// property TYPE = null
		JMSAlarmMessageList messageList = new JMSAlarmMessageList(columnNames
				.split(";"));
		messageList.setSound(false);
		MapMessage message1 = createMapMessage("NAME", "MAJOR", null);
		messageList.addJMSMessage(message1);
		Assert.assertEquals(0, messageList.getJMSMessageList().size());

		MapMessage message4 = createMapMessage("NAME", "MAJOR", "status");
		messageList.addJMSMessage(message4);
		Assert.assertEquals(0, messageList.getJMSMessageList().size());

		// property SEVERITY = null
		MapMessage message3 = createMapMessage("NAME", null, "event");
		messageList.addJMSMessage(message3);
		Assert.assertEquals(0, messageList.getJMSMessageList().size());

		ActiveMQMapMessage message2 = null;
		messageList.addJMSMessage(message2);
		Assert.assertEquals(0, messageList.getJMSMessageList().size());
	}
}
