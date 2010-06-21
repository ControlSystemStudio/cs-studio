package org.csstudio.alarm.treeView;

import org.csstudio.alarm.treeView.ldap.LdapNameUtilsTest;
import org.csstudio.alarm.treeView.ldap.TreeBuilderTest;
import org.csstudio.alarm.treeView.model.AlarmTest;
import org.csstudio.alarm.treeView.model.ProcessVariableNodeTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * TODO (bknerr) : Das muss geaendert werden.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 14.06.2010
 */
@RunWith(Suite.class)
@SuiteClasses({
    AlarmTest.class,
    TreeBuilderTest.class,
    ProcessVariableNodeTest.class,
    LdapNameUtilsTest.class})
public class AllTests {
    // This is a suite which should not contain code.
}
