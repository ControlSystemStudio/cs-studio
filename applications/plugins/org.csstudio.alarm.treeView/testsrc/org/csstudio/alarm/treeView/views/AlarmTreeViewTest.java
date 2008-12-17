package org.csstudio.alarm.treeView.views;

import static org.junit.Assert.*;

import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.junit.Test;
import org.mockito.Mockito;

public class AlarmTreeViewTest {

    @Test
    public void testHasHelpGuidance() {
        AlarmTreeView view = new AlarmTreeView();
        
        IAlarmTreeNode node = Mockito.mock(IAlarmTreeNode.class);
        Mockito.when(node.getHelpGuidance()).thenReturn("Hallo, das ist die Hilfe");
        
        assertTrue(view.hasHelpGuidance((Object)node));
        
        Mockito.verify(node, Mockito.times(1)).getHelpGuidance();
        Mockito.verifyNoMoreInteractions(node);
    }
    

}
