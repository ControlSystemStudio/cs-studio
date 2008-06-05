package org.csstudio.nams.application.department.decision;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import junit.framework.TestCase;

import org.csstudio.nams.common.material.SyncronisationsAufforderungsSystemNachchricht;
import org.csstudio.nams.common.material.SyncronisationsBestaetigungSystemNachricht;
import org.csstudio.nams.common.material.SystemNachricht;
import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.DefaultNAMSMessage;
import org.csstudio.nams.service.messaging.declaration.NAMSMessage;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.messaging.declaration.DefaultNAMSMessage.AcknowledgeHandler;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SyncronisationsAutomat_Test extends TestCase {
	protected SystemNachricht zuletzGesendeteNachricht;
	protected Queue<NAMSMessage> neuZuEmpfangedeNachrichten;
	private Producer amsAusgangsProducer;
	private Consumer amsCommandConsumer;

	@Before
	public void setUp() throws Exception {
		neuZuEmpfangedeNachrichten = null;
		zuletzGesendeteNachricht = null;
		neuZuEmpfangedeNachrichten = null;
		amsAusgangsProducer = null;
		amsCommandConsumer = null;
		
		neuZuEmpfangedeNachrichten = new LinkedList<NAMSMessage>();
		amsAusgangsProducer = new Producer() {

			public void close() {
				fail("should not be called");
			}

			public boolean isClosed() {
				return false;
			}

			public void sendeMap(Map<String, String> map) {
				fail("should not be called");
			}

			public void sendeSystemnachricht(SystemNachricht systemNachricht) {
				if( zuletzGesendeteNachricht != null )
				{
					fail();
				}
				zuletzGesendeteNachricht = systemNachricht;
			}

		};
		amsCommandConsumer = new Consumer() {

			public void close() {
				fail("should not be called");
			}

			public boolean isClosed() {
				return false;
			}

			public NAMSMessage receiveMessage() throws MessagingException {
				if (neuZuEmpfangedeNachrichten == null) {
					fail("vergessen Nachricht anzulegen");
				}
				if (neuZuEmpfangedeNachrichten.isEmpty()) {
					fail("keine weiteren Nachrichten");
				}
				return neuZuEmpfangedeNachrichten.poll();
			}

		};
	}

	@After
	public void tearDown() throws Exception {
		neuZuEmpfangedeNachrichten = null;
		zuletzGesendeteNachricht = null;
		neuZuEmpfangedeNachrichten = null;
		amsAusgangsProducer = null;
		amsCommandConsumer = null;
	}

	final static String MSGPROP_COMMAND = "COMMAND";
	final static String MSGVALUE_TCMD_RELOAD = "AMS_RELOAD_CFG";
	final static String MSGVALUE_TCMD_RELOAD_CFG_START = MSGVALUE_TCMD_RELOAD
			+ "_START";
	final static String MSGVALUE_TCMD_RELOAD_CFG_END = MSGVALUE_TCMD_RELOAD
			+ "_END";

	int ackHandlerCallCount;

	@Test
	public void testSyncronisationUeberDistributorAusfueren()
			throws MessagingException {
		AcknowledgeHandler handler = new AcknowledgeHandler() {
			public void acknowledge() throws Throwable {
				ackHandlerCallCount++;
			}
		};

		ackHandlerCallCount = 0;
		neuZuEmpfangedeNachrichten.add(new DefaultNAMSMessage(
				new SyncronisationsAufforderungsSystemNachchricht(), handler));
		neuZuEmpfangedeNachrichten.add(new DefaultNAMSMessage(
				new SyncronisationsAufforderungsSystemNachchricht(), handler));
		neuZuEmpfangedeNachrichten.add(new DefaultNAMSMessage(
				new SyncronisationsBestaetigungSystemNachricht(), handler));
		
		SyncronisationsAutomat.syncronisationUeberDistributorAusfueren(
				amsAusgangsProducer, amsCommandConsumer);
		
		assertNotNull(zuletzGesendeteNachricht);
		assertTrue(zuletzGesendeteNachricht instanceof SyncronisationsAufforderungsSystemNachchricht);
		assertEquals("Alle Nachrichten wurden acknowledged.", 3,
				ackHandlerCallCount);
	}

}
