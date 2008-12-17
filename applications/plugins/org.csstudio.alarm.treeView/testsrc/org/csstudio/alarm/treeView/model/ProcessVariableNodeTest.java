package org.csstudio.alarm.treeView.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.csstudio.platform.model.IProcessVariable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProcessVariableNodeTest {

    private ProcessVariableNode _out;
    private SubtreeNode _subtreeNode;

    @Before
    public void setUp() {
        _subtreeNode = new SubtreeNode("SubTree");
        _out = new ProcessVariableNode(_subtreeNode, "A node");
    }

    @After
    public void tearDown() {
        _out = null;
        _subtreeNode = null;
    }

    @Test(expected=NullPointerException.class)
    public void invalidConstructorNameArgument() {
        new ProcessVariableNode(_subtreeNode, null);
    }

    @Test(expected=NullPointerException.class)
    public void invalidConstructorSubtreeNodeArgument() {
        new ProcessVariableNode(null, "A node");
    }
    
    @Test
    public void unacknowledgedAlarms() {
        assertEquals(Severity.NO_ALARM, _out.getUnacknowledgedAlarmSeverity());
        
        Alarm major = new Alarm("Pumpe 5", Severity.MAJOR);
        Alarm minor = new Alarm("Pumpe 5", Severity.MINOR);

        assertFalse(_out.hasAlarm());
        
        _out.setActiveAlarm(minor);
        
        assertEquals(Severity.MINOR, _out.getAlarmSeverity());
        assertEquals(Severity.MINOR, _out.getUnacknowledgedAlarmSeverity());
        assertTrue(_out.hasAlarm());
        
        _out.setActiveAlarm(major);
        
        assertEquals(Severity.MAJOR, _out.getAlarmSeverity());
        assertEquals(Severity.MAJOR, _out.getUnacknowledgedAlarmSeverity());
        assertTrue(_out.hasAlarm());
        
        _out.setHighestUnacknowledgedAlarm(null);
        
        assertEquals(Severity.MAJOR, _out.getAlarmSeverity());
        assertEquals(Severity.NO_ALARM, _out.getUnacknowledgedAlarmSeverity());
        assertTrue(_out.hasAlarm());
        
        _out.setActiveAlarm(null);

        assertEquals(Severity.NO_ALARM, _out.getAlarmSeverity());
        assertEquals(Severity.NO_ALARM, _out.getUnacknowledgedAlarmSeverity());
        assertFalse(_out.hasAlarm());
    }
    
    @Test
    public void testConstructor() throws InterruptedException {
        assertEquals("A node", _out.getName());
        assertSame(_subtreeNode, _out.getParent());
    }

    @Test
    public void testGetObjectClass() {
        assertEquals(ObjectClass.RECORD, _out.getObjectClass());
    }

    @Test
    public void testGetName() {
        assertEquals("A node", _out.getName());
    }

    @Test
    public void testGetParent() {
        assertSame(_subtreeNode, _out.getParent());
    }

    @Test
    public void testGetTypeId() {
        assertEquals(IProcessVariable.TYPE_ID, _out.getTypeId());
    }

    @Test
    public void testGetAlarmSeverity() {
        assertEquals(Severity.NO_ALARM, _out.getAlarmSeverity());
    }

    @Test
    public void testGetUnacknowledgedAlarmSeverity() {
        assertEquals(Severity.NO_ALARM, _out.getUnacknowledgedAlarmSeverity());
    }

    @Test
    public void testHasAlarm() {
        assertFalse(_out.hasAlarm());
    }

    @Test
    public void testSetActiveAlarm() {
        assertEquals(Severity.NO_ALARM, _out.getAlarmSeverity());
       
        Alarm alarm = new Alarm("Pumpe 5", Severity.MAJOR);
        _out.setActiveAlarm(alarm);

        assertEquals(Severity.MAJOR, _out.getAlarmSeverity());

        Alarm alarm2 = new Alarm("Pumpe 5", Severity.INVALID);
        _out.setActiveAlarm(alarm2);
        
        assertEquals(Severity.INVALID, _out.getAlarmSeverity());
    }

    @Test
    public void testSetHighestUnacknowledgedAlarm() {
        Alarm alarm = new Alarm("Pumpe 5", Severity.MAJOR);
        
        _out.setHighestUnacknowledgedAlarm(alarm);
        
        assertEquals(Severity.MAJOR, _out.getUnacknowledgedAlarmSeverity());
    }

    @Test
    public void testCancelAlarm() {
        Alarm alarm = new Alarm("Pumpe 5", Severity.MAJOR);
        
        _out.setActiveAlarm(alarm);
        
        assertEquals(Severity.MAJOR, _out.getAlarmSeverity());
        
        _out.cancelAlarm();
        
        assertEquals(Severity.NO_ALARM, _out.getAlarmSeverity());
    }

    @Test
    public void testRemoveHighestUnacknowledgedAlarm() {
        Alarm alarm = new Alarm("Pumpe 5", Severity.MAJOR);
        
        _out.setHighestUnacknowledgedAlarm(alarm);
        
        assertEquals(Severity.MAJOR, _out.getUnacknowledgedAlarmSeverity());
        
        _out.removeHighestUnacknowledgedAlarm();
        
        assertEquals(Severity.NO_ALARM, _out.getUnacknowledgedAlarmSeverity());
    }

    @Test
    public void testToString() {
        assertEquals("A node", _out.toString());
    }

}
