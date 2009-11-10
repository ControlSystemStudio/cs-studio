package org.csstudio.nams.configurator;

import org.csstudio.nams.configurator.editor.FilterTreeContentProvider_Test;
import org.csstudio.nams.configurator.service.ConfigurationBeanServiceImpl_Test;
import org.csstudio.nams.configurator.service.synchronize.SynchronizeServiceImpl_Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;



@RunWith(Suite.class)
@SuiteClasses( { FilterTreeContentProvider_Test.class,
	
	ConfigurationBeanServiceImpl_Test.class,
	
	SynchronizeServiceImpl_Test.class})
public class AllTests {
}
