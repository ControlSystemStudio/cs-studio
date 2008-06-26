package org.csstudio.nams.application.department.decision;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import junit.framework.TestCase;

import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.material.SyncronisationsAufforderungsSystemNachchricht;
import org.csstudio.nams.common.material.SyncronisationsBestaetigungSystemNachricht;
import org.csstudio.nams.common.material.SystemNachricht;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.HistoryDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO.ReplicationState;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.UnknownConfigurationElementError;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.TopicConfigurationId;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
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

			public void sendeSystemnachricht(SystemNachricht systemNachricht) {
				if (zuletzGesendeteNachricht != null) {
					fail();
				}
				zuletzGesendeteNachricht = systemNachricht;
			}

			public void sendeVorgangsmappe(Vorgangsmappe vorgangsmappe) {
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

	ReplicationStateDTO nextToBeDelivered = null;
	ReplicationStateDTO lastSended = null;

	@Test
	public void testSyncronisationUeberDistributorAusfueren()
			throws MessagingException, StorageError, StorageException, InconsistentConfiguration, UnknownConfigurationElementError {
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

		nextToBeDelivered = new ReplicationStateDTO();
		nextToBeDelivered.setReplicationState(ReplicationState.FLAGVALUE_SYNCH_IDLE);
		
		SyncronisationsAutomat.syncronisationUeberDistributorAusfueren(
				amsAusgangsProducer, amsCommandConsumer,
				new LocalStoreConfigurationService() {

					public ReplicationStateDTO getCurrentReplicationState()
							throws StorageError, StorageException,
							InconsistentConfiguration {
						if (nextToBeDelivered == null)
							fail("missing init of nextToBeDelivered");
						return nextToBeDelivered;
					}

					public Configuration getEntireConfiguration()
							throws StorageError, StorageException,
							InconsistentConfiguration {
						fail("unexpected method call!");
						return null;
					}

					public TopicDTO getTopicConfigurations(
							TopicConfigurationId topicConfigurationDatabaseId) {
						fail("unexpected method call!");
						return null;
					}

					public void saveCurrentReplicationState(
							ReplicationStateDTO currentState)
							throws StorageError, StorageException,
							UnknownConfigurationElementError {
						if (lastSended != null)
							fail("missing clean of of lastSended");
						lastSended = currentState;
					}

					public List<FilterConditionDTO> getFilterConditionDTOConfigurations() {
						fail("unexpected method call!");
						return null;
					}

					public List<StringArrayFilterConditionDTO> getStringArrayFilterConditionDTOConfigurations() {
						fail("unexpected method call!");
						return null;
					}

					public List<JunctorConditionDTO> getJunctorConditionDTOConfigurations() {
						fail("unexpected method call!");
						return null;
					}

					public void saveJunctorConditionDTO(
							JunctorConditionDTO junctorConditionDTO) {
						fail("unexpected method call!");
						
					}

					public void saveStringFilterConditionDTO(
							StringFilterConditionDTO stringConditionDTO) {
						fail("unexpected method call!");
						
					}

					public List<StringFilterConditionDTO> getStringFilterConditionDTOConfigurations() {
						fail("unexpected method call!");
						return null;
					}

					public void saveHistoryDTO(HistoryDTO historyDTO) {
						fail("unexpected method call!");
					}

					public AlarmbearbeiterDTO saveAlarmbearbeiterDTO(
							AlarmbearbeiterDTO alarmBearbeiterDTO) {
						fail("unexpected method call!");
						return null;
					}

					public AlarmbearbeiterGruppenDTO saveAlarmbearbeiterGruppenDTO(
							AlarmbearbeiterGruppenDTO alarmBearbeiterGruppenDTO) {
						fail("unexpected method call!");
						return null;
					}

					public TopicDTO saveTopicDTO(TopicDTO topicDTO) {
						fail("unexpected method call!");
						return null;
					}

				}, new HistoryService() {

					public void logReceivedReplicationDoneMessage() {
						
					}

					public void logReceivedStartReplicationMessage() {
						
					}

					public void logTimeOutForTimeBased(int regelwerkID,
							int messageDescId, int regelId) {
						fail("unexpected method call!");
					}
					
				});

		assertNotNull(zuletzGesendeteNachricht);
		assertTrue(zuletzGesendeteNachricht instanceof SyncronisationsAufforderungsSystemNachchricht);
		assertEquals("Alle Nachrichten wurden acknowledged.", 3,
				ackHandlerCallCount);
		assertEquals(ReplicationState.FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED, lastSended.getReplicationState());
	}

}
