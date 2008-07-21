package org.csstudio.nams.service.messaging.declaration;

import junit.framework.Assert;

import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.SyncronisationsAufforderungsSystemNachchricht;
import org.csstudio.nams.common.material.SyncronisationsBestaetigungSystemNachricht;
import org.csstudio.nams.common.material.SystemNachricht;
import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.csstudio.nams.service.messaging.declaration.DefaultNAMSMessage.AcknowledgeHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultNAMSMessage_Test extends
		AbstractObject_TestCase<NAMSMessage> {

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
		NAMSMessage msg = new DefaultNAMSMessage(
				new SyncronisationsBestaetigungSystemNachricht(),
				new AcknowledgeHandler() {
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
		msg = new DefaultNAMSMessage(
				new SyncronisationsAufforderungsSystemNachchricht(),
				new AcknowledgeHandler() {
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

	@Override
	protected NAMSMessage getNewInstanceOfClassUnderTest() {
		return new DefaultNAMSMessage(
				new DefaultNAMSMessage.AcknowledgeHandler() {
					public void acknowledge() throws Throwable {
					}
				});
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected NAMSMessage[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		NAMSMessage[] result = new NAMSMessage[3];

		result[0] = new DefaultNAMSMessage(
				new DefaultNAMSMessage.AcknowledgeHandler() {
					public void acknowledge() throws Throwable {
					}
				});
		result[1] = new DefaultNAMSMessage(
				new SyncronisationsAufforderungsSystemNachchricht(),
				new DefaultNAMSMessage.AcknowledgeHandler() {
					public void acknowledge() throws Throwable {
					}
				});
		result[2] = new DefaultNAMSMessage(new AlarmNachricht("Test"),
				new DefaultNAMSMessage.AcknowledgeHandler() {
					public void acknowledge() throws Throwable {
					}
				});

		return result;
	}
}
