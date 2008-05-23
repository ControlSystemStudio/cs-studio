package org.csstudio.nams.application.department.decision;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.csstudio.nams.common.material.SystemNachricht;
import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.DefaultNAMSMessage;
import org.csstudio.nams.service.messaging.declaration.NAMSMessage;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SyncronisationsAutomat_Test {
	protected SystemNachricht zuletzGesendeteNachricht;
	protected Queue<NAMSMessage> neuZuEmpfangedeNachrichten;
	private Producer amsAusgangsProducer;
	private Consumer amsCommandConsumer;

	@Before
	public void setUp() throws Exception {
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
	final static String MSGVALUE_TCMD_RELOAD_CFG_START = MSGVALUE_TCMD_RELOAD + "_START";
	final static String MSGVALUE_TCMD_RELOAD_CFG_END = MSGVALUE_TCMD_RELOAD + "_END";
	
	@Test
	public void testSyncronisationUeberDistributorAusfueren() throws MessagingException {
		// Antworten des distributors vorbereiten
		Map<String, String> map1 = new HashMap<String, String>();
		Map<String, String> map2 = new HashMap<String, String>();
		Map<String, String> map3 = new HashMap<String, String>();
		
		map3.put(MSGPROP_COMMAND, MSGVALUE_TCMD_RELOAD_CFG_END);
		
		neuZuEmpfangedeNachrichten.add(new DefaultNAMSMessage(map1));
		neuZuEmpfangedeNachrichten.add(new DefaultNAMSMessage(map2));
		neuZuEmpfangedeNachrichten.add(new DefaultNAMSMessage(map3));
		SyncronisationsAutomat.syncronisationUeberDistributorAusfueren(amsAusgangsProducer, amsCommandConsumer);
		
	}

}
