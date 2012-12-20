package org.csstudio.alarm.treeview.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.csstudio.alarm.treeview.model.IProcessVariableNodeListener;
import org.csstudio.alarm.treeview.model.ProcessVariableNode;
import org.csstudio.alarm.treeview.model.SubtreeNode;
import org.csstudio.alarm.treeview.model.TreeNodeSource;
import org.csstudio.alarm.treeview.model.ProcessVariableNode.Builder;
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
