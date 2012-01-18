package org.csstudio.channel.opiwidgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ChannelNotificationStringTest {

	@Test
	public void validString1() {
		ChannelNotificationString string = new ChannelNotificationString("abcd");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().isEmpty(), true);
		assertEquals(string.notification(Collections.<String>emptyList()), "abcd");
	}

	@Test
	public void validString2() {
		ChannelNotificationString string = new ChannelNotificationString("#(prop1)");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().size(), 1);
		assertEquals(string.getRequiredProperties().get(0), "prop1");
		assertEquals(string.notification(Arrays.asList("value1")), "value1");
	}

	@Test
	public void validString3() {
		ChannelNotificationString string = new ChannelNotificationString("before#(prop1)after");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().size(), 1);
		assertEquals(string.getRequiredProperties().get(0), "prop1");
		assertEquals(string.notification(Arrays.asList("value1")), "beforevalue1after");
	}

	@Test
	public void validString4() {
		ChannelNotificationString string = new ChannelNotificationString("before#(prop1)middle#(prop2)after");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().size(), 2);
		assertEquals(string.getRequiredProperties().get(0), "prop1");
		assertEquals(string.getRequiredProperties().get(1), "prop2");
		assertEquals(string.notification(Arrays.asList("value1", "value2")), "beforevalue1middlevalue2after");
	}

	@Test
	public void validString5() {
		ChannelNotificationString string = new ChannelNotificationString("before#(prop1)#(prop2)after");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().size(), 2);
		assertEquals(string.getRequiredProperties().get(0), "prop1");
		assertEquals(string.getRequiredProperties().get(1), "prop2");
		assertEquals(string.notification(Arrays.asList("value1", "value2")), "beforevalue1value2after");
	}

	@Test
	public void validString6() {
		ChannelNotificationString string = new ChannelNotificationString("#(prop1)#(prop2)");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().size(), 2);
		assertEquals(string.getRequiredProperties().get(0), "prop1");
		assertEquals(string.getRequiredProperties().get(1), "prop2");
		assertEquals(string.notification(Arrays.asList("value1", "value2")), "value1value2");
	}

	@Test
	public void invalidString1() {
		ChannelNotificationString string = new ChannelNotificationString("#(prop1");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().size(), 0);
		assertEquals(string.notification(Collections.<String>emptyList()), "#(prop1");
	}

	@Test
	public void propMap1() {
		ChannelNotificationString string = new ChannelNotificationString("#(prop1)#(prop2)");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().size(), 2);
		assertEquals(string.getRequiredProperties().get(0), "prop1");
		assertEquals(string.getRequiredProperties().get(1), "prop2");
		Map<String, String> map = new HashMap<String, String>();
		map.put("prop1", "value1");
		assertEquals(string.notification(map), "");
		map.put("prop2", "value2");
		assertEquals(string.notification(map), "value1value2");
	}

}
