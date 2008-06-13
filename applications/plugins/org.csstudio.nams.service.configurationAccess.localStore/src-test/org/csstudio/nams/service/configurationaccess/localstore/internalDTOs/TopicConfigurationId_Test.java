package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import org.csstudio.nams.common.testutils.AbstractValue_TestCase;

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
