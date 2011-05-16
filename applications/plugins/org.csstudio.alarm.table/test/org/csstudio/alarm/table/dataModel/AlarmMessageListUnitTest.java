package org.csstudio.alarm.table.dataModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.table.preferences.ISeverityMapping;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the abstract message list. The message list is the model for the
 * alarm table views (log, ams, alarm).
 * 
 * @author jpenning
 * @author $Author: jpenning $
 * @since 04.10.2010
 */
public class AlarmMessageListUnitTest extends AbstractMessageListUnitTest {
    
    private static Integer INCREMENTED_MSEC = 100;
    
    @Override
    protected AlarmMessageList createMessageListForTest() {
        return new AlarmMessageList();
    }
    
    @Override
    protected AlarmMessage createMessage() {
        // Define a message that will pass the checkValidity-Test in
        // AlarmMessageList
        AlarmMessage result = new AlarmMessage();
        result.setProperty(AlarmMessageKey.NAME.getDefiningName(), "PV for test");
        result.setProperty(AlarmMessageKey.SEVERITY.getDefiningName(), "Severity for test");
        result.setProperty(AlarmMessageKey.TYPE.getDefiningName(), "Type for test");
        return result;
    }
    
    @Nonnull
    private AlarmMessage createNamedMessage(@Nonnull final String name) {
        AlarmMessage result = createMessage();
        result.setProperty(AlarmMessageKey.NAME.getDefiningName(), name);
        return result;
    }
    
    @Before
    public void setUp() {
        ISeverityMapping severityMapping = new TestSeverityMapping();
        SeverityRegistry.setSeverityMapping(severityMapping);
    }
    
    @Override
    @Test
    public void testAddRemoveListener() throws Exception {
        // Adding messages has a different semantics compared to the abstract
        // implementation, so the test is overridden here.
        IMessageViewer messageViewer0 = mock(IMessageViewer.class);
        AbstractMessageList messageList = createMessageListForTest();
        assertEquals(0, messageList.getMessageListSize());
        
        // listener is not called if not registered, but the message is already
        // stored
        BasicMessage message0 = createNamedMessage("PV0");
        messageList.addMessage(message0);
        verify(messageViewer0, never()).addJMSMessage(any(BasicMessage.class));
        assertEquals(1, messageList.getMessageListSize());
        
        // now registered, so listener is called once. message is stored too.
        BasicMessage message1 = createNamedMessage("PV1");
        messageList.addChangeListener(messageViewer0);
        messageList.addMessage(message1);
        verify(messageViewer0, times(1)).addJMSMessage(any(BasicMessage.class));
        assertEquals(2, messageList.getMessageListSize());
        
        // no longer registered, listener not called again, but one more message
        // is stored
        BasicMessage message2 = createNamedMessage("PV2");
        messageList.removeChangeListener(messageViewer0);
        messageList.addMessage(message2);
        verify(messageViewer0, times(1)).addJMSMessage(any(BasicMessage.class));
        assertEquals(3, messageList.getMessageListSize());
    }
    
