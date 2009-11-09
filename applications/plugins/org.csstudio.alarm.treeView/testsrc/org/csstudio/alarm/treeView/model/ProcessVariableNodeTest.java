package org.csstudio.alarm.treeView.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.csstudio.platform.model.IProcessVariable;
import org.junit.Before;
import org.junit.Test;

public class ProcessVariableNodeTest {

    private ProcessVariableNode _node;
    private SubtreeNode _subtreeNode;

    @Before
    public void setUp() {
        _subtreeNode = new SubtreeNode("SubTree");
        _node = new ProcessVariableNode(_subtreeNode, "A node");
    }

    @Test
    public void testGetters() throws InterruptedException {
        assertEquals("A node", _node.getName());
        assertSame(_subtreeNode, _node.getParent());
        assertEquals(ObjectClass.RECORD, _node.getObjectClass());
        assertEquals(IProcessVariable.TYPE_ID, _node.getTypeId());
        assertEquals("A node", _node.toString());
    }

    @Test
    public void testNewlyCreatedNodeHasNoAlarm() {
        assertEquals(Severity.NO_ALARM, _node.getAlarmSeverity());
        assertEquals(Severity.NO_ALARM, _node.getUnacknowledgedAlarmSeverity());
        assertFalse(_node.hasAlarm());
    }
    
    @Test
	public void testAlarmIncreasesSeverityAndUnacknowledgedSeverity() throws Exception {
		_node.setActiveAlarm(new Alarm("", Severity.MINOR));
        assertEquals(Severity.MINOR, _node.getAlarmSeverity());
        assertEquals(Severity.MINOR, _node.getUnacknowledgedAlarmSeverity());
        assertTrue(_node.hasAlarm());
	}
    
    @Test
	public void testMajorAlarmIncreasesSeverityAfterMinorAlarm() throws Exception {
		_node.setActiveAlarm(new Alarm("", Severity.MINOR));
		_node.setActiveAlarm(new Alarm("", Severity.MAJOR));
        assertEquals(Severity.MAJOR, _node.getAlarmSeverity());
        assertEquals(Severity.MAJOR, _node.getUnacknowledgedAlarmSeverity());
        assertTrue(_node.hasAlarm());
	}
    
    @Test
	public void testMinorAfterMajorLowersSeverityButKeepsUnacknowledgedSeverity() throws Exception {
		_node.setActiveAlarm(new Alarm("", Severity.MAJOR));
		_node.setActiveAlarm(new Alarm("", Severity.MINOR));
        assertEquals(Severity.MINOR, _node.getAlarmSeverity());
        assertEquals(Severity.MAJOR, _node.getUnacknowledgedAlarmSeverity());
	}
    
    @Test
	public void testCancelAlarmSetsAlarmToNoAlarmButKeepsUnacknowledged() throws Exception {
		_node.setActiveAlarm(new Alarm("", Severity.MAJOR));
		_node.cancelAlarm();
        assertEquals(Severity.NO_ALARM, _node.getAlarmSeverity());
        assertEquals(Severity.MAJOR, _node.getUnacknowledgedAlarmSeverity());
        assertTrue(_node.hasAlarm()); // XXX: This is unexpected behavior
	}
    
    @Test
	public void testAcknowledgeAlarm() throws Exception {
		_node.setActiveAlarm(new Alarm("", Severity.MAJOR));
		_node.removeHighestUnacknowledgedAlarm();
        assertEquals(Severity.MAJOR, _node.getAlarmSeverity());
        assertEquals(Severity.NO_ALARM, _node.getUnacknowledgedAlarmSeverity());
        assertTrue(_node.hasAlarm());
	}

    @Test(expected=NullPointerException.class)
    public void invalidConstructorNameArgument() {
        new ProcessVariableNode(_subtreeNode, null);
    }

    @Test(expected=NullPointerException.class)
    public void invalidConstructorSubtreeNodeArgument() {
        new ProcessVariableNode(null, "A node");
    }
    
}
