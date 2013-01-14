/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.alarm.service.declaration;

import java.util.Map;


import org.csstudio.alarm.service.internal.AlarmMessageDALImpl;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.simple.AnyData;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Test for the dal-based implementation of the alarm message.
 * 
 * @author jpenning
 * @since 06.08.2012
 */
public class AlarmMessageDALImplTest extends IAlarmMessageTest {
    
    @Override
    @Test
    public void testGetStringFromUnitializedMessage() {
        SimpleProperty<?> property = Mockito.mock(SimpleProperty.class);
        AnyData anyData = Mockito.mock(AnyData.class);
        IAlarmMessage objectUnderTest = AlarmMessageDALImpl.newAlarmMessage(property, anyData);
        assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.ACK));
        assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.EVENTTIME));
        assertNull(objectUnderTest.getString(AlarmMessageKey.NAME));
        // Severity has its own test
        assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.STATUS));
        assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.STATUS_OLD));
        assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.HOST_PHYS));
        assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.HOST));
        assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.FACILITY));
        assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.TEXT));
        assertEquals("event", objectUnderTest.getString(AlarmMessageKey.TYPE));
        assertNull(objectUnderTest.getString(AlarmMessageKey.VALUE));
        assertEquals("CSS_AlarmService", objectUnderTest.getString(AlarmMessageKey.APPLICATION_ID));
        assertEquals("", objectUnderTest.getString(AlarmMessageKey.ALARMUSERGROUP));
    }
    
    @Override
    @Test
    public void testGetSeverity() {
        SimpleProperty<?> property = Mockito.mock(SimpleProperty.class);
        AnyData anyData = Mockito.mock(AnyData.class);
        IAlarmMessage objectUnderTest = AlarmMessageDALImpl.newAlarmMessage(property, anyData);
        
        assertEquals(EpicsAlarmSeverity.UNKNOWN, objectUnderTest.getSeverity());
        assertEquals("UNKNOWN", objectUnderTest.getString(AlarmMessageKey.SEVERITY));
        assertEquals("n.a.", objectUnderTest.getString(AlarmMessageKey.SEVERITY_OLD));
        
        // construct a major severity (cannot mock DynamicValueCondition because it is final)
        // DynamicValueCondition condition = Mockito.mock(DynamicValueCondition.class);
        // Mockito.when(condition.isMajor()).thenReturn(true);
        DynamicValueCondition condition = new DynamicValueCondition(DynamicValueState.ALARM);
        Mockito.when(property.getCondition()).thenReturn(condition);
        
        assertEquals(EpicsAlarmSeverity.MAJOR, objectUnderTest.getSeverity());
        assertEquals("MAJOR", objectUnderTest.getString(AlarmMessageKey.SEVERITY));
        
        condition = new DynamicValueCondition(DynamicValueState.WARNING);
        Mockito.when(property.getCondition()).thenReturn(condition);
        assertEquals(EpicsAlarmSeverity.MINOR, objectUnderTest.getSeverity());
        assertEquals("MINOR", objectUnderTest.getString(AlarmMessageKey.SEVERITY));
        
        condition = new DynamicValueCondition(DynamicValueState.NORMAL);
        Mockito.when(property.getCondition()).thenReturn(condition);
        assertEquals(EpicsAlarmSeverity.NO_ALARM, objectUnderTest.getSeverity());
        assertEquals("NO_ALARM", objectUnderTest.getString(AlarmMessageKey.SEVERITY));
        
        condition = new DynamicValueCondition(DynamicValueState.ERROR);
        Mockito.when(property.getCondition()).thenReturn(condition);
        assertEquals(EpicsAlarmSeverity.INVALID, objectUnderTest.getSeverity());
        assertEquals("INVALID", objectUnderTest.getString(AlarmMessageKey.SEVERITY));
    }
    
    @Override
    @Test
    public void testIsAcknowledgement() throws Exception {
        SimpleProperty<?> property = Mockito.mock(SimpleProperty.class);
        AnyData anyData = Mockito.mock(AnyData.class);
        IAlarmMessage objectUnderTest = AlarmMessageDALImpl.newAlarmMessage(property, anyData);

        assertFalse(objectUnderTest.isAcknowledgement());
        
        // currently acknowledgment is not supported via the dal implementation
    }

    @Override
    @Test
    public void testGetMap() throws Exception {
        SimpleProperty<?> property = Mockito.mock(SimpleProperty.class);
        AnyData anyData = Mockito.mock(AnyData.class);
        IAlarmMessage objectUnderTest = AlarmMessageDALImpl.newAlarmMessage(property, anyData);
        
        Map<String, String> map = objectUnderTest.getMap();
        
        // check the count of the keys of the map
        // if uninitialized the alarm user group must not be given, therefore we expect one less than the whole count
        assertEquals("You probably changed the key set of the map.", 14, map.keySet().size());
    }
    
}
