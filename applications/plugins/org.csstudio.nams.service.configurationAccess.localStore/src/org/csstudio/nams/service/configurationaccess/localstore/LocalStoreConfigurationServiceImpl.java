package org.csstudio.nams.service.configurationaccess.localstore;

import java.util.List;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.UnknownConfigurationElementError;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.TopicConfigurationId;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
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
		ReplicationStateDTO result = null;
		try {
		final Transaction newTransaction = this.session.beginTransaction();
		final List<?> messages = this.session.createQuery(
				"from ReplicationStateDTO r where r.flagName = '"+ ReplicationStateDTO.DB_FLAG_NAME+"'").list();

		if (!messages.isEmpty()) {
			result = (ReplicationStateDTO) messages.get(0);
		}
		newTransaction.commit();

		} catch(Throwable t ){
			new StorageError("Failed to write replication flag", t);
		}
		
		if( result == null )
		{
			throw new InconsistentConfiguration("Replication state unavailable.");
		}
		
		return result;
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

	public Configuration getEntireConfiguration() throws StorageError,
			StorageException, InconsistentConfiguration {
		final Transaction transaction = this.session.beginTransaction();
		Configuration result = new Configuration(session);
		transaction.commit();
		return result;
	}

	public List<JunctorConditionDTO> getJunctorConditionDTOConfigurations() {
		final Transaction newTransaction = this.session.beginTransaction();
		final List<JunctorConditionDTO> junctorConditionDTOs = this.session.createQuery(
				"from JunctorConditionDTO").list();

		newTransaction.commit();
		System.out.println(junctorConditionDTOs.toString());

		return junctorConditionDTOs;
	}
	
	public void saveJunctorConditionDTO(JunctorConditionDTO junctorConditionDTO){
		 Transaction tx = session.beginTransaction();
		 Integer msgId = (Integer) session.save(junctorConditionDTO);
//		 final List<?> messages = this.session.createQuery(
//					"from JunctorConditionDTO r where r.iFilterConditionID = '"+ junctorConditionDTO.getIFilterConditionID()+"'").list();
		 System.out.println("New JunctorConditionDTO id: " + msgId);
		 tx.commit();
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
		
		final Transaction newTransaction = this.session.beginTransaction();
		this.session.saveOrUpdate(currentState);
		newTransaction.commit();		
//		throw new RuntimeException("Not implemented yet.");
	}

	@SuppressWarnings("unchecked")
	public List<FilterConditionDTO> getFilterConditionDTOConfigurations() {
		final Transaction newTransaction = this.session.beginTransaction();
		final List<FilterConditionDTO> vrs = this.session.createQuery(
				"from FilterConditionDTO t").list();
		System.out.println(vrs.size() + " FilterConditionDTO(s) found:");

		newTransaction.commit();

		return vrs;
	}

	@SuppressWarnings("unchecked")
	public List<StringArrayFilterConditionDTO> getStringArrayFilterConditionDTOConfigurations() {
		final Transaction newTransaction = this.session.beginTransaction();
		final List<StringArrayFilterConditionDTO> vrs = this.session.createQuery(
				"from StringArrayFilterConditionDTO t").list();
		System.out.println(vrs.size() + " StringArrayFilterConditionDTO(s) found:");

		newTransaction.commit();

		return vrs;
	}

	public void saveStringFilterConditionDTO(
			StringFilterConditionDTO stringConditionDTO) {
		 Transaction tx = session.beginTransaction();
		 Integer msgId = (Integer) session.save(stringConditionDTO);
//		 final List<?> messages = this.session.createQuery(
//					"from JunctorConditionDTO r where r.iFilterConditionID = '"+ junctorConditionDTO.getIFilterConditionID()+"'").list();
		 System.out.println("New StringrConditionDTO id: " + msgId);
		 tx.commit();
		
	}

	public List<StringFilterConditionDTO> getStringFilterConditionDTOConfigurations() {
		final Transaction newTransaction = this.session.beginTransaction();
		final List<StringFilterConditionDTO> stringFilterConditionDTOs = this.session.createQuery(
				"from StringFilterConditionDTO").list();

		newTransaction.commit();
		System.out.println(stringFilterConditionDTOs.toString());

		return stringFilterConditionDTOs;
	}

}
