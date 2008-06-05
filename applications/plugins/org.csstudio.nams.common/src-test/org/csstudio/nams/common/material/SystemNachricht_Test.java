package org.csstudio.nams.common.material;

import junit.framework.Assert;

import org.junit.Test;


public class SystemNachricht_Test {
	@Test
	public void testStdImplementation() {
		// Bestaetigungsnachricht erstellen
//		Map<String, String> map = new HashMap<String, String>();
//		map.put(SystemNachricht.MSGPROP_COMMAND, SystemNachricht.MSGVALUE_TCMD_RELOAD_CFG_END);
		SystemNachricht systemNachricht = new SyncronisationsBestaetigungSystemNachricht();
		
		Assert.assertTrue(systemNachricht.istSyncronisationsBestaetigung());
		Assert.assertFalse(systemNachricht.istSyncronisationsAufforderung());
		
		// Aufforderungsnachricht erstellen
//		map = new HashMap<String, String>();
//		map.put(SystemNachricht.MSGPROP_COMMAND, SystemNachricht.MSGVALUE_TCMD_RELOAD_CFG_START);
		systemNachricht = new SyncronisationsAufforderungsSystemNachchricht();
		
		Assert.assertFalse(systemNachricht.istSyncronisationsBestaetigung());
		Assert.assertTrue(systemNachricht.istSyncronisationsAufforderung());
	}
}
