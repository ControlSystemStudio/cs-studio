package org.csstudio.alarm.treeView.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.junit.Before;
import org.junit.Test;

/**
 * Test
 * 
 * @author jpenning
 * @since 11.01.2011
 */
public class SubtreeNodeUnitTestTest {
    
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
