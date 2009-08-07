package org.csstudio.nams.common.fachwert;

import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.junit.Assert;
import org.junit.Test;

public class MessageKeyEnum_Test extends
		AbstractObject_TestCase<MessageKeyEnum> {
	@Test
	public void testGetEnumForKey() {

		// 27 ELEMENTS ARE BEING MAPPED
		Assert
				.assertEquals(
						"34 Elements are being mapped(IF AMOUNT OF ELEMENTS CHANGES MessageKeyConverter must be updated)",
						34, MessageKeyEnum.values().length);

		// AMS-REINSERTED
		Assert.assertEquals(MessageKeyEnum.AMS_REINSERTED, MessageKeyEnum
				.getEnumFor("AMS-REINSERTED"));
		Assert.assertEquals(MessageKeyEnum.AMS_REINSERTED, MessageKeyEnum
				.getEnumFor("ams-reinserted"));

		// AMS_FILTERID
		Assert.assertEquals(MessageKeyEnum.AMS_FILTERID, MessageKeyEnum
				.getEnumFor("AMS-FILTERID"));
		Assert.assertEquals(MessageKeyEnum.AMS_FILTERID, MessageKeyEnum
				.getEnumFor("ams-filterid"));

		// APPLICATION_ID
		Assert.assertEquals(MessageKeyEnum.APPLICATION, MessageKeyEnum
				.getEnumFor("application"));
		Assert.assertEquals(MessageKeyEnum.APPLICATION, MessageKeyEnum
				.getEnumFor("APPLICATION"));
		
		// APPLICATION_ID
		Assert.assertEquals(MessageKeyEnum.APPLICATION_ID, MessageKeyEnum
				.getEnumFor("application-id"));
		Assert.assertEquals(MessageKeyEnum.APPLICATION_ID, MessageKeyEnum
				.getEnumFor("APPLICATION-ID"));

		// CLASS
		Assert.assertEquals(MessageKeyEnum.CLASS, MessageKeyEnum
				.getEnumFor("class"));
		Assert.assertEquals(MessageKeyEnum.CLASS, MessageKeyEnum
				.getEnumFor("CLASS"));

		// CREATETIME
		Assert.assertEquals(MessageKeyEnum.CREATETIME, MessageKeyEnum
				.getEnumFor("createtime"));
		Assert.assertEquals(MessageKeyEnum.CREATETIME, MessageKeyEnum
				.getEnumFor("CREATETIME"));
		
		// DESTINATION
		Assert.assertEquals(MessageKeyEnum.DESTINATION, MessageKeyEnum
				.getEnumFor("destination"));
		Assert.assertEquals(MessageKeyEnum.DESTINATION, MessageKeyEnum
				.getEnumFor("DESTINATION"));

		// DOMAIN
		Assert.assertEquals(MessageKeyEnum.DOMAIN, MessageKeyEnum
				.getEnumFor("domain"));
		Assert.assertEquals(MessageKeyEnum.DOMAIN, MessageKeyEnum
				.getEnumFor("DOMAIN"));

		// EVENTTIME
		Assert.assertEquals(MessageKeyEnum.EVENTTIME, MessageKeyEnum
				.getEnumFor("eventtime"));
		Assert.assertEquals(MessageKeyEnum.EVENTTIME, MessageKeyEnum
				.getEnumFor("EVENTTIME"));

		// FACILITY
		Assert.assertEquals(MessageKeyEnum.FACILITY, MessageKeyEnum
				.getEnumFor("facility"));
		Assert.assertEquals(MessageKeyEnum.FACILITY, MessageKeyEnum
				.getEnumFor("FACILITY"));

		// HOST
		Assert.assertEquals(MessageKeyEnum.HOST, MessageKeyEnum
				.getEnumFor("host"));
		Assert.assertEquals(MessageKeyEnum.HOST, MessageKeyEnum
				.getEnumFor("HOST"));
		
		// HOST-PHYS
		Assert.assertEquals(MessageKeyEnum.HOST_PHYS, MessageKeyEnum
				.getEnumFor("host-phys"));
		Assert.assertEquals(MessageKeyEnum.HOST_PHYS, MessageKeyEnum
				.getEnumFor("HOST-PHYS"));
		
		// HOWTO
		Assert.assertEquals(MessageKeyEnum.HOWTO, MessageKeyEnum
				.getEnumFor("howto"));
		Assert.assertEquals(MessageKeyEnum.HOWTO, MessageKeyEnum
				.getEnumFor("HOWTO"));

		// LOCATION
		Assert.assertEquals(MessageKeyEnum.LOCATION, MessageKeyEnum
				.getEnumFor("location"));
		Assert.assertEquals(MessageKeyEnum.LOCATION, MessageKeyEnum
				.getEnumFor("LOCATION"));

		// MSGPROP_COMMAND
		Assert.assertEquals(MessageKeyEnum.MSGPROP_COMMAND, MessageKeyEnum
				.getEnumFor("command"));
		Assert.assertEquals(MessageKeyEnum.MSGPROP_COMMAND, MessageKeyEnum
				.getEnumFor("COMMAND"));

		// NAME
		Assert.assertEquals(MessageKeyEnum.NAME, MessageKeyEnum
				.getEnumFor("name"));
		Assert.assertEquals(MessageKeyEnum.NAME, MessageKeyEnum
				.getEnumFor("NAME"));

		// OVERWRITES
		Assert.assertEquals(MessageKeyEnum.OVERWRITES, MessageKeyEnum
				.getEnumFor("OVERWRITES"));
		Assert.assertEquals(MessageKeyEnum.OVERWRITES, MessageKeyEnum
				.getEnumFor("overwrites"));
		
		// PROCESS-ID
		Assert.assertEquals(MessageKeyEnum.PROCESS_ID, MessageKeyEnum
				.getEnumFor("process-id"));
		Assert.assertEquals(MessageKeyEnum.PROCESS_ID, MessageKeyEnum
				.getEnumFor("PROCESS-ID"));

		// SEVERITY
		Assert.assertEquals(MessageKeyEnum.SEVERITY, MessageKeyEnum
				.getEnumFor("severity"));
		Assert.assertEquals(MessageKeyEnum.SEVERITY, MessageKeyEnum
				.getEnumFor("SEVERITY"));

		// SEVERITY-MAX
		Assert.assertEquals(MessageKeyEnum.SEVERITY_MAX, MessageKeyEnum
				.getEnumFor("severity-max"));
		Assert.assertEquals(MessageKeyEnum.SEVERITY_MAX, MessageKeyEnum
				.getEnumFor("SEVERITY-MAX"));
		
		// SEVERITY-OLD
		Assert.assertEquals(MessageKeyEnum.SEVERITY_OLD, MessageKeyEnum
				.getEnumFor("severity-old"));
		Assert.assertEquals(MessageKeyEnum.SEVERITY_OLD, MessageKeyEnum
				.getEnumFor("SEVERITY-OLD"));

		// STATUS
		Assert.assertEquals(MessageKeyEnum.STATUS, MessageKeyEnum
				.getEnumFor("status"));
		Assert.assertEquals(MessageKeyEnum.STATUS, MessageKeyEnum
				.getEnumFor("STATUS"));
		
		// STATUS-OLD
		Assert.assertEquals(MessageKeyEnum.STATUS_OLD, MessageKeyEnum
				.getEnumFor("status-old"));
		Assert.assertEquals(MessageKeyEnum.STATUS_OLD, MessageKeyEnum
				.getEnumFor("STATUS-OLD"));

		// TEXT
		Assert.assertEquals(MessageKeyEnum.TEXT, MessageKeyEnum
				.getEnumFor("text"));
		Assert.assertEquals(MessageKeyEnum.TEXT, MessageKeyEnum
				.getEnumFor("TEXT"));

		// TYPE
		Assert.assertEquals(MessageKeyEnum.TYPE, MessageKeyEnum
				.getEnumFor("type"));
		Assert.assertEquals(MessageKeyEnum.TYPE, MessageKeyEnum
				.getEnumFor("TYPE"));

		// USER
		Assert.assertEquals(MessageKeyEnum.USER, MessageKeyEnum
				.getEnumFor("user"));
		Assert.assertEquals(MessageKeyEnum.USER, MessageKeyEnum
				.getEnumFor("USER"));

		// VALUE
		Assert.assertEquals(MessageKeyEnum.VALUE, MessageKeyEnum
				.getEnumFor("value"));
		Assert.assertEquals(MessageKeyEnum.VALUE, MessageKeyEnum
				.getEnumFor("VALUE"));
	}

	@Override
	protected MessageKeyEnum getNewInstanceOfClassUnderTest() {
		return MessageKeyEnum.AMS_FILTERID;
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected MessageKeyEnum[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new MessageKeyEnum[] { MessageKeyEnum.AMS_REINSERTED,
				MessageKeyEnum.APPLICATION_ID, MessageKeyEnum.DOMAIN };
	}
}
