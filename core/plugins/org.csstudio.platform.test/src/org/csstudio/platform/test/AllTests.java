package org.csstudio.platform.test;

import org.csstudio.platform.internal.model.ArchiveDataSourceFactoryTest;
import org.csstudio.platform.internal.model.ArchiveDataSourceTest;
import org.csstudio.platform.internal.model.ControlSystemItemFactoriesRegistryTest;
import org.csstudio.platform.internal.model.ProcessVariableFactoryTest;
import org.csstudio.platform.internal.model.ProcessVariableTest;
import org.csstudio.platform.logging.CentralLoggerTest;
import org.csstudio.platform.logging.JMSLogThreadTest;
import org.csstudio.platform.management.CommandDescriptionTest;
import org.csstudio.platform.management.CommandParameterDefinitionTest;
import org.csstudio.platform.management.CommandParameterEnumValueTest;
import org.csstudio.platform.management.CommandParametersTest;
import org.csstudio.platform.util.StringUtilTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { 
//		SimpleDALTest.class,
//		DalConnectorTest.class,
//		SimpleDAL_EPICSTest.class,
//		ProcessVariableConnectionServiceTest.class,
		ProcessVariableFactoryTest.class,
		ControlSystemItemFactoriesRegistryTest.class,
//		MinMaxDoubleValueTest.class,
		ProcessVariableTest.class,
		JMSLogThreadTest.class,
//		ControlSystemEnumTest.class,
		ArchiveDataSourceTest.class,
		CentralLoggerTest.class, ArchiveDataSourceFactoryTest.class,
//		ValueTest.class,
//		AbstractProcessVariableNameParserTest.class,
		StringUtilTest.class, CommandDescriptionTest.class,
		CommandParameterDefinitionTest.class,
		CommandParameterEnumValueTest.class, CommandParametersTest.class})
public class AllTests {
}
