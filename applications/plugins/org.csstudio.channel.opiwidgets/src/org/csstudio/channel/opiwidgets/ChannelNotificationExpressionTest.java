package org.csstudio.channel.opiwidgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static gov.bnl.channelfinder.api.Property.Builder.*;
import gov.bnl.channelfinder.api.Channel;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class ChannelNotificationExpressionTest {

	@Test
	public void validString1() {
		ChannelNotificationExpression string = new ChannelNotificationExpression("abcd");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().isEmpty(), true);
		assertEquals(string.notification(Collections.<String>emptyList()), "abcd");
	}

	@Test
	public void validString2() {
		ChannelNotificationExpression string = new ChannelNotificationExpression("#(prop1)");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().size(), 1);
		assertEquals(string.getRequiredProperties().get(0), "prop1");
		assertEquals(string.notification(Arrays.asList("value1")), "value1");
	}

	@Test
	public void validString3() {
		ChannelNotificationExpression string = new ChannelNotificationExpression("before#(prop1)after");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().size(), 1);
		assertEquals(string.getRequiredProperties().get(0), "prop1");
		assertEquals(string.notification(Arrays.asList("value1")), "beforevalue1after");
	}

	@Test
	public void validString4() {
		ChannelNotificationExpression string = new ChannelNotificationExpression("before#(prop1)middle#(prop2)after");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().size(), 2);
		assertEquals(string.getRequiredProperties().get(0), "prop1");
		assertEquals(string.getRequiredProperties().get(1), "prop2");
		assertEquals(string.notification(Arrays.asList("value1", "value2")), "beforevalue1middlevalue2after");
	}

	@Test
	public void validString5() {
		ChannelNotificationExpression string = new ChannelNotificationExpression("before#(prop1)#(prop2)after");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().size(), 2);
		assertEquals(string.getRequiredProperties().get(0), "prop1");
		assertEquals(string.getRequiredProperties().get(1), "prop2");
		assertEquals(string.notification(Arrays.asList("value1", "value2")), "beforevalue1value2after");
	}

	@Test
	public void validString6() {
		ChannelNotificationExpression string = new ChannelNotificationExpression("#(prop1)#(prop2)");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().size(), 2);
		assertEquals(string.getRequiredProperties().get(0), "prop1");
		assertEquals(string.getRequiredProperties().get(1), "prop2");
		assertEquals(string.notification(Arrays.asList("value1", "value2")), "value1value2");
	}

	@Test
	public void invalidString1() {
		ChannelNotificationExpression string = new ChannelNotificationExpression("#(prop1");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().size(), 0);
		assertEquals(string.notification(Collections.<String>emptyList()), "#(prop1");
	}

	@Test
	public void propMap1() {
		ChannelNotificationExpression string = new ChannelNotificationExpression("#(prop1)#(prop2)");
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

	@Test
	public void propChannel1() {
		ChannelNotificationExpression string = new ChannelNotificationExpression("#(prop1)#(prop2)");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().size(), 2);
		assertEquals(string.getRequiredProperties().get(0), "prop1");
		assertEquals(string.getRequiredProperties().get(1), "prop2");
		Channel channel = Channel.Builder.channel("myChannel").with(property("prop1").value("value1")).build();
		assertEquals(string.notification(channel), "");
		channel = Channel.Builder.channel("myChannel").with(property("prop1").value("value1"))
				.with(property("prop2").value("value2")).build();
		assertEquals(string.notification(channel), "value1value2");
	}

	@Test
	public void propChannel2() {
		ChannelNotificationExpression string = new ChannelNotificationExpression("#(Channel Name) #(prop2)");
		assertNotNull(string.getRequiredProperties());
		assertEquals(string.getRequiredProperties().size(), 2);
		assertEquals(string.getRequiredProperties().get(0), "Channel Name");
		assertEquals(string.getRequiredProperties().get(1), "prop2");
		Channel channel = Channel.Builder.channel("myChannel").with(property("prop1").value("value1")).build();
		assertEquals(string.notification(channel), "");
		channel = Channel.Builder.channel("myChannel").with(property("prop1").value("value1"))
				.with(property("prop2").value("value2")).build();
		assertEquals(string.notification(channel), "myChannel value2");
	}

	@Test
	public void propMultipleChannel1() {
		Set<Channel> channels = new HashSet<Channel>();
		channels.add(Channel.Builder.channel("channel1").with(property("same").value("value"))
				.with(property("different").value("value1")).build());
		channels.add(Channel.Builder.channel("channel2").with(property("same").value("value"))
				.with(property("different").value("value2")).build());
		ChannelNotificationExpression string = new ChannelNotificationExpression("#(same)");
		assertEquals(string.notification(channels), "value");
		string = new ChannelNotificationExpression("#(different)");
		assertEquals(string.notification(channels), "");
		string = new ChannelNotificationExpression("#(Channel Name)");
		assertEquals(string.notification(channels), "");
	}

}
