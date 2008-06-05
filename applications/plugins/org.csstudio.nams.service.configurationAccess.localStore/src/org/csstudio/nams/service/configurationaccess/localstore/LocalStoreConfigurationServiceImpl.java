package org.csstudio.nams.service.configurationaccess.localstore;

import java.util.List;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.InconsistentConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicConfigurationId;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.UnknownConfigurationElementError;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

class LocalStoreConfigurationServiceImpl implements
		LocalStoreConfigurationService {

	private final Session session;

	public LocalStoreConfigurationServiceImpl(final Session session) {
		this.session = session;
	}

	public ReplicationStateDTO getCurrentReplicationState()
			throws StorageError, StorageException, InconsistentConfiguration {
		final Transaction newTransaction = this.session.beginTransaction();
		final List<?> messages = this.session.createQuery(
				"from ReplicationStateDTO r where r.flagName = '"+ ReplicationStateDTO.DB_FLAG_NAME+"'").list();

		ReplicationStateDTO result = null;
		if (!messages.isEmpty()) {
			result = (ReplicationStateDTO) messages.get(0);
		}
		newTransaction.commit();

		return result;
//		throw new RuntimeException("Not implemented yet.");
	}

	// private void test() {
	// Transaction tx = session.beginTransaction();
	// TopicDTO message = new TopicDTO();
	// Integer msgId = (Integer) session.save(message);
	// System.out.println("New TOPIC id: " + msgId);
	// tx.commit();
	//
	// // Second unit of work
	//
	// Transaction newTransaction = session.beginTransaction();
	// List<?> messages = session.createQuery(
	// "from TopicDTO t order by t.id asc").list();
	// System.out.println(messages.size() + " TOPIC(s) found:");
	//
	// for (Iterator<?> iter = messages.iterator(); iter.hasNext();) {
	// TopicDTO loadedMsg = (TopicDTO) iter.next();
	// System.out.println(loadedMsg.toString());
	// }
	// newTransaction.commit();
	// }

	public ConfigurationDTO getEntireConfiguration() throws StorageError,
			StorageException, InconsistentConfiguration {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet.");
	}

	public TopicDTO getTopicConfigurations(final TopicConfigurationId id) {
		final Transaction newTransaction = this.session.beginTransaction();
		final List<?> messages = this.session.createQuery(
				"from TopicDTO t where t.id = " + id.asDatabaseId()).list();
		System.out.println(messages.size() + " TOPIC(s) found:");

		TopicDTO result = null;
		if (!messages.isEmpty()) {
			result = (TopicDTO) messages.get(0);
		}
		newTransaction.commit();

		return result;
	}

	public void saveCurrentReplicationState(
			final ReplicationStateDTO currentState) throws StorageError,
			StorageException, UnknownConfigurationElementError {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet.");
	}
}
