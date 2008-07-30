package org.csstudio.nams.service.messaging.impl.jms;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.junit.Assert;
import org.junit.Test;

public class MessageKeyKonverter_Test extends TestCase {

	@Test
	public void testIstSynchronisation() {

		Map<MessageKeyEnum, String> map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.MSGPROP_COMMAND, "AMS_RELOAD_CFG_START");
		Assert.assertTrue(MessageKeyUtil.istSynchronisationAuforderung(map));
		Assert.assertFalse(MessageKeyUtil.istSynchronisationBestaetigung(map));

		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.MSGPROP_COMMAND, "AMS_RELOAD_CFG_END");
		Assert.assertTrue(MessageKeyUtil.istSynchronisationBestaetigung(map));
		Assert.assertFalse(MessageKeyUtil.istSynchronisationAuforderung(map));
	}
}
