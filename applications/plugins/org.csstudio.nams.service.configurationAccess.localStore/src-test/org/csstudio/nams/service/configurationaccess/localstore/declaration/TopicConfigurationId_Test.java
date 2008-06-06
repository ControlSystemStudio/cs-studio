package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import org.csstudio.nams.common.testutils.AbstractValue_TestCase;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.TopicConfigurationId;

public class TopicConfigurationId_Test extends AbstractValue_TestCase<TopicConfigurationId> {

	@Override
	protected TopicConfigurationId doGetAValueOfTypeUnderTest() {
		return TopicConfigurationId.valueOf(1);
	}

	@Override
	protected TopicConfigurationId[] doGetDifferentInstancesOfTypeUnderTest() {
		return new TopicConfigurationId[] {
				TopicConfigurationId.valueOf(1),
				TopicConfigurationId.valueOf(13),
				TopicConfigurationId.valueOf(42)
		};
	}

}
