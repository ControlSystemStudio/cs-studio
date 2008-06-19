package org.csstudio.nams.common.fachwert;

import org.junit.Assert;
import org.junit.Test;


public class MessageKeyEnumTest {
	@Test
	public void testGetEnumForKey () {
		
		// 18 ELEMENTS ARE BEING MAPPED
		Assert.assertEquals("19 Elements are being mapped(IF AMOUNT OF ELEMENTS CHANGES MessageKeyConverter must be updated)", 19, MessageKeyEnum.values().length);
		
		// AMS-REINSERTED
		Assert.assertEquals(MessageKeyEnum.AMS_REINSERTED, MessageKeyEnum.getEnumFor("AMS-REINSERTED"));
		Assert.assertEquals(MessageKeyEnum.AMS_REINSERTED, MessageKeyEnum.getEnumFor("ams-reinserted"));
		
		// AMS_FILTERID
		Assert.assertEquals(MessageKeyEnum.AMS_FILTERID, MessageKeyEnum.getEnumFor("AMS-FILTERID"));
		Assert.assertEquals(MessageKeyEnum.AMS_FILTERID, MessageKeyEnum.getEnumFor("ams-filterid"));
		
		// APPLICATION_ID
		Assert.assertEquals(MessageKeyEnum.APPLICATION_ID, MessageKeyEnum.getEnumFor("application-id"));
		Assert.assertEquals(MessageKeyEnum.APPLICATION_ID, MessageKeyEnum.getEnumFor("APPLICATION-ID"));

		// CLASS
		Assert.assertEquals(MessageKeyEnum.CLASS, MessageKeyEnum.getEnumFor("class"));
		Assert.assertEquals(MessageKeyEnum.CLASS, MessageKeyEnum.getEnumFor("CLASS"));
		
		// DESTINATION
		Assert.assertEquals(MessageKeyEnum.DESTINATION, MessageKeyEnum.getEnumFor("destination"));
		Assert.assertEquals(MessageKeyEnum.DESTINATION, MessageKeyEnum.getEnumFor("DESTINATION"));

		// DOMAIN
		Assert.assertEquals(MessageKeyEnum.DOMAIN, MessageKeyEnum.getEnumFor("domain"));
		Assert.assertEquals(MessageKeyEnum.DOMAIN, MessageKeyEnum.getEnumFor("DOMAIN"));
		
		// EVENTTIME
		Assert.assertEquals(MessageKeyEnum.EVENTTIME, MessageKeyEnum.getEnumFor("eventtime"));
		Assert.assertEquals(MessageKeyEnum.EVENTTIME, MessageKeyEnum.getEnumFor("eventtime"));
		
		// FACILITY
		Assert.assertEquals(MessageKeyEnum.FACILITY, MessageKeyEnum.getEnumFor("facility"));
		Assert.assertEquals(MessageKeyEnum.FACILITY, MessageKeyEnum.getEnumFor("FACILITY"));
		
		// HOST
		Assert.assertEquals(MessageKeyEnum.HOST, MessageKeyEnum.getEnumFor("host"));
		Assert.assertEquals(MessageKeyEnum.HOST, MessageKeyEnum.getEnumFor("host"));
		
		// LOCATION
		Assert.assertEquals(MessageKeyEnum.LOCATION, MessageKeyEnum.getEnumFor("location"));
		Assert.assertEquals(MessageKeyEnum.LOCATION, MessageKeyEnum.getEnumFor("LOCATION"));
		
		// MSGPROP_COMMAND
		Assert.assertEquals(MessageKeyEnum.MSGPROP_COMMAND, MessageKeyEnum.getEnumFor("command"));
		Assert.assertEquals(MessageKeyEnum.MSGPROP_COMMAND, MessageKeyEnum.getEnumFor("COMMAND"));
		
		// NAME
		Assert.assertEquals(MessageKeyEnum.NAME, MessageKeyEnum.getEnumFor("name"));
		Assert.assertEquals(MessageKeyEnum.NAME, MessageKeyEnum.getEnumFor("NAME"));
		
		// PROCESS-ID
		Assert.assertEquals(MessageKeyEnum.PROCESS_ID, MessageKeyEnum.getEnumFor("process-id"));
		Assert.assertEquals(MessageKeyEnum.PROCESS_ID, MessageKeyEnum.getEnumFor("PROCESS-ID"));
		
		// SEVERITY
		Assert.assertEquals(MessageKeyEnum.SEVERITY, MessageKeyEnum.getEnumFor("severity"));
		Assert.assertEquals(MessageKeyEnum.SEVERITY, MessageKeyEnum.getEnumFor("SEVERITY"));
		
		// STATUS
		Assert.assertEquals(MessageKeyEnum.STATUS, MessageKeyEnum.getEnumFor("status"));
		Assert.assertEquals(MessageKeyEnum.STATUS, MessageKeyEnum.getEnumFor("STATUS"));
		
		// TEXT
		Assert.assertEquals(MessageKeyEnum.TEXT, MessageKeyEnum.getEnumFor("text"));
		Assert.assertEquals(MessageKeyEnum.TEXT, MessageKeyEnum.getEnumFor("TEXT"));
		
		// TYPE
		Assert.assertEquals(MessageKeyEnum.TYPE, MessageKeyEnum.getEnumFor("type"));
		Assert.assertEquals(MessageKeyEnum.TYPE, MessageKeyEnum.getEnumFor("TYPE"));

		// USER
		Assert.assertEquals(MessageKeyEnum.USER, MessageKeyEnum.getEnumFor("user"));
		Assert.assertEquals(MessageKeyEnum.USER, MessageKeyEnum.getEnumFor("USER"));
		
		// VALUE
		Assert.assertEquals(MessageKeyEnum.VALUE, MessageKeyEnum.getEnumFor("value"));
		Assert.assertEquals(MessageKeyEnum.VALUE, MessageKeyEnum.getEnumFor("VALUE"));
	}
}