    // Test case to reproduce an error 2008-01-06:
    // more than one message that should be deleted for a corresponding
    // acknowledge message are in the table (NO_ALARM or grayed out).
    // -> receive acknowledge message for all messages.
    // -> only one message is removed.
    @Test
    public void testRemoveMessages() {
        final AlarmMessageList messageList = createMessageListForTest();
        final String eventtimeMajor = createAndIncrementDate();
        final String eventtimeMinor = createAndIncrementDate();
        
        for (int i = 0; i < 5; i++) {
            messageList.addMessage(createJMSMessage("NAME_" + i,
                                                    "MINOR",
                                                    "event",
                                                    false,
                                                    eventtimeMinor));
        }
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(true, checkForAlarm("NAME_" + i, "MINOR", messageList));
        }
        for (int i = 0; i < 5; i++) {
            messageList.addMessage(createJMSMessage("NAME_" + i,
                                                    "MAJOR",
                                                    "event",
                                                    false,
                                                    eventtimeMajor));
        }
        Assert.assertEquals(10, messageList.getMessageListSize());
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(true, checkForAlarm("NAME_" + i, "MAJOR", messageList));
            Assert.assertEquals(true, checkForAlarm("NAME_" + i, "MINOR", messageList));
        }
        
        // Send acknowledges to minor
        for (int i = 0; i < 5; i++) {
            messageList.addMessage(createJMSMessage("NAME_" + i,
                                                    "MINOR",
                                                    "event",
                                                    true,
                                                    eventtimeMinor));
        }
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(false, checkForAlarm("NAME_" + i, "MINOR", messageList));
        }
        for (int i = 0; i < 5; i++) {
            messageList.addMessage(createJMSMessage("NAME_" + i,
                                                    "MAJOR",
                                                    "event",
                                                    true,
                                                    eventtimeMajor));
        }
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(true, checkForAlarm("NAME_" + i, "MAJOR", messageList));
        }
    }
    
    @Test
    public void testAddStatusDisconnected() {
        final AlarmMessageList messageList = createMessageListForTest();
        messageList.addMessage(createJMSMessage("NAME",
                                                "MAJOR",
                                                "event",
                                                false,
                                                createAndIncrementDate()));
        messageList.addMessage(createJMSMessage("NAME",
                                                "MINOR",
                                                "event",
                                                false,
                                                createAndIncrementDate()));
        Assert.assertEquals(2, messageList.getMessageListSize());
        messageList.addMessage(addJMSMessage("NAME",
                                             "MINOR",
                                             "status",
                                             false,
                                             createAndIncrementDate(),
                                             "DISCONNECTED"));
        Assert.assertEquals(1, messageList.getMessageListSize());
        Assert.assertEquals(true, checkForAlarm("NAME", "MINOR", messageList, "DISCONNECTED"));
        // messageList.deleteAllMessages();
        
        messageList.addMessage(createJMSMessage("NAME",
                                                "INVALID",
                                                "event",
                                                false,
                                                createAndIncrementDate()));
        messageList.addMessage(addJMSMessage("NAME",
                                             "MINOR",
                                             "status",
                                             false,
                                             createAndIncrementDate(),
                                             "DISCONNECTED"));
        Assert.assertEquals(1, messageList.getMessageListSize());
        Assert.assertEquals(true, checkForAlarm("NAME", "INVALID", messageList, "DISCONNECTED"));
        // messageList.deleteAllMessages();
        
        messageList.addMessage(createJMSMessage("NAME",
                                                "MINOR",
                                                "event",
                                                false,
                                                createAndIncrementDate()));
        messageList.addMessage(addJMSMessage("NAME",
                                             "NO_ALARM",
                                             "status",
                                             false,
                                             createAndIncrementDate(),
                                             "DISCONNECTED"));
        Assert.assertEquals(1, messageList.getMessageListSize());
        Assert.assertEquals(true, checkForAlarm("NAME", "MINOR", messageList, "DISCONNECTED"));
        messageList.addMessage(addJMSMessage("NAME",
                                             "MAJOR",
                                             "status",
                                             false,
                                             createAndIncrementDate(),
                                             "CONNECTED"));
        Assert.assertEquals(true, checkForAlarm("NAME", "MAJOR", messageList, "CONNECTED"));
        
        messageList.addMessage(createJMSMessage("NAME",
                                                "MAJOR",
                                                "event",
                                                false,
                                                createAndIncrementDate()));
        messageList.addMessage(createJMSMessage("NAME_NEU",
                                                "MINOR",
                                                "status",
                                                false,
                                                createAndIncrementDate()));
        Assert.assertEquals(1, messageList.getMessageListSize());
        messageList.addMessage(addJMSMessage("NAME_NEU",
                                             "MINOR",
                                             "status",
                                             false,
                                             createAndIncrementDate(),
                                             "DISCONNECTED"));
        Assert.assertEquals(1, messageList.getMessageListSize());
        // messageList.deleteAllMessages();
    }
    
    @Test
    public void testSimpleMessageSequence() {
        final AlarmMessageList messageList = createMessageListForTest();
        
        // adding two equal messages gives only one entry
        messageList.addMessage(createJMSMessage("NAME", "MINOR", "event", false, null));
        messageList.addMessage(createJMSMessage("NAME", "MINOR", "event", false, null));
        assertEquals(1, messageList.getMessageListSize());
        
        // keep eventtime to create later an ack-message with the same eventtime
        final String eventtime = createAndIncrementDate();
        
        // add another message with a differing severity and event time, both will be there
        messageList.addMessage(createJMSMessage("NAME", "MAJOR", "event", false, eventtime));
        assertEquals(2, messageList.getMessageListSize());
        assertTrue(checkForAlarm("NAME", "MAJOR", messageList));
        assertTrue(checkForAlarm("NAME", "MINOR", messageList));
        
        // add the same message again, it will be ignored
        messageList.addMessage(createJMSMessage("NAME", "MAJOR", "event", true, eventtime));
        assertEquals(2, messageList.getMessageListSize());
        assertTrue(checkForAlarm("NAME", "MAJOR", messageList));
        assertTrue(checkForAlarm("NAME", "MINOR", messageList));
        
        // reset the alarm state with the new message, both old entries go, the 'no alarm' will be there
        messageList.addMessage(createJMSMessage("NAME", "NO_ALARM", "event", false, null));
        assertEquals(1, messageList.getMessageListSize());
        assertTrue(checkForAlarm("NAME", "NO_ALARM", messageList));
    }
    
    @Test
    public void testInvalidMapMessages() {
        // This test ensures that only useful messages are added to the table.
        
        final AlarmMessageList messageList = createMessageListForTest();
        
        // add empty message
        messageList.addMessage(new BasicMessage());
        Assert.assertEquals(0, messageList.getMessageListSize());
        
        // property severity = null
        messageList.addMessage(createJMSMessage("NAME", null, "type", Boolean.TRUE, null));
        
        // if no type is given, only messages which ack TRUE will be processed
        // property type = null and ack is not set
        messageList.addMessage(createJMSMessage("NAME", "MAJOR", null, null, null));
        Assert.assertEquals(0, messageList.getMessageListSize());
        
        // property type = null and ack is set to FALSE
        messageList.addMessage(createJMSMessage("NAME", "MINOR", null, Boolean.FALSE, null));
        Assert.assertEquals(0, messageList.getMessageListSize());
    }
    
    @Test
    public void testMessageSequenceWithOutdated() {
        final AlarmMessageList messageList = createMessageListForTest();
        final String eventtime = createAndIncrementDate();
        
        // add a message
        messageList.addMessage(createJMSMessage("NAME", "MAJOR", "event", false, eventtime));
        assertEquals(1, messageList.getMessageListSize());
        assertTrue(checkForAlarm("NAME", "MAJOR", messageList));
        
        // add a message with different severity -> old message is outdated
        messageList.addMessage(createJMSMessage("NAME", "MINOR", "event", false, eventtime));
        assertEquals(2, messageList.getMessageListSize());
        AlarmMessage alarmMessage0 = (AlarmMessage) messageList.getMessageList().get(0);
        AlarmMessage alarmMessage1 = (AlarmMessage) messageList.getMessageList().get(1);
        assertTrue(alarmMessage0.isOutdated());
        assertEquals("MAJOR", alarmMessage0.getProperty(AlarmMessageKey.SEVERITY.getDefiningName()));
        assertFalse(alarmMessage1.isOutdated());
        assertEquals("MINOR", alarmMessage1.getProperty(AlarmMessageKey.SEVERITY.getDefiningName()));
        
        // add another message with different severity ->
        // the 1st goes away, the 2nd is outdated, the 3rd is present
        messageList.addMessage(createJMSMessage("NAME", "INVALID", "event", false, eventtime));
        assertEquals(2, messageList.getMessageListSize());
        alarmMessage0 = (AlarmMessage) messageList.getMessageList().get(0);
        alarmMessage1 = (AlarmMessage) messageList.getMessageList().get(1);
        assertTrue(alarmMessage0.isOutdated());
        assertEquals("MINOR", alarmMessage0.getProperty(AlarmMessageKey.SEVERITY.getDefiningName()));
        assertFalse(alarmMessage1.isOutdated());
        assertEquals("INVALID",
                     alarmMessage1.getProperty(AlarmMessageKey.SEVERITY.getDefiningName()));
        
    }
    
    @Test
    public void testMessageSequenceNoOutdated() {
        final AlarmMessageList messageList = createMessageListForTest();
        messageList.showOutdatedMessages(false);
        final String eventtime = createAndIncrementDate();
        
        // add a message
        messageList.addMessage(createJMSMessage("NAME", "MAJOR", "event", false, eventtime));
        assertEquals(1, messageList.getMessageListSize());
        assertTrue(checkForAlarm("NAME", "MAJOR", messageList));
        
        // add a message with different severity -> old message is gone
        messageList.addMessage(createJMSMessage("NAME", "MINOR", "event", false, eventtime));
        assertEquals(1, messageList.getMessageListSize());
        AlarmMessage alarmMessage0 = (AlarmMessage) messageList.getMessageList().get(0);
        assertFalse(alarmMessage0.isOutdated());
        assertEquals("MINOR", alarmMessage0.getProperty(AlarmMessageKey.SEVERITY.getDefiningName()));
        
        // add another message with different severity -> old message is gone
        messageList.addMessage(createJMSMessage("NAME", "INVALID", "event", false, eventtime));
        assertEquals(1, messageList.getMessageListSize());
        alarmMessage0 = (AlarmMessage) messageList.getMessageList().get(0);
        assertFalse(alarmMessage0.isOutdated());
        assertEquals("INVALID",
                     alarmMessage0.getProperty(AlarmMessageKey.SEVERITY.getDefiningName()));
        
    }
    
    private boolean checkForAlarm(@Nonnull final String name,
                                  @Nonnull final String severity,
                                  @Nonnull final AlarmMessageList messageList) {
        return checkForAlarm(name, severity, messageList, null);
    }
    
    private boolean checkForAlarm(@Nonnull final String name,
                                  @Nonnull final String severity,
                                  @Nonnull final AlarmMessageList inputList,
                                  @CheckForNull final String status) {
        boolean isEqual = false;
        for (final BasicMessage message : inputList.getMessageList()) {
            final Map<String, String> messageHashMap = message.getHashMap();
            if ( (messageHashMap.get("NAME").equalsIgnoreCase(name))
                    && (messageHashMap.get("SEVERITY").equalsIgnoreCase(severity))) {
                isEqual = true;
                if (status != null) {
                    if (!messageHashMap.get("STATUS").equalsIgnoreCase(status)) {
                        isEqual = false;
                    }
                }
                return isEqual;
            }
        }
        return isEqual;
    }
    
    @Nonnull
    private String createAndIncrementDate() {
        final String time = "2008-10-11 12:13:14." + INCREMENTED_MSEC.toString();
        INCREMENTED_MSEC++;
        return time;
    }
    
    @Nonnull
    private BasicMessage createJMSMessage(@Nonnull final String name,
                                          @Nonnull final String severity,
                                          @Nonnull final String type,
                                          @CheckForNull final Boolean acknowledged,
                                          @Nonnull final String eventtime) {
        return addJMSMessage(name, severity, type, acknowledged, eventtime, null);
    }
    
    @Nonnull
    private BasicMessage addJMSMessage(@Nonnull final String name,
                                       @Nonnull final String severity,
                                       @Nonnull final String type,
                                       @CheckForNull final Boolean acknowledged,
                                       @Nonnull final String eventtime,
                                       @CheckForNull final String status) {
        
        final BasicMessage message = new BasicMessage();
        message.setProperty("TYPE", type);
        message.setProperty("NAME", name);
        message.setProperty("SEVERITY", severity);
        message.setProperty("EVENTTIME", eventtime);
        if (acknowledged == null) {
            message.setProperty("ACK", null);
        } else {
            message.setProperty("ACK", acknowledged.toString());
        }
        if (status != null) {
            message.setProperty("STATUS", status);
        }
        return message;
    }
    
    /**
     * Mapping for test
     */
    private static class TestSeverityMapping implements ISeverityMapping {
        
        private final HashMap<String, String> _severityKeyValueMapping = new HashMap<String, String>();
        private final HashMap<String, Integer> _severityKeyNumberMapping = new HashMap<String, Integer>();
        
        public TestSeverityMapping() {
            enterValueAndNumberForKey("MAJOR", "MAJOR", 0);
            enterValueAndNumberForKey("MINOR", "MINOR", 1);
            enterValueAndNumberForKey("NO_ALARM", "NO_ALARM", 2);
            enterValueAndNumberForKey("INVALID", "INVALID", 3);
            enterValueAndNumberForKey("4", "NOT DEFINED", 4);
            enterValueAndNumberForKey("FATAL", "FATAL", 5);
            enterValueAndNumberForKey("ERROR", "ERROR", 6);
            enterValueAndNumberForKey("WARN", "WARN", 7);
            enterValueAndNumberForKey("INFO", "INFO", 8);
            enterValueAndNumberForKey("DEBUG", "DEBUG", 9);
        }
        
        private void enterValueAndNumberForKey(@Nonnull String key,
                                               @Nonnull String value,
                                               int number) {
            _severityKeyValueMapping.put(key, value);
            _severityKeyNumberMapping.put(key, number);
        }
        
        @Override
        public String findSeverityValue(@Nonnull final String severityKey) {
            String severityValue = _severityKeyValueMapping.get(severityKey);
            if (severityValue == null) {
                return "invalid severity";
            } else {
                return severityValue;
            }
        }
        
        @Override
        public int getSeverityNumber(@Nonnull final String severityKey) {
            Integer severityNumber = _severityKeyNumberMapping.get(severityKey);
            //if there is no mapping return 10, that means the lowest severity
            if (severityNumber == null) {
                return 10;
            } else {
                return severityNumber;
            }
        }
        
    }
    
}
