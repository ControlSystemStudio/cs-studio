package org.csstudio.alarm.table;

import org.csstudio.alarm.table.dataModel.JMSAlarmMessageListTest;
import org.csstudio.alarm.table.preferences.TopicSetTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    JMSAlarmMessageListTest.class,
    TopicSetTest.class
})
public class AllTests {
    // This is a suite which should not contain code.
}
