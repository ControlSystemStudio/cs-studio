package org.csstudio.alarm.treeView;

import org.csstudio.alarm.treeView.ldap.LdapNameUtilsTest;
import org.csstudio.alarm.treeView.ldap.TreeBuilderTest;
import org.csstudio.alarm.treeView.model.AlarmTest;
import org.csstudio.alarm.treeView.model.ProcessVariableNodeTest;
import org.csstudio.alarm.treeView.model.SeverityTest;
import org.csstudio.alarm.treeView.views.PendingUpdateTest;
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
    EventtimeUtilTest.class,
    LdapNameUtilsTest.class,
})
public class AllTests {
    // This is a suite which should not contain code.
}
