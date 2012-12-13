package org.csstudio.alarm.treeview.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Date;

import org.csstudio.alarm.treeview.model.IProcessVariableNodeListener;
import org.csstudio.alarm.treeview.model.ProcessVariableNode;
import org.csstudio.alarm.treeview.model.SubtreeNode;
import org.csstudio.alarm.treeview.model.TreeNodeSource;
import org.csstudio.alarm.treeview.model.ProcessVariableNode.Builder;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test
 * 
 * @author jpenning
 * @since 11.01.2011
 */
public class SubtreeNodeUnitTest {
    private final Date date1 = new Date(1L);
    private final Date date2 = new Date(2L);
    private final Date date3 = new Date(3L);
    private final Date date4 = new Date(4L);

    private ProcessVariableNode _node0;
    private ProcessVariableNode _node1;
    private SubtreeNode _subtreeNode;
    
    @Before
    public void setUp() {
        _subtreeNode = new SubtreeNode.Builder("SubTree",
                                               LdapEpicsAlarmcfgConfiguration.COMPONENT,
                                               TreeNodeSource.LDAP).build();
        _node0 = new ProcessVariableNode.Builder("node 0", TreeNodeSource.LDAP).build();
        _node1 = new ProcessVariableNode.Builder("node 1", TreeNodeSource.LDAP).build();
    }

    
    @Test
    public void testAlarmAggregationOfUnknown() {
    	_subtreeNode.addChild(_node0);
    	assertEquals(EpicsAlarmSeverity.UNKNOWN, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.UNKNOWN, _subtreeNode.getUnacknowledgedAlarmSeverity());

    	_subtreeNode.addChild(_node1);
    	assertEquals(EpicsAlarmSeverity.UNKNOWN, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.UNKNOWN, _subtreeNode.getUnacknowledgedAlarmSeverity());
    }

