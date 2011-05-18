/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 */
package org.csstudio.alarm.treeview.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Date;

import javax.annotation.Nonnull;

import org.csstudio.alarm.treeView.model.Alarm;
import org.csstudio.alarm.treeView.model.IProcessVariableNodeListener;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.model.TreeNodeSource;
import org.csstudio.alarm.treeView.model.ProcessVariableNode.Builder;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.junit.Before;
import org.junit.Test;

/**
 * Test.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 14.06.2010
 */
public class ProcessVariableNodeUnitTest {

    private ProcessVariableNode _node;
    private SubtreeNode _subtreeNode;
    private final Date t1 = new Date(1L);
    private final Date t2 = new Date(2L);

    @Before
    public void setUp() {
        _subtreeNode = new SubtreeNode.Builder("SubTree", LdapEpicsAlarmcfgConfiguration.UNIT, TreeNodeSource.LDAP).build();
        _node = new ProcessVariableNode.Builder("A node", TreeNodeSource.LDAP).setParent(_subtreeNode).build();
    }

    @Test
    public void testGetters() {
        assertEquals("A node", _node.getName());
        assertSame(_subtreeNode, _node.getParent());
        assertEquals(LdapEpicsAlarmcfgConfiguration.RECORD, _node.getTreeNodeConfiguration());
        assertEquals(IProcessVariable.TYPE_ID, _node.getTypeId());
        assertEquals("A node", _node.toString());
    }

    @Test
    public void testNewlyCreatedNodeHasNoAlarm() {
        assertEquals(EpicsAlarmSeverity.UNKNOWN, _node.getAlarmSeverity());
        assertEquals(EpicsAlarmSeverity.UNKNOWN, _node.getUnacknowledgedAlarmSeverity());
    }

    @Test
	public void testAlarmIncreasesSeverityAndUnacknowledgedSeverity() throws Exception {
		_node.updateAlarm(new Alarm("", EpicsAlarmSeverity.MINOR, t1));
        assertEquals(EpicsAlarmSeverity.MINOR, _node.getAlarmSeverity());
        assertEquals(EpicsAlarmSeverity.MINOR, _node.getUnacknowledgedAlarmSeverity());
	}

    @Test
	public void testMajorAlarmIncreasesSeverityAfterMinorAlarm() throws Exception {
		_node.updateAlarm(new Alarm("", EpicsAlarmSeverity.MINOR, t1));
		_node.updateAlarm(new Alarm("", EpicsAlarmSeverity.MAJOR, t2));
        assertEquals(EpicsAlarmSeverity.MAJOR, _node.getAlarmSeverity());
        assertEquals(EpicsAlarmSeverity.MAJOR, _node.getUnacknowledgedAlarmSeverity());
	}

    @Test
	public void testMinorAfterMajorLowersSeverityButKeepsUnacknowledgedSeverity() throws Exception {
		_node.updateAlarm(new Alarm("", EpicsAlarmSeverity.MAJOR, t1));
		_node.updateAlarm(new Alarm("", EpicsAlarmSeverity.MINOR, t2));
        assertEquals(EpicsAlarmSeverity.MINOR, _node.getAlarmSeverity());
        assertEquals(EpicsAlarmSeverity.MAJOR, _node.getUnacknowledgedAlarmSeverity());
	}

    @Test
	public void testNoAlarmAfterAlarmKeepsUnacknowledged() throws Exception {
		_node.updateAlarm(new Alarm("", EpicsAlarmSeverity.MAJOR, t1));
		_node.updateAlarm(new Alarm("", EpicsAlarmSeverity.NO_ALARM, t2));
        assertEquals(EpicsAlarmSeverity.NO_ALARM, _node.getAlarmSeverity());
        assertEquals(EpicsAlarmSeverity.MAJOR, _node.getUnacknowledgedAlarmSeverity());
	}

    @Test
	public void testAcknowledgeAlarm() throws Exception {
		_node.updateAlarm(new Alarm("", EpicsAlarmSeverity.MAJOR, t1));
		_node.acknowledgeAlarm();
        assertEquals(EpicsAlarmSeverity.MAJOR, _node.getAlarmSeverity());
        assertEquals(EpicsAlarmSeverity.UNKNOWN, _node.getUnacknowledgedAlarmSeverity());
	}

    @Test
	public void testPropertiesAreNullByDefault() throws Exception {
		for (final EpicsAlarmcfgTreeNodeAttribute id : EpicsAlarmcfgTreeNodeAttribute.values()) {
			assertNull(_node.getInheritedProperty(id));
		}
	}

    @Test
    public void testPropertyInheritance() throws Exception {
        _subtreeNode.setProperty(EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY, "foo");
        assertEquals("foo", _node.getInheritedProperty(EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY));
        assertNull(_node.getOwnProperty(EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY));
    }

    @Test
    public void testPropertyWithUrlProtocol() throws Exception {
        _subtreeNode.setProperty(EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY, "foo");
        assertEquals("file:foo", _node.getInheritedPropertyWithUrlProtocol(EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY));

        _subtreeNode.setProperty(EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY, "/foo");
        assertEquals("file:/foo", _node.getInheritedPropertyWithUrlProtocol(EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY));

        _subtreeNode.setProperty(EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY, "http:foo");
        assertEquals("http:foo", _node.getInheritedPropertyWithUrlProtocol(EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY));

        _subtreeNode.setProperty(EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY, "xttp:foo");
        assertEquals("xttp:foo", _node.getInheritedPropertyWithUrlProtocol(EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY));
    }

    @Test
    public void testProcessVariableNodeListener() throws Exception {
        final StringBuilder wasAddedString = new StringBuilder();
        final StringBuilder wasRemovedString = new StringBuilder();

        final SubtreeNode subtreeNode = new SubtreeNode.Builder("SubTree",
                                                          LdapEpicsAlarmcfgConfiguration.UNIT,
                                                          TreeNodeSource.XML).build();
        final ProcessVariableNode node = new ProcessVariableNode.Builder("A node", TreeNodeSource.XML)
                .setParent(subtreeNode).setListener(new IProcessVariableNodeListener() {

                    @Override
                    public void wasAdded(@Nonnull final String newName) {
                        wasAddedString.append(newName);
                    }

                    @Override
                    public void wasRemoved(@Nonnull final String newName) {
                        wasRemovedString.append(newName);
                    }
                }).build();
        assertEquals("A node", wasAddedString.toString());

        subtreeNode.removeChild(node);
        assertEquals("A node", wasRemovedString.toString());
    }


}
