package org.csstudio.alarm.treeView;

import org.csstudio.alarm.treeView.ldap.TreeBuilderTest;
import org.csstudio.alarm.treeView.model.AlarmTest;
import org.csstudio.alarm.treeView.model.MathTest;
import org.csstudio.alarm.treeView.model.ProcessVariableNodeTest;
import org.csstudio.alarm.treeView.model.SeverityTest;
import org.csstudio.alarm.treeView.views.AlarmTreeViewTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    AlarmTest.class,
    TreeBuilderTest.class,
    MathTest.class,
    AlarmTreeViewTest.class,
    SeverityTest.class,
    ProcessVariableNodeTest.class
})
public class AllUnitTests {
    // This is a suite which should not contain code.
}
