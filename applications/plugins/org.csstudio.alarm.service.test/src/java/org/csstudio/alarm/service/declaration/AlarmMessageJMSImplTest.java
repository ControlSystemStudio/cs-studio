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

import java.util.Enumeration;
import java.util.Map;

import javax.jms.MapMessage;

import org.csstudio.alarm.service.internal.AlarmMessageJMSImpl;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Test for the jms-based implementation of the alarm message.
 * 
 * @author jpenning
 * @since 06.08.2012
 */
public class AlarmMessageJMSImplTest extends IAlarmMessageTest {
    
    @Override
    @Test
    public void testGetStringFromUnitializedMessage() {
        MapMessage message = Mockito.mock(MapMessage.class);
        IAlarmMessage objectUnderTest = AlarmMessageJMSImpl.newAlarmMessage(message);
        assertNull(objectUnderTest.getString(AlarmMessageKey.ACK));
        assertNull(objectUnderTest.getString(AlarmMessageKey.EVENTTIME));
        assertNull(objectUnderTest.getString(AlarmMessageKey.NAME));
        // severity has its own test
        assertNull(objectUnderTest.getString(AlarmMessageKey.STATUS));
        assertNull(objectUnderTest.getString(AlarmMessageKey.STATUS_OLD));
        assertNull(objectUnderTest.getString(AlarmMessageKey.HOST_PHYS));
        assertNull(objectUnderTest.getString(AlarmMessageKey.HOST));
        assertNull(objectUnderTest.getString(AlarmMessageKey.FACILITY));
        assertNull(objectUnderTest.getString(AlarmMessageKey.TEXT));
        assertNull(objectUnderTest.getString(AlarmMessageKey.TYPE));
        assertNull(objectUnderTest.getString(AlarmMessageKey.VALUE));
        assertNull(objectUnderTest.getString(AlarmMessageKey.APPLICATION_ID));
        assertNull(objectUnderTest.getString(AlarmMessageKey.ALARMUSERGROUP));
    }
    
    @Override
    @Test
    public void testGetSeverity() throws Exception {
        MapMessage message = Mockito.mock(MapMessage.class);
        IAlarmMessage objectUnderTest = AlarmMessageJMSImpl.newAlarmMessage(message);
        
        assertEquals(EpicsAlarmSeverity.UNKNOWN, objectUnderTest.getSeverity());
        assertNull(objectUnderTest.getString(AlarmMessageKey.SEVERITY));
        assertNull(objectUnderTest.getString(AlarmMessageKey.SEVERITY_OLD));
        
        Mockito.when(message.getString(AlarmMessageKey.SEVERITY.getDefiningName()))
                .thenReturn("MAJOR");
        assertEquals(EpicsAlarmSeverity.MAJOR, objectUnderTest.getSeverity());
        assertEquals("MAJOR", objectUnderTest.getString(AlarmMessageKey.SEVERITY));
        
        Mockito.when(message.getString(AlarmMessageKey.SEVERITY.getDefiningName()))
                .thenReturn("MINOR");
        assertEquals(EpicsAlarmSeverity.MINOR, objectUnderTest.getSeverity());
        assertEquals("MINOR", objectUnderTest.getString(AlarmMessageKey.SEVERITY));
        
        Mockito.when(message.getString(AlarmMessageKey.SEVERITY.getDefiningName()))
                .thenReturn("NO_ALARM");
        assertEquals(EpicsAlarmSeverity.NO_ALARM, objectUnderTest.getSeverity());
        assertEquals("NO_ALARM", objectUnderTest.getString(AlarmMessageKey.SEVERITY));
        
        Mockito.when(message.getString(AlarmMessageKey.SEVERITY.getDefiningName()))
                .thenReturn("INVALID");
        assertEquals(EpicsAlarmSeverity.INVALID, objectUnderTest.getSeverity());
        assertEquals("INVALID", objectUnderTest.getString(AlarmMessageKey.SEVERITY));
    }
    
    @Override
    @Test
    public void testIsAcknowledgement() throws Exception {
        MapMessage message = Mockito.mock(MapMessage.class);
        IAlarmMessage objectUnderTest = AlarmMessageJMSImpl.newAlarmMessage(message);
        
        assertFalse(objectUnderTest.isAcknowledgement());
        
        Mockito.when(message.getString(AlarmMessageKey.ACK.getDefiningName())).thenReturn("true");
        assertTrue(objectUnderTest.isAcknowledgement());
        
    }
    
    @Test
    public void testGetMapWhenEmpty() throws Exception {
        MapMessage message = Mockito.mock(MapMessage.class);
        IAlarmMessage objectUnderTest = AlarmMessageJMSImpl.newAlarmMessage(message);
        
        @SuppressWarnings("unchecked")
        Enumeration<String> enumeration = Mockito.mock(Enumeration.class);
        Mockito.when(enumeration.hasMoreElements()).thenReturn(false);
        
        Mockito.when(message.getMapNames()).thenReturn(enumeration);
        
        Map<String, String> map = objectUnderTest.getMap();
        // check the count of the keys of the map
        // the jms implementation only forwards what it got so now we only find an empty map
        assertEquals(0, map.keySet().size());
    }
    
    @Override
    @Test
    public void testGetMap() throws Exception {
        MapMessage message = Mockito.mock(MapMessage.class);
        IAlarmMessage objectUnderTest = AlarmMessageJMSImpl.newAlarmMessage(message);
        
        // put one entry into the map
        @SuppressWarnings("unchecked")
        Enumeration<String> enumeration = Mockito.mock(Enumeration.class);
        Mockito.when(enumeration.hasMoreElements()).thenReturn(true).thenReturn(false);
        Mockito.when(enumeration.nextElement()).thenReturn("ACK");
        Mockito.when(message.getMapNames()).thenReturn(enumeration);
        Mockito.when(message.getString("ACK")).thenReturn("true");
        
        Map<String, String> map = objectUnderTest.getMap();
        // we only find what has been provided from outside
        assertEquals(1, map.keySet().size());
        assertEquals("ACK", map.keySet().iterator().next());
        assertEquals("true", objectUnderTest.getString(AlarmMessageKey.ACK));
    }
    
}
