package org.csstudio.nams.service.messaging.declaration;


import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.csstudio.nams.common.material.SyncronisationsAufforderungsSystemNachchricht;
import org.csstudio.nams.common.material.SyncronisationsBestaetigungSystemNachricht;
import org.csstudio.nams.common.material.SystemNachricht;
import org.csstudio.nams.service.messaging.declaration.DefaultNAMSMessage.AcknowledgeHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultNAMSMessage_Test {

	protected boolean acknowledged;


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testStdImplementation() {
		// Bestaetigungsnachricht erstellen
		Map<String, String> map = new HashMap<String, String>();
		map.put(DefaultNAMSMessage.MSGPROP_COMMAND, DefaultNAMSMessage.MSGVALUE_TCMD_RELOAD_CFG_END);
		NAMSMessage msg = new DefaultNAMSMessage(map, new AcknowledgeHandler() {
			public void acknowledge() throws Throwable {
				acknowledged = true;
			}
		});
		
		Assert.assertTrue(msg.enthaeltSystemnachricht());
		Assert.assertFalse(msg.enthaeltAlarmnachricht());
		SystemNachricht systemNachricht = msg.alsSystemachricht();
		Assert.assertTrue(systemNachricht.istSyncronisationsBestaetigung());
		Assert.assertFalse(systemNachricht.istSyncronisationsAufforderung());
		
		// Aufforderungsnachricht erstellen
		map = new HashMap<String, String>();
		map.put(DefaultNAMSMessage.MSGPROP_COMMAND, DefaultNAMSMessage.MSGVALUE_TCMD_RELOAD_CFG_START);
		msg = new DefaultNAMSMessage(map, new AcknowledgeHandler() {
			public void acknowledge() throws Throwable {
				acknowledged = true;
			}
		});
		
		Assert.assertTrue(msg.enthaeltSystemnachricht());
		Assert.assertFalse(msg.enthaeltAlarmnachricht());
		systemNachricht = msg.alsSystemachricht();
		Assert.assertFalse(systemNachricht.istSyncronisationsBestaetigung());
		Assert.assertTrue(systemNachricht.istSyncronisationsAufforderung());
	}
}
