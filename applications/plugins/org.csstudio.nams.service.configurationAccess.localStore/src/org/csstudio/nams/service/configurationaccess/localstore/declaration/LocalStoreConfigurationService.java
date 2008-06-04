package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import org.csstudio.nams.service.configurationaccess.localstore.configurationElements.TopicConfigurationId;
import org.csstudio.nams.service.configurationaccess.localstore.configurationElements.TopicDTO;

public interface LocalStoreConfigurationService {
	
	public TopicDTO getTopicConfigurations(TopicConfigurationId topicConfigurationDatabaseId);
}
