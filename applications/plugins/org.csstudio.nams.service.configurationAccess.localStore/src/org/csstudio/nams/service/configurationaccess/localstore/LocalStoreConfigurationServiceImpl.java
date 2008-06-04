package org.csstudio.nams.service.configurationaccess.localstore;

import java.util.Iterator;
import java.util.List;

import org.csstudio.nams.service.configurationaccess.localstore.configurationElements.TopicConfigurationId;
import org.csstudio.nams.service.configurationaccess.localstore.configurationElements.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

class LocalStoreConfigurationServiceImpl implements
		LocalStoreConfigurationService {

	private final Session session;

	public LocalStoreConfigurationServiceImpl(Session session) {
		this.session = session;
	}

	public TopicDTO getTopicConfigurations(TopicConfigurationId id) {
		Transaction newTransaction = session.beginTransaction();
		List<?> messages = session.createQuery(
				"from TopicDTO t where t.id = "+id.asDatabaseId()).list();
		System.out.println(messages.size() + " TOPIC(s) found:");

		TopicDTO result = null;
		if( !messages.isEmpty() )
		{
			result = (TopicDTO) messages.get(0);
		}
		newTransaction.commit();
		
		return result;
	}

	
	private void test() {
		Transaction tx = session.beginTransaction();
		TopicDTO message = new TopicDTO();
		Integer msgId = (Integer) session.save(message);
		System.out.println("New TOPIC id: " + msgId);
		tx.commit();

		// Second unit of work

		Transaction newTransaction = session.beginTransaction();
		List<?> messages = session.createQuery(
				"from TopicDTO t order by t.id asc").list();
		System.out.println(messages.size() + " TOPIC(s) found:");

		for (Iterator<?> iter = messages.iterator(); iter.hasNext();) {
			TopicDTO loadedMsg = (TopicDTO) iter.next();
			System.out.println(loadedMsg.toString());
		}
		newTransaction.commit();
	}
}
