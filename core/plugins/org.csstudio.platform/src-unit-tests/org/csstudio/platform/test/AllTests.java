package org.csstudio.platform.test;

import org.csstudio.platform.data.ValueTest;
import org.csstudio.platform.internal.data.DoubleValueTest;
import org.csstudio.platform.internal.data.MetaDataTest;
import org.csstudio.platform.internal.data.MinMaxDoubleValueTest;
import org.csstudio.platform.internal.data.TimestampTests;
import org.csstudio.platform.internal.model.ArchiveDataSourceFactoryTest;
import org.csstudio.platform.internal.model.ArchiveDataSourceTest;
import org.csstudio.platform.internal.model.ControlSystemItemFactoriesRegistryTest;
import org.csstudio.platform.internal.model.ProcessVariableFactoryTest;
import org.csstudio.platform.internal.model.ProcessVariableTest;
import org.csstudio.platform.internal.model.pvs.AbstractProcessVariableNameParserTest;
import org.csstudio.platform.internal.model.pvs.ControlSystemEnumTest;
import org.csstudio.platform.internal.model.pvs.DALPropertyFactoriesProviderTest;
import org.csstudio.platform.internal.model.pvs.DalNameParserTest;
import org.csstudio.platform.internal.model.pvs.ProcessVariableAdressTest;
import org.csstudio.platform.internal.model.pvs.SimpleNameParserTest;
import org.csstudio.platform.internal.simpledal.ConnectorIdentificationTest;
import org.csstudio.platform.internal.simpledal.DalCacheBugTest;
import org.csstudio.platform.internal.simpledal.ProcessVariableConnectionServiceTest;
import org.csstudio.platform.internal.simpledal.SimpleDALTest;
import org.csstudio.platform.internal.simpledal.SimpleDAL_EPICSTest;
import org.csstudio.platform.internal.simpledal.dal.DalConnectorTest;
import org.csstudio.platform.internal.simpledal.local.DataGeneratorInfosTest;
import org.csstudio.platform.logging.CentralLoggerTest;
import org.csstudio.platform.logging.JMSLogThreadTest;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactoryTest;
import org.csstudio.platform.simpledal.ConnectionStateTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { ConnectionStateTest.class, ConnectorIdentificationTest.class,
	SimpleDALTest.class, DalConnectorTest.class, SimpleDAL_EPICSTest.class,
	DalCacheBugTest.class, ProcessVariableConnectionServiceTest.class,
	SimpleNameParserTest.class, ProcessVariableFactoryTest.class,
	ControlSystemItemFactoriesRegistryTest.class,
	ProcessVariableAdressTest.class, DataGeneratorInfosTest.class,
	MetaDataTest.class, DalNameParserTest.class,
	MinMaxDoubleValueTest.class, ProcessVariableTest.class,
	JMSLogThreadTest.class, ControlSystemEnumTest.class,
	ArchiveDataSourceTest.class, TimestampTests.class,
	CentralLoggerTest.class, ArchiveDataSourceFactoryTest.class,
	ValueTest.class, AbstractProcessVariableNameParserTest.class,
	DALPropertyFactoriesProviderTest.class,
	ProcessVariableAdressFactoryTest.class, DoubleValueTest.class })
public class AllTests {
    // This is just a declarative suite definition without any code definitions.
}
