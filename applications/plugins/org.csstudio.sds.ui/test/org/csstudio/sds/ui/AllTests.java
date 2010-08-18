package org.csstudio.sds.ui;

import org.csstudio.sds.ui.behaviors.BehaviourServiceTest;
import org.csstudio.sds.ui.internal.editparts.EditPartServiceTest;
import org.csstudio.sds.ui.internal.editparts.WidgetEditPartFactoryTest;
import org.csstudio.sds.ui.internal.properties.PropertyDescriptorFactoryServiceTest;
import org.csstudio.sds.ui.internal.runmode.RunModeBoxInputTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses( {
        BehaviourServiceTest.class,

		EditPartServiceTest.class,
		WidgetEditPartFactoryTest.class,

		PropertyDescriptorFactoryServiceTest.class,

		RunModeBoxInputTest.class
})
public class AllTests {
    // EMPTY
}
