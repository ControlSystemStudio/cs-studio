package org.csstudio.nams.service.messaging.impl.jms;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.junit.Assert;
import org.junit.Test;

public class MessageKeyKonverter_Test {

	@Test
	public void testGetEnumForKey () {
		
		// 18 ELEMENTS ARE BEING MAPPED
		Assert.assertEquals("18 Elements are being mapped(IF AMOUNT OF ELEMENTS CHANGES MessageKeyConverter must be updated)", 18, MessageKeyEnum.values().length);
		
		// AMS-REINSERTED
		Assert.assertEquals(MessageKeyEnum.AMS_REINSERTED, MessageKeyConverter.getEnumKeyFor("ams-filterid"));
		Assert.assertEquals(MessageKeyEnum.AMS_REINSERTED, MessageKeyConverter.getEnumKeyFor("AMS-FILTERID"));
		
		// APPLICATION_ID
		Assert.assertEquals(MessageKeyEnum.APPLICATION_ID, MessageKeyConverter.getEnumKeyFor("application-id"));
		Assert.assertEquals(MessageKeyEnum.APPLICATION_ID, MessageKeyConverter.getEnumKeyFor("APPLICATION-ID"));

		// CLASS
		Assert.assertEquals(MessageKeyEnum.CLASS, MessageKeyConverter.getEnumKeyFor("class"));
		Assert.assertEquals(MessageKeyEnum.CLASS, MessageKeyConverter.getEnumKeyFor("CLASS"));
		
		// DESTINATION
		Assert.assertEquals(MessageKeyEnum.DESTINATION, MessageKeyConverter.getEnumKeyFor("destination"));
		Assert.assertEquals(MessageKeyEnum.DESTINATION, MessageKeyConverter.getEnumKeyFor("DESTINATION"));

		// DOMAIN
		Assert.assertEquals(MessageKeyEnum.DOMAIN, MessageKeyConverter.getEnumKeyFor("domain"));
		Assert.assertEquals(MessageKeyEnum.DOMAIN, MessageKeyConverter.getEnumKeyFor("DOMAIN"));
		
		// EVENTTIME
		Assert.assertEquals(MessageKeyEnum.EVENTTIME, MessageKeyConverter.getEnumKeyFor("eventtime"));
		Assert.assertEquals(MessageKeyEnum.EVENTTIME, MessageKeyConverter.getEnumKeyFor("eventtime"));
		
		// FACILITY
		Assert.assertEquals(MessageKeyEnum.FACILITY, MessageKeyConverter.getEnumKeyFor("facility"));
		Assert.assertEquals(MessageKeyEnum.FACILITY, MessageKeyConverter.getEnumKeyFor("FACILITY"));
		
		// HOST
		Assert.assertEquals(MessageKeyEnum.HOST, MessageKeyConverter.getEnumKeyFor("host"));
		Assert.assertEquals(MessageKeyEnum.HOST, MessageKeyConverter.getEnumKeyFor("host"));
		
		// LOCATION
		Assert.assertEquals(MessageKeyEnum.LOCATION, MessageKeyConverter.getEnumKeyFor("location"));
		Assert.assertEquals(MessageKeyEnum.LOCATION, MessageKeyConverter.getEnumKeyFor("LOCATION"));
		
		// MSGPROP_COMMAND
		Assert.assertEquals(MessageKeyEnum.MSGPROP_COMMAND, MessageKeyConverter.getEnumKeyFor("command"));
		Assert.assertEquals(MessageKeyEnum.MSGPROP_COMMAND, MessageKeyConverter.getEnumKeyFor("COMMAND"));
		
		// NAME
		Assert.assertEquals(MessageKeyEnum.NAME, MessageKeyConverter.getEnumKeyFor("name"));
		Assert.assertEquals(MessageKeyEnum.NAME, MessageKeyConverter.getEnumKeyFor("NAME"));
		
		// PROCESS-ID
		Assert.assertEquals(MessageKeyEnum.PROCESS_ID, MessageKeyConverter.getEnumKeyFor("process-id"));
		Assert.assertEquals(MessageKeyEnum.PROCESS_ID, MessageKeyConverter.getEnumKeyFor("PROCESS-ID"));
		
		// SEVERITY
		Assert.assertEquals(MessageKeyEnum.SEVERITY, MessageKeyConverter.getEnumKeyFor("severity"));
		Assert.assertEquals(MessageKeyEnum.SEVERITY, MessageKeyConverter.getEnumKeyFor("SEVERITY"));
		
		// STATUS
		Assert.assertEquals(MessageKeyEnum.STATUS, MessageKeyConverter.getEnumKeyFor("status"));
		Assert.assertEquals(MessageKeyEnum.STATUS, MessageKeyConverter.getEnumKeyFor("STATUS"));
		
		// TEXT
		Assert.assertEquals(MessageKeyEnum.TEXT, MessageKeyConverter.getEnumKeyFor("text"));
		Assert.assertEquals(MessageKeyEnum.TEXT, MessageKeyConverter.getEnumKeyFor("TEXT"));
		
		// TYPE
		Assert.assertEquals(MessageKeyEnum.TYPE, MessageKeyConverter.getEnumKeyFor("type"));
		Assert.assertEquals(MessageKeyEnum.TYPE, MessageKeyConverter.getEnumKeyFor("TYPE"));

		// USER
		Assert.assertEquals(MessageKeyEnum.USER, MessageKeyConverter.getEnumKeyFor("user"));
		Assert.assertEquals(MessageKeyEnum.USER, MessageKeyConverter.getEnumKeyFor("USER"));
		
		// VALUE
		Assert.assertEquals(MessageKeyEnum.VALUE, MessageKeyConverter.getEnumKeyFor("value"));
		Assert.assertEquals(MessageKeyEnum.VALUE, MessageKeyConverter.getEnumKeyFor("VALUE"));
	}

	@Test
	public void testIstSynchronisation(){
		
		Map<MessageKeyEnum, String> map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.MSGPROP_COMMAND, "AMS_RELOAD_CFG_START");
		Assert.assertTrue(MessageKeyConverter.istSynchronisationAuforderung(map));
		Assert.assertFalse(MessageKeyConverter.istSynchronisationBestaetigung(map));
		
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.MSGPROP_COMMAND, "AMS_RELOAD_CFG_END");
		Assert.assertTrue(MessageKeyConverter.istSynchronisationBestaetigung(map)); 
		Assert.assertFalse(MessageKeyConverter.istSynchronisationAuforderung(map));
	}
}