    @Test
    public void testAlarmAggregationOfNormal() {
    	_subtreeNode.addChild(_node0);

    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.NO_ALARM, date1));
    	assertEquals(EpicsAlarmSeverity.NO_ALARM, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.NO_ALARM, _subtreeNode.getUnacknowledgedAlarmSeverity());
    	
    }
    
    @Test
    public void testAlarmAggregationOfMinor() {
    	_subtreeNode.addChild(_node0);
    	
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.MINOR, date1));
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getUnacknowledgedAlarmSeverity());
    	
    }
    
    @Test
    public void testAlarmAggregationOfMajor() {
    	_subtreeNode.addChild(_node0);
    	
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.MAJOR, date1));
    	assertEquals(EpicsAlarmSeverity.MAJOR, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.MAJOR, _subtreeNode.getUnacknowledgedAlarmSeverity());
    }
    
    @Test
    public void testAlarmAggregationMinorAfterMajor() {
        _subtreeNode.addChild(_node0);

    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.MAJOR, date3));
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.MINOR, date4));
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.MAJOR, _subtreeNode.getUnacknowledgedAlarmSeverity());
    }
    
    @Test
    public void testAlarmAggregationNormalAfterMinor() {
    	_subtreeNode.addChild(_node0);
    	
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.MINOR, date3));
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.NO_ALARM, date4));
    	assertEquals(EpicsAlarmSeverity.NO_ALARM, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getUnacknowledgedAlarmSeverity());
    }
    
    @Test
    public void testAlarmAggregationNormalAfterMajor() {
    	_subtreeNode.addChild(_node0);
    	
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.MAJOR, date3));
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.NO_ALARM, date4));
    	assertEquals(EpicsAlarmSeverity.NO_ALARM, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.MAJOR, _subtreeNode.getUnacknowledgedAlarmSeverity());
    }
    
    @Test
    public void testAlarmAggregationFromTwoChildrenOneStaysNormal() {
    	_subtreeNode.addChild(_node0);
    	_subtreeNode.addChild(_node1);
    	
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.NO_ALARM, date1));
    	_node1.updateAlarm(new Alarm("", EpicsAlarmSeverity.NO_ALARM, date1));
    	assertEquals(EpicsAlarmSeverity.NO_ALARM, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.NO_ALARM, _subtreeNode.getUnacknowledgedAlarmSeverity());
    	
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.MINOR, date2));
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getUnacknowledgedAlarmSeverity());
    	
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.MAJOR, date3));
    	assertEquals(EpicsAlarmSeverity.MAJOR, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.MAJOR, _subtreeNode.getUnacknowledgedAlarmSeverity());
    	
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.MINOR, date4));
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.MAJOR, _subtreeNode.getUnacknowledgedAlarmSeverity());
    }
    
    @Test
    public void testAlarmAggregationFromTwoChildrenOneStaysMinor() {
    	_subtreeNode.addChild(_node0);
    	_subtreeNode.addChild(_node1);
    	
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.NO_ALARM, date1));
    	_node1.updateAlarm(new Alarm("", EpicsAlarmSeverity.MINOR, date1));
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getUnacknowledgedAlarmSeverity());
    	
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.MINOR, date2));
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getUnacknowledgedAlarmSeverity());
    	
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.MAJOR, date3));
    	assertEquals(EpicsAlarmSeverity.MAJOR, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.MAJOR, _subtreeNode.getUnacknowledgedAlarmSeverity());
    	
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.MINOR, date4));
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.MAJOR, _subtreeNode.getUnacknowledgedAlarmSeverity());

    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.NO_ALARM, date1));
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.MAJOR, _subtreeNode.getUnacknowledgedAlarmSeverity());
    }
    
    @Test
    public void testAlarmAggregationAndAcknowledgeOneChild() {
    	_subtreeNode.addChild(_node0);

    	_node0.acknowledgeAlarm();
    	assertEquals(EpicsAlarmSeverity.UNKNOWN, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.UNKNOWN, _subtreeNode.getUnacknowledgedAlarmSeverity());

    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.NO_ALARM, date1));
    	assertEquals(EpicsAlarmSeverity.NO_ALARM, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.NO_ALARM, _subtreeNode.getUnacknowledgedAlarmSeverity());

    	_node0.acknowledgeAlarm();
    	assertEquals(EpicsAlarmSeverity.NO_ALARM, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.UNKNOWN, _subtreeNode.getUnacknowledgedAlarmSeverity());
    	
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.MINOR, date2));
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getUnacknowledgedAlarmSeverity());
    	
    	_node0.acknowledgeAlarm();
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.UNKNOWN, _subtreeNode.getUnacknowledgedAlarmSeverity());
    }
    
    @Test
    public void testAlarmAggregationAndAcknowledgeTwoChildrenOneStaysNoAlarm() {
    	_subtreeNode.addChild(_node0);
    	_subtreeNode.addChild(_node1);
    	
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.NO_ALARM, date1));
    	_node1.updateAlarm(new Alarm("", EpicsAlarmSeverity.NO_ALARM, date1));
    	assertEquals(EpicsAlarmSeverity.NO_ALARM, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.NO_ALARM, _subtreeNode.getUnacknowledgedAlarmSeverity());
    	
    	_node0.acknowledgeAlarm();
    	assertEquals(EpicsAlarmSeverity.NO_ALARM, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.UNKNOWN, _subtreeNode.getUnacknowledgedAlarmSeverity());
    	
    	_node0.updateAlarm(new Alarm("", EpicsAlarmSeverity.MINOR, date2));
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getUnacknowledgedAlarmSeverity());
    	
    	_node0.acknowledgeAlarm();
    	assertEquals(EpicsAlarmSeverity.MINOR, _subtreeNode.getAlarmSeverity());
    	assertEquals(EpicsAlarmSeverity.UNKNOWN, _subtreeNode.getUnacknowledgedAlarmSeverity());
    }
    
    @Test
    public void testAdd() throws Exception {
        // ensure add and its precondition
        Assert.assertTrue(_subtreeNode.canAddChild(_node0.getName()));
        Assert.assertTrue(_subtreeNode.addChild(_node0));
        // cannot be added again
        Assert.assertFalse(_subtreeNode.canAddChild(_node0.getName()));
        Assert.assertFalse(_subtreeNode.addChild(_node0));
    }
    
    @Test
    public void testProcessVariableListener() throws Exception {
        // ensures that the wasAdded / wasRemoved callbacks are called
        
        IProcessVariableNodeListener mockListener = mock(IProcessVariableNodeListener.class);
        _node0.setListener(mockListener);
        _node1.setListener(mockListener);
        
        _subtreeNode.addChild(_node0);
        verify(mockListener).wasAdded("node 0");
        
        _subtreeNode.addChild(_node1);
        verify(mockListener).wasAdded("node 1");
        
        _subtreeNode.removeChild(_node1);
        verify(mockListener).wasRemoved("node 1");
        
        _subtreeNode.clearChildren();
        verify(mockListener).wasRemoved("node 0");
    }
}
