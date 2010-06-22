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
package org.csstudio.alarm.treeView.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.csstudio.alarm.service.declaration.AlarmTreeNodePropertyId;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.alarm.service.declaration.Severity;
import org.csstudio.platform.model.IProcessVariable;
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
public class ProcessVariableNodeTest {

    private ProcessVariableNode _node;
    private SubtreeNode _subtreeNode;
    private final Date t1 = new Date(0);
    private final Date t2 = new Date(1);

    @Before
    public void setUp() {
        _subtreeNode = new SubtreeNode.Builder("SubTree", LdapEpicsAlarmcfgConfiguration.ROOT, TreeNodeSource.LDAP).build();
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
        assertEquals(Severity.NO_ALARM, _node.getAlarmSeverity());
        assertEquals(Severity.NO_ALARM, _node.getUnacknowledgedAlarmSeverity());
        assertFalse(_node.hasAlarm());
    }

    @Test
	public void testAlarmIncreasesSeverityAndUnacknowledgedSeverity() throws Exception {
		_node.updateAlarm(new Alarm("", Severity.MINOR, t1));
        assertEquals(Severity.MINOR, _node.getAlarmSeverity());
        assertEquals(Severity.MINOR, _node.getUnacknowledgedAlarmSeverity());
        assertTrue(_node.hasAlarm());
	}

    @Test
	public void testMajorAlarmIncreasesSeverityAfterMinorAlarm() throws Exception {
		_node.updateAlarm(new Alarm("", Severity.MINOR, t1));
		_node.updateAlarm(new Alarm("", Severity.MAJOR, t2));
        assertEquals(Severity.MAJOR, _node.getAlarmSeverity());
        assertEquals(Severity.MAJOR, _node.getUnacknowledgedAlarmSeverity());
        assertTrue(_node.hasAlarm());
	}

    @Test
	public void testMinorAfterMajorLowersSeverityButKeepsUnacknowledgedSeverity() throws Exception {
		_node.updateAlarm(new Alarm("", Severity.MAJOR, t1));
		_node.updateAlarm(new Alarm("", Severity.MINOR, t2));
        assertEquals(Severity.MINOR, _node.getAlarmSeverity());
        assertEquals(Severity.MAJOR, _node.getUnacknowledgedAlarmSeverity());
	}

    @Test
	public void testNoAlarmAfterAlarmKeepsUnacknowledged() throws Exception {
		_node.updateAlarm(new Alarm("", Severity.MAJOR, t1));
		_node.updateAlarm(new Alarm("", Severity.NO_ALARM, t2));
        assertEquals(Severity.NO_ALARM, _node.getAlarmSeverity());
        assertEquals(Severity.MAJOR, _node.getUnacknowledgedAlarmSeverity());
        assertTrue(_node.hasAlarm()); // XXX: This is unexpected behavior
	}

    @Test
	public void testAcknowledgeAlarm() throws Exception {
		_node.updateAlarm(new Alarm("", Severity.MAJOR, t1));
		_node.removeHighestUnacknowledgedAlarm();
        assertEquals(Severity.MAJOR, _node.getAlarmSeverity());
        assertEquals(Severity.NO_ALARM, _node.getUnacknowledgedAlarmSeverity());
        assertTrue(_node.hasAlarm());
	}

    @Test
	public void testPropertiesAreNullByDefault() throws Exception {
		for (final AlarmTreeNodePropertyId id : AlarmTreeNodePropertyId.values()) {
			assertNull(_node.getProperty(id));
		}
	}

//    @Test
//	public void testPropertyGettersAndSetters() throws Exception {
//		_node.setCssAlarmDisplay("alarmdisplay");
//		assertEquals("alarmdisplay", _node.getCssAlarmDisplay());
//		_node.setCssDisplay("display");
//		assertEquals("display", _node.getCssDisplay());
//		_node.setCssStripChart("stripchart");
//		assertEquals("stripchart", _node.getCssStripChart());
//		_node.setHelpGuidance("helpguidance");
//		assertEquals("helpguidance", _node.getHelpGuidance());
//		_node.setHelpPage(new URL("http://example.com/helppage"));
//		assertEquals("http://example.com/helppage", _node.getHelpPage().toString());
//	}

//    @Test
//	public void testPropertiesAreInheritedFromParentNode() throws Exception {
//		_subtreeNode.setCssAlarmDisplay("alarmdisplay");
//		assertEquals("alarmdisplay", _node.getCssAlarmDisplay());
//		_subtreeNode.setCssDisplay("display");
//		assertEquals("display", _node.getCssDisplay());
//		_subtreeNode.setCssStripChart("stripchart");
//		assertEquals("stripchart", _node.getCssStripChart());
//		_subtreeNode.setHelpGuidance("helpguidance");
//		assertEquals("helpguidance", _node.getHelpGuidance());
//		_subtreeNode.setHelpPage(new URL("http://example.com/helppage"));
//		assertEquals("http://example.com/helppage", _node.getHelpPage().toString());
//	}

    @Test
	public void testPropertyInheritance() throws Exception {
		_subtreeNode.setProperty(AlarmTreeNodePropertyId.CSS_DISPLAY, "foo");
		assertEquals("foo", _node.getProperty(AlarmTreeNodePropertyId.CSS_DISPLAY));
		assertNull(_node.getOwnProperty(AlarmTreeNodePropertyId.CSS_DISPLAY));
	}

}
