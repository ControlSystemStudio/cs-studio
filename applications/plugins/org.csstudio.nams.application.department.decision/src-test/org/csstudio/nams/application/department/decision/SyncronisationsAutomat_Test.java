package org.csstudio.nams.application.department.decision;

import java.util.LinkedList;
import java.util.Queue;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.material.SyncronisationsAufforderungsSystemNachchricht;
import org.csstudio.nams.common.material.SyncronisationsBestaetigungSystemNachricht;
import org.csstudio.nams.common.material.SystemNachricht;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.HistoryDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO.ReplicationState;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.UnknownConfigurationElementError;
import org.csstudio.nams.service.history.declaration.HistoryService;
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
	final static String MSGPROP_COMMAND = "COMMAND";
	final static String MSGVALUE_TCMD_RELOAD = "AMS_RELOAD_CFG";
	final static String MSGVALUE_TCMD_RELOAD_CFG_START = SyncronisationsAutomat_Test.MSGVALUE_TCMD_RELOAD
			+ "_START";
	final static String MSGVALUE_TCMD_RELOAD_CFG_END = SyncronisationsAutomat_Test.MSGVALUE_TCMD_RELOAD
			+ "_END";

	int ackHandlerCallCount;

	ReplicationStateDTO nextToBeDelivered = null;

	ReplicationStateDTO lastSended = null;
	protected SystemNachricht zuletzGesendeteNachricht;
	protected Queue<NAMSMessage> neuZuEmpfangedeNachrichten;
	private Producer amsAusgangsProducer;

	private Consumer amsCommandConsumer;

	@Override
	@Before
	public void setUp() throws Exception {
		this.neuZuEmpfangedeNachrichten = null;
		this.zuletzGesendeteNachricht = null;
		this.neuZuEmpfangedeNachrichten = null;
		this.amsAusgangsProducer = null;
		this.amsCommandConsumer = null;

		this.neuZuEmpfangedeNachrichten = new LinkedList<NAMSMessage>();
		this.amsAusgangsProducer = new Producer() {

			public boolean isClosed() {
				return false;
			}

			public void sendeSystemnachricht(
					final SystemNachricht systemNachricht) {
				if (SyncronisationsAutomat_Test.this.zuletzGesendeteNachricht != null) {
					Assert.fail();
				}
				SyncronisationsAutomat_Test.this.zuletzGesendeteNachricht = systemNachricht;
			}

			public void sendeVorgangsmappe(final Vorgangsmappe vorgangsmappe) {
				Assert.fail("should not be called");
			}

			public void tryToClose() {
				Assert.fail("should not be called");
			}

		};
		this.amsCommandConsumer = new Consumer() {

			public void close() {
				Assert.fail("should not be called");
			}

			public boolean isClosed() {
				return false;
			}

			public NAMSMessage receiveMessage() throws MessagingException {
				if (SyncronisationsAutomat_Test.this.neuZuEmpfangedeNachrichten == null) {
					Assert.fail("vergessen Nachricht anzulegen");
				}
				if (SyncronisationsAutomat_Test.this.neuZuEmpfangedeNachrichten
						.isEmpty()) {
					Assert.fail("keine weiteren Nachrichten");
				}
				return SyncronisationsAutomat_Test.this.neuZuEmpfangedeNachrichten
						.poll();
			}

		};
	}

	@Override
	@After
	public void tearDown() throws Exception {
		this.neuZuEmpfangedeNachrichten = null;
		this.zuletzGesendeteNachricht = null;
		this.neuZuEmpfangedeNachrichten = null;
		this.amsAusgangsProducer = null;
		this.amsCommandConsumer = null;
	}

	@Test
	public void testSyncronisationUeberDistributorAusfueren()
			throws MessagingException, StorageError, StorageException,
			InconsistentConfigurationException,
			UnknownConfigurationElementError {
		final AcknowledgeHandler handler = new AcknowledgeHandler() {
			public void acknowledge() throws Throwable {
				SyncronisationsAutomat_Test.this.ackHandlerCallCount++;
			}
		};

		this.ackHandlerCallCount = 0;
		this.neuZuEmpfangedeNachrichten.add(new DefaultNAMSMessage(
				new SyncronisationsAufforderungsSystemNachchricht(), handler));
		this.neuZuEmpfangedeNachrichten.add(new DefaultNAMSMessage(
				new SyncronisationsAufforderungsSystemNachchricht(), handler));
		this.neuZuEmpfangedeNachrichten.add(new DefaultNAMSMessage(
				new SyncronisationsBestaetigungSystemNachricht(), handler));

		this.nextToBeDelivered = new ReplicationStateDTO();
		this.nextToBeDelivered
				.setReplicationState(ReplicationState.FLAGVALUE_SYNCH_IDLE);

		SyncronisationsAutomat.syncronisationUeberDistributorAusfueren(
				this.amsAusgangsProducer, this.amsCommandConsumer,
				new LocalStoreConfigurationService() {

					public void deleteDTO(
							final NewAMSConfigurationElementDTO dto)
							throws StorageError, StorageException,
							InconsistentConfigurationException {
						Assert.fail("unexpected method call!");
					}

					public ReplicationStateDTO getCurrentReplicationState()
							throws StorageError, StorageException,
							InconsistentConfigurationException {
						if (SyncronisationsAutomat_Test.this.nextToBeDelivered == null) {
							Assert.fail("missing init of nextToBeDelivered");
						}
						return SyncronisationsAutomat_Test.this.nextToBeDelivered;
					}

					public Configuration getEntireConfiguration()
							throws StorageError, StorageException,
							InconsistentConfigurationException {
						Assert.fail("unexpected method call!");
						return null;
					}

					public void prepareSynchonization() {
						Assert.fail("unexpected method call!");
					}

					public void saveCurrentReplicationState(
							final ReplicationStateDTO currentState)
							throws StorageError, StorageException,
							UnknownConfigurationElementError {
						if (SyncronisationsAutomat_Test.this.lastSended != null) {
							Assert.fail("missing clean of of lastSended");
						}
						SyncronisationsAutomat_Test.this.lastSended = currentState;
					}

					public void saveDTO(final NewAMSConfigurationElementDTO dto)
							throws StorageError, StorageException,
							InconsistentConfigurationException {
						Assert.fail("unexpected method call!");
					}

					public void saveHistoryDTO(final HistoryDTO historyDTO) {
						Assert.fail("unexpected method call!");
					}

					public FilterConfiguration getEntireFilterConfiguration()
							throws StorageError, StorageException,
							InconsistentConfigurationException {
						// TODO Auto-generated method stub
						return null;
					}

				}, new HistoryService() {

					public void logReceivedReplicationDoneMessage() {

					}

					public void logReceivedStartReplicationMessage() {

					}

					public void logTimeOutForTimeBased(final Vorgangsmappe v) {
						Assert.fail("unexpected method call!");
					}

				});

		Assert.assertNotNull(this.zuletzGesendeteNachricht);
		Assert
				.assertTrue(this.zuletzGesendeteNachricht instanceof SyncronisationsAufforderungsSystemNachchricht);
		Assert.assertEquals("Alle Nachrichten wurden acknowledged.", 3,
				this.ackHandlerCallCount);
		Assert.assertEquals(
				ReplicationState.FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED,
				this.lastSended.getReplicationState());
	}

}
