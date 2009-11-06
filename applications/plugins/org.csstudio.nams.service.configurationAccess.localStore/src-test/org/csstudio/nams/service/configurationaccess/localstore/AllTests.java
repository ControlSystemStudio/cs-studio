package org.csstudio.nams.service.configurationaccess.localstore;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.TopicConfigurationId_Test;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO_Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { ConfigurationaccessLocalStoreWithoutDBAllTestsSuite.class,
	ConfigurationServiceFactoryImpl_DatabaseIntegrationTest_RequiresHSQL.class,
	LocalConfigurationStoreServiceActivator_Test.class,
	
	TopicConfigurationId_Test.class,
	
	StringFilterConditionDTO_Test.class})
public class AllTests {
}
