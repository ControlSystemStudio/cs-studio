package org.csstudio.nams.application.department.decision;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.SystemNachricht;
import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.NAMSMessage;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SyncronisationsAutomat_Test {
	protected Map<String, String> zuletzGesendeteNachricht;
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
				zuletzGesendeteNachricht = map;
			}

			public void sendeSystemnachricht(SystemNachricht vorgangsmappe) {
				fail("should not be called");
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
				if (neuZuEmpfangedeNachrichten == null || neuZuEmpfangedeNachrichten.isEmpty()) {
					fail("vergessen Nachricht anzulegen");
				}
				return neuZuEmpfangedeNachrichten.poll();
			}
			
		};
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSyncronisationUeberDistributorAusfueren() {
		// Antworten des distributors vorbereiten
		neuZuEmpfangedeNachrichten.add(new NAMSMessage() {

			public void acknowledge() throws MessagingException {
				
			}

			public AlarmNachricht alsAlarmnachricht() {
				fail("should not be called");
				return null;
			}

			public Map<String, String> alsMap() {
				fail("should not be called");
				return null;
			}

			public SystemNachricht alsSystemachricht() {
				return null;//new SystemNachricht();
			}

			public boolean enthaeltAlarmnachricht() {
				fail("should not be called");
				return false;
			}

			public boolean enthaeltSystemnachricht() {
				return true;
			}
			
		});
		SyncronisationsAutomat.syncronisationUeberDistributorAusfueren(amsAusgangsProducer, amsCommandConsumer);
		
	}

}
