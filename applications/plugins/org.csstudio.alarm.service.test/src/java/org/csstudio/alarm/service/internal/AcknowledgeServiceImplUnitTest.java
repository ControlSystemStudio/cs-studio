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
package org.csstudio.alarm.service.internal;

import java.util.Collection;
import java.util.Map;

import org.csstudio.alarm.service.declaration.ITimeService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import static org.mockito.Mockito.*;

/**
 * Test for the acknowledge service 
 * 
 * @author jpenning
 * @since 30.03.2012
 */
public class AcknowledgeServiceImplUnitTest {
    private AcknowledgeServiceImpl _serviceUnderTest;
    private ITimeService _timeService;
    
    @Before
    public void setUp() throws Exception {
        _timeService = mock(ITimeService.class);
        _serviceUnderTest = new AcknowledgeServiceImpl(_timeService);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAcknowledgeChecksForNull() {
        _serviceUnderTest.announceAcknowledge(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetAcknowledgeTimeChecksForNull() {
        _serviceUnderTest.getAcknowledgeTime(null);
    }
    
    @Test
    public void testGetAcknowledgeTime() {
        assertNull(_serviceUnderTest.getAcknowledgeTime("myPV1"));
        assertNull(_serviceUnderTest.getAcknowledgeTime("myPV2"));
        
        acknowledge("myPV1", "timePV1");
        acknowledge("myPV2", "timePV2");
        
        assertEquals("timePV1", _serviceUnderTest.getAcknowledgeTime("myPV1"));
        assertEquals("timePV2", _serviceUnderTest.getAcknowledgeTime("myPV2"));
        
        acknowledge("myPV1", "timePV1b");
        assertEquals("timePV1b", _serviceUnderTest.getAcknowledgeTime("myPV1"));
        assertEquals("timePV2", _serviceUnderTest.getAcknowledgeTime("myPV2"));
    }
    
    @Test
    public void testAlarmAfterAckCancelsAck() {
        acknowledge("myPV1", "timePV1");
        assertEquals("timePV1", _serviceUnderTest.getAcknowledgeTime("myPV1"));
        
        _serviceUnderTest.announceAlarm("myPV1");
        assertNull(_serviceUnderTest.getAcknowledgeTime("myPV1"));
    }
    
    @Test
    public void testGetAcknowledgedPvs() {
        assertEquals(0, _serviceUnderTest.getAcknowledgedPvs().size());
        
        acknowledge("myPV1", "timePV1");
        acknowledge("myPV2", "timePV2");
        acknowledge("myPV3", "timePV3");
        Collection<String> acknowledgedPvs = _serviceUnderTest.getAcknowledgedPvs();
        assertEquals(3, acknowledgedPvs.size());
        
        assertThat(acknowledgedPvs, hasItems("myPV1", "myPV2", "myPV3"));
        
        _serviceUnderTest.announceAlarm("myPV2");
        assertEquals(2, acknowledgedPvs.size());
        assertThat(acknowledgedPvs, hasItems("myPV1", "myPV3"));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testRestoreMementoChecksForNull() {
        _serviceUnderTest.restoreMemento(null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testRestoreMementoChecksForType() {
        _serviceUnderTest.restoreMemento(new Object());
    }
    
    @Test
    public void testMemento() {
        Map<String, String> memento = _serviceUnderTest.getMemento();
        assertEquals(0, memento.size());
        
        acknowledge("myPV1", "timePV1");
        acknowledge("myPV2", "timePV2");
        memento = _serviceUnderTest.getMemento();
        assertEquals("timePV1", _serviceUnderTest.getAcknowledgeTime("myPV1"));
        assertEquals("timePV2", _serviceUnderTest.getAcknowledgeTime("myPV2"));
        assertEquals(2, memento.size());
        
        // this acknowledge is not contained in the memento, it will not be present after restoring the memento
        acknowledge("myPV3", "timePV3");
        assertEquals("timePV3", _serviceUnderTest.getAcknowledgeTime("myPV3"));
        
        _serviceUnderTest.restoreMemento(memento);
        assertEquals("timePV1", _serviceUnderTest.getAcknowledgeTime("myPV1"));
        assertEquals("timePV2", _serviceUnderTest.getAcknowledgeTime("myPV2"));
        assertNull(_serviceUnderTest.getAcknowledgeTime("myPV3"));
    }
    
    private void acknowledge(String pvName, String time) {
        when(_timeService.getCurrentTimeAsString()).thenReturn(time);
        _serviceUnderTest.announceAcknowledge(pvName);
    }
    
}
