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
import org.csstudio.alarm.treeview.model.ProcessVariableNode;
import org.csstudio.alarm.treeview.model.TreeNodeSource;
import org.csstudio.alarm.treeview.views.AlarmTreeLabelProvider;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
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
        String[] iconNames = _labelProvider.getIconNames(_node.getAlarmSeverity(),
                                                         _node.getUnacknowledgedAlarmSeverity());
        Assert.assertEquals(1, iconNames.length);
        Assert.assertEquals("grey", iconNames[0]);
    }
    
    @Test
    public void testIconNamesNoAlarm() throws Exception {
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.NO_ALARM));
        String[] iconNames = _labelProvider.getIconNames(_node.getAlarmSeverity(),
                                                         _node.getUnacknowledgedAlarmSeverity());
        Assert.assertEquals(1, iconNames.length);
        Assert.assertEquals("green", iconNames[0]);
    }
    
    @Test
    public void testIconNamesError() throws Exception {
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.INVALID));
        String[] iconNames = _labelProvider.getIconNames(_node.getAlarmSeverity(),
                                                         _node.getUnacknowledgedAlarmSeverity());
        Assert.assertEquals(1, iconNames.length);
        Assert.assertEquals("blue", iconNames[0]);
    }
    
    @Test
    public void testIconNamesMinor() throws Exception {
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.MINOR));
        String[] iconNames = _labelProvider.getIconNames(_node.getAlarmSeverity(),
                                                         _node.getUnacknowledgedAlarmSeverity());
        Assert.assertEquals(1, iconNames.length);
        Assert.assertEquals("yellow", iconNames[0]);
    }
    
    @Test
    public void testIconNamesMajor() throws Exception {
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.MAJOR));
        String[] iconNames = _labelProvider.getIconNames(_node.getAlarmSeverity(),
                                                         _node.getUnacknowledgedAlarmSeverity());
        Assert.assertEquals(1, iconNames.length);
        Assert.assertEquals("red", iconNames[0]);
    }
    
    @Test
    public void testIconNamesMinorAfterMajor() throws Exception {
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.MAJOR));
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.MINOR));
        String[] iconNames = _labelProvider.getIconNames(_node.getAlarmSeverity(),
                                                         _node.getUnacknowledgedAlarmSeverity());
        Assert.assertEquals(2, iconNames.length);
        Assert.assertEquals("red", iconNames[0]);
        Assert.assertEquals("yellow", iconNames[1]);
    }
    
    @Test
    public void testIconNamesMajorAcknowledged() throws Exception {
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.MAJOR));
        _node.acknowledgeAlarm();
        String[] iconNames = _labelProvider.getIconNames(_node.getAlarmSeverity(),
                                                         _node.getUnacknowledgedAlarmSeverity());
        Assert.assertEquals(2, iconNames.length);
        Assert.assertEquals("red", iconNames[0]);
        Assert.assertEquals("checked", iconNames[1]);
    }
    
    @Test
    public void testIconNamesMinorAfterMajorAcknowledged() throws Exception {
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.MAJOR));
        _node.updateAlarm(createAlarm(EpicsAlarmSeverity.MINOR));
        _node.acknowledgeAlarm();
        String[] iconNames = _labelProvider.getIconNames(_node.getAlarmSeverity(),
                                                         _node.getUnacknowledgedAlarmSeverity());
        Assert.assertEquals(2, iconNames.length);
        Assert.assertEquals("yellow", iconNames[0]);
        Assert.assertEquals("checked", iconNames[1]);
    }
    
    @Nonnull
    private Alarm createAlarm(@Nonnull final EpicsAlarmSeverity severity) {
        // the date creation ensures useful timestamps
        return new Alarm("test", severity, new Date(++DATE_PARAM));
    }
}
