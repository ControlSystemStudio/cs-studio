package org.csstudio.alarm.treeView;

import org.csstudio.alarm.treeView.jms.PendingUpdateTest;
import org.csstudio.alarm.treeView.ldap.TreeBuilderTest;
import org.csstudio.alarm.treeView.model.AlarmTest;
import org.csstudio.alarm.treeView.model.ProcessVariableNodeTest;
import org.csstudio.alarm.treeView.model.SeverityTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    AlarmTest.class,
    TreeBuilderTest.class,
    SeverityTest.class,
    ProcessVariableNodeTest.class,
    PendingUpdateTest.class,
})
public class AllUnitTests {
    // This is a suite which should not contain code.
}
