/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id$
 */
package org.csstudio.alarm.treeview.views;

import java.util.Date;

import javax.annotation.Nonnull;

import org.csstudio.alarm.treeview.model.Alarm;
import org.csstudio.alarm.treeview.model.IAlarmTreeNode;
import org.csstudio.alarm.treeview.model.ProcessVariableNode;
import org.csstudio.alarm.treeview.model.SubtreeNode;
import org.csstudio.alarm.treeview.model.TreeNodeSource;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the label provider. The package scoped method for determination of the icon names is tested, this way we only need a unit test.
 * 
 * @author jpenning
 * @since 11.01.2011
 */
public class AlarmTreeLabelProviderUnitTest {
    
    private static long DATE_PARAM = 0L;
    private AlarmTreeLabelProvider _labelProvider; // object under test
    private ProcessVariableNode _node;
    
	private SubtreeNode _subtreeNode;
	private ProcessVariableNode _node0;
	private ProcessVariableNode _node1;

    @Before
    public void setUp() {
        _labelProvider = new AlarmTreeLabelProvider();
        _node = new ProcessVariableNode.Builder("A node", TreeNodeSource.LDAP).build();
    }
    
    @Test
    public void testTextCreation() throws Exception {
        Assert.assertEquals("A node", _labelProvider.getText(_node));
    }
    
    @Test
    public void testIconNamesAfterCreation() throws Exception {
        checkIconNames(_node, "grey");
    }
    
    @Test
    public void testIconNamesNoAlarm() throws Exception {
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.NO_ALARM));
        checkIconNames(_node, "green");
    }
    
    @Test
    public void testIconNamesError() throws Exception {
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.INVALID));
        checkIconNames(_node, "blue");
    }
    
    @Test
    public void testIconNamesMinor() throws Exception {
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.MINOR));
        checkIconNames(_node, "yellow");
    }
    
    @Test
    public void testIconNamesMajor() throws Exception {
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.MAJOR));
        checkIconNames(_node, "red");
    }
    
    @Test
    public void testIconNamesMinorAfterNoAlarm() throws Exception {
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.NO_ALARM));
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.MINOR));
        checkIconNames(_node, "yellow");
    }
    
    @Test
    public void testIconNamesNoAlarmAfterMinor() throws Exception {
    	_node.updateAlarm(createAlarm(EpicsAlarmSeverity.MINOR));
    	_node.updateAlarm(createAlarm(EpicsAlarmSeverity.NO_ALARM));
    	checkIconNames(_node, "yellow", "green");
    }
    
    @Test
    public void testIconNamesMinorAfterMajor() throws Exception {
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.MAJOR));
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.MINOR));
        checkIconNames(_node, "red", "yellow");
    }
    
    @Test
    public void testIconNamesMajorAcknowledged() throws Exception {
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.MAJOR));
        _node.acknowledgeAlarm();
        checkIconNames(_node, "red", "checked");
    }
    
    @Test
    public void testIconNamesMinorAfterMajorAcknowledged() throws Exception {
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.MAJOR));
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.MINOR));
        _node.acknowledgeAlarm();
    	checkIconNames(_node, "yellow", "checked");
    }
    
    @Test
	public void testIconNamesAggregatedNoAlarm() throws Exception {
		createNodes();

		_node0.updateAlarm(createAlarm(EpicsAlarmSeverity.NO_ALARM));
		_node1.updateAlarm(createAlarm(EpicsAlarmSeverity.NO_ALARM));
		checkIconNames(_subtreeNode, "green");
	}

    @Test
    public void testIconNamesAggregatedNoAlarmAcknowledged() throws Exception {
    	createNodes();
    	
    	_node0.updateAlarm(createAlarm(EpicsAlarmSeverity.NO_ALARM));
    	_node1.updateAlarm(createAlarm(EpicsAlarmSeverity.NO_ALARM));
    	_node0.acknowledgeAlarm();
    	checkIconNames(_subtreeNode, "green");
    }
    
    @Test
    public void testIconNamesAggregatedMinorMajor() throws Exception {
    	createNodes();
    	
    	_node0.updateAlarm(createAlarm(EpicsAlarmSeverity.MINOR));
    	_node1.updateAlarm(createAlarm(EpicsAlarmSeverity.MAJOR));
    	checkIconNames(_subtreeNode, "red");
    }
    
    @Test
    public void testIconNamesAggregatedMinorNoAlarm() throws Exception {
    	createNodes();
    	
    	_node0.updateAlarm(createAlarm(EpicsAlarmSeverity.MINOR));
    	_node1.updateAlarm(createAlarm(EpicsAlarmSeverity.NO_ALARM));
    	checkIconNames(_subtreeNode, "yellow");
    }
    
    @Test
    public void testIconNamesAggregatedMinorNoAlarmAcknowledged() throws Exception {
    	createNodes();
    	
    	_node0.updateAlarm(createAlarm(EpicsAlarmSeverity.MINOR));
    	_node1.updateAlarm(createAlarm(EpicsAlarmSeverity.NO_ALARM));
    	_node0.acknowledgeAlarm();
    	checkIconNames(_subtreeNode, "yellow", "checked");
    }
    
	private void checkIconNames(@Nonnull final IAlarmTreeNode node, @Nonnull final String ...strings ) {
		String[] iconNames = _labelProvider.getIconNames(
				node.getAlarmSeverity(),
				node.getUnacknowledgedAlarmSeverity());
		
		Assert.assertEquals(strings.length, iconNames.length);
		for (int i = 0; i < strings.length; i++) {
			Assert.assertEquals(strings[i], iconNames[i]);
		}
	}
    
	private void createNodes() {
		_subtreeNode = new SubtreeNode.Builder("SubTree", LdapEpicsAlarmcfgConfiguration.COMPONENT,
				TreeNodeSource.LDAP).build();
		_node0 = new ProcessVariableNode.Builder("node 0", TreeNodeSource.LDAP).
				setParent(_subtreeNode).build();
		_node1 = new ProcessVariableNode.Builder("node 1", TreeNodeSource.LDAP).
				setParent(_subtreeNode).build();
	}
    
    @Nonnull
    private Alarm createAlarm(@Nonnull final EpicsAlarmSeverity severity) {
        // the date creation ensures useful timestamps
        return new Alarm("test", severity, new Date(++DATE_PARAM));
    }
}
