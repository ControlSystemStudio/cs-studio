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
 */
package org.csstudio.sds.behavior.desy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.Severity;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 03.11.2011
 */
public class TestAbstractDesyConnectionBehaviorTest {

    private TestAbstractDesyConnectionBehavior _testAbstractDesyConnectionBehavior;
    private AnyDataChannel _anyDataChannelMock;
    private AbstractWidgetModel _abstractWidgetModelMock;
    private AnyData _anyDataMock;
    private DynamicValueProperty _dynamicValuePropertyMock;
    private Severity _severityMock;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _testAbstractDesyConnectionBehavior = new TestAbstractDesyConnectionBehavior();
        _abstractWidgetModelMock = mock(AbstractWidgetModel.class);
        _anyDataChannelMock = mock(AnyDataChannel.class);
        _anyDataMock = mock(AnyData.class);
        _dynamicValuePropertyMock = mock(DynamicValueProperty.class);
        _severityMock = mock(Severity.class);
        when(_anyDataChannelMock.getData()).thenReturn(_anyDataMock);
        when(_anyDataChannelMock.getProperty()).thenReturn(_dynamicValuePropertyMock);
        when(_dynamicValuePropertyMock.getCondition()).thenReturn(new DynamicValueCondition(DynamicValueState.HAS_LIVE_DATA));
        when(_anyDataMock.getParentChannel()).thenReturn(_anyDataChannelMock);
        when(_anyDataMock.getSeverity()).thenReturn(_severityMock);
        when(_abstractWidgetModelMock.getColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND)).thenReturn("MyBC");
    }


    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyConnectionBehavior#doProcessConnectionStateChange(org.csstudio.sds.model.AbstractWidgetModel, org.csstudio.dal.simple.AnyDataChannel)}.
     */
    @Test
    public void testDoProcessConnectionStateChange() {
        _testAbstractDesyConnectionBehavior.doInitialize(_abstractWidgetModelMock);
        verify(_abstractWidgetModelMock, times(1)).setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "${Initial}");
        _testAbstractDesyConnectionBehavior.doProcessConnectionStateChange(_abstractWidgetModelMock, _anyDataChannelMock);
        verify(_abstractWidgetModelMock, times(2)).setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "${Initial}");

        when(_dynamicValuePropertyMock.getConnectionState()).thenReturn(ConnectionState.CONNECTING);
        _testAbstractDesyConnectionBehavior.doProcessConnectionStateChange(_abstractWidgetModelMock, _anyDataChannelMock);
        verify(_abstractWidgetModelMock, times(3)).setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "${Initial}");

        when(_dynamicValuePropertyMock.getConnectionState()).thenReturn(ConnectionState.CONNECTED);
        _testAbstractDesyConnectionBehavior.doProcessConnectionStateChange(_abstractWidgetModelMock, _anyDataChannelMock);
        verify(_abstractWidgetModelMock).setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "MyBC");

        when(_dynamicValuePropertyMock.getConnectionState()).thenReturn(ConnectionState.OPERATIONAL);
        _testAbstractDesyConnectionBehavior.doProcessConnectionStateChange(_abstractWidgetModelMock, _anyDataChannelMock);
        verify(_abstractWidgetModelMock, times(2)).setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "MyBC");

        when(_dynamicValuePropertyMock.getConnectionState()).thenReturn(ConnectionState.DESTROYED);
        _testAbstractDesyConnectionBehavior.doProcessConnectionStateChange(_abstractWidgetModelMock, _anyDataChannelMock);
        verify(_abstractWidgetModelMock).setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND, "${VerbAbbr}");
    }

    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyConnectionBehavior#doProcessValueChange(org.csstudio.sds.model.AbstractWidgetModel, org.csstudio.dal.simple.AnyData)}.
     */
    @Test
    public void testDoProcessValueChange() {
        when(_severityMock.isInvalid()).thenReturn(true);
        _testAbstractDesyConnectionBehavior.doProcessValueChange(_abstractWidgetModelMock, _anyDataMock);
        verify(_abstractWidgetModelMock).setPropertyValue(AbstractWidgetModel.PROP_CROSSED_OUT, true);
        when(_severityMock.isInvalid()).thenReturn(false);
        _testAbstractDesyConnectionBehavior.doProcessValueChange(_abstractWidgetModelMock, _anyDataMock);
        verify(_abstractWidgetModelMock).setPropertyValue(AbstractWidgetModel.PROP_CROSSED_OUT, false);
    }

    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyConnectionBehavior#addInvisiblePropertyId(java.lang.String)}.
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyConnectionBehavior#removeInvisiblePropertyId(java.lang.String)}.
     */
    @Test
    public void testAddAnsRemoveInvisiblePropertyId() {
        _testAbstractDesyConnectionBehavior.addInvisiblePropertyId("testPropertyId");
        List<String> asList = Arrays.asList(_testAbstractDesyConnectionBehavior.getInvisiblePropertyIds());
        assertTrue(asList.contains("testPropertyId"));
        _testAbstractDesyConnectionBehavior.removeInvisiblePropertyId("testPropertyId");
        asList = Arrays.asList(_testAbstractDesyConnectionBehavior.getInvisiblePropertyIds());
        assertFalse(asList.contains("testPropertyId"));
    }


    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyConnectionBehavior#isConnected(org.csstudio.dal.simple.AnyDataChannel)}.
     */
    @Test
    public void testIsConnected() {
        assertFalse(_testAbstractDesyConnectionBehavior.isConnected(_anyDataChannelMock));
        when(_dynamicValuePropertyMock.getConnectionState()).thenReturn(ConnectionState.INITIAL);
        assertFalse(_testAbstractDesyConnectionBehavior.isConnected(_anyDataChannelMock));
        when(_dynamicValuePropertyMock.getConnectionState()).thenReturn(ConnectionState.CONNECTED);
        assertTrue(_testAbstractDesyConnectionBehavior.isConnected(_anyDataChannelMock));
        when(_dynamicValuePropertyMock.getConnectionState()).thenReturn(ConnectionState.OPERATIONAL);
        assertTrue(_testAbstractDesyConnectionBehavior.isConnected(_anyDataChannelMock));
        when(_dynamicValuePropertyMock.getConnectionState()).thenReturn(ConnectionState.READY);
        assertFalse(_testAbstractDesyConnectionBehavior.isConnected(_anyDataChannelMock));
    }

    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyConnectionBehavior#getColorFromDigLogColorRule(org.csstudio.dal.simple.AnyData)}.
     */
    @Test
    public void testGetColorFromDigLogColorRule() {
        when(_dynamicValuePropertyMock.getCondition()).thenReturn(new DynamicValueCondition(DynamicValueState.ERROR));
        String colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(null);
        assertEquals("${Illegal}", colorFromDigLogColorRule);

        when(_anyDataMock.numberValue()).thenReturn(-1);
        colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromDigLogColorRule);

        when(_anyDataMock.numberValue()).thenReturn(0);
        colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromDigLogColorRule);

        when(_anyDataMock.numberValue()).thenReturn(1);
        colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromDigLogColorRule);

        when(_anyDataMock.numberValue()).thenReturn(2);
        colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromDigLogColorRule);

        when(_anyDataMock.numberValue()).thenReturn(3);
        colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromDigLogColorRule);

        when(_anyDataMock.numberValue()).thenReturn(7);
        colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromDigLogColorRule);

        when(_anyDataMock.numberValue()).thenReturn(1234);
        colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromDigLogColorRule);

        // Has live Data
        when(_dynamicValuePropertyMock.getCondition()).thenReturn(new DynamicValueCondition(DynamicValueState.HAS_LIVE_DATA));
        when(_anyDataMock.numberValue()).thenReturn(null);
        colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(_anyDataMock);
        assertEquals("${Illegal}", colorFromDigLogColorRule);

        when(_anyDataMock.numberValue()).thenReturn(-1);
        colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(_anyDataMock);
        assertEquals("${Illegal}", colorFromDigLogColorRule);

        when(_anyDataMock.numberValue()).thenReturn(0);
        colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(_anyDataMock);
        assertEquals("${Zu}", colorFromDigLogColorRule);

        when(_anyDataMock.numberValue()).thenReturn(1);
        colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(_anyDataMock);
        assertEquals("${Geregelt}", colorFromDigLogColorRule);

        when(_anyDataMock.numberValue()).thenReturn(2);
        colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(_anyDataMock);
        assertEquals("${Offen}", colorFromDigLogColorRule);

        when(_anyDataMock.numberValue()).thenReturn(3);
        colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(_anyDataMock);
        assertEquals("${Minor}", colorFromDigLogColorRule);

        when(_anyDataMock.numberValue()).thenReturn(7);
        colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(_anyDataMock);
        assertEquals("${Minor}", colorFromDigLogColorRule);

        when(_anyDataMock.numberValue()).thenReturn(1234);
        colorFromDigLogColorRule = _testAbstractDesyConnectionBehavior.getColorFromDigLogColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromDigLogColorRule);
    }

    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyConnectionBehavior#getColorFromAlarmColorRule(org.csstudio.dal.simple.AnyData)}.
     */
    @Test
    public void testGetColorFromAlarmColorRule() {
        when(_dynamicValuePropertyMock.getCondition()).thenReturn(new DynamicValueCondition(DynamicValueState.ERROR));
        String colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(null);
        assertEquals("${Illegal}", colorFromAlarmColorRule);

        when(_anyDataMock.numberValue()).thenReturn(-1);
        colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromAlarmColorRule);

        when(_anyDataMock.numberValue()).thenReturn(0);
        colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromAlarmColorRule);

        when(_anyDataMock.numberValue()).thenReturn(1);
        colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromAlarmColorRule);

        when(_anyDataMock.numberValue()).thenReturn(2);
        colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromAlarmColorRule);

        when(_anyDataMock.numberValue()).thenReturn(3);
        colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromAlarmColorRule);

        when(_anyDataMock.numberValue()).thenReturn(7);
        colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromAlarmColorRule);

        when(_anyDataMock.numberValue()).thenReturn(1234);
        colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromAlarmColorRule);

        // Has live Data
        when(_dynamicValuePropertyMock.getCondition()).thenReturn(new DynamicValueCondition(DynamicValueState.HAS_LIVE_DATA));
        when(_anyDataMock.numberValue()).thenReturn(null);
        colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(_anyDataMock);
        assertEquals("${Illegal}", colorFromAlarmColorRule);

        when(_anyDataMock.numberValue()).thenReturn(-1);
        colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(_anyDataMock);
        assertEquals("${Illegal}", colorFromAlarmColorRule);

        when(_anyDataMock.numberValue()).thenReturn(0);
        colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(_anyDataMock);
        assertEquals("${NoAlarm}", colorFromAlarmColorRule);

        when(_anyDataMock.numberValue()).thenReturn(1);
        colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(_anyDataMock);
        assertEquals("${Minor}", colorFromAlarmColorRule);

        when(_anyDataMock.numberValue()).thenReturn(2);
        colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(_anyDataMock);
        assertEquals("${Major}", colorFromAlarmColorRule);

        when(_anyDataMock.numberValue()).thenReturn(3);
        colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromAlarmColorRule);

        when(_anyDataMock.numberValue()).thenReturn(7);
        colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(_anyDataMock);
        assertEquals("${Illegal}", colorFromAlarmColorRule);

        when(_anyDataMock.numberValue()).thenReturn(1234);
        colorFromAlarmColorRule = _testAbstractDesyConnectionBehavior.getColorFromAlarmColorRule(_anyDataMock);
        assertEquals("${Illegal}", colorFromAlarmColorRule);
    }

    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyConnectionBehavior#getColorFromOperatingRule(org.csstudio.dal.simple.AnyData)}.
     */
    @Test
    public void testGetColorFromOperatingRule() {
        when(_dynamicValuePropertyMock.getCondition()).thenReturn(new DynamicValueCondition(DynamicValueState.ERROR));
        String colorFromOperatingRule = _testAbstractDesyConnectionBehavior.getColorFromOperatingRule(null);
        assertEquals("${Illegal}", colorFromOperatingRule);

        when(_anyDataMock.numberValue()).thenReturn(0);
        colorFromOperatingRule = _testAbstractDesyConnectionBehavior.getColorFromOperatingRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromOperatingRule);

        when(_anyDataMock.numberValue()).thenReturn(1);
        colorFromOperatingRule = _testAbstractDesyConnectionBehavior.getColorFromOperatingRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromOperatingRule);

        when(_anyDataMock.numberValue()).thenReturn(-1);
        colorFromOperatingRule = _testAbstractDesyConnectionBehavior.getColorFromOperatingRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromOperatingRule);

        when(_anyDataMock.numberValue()).thenReturn(3);
        colorFromOperatingRule = _testAbstractDesyConnectionBehavior.getColorFromOperatingRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromOperatingRule);

        when(_anyDataMock.numberValue()).thenReturn(1234);
        colorFromOperatingRule = _testAbstractDesyConnectionBehavior.getColorFromOperatingRule(_anyDataMock);
        assertEquals("${Invalid}", colorFromOperatingRule);

        // Has live Data
        when(_dynamicValuePropertyMock.getCondition()).thenReturn(new DynamicValueCondition(DynamicValueState.HAS_LIVE_DATA));
        when(_anyDataMock.numberValue()).thenReturn(null);
        colorFromOperatingRule = _testAbstractDesyConnectionBehavior.getColorFromOperatingRule(_anyDataMock);
        assertEquals("${Illegal}", colorFromOperatingRule);

        when(_anyDataMock.numberValue()).thenReturn(0);
        colorFromOperatingRule = _testAbstractDesyConnectionBehavior.getColorFromOperatingRule(_anyDataMock);
        assertEquals("${KeinBetrieb}", colorFromOperatingRule);

        when(_anyDataMock.numberValue()).thenReturn(1);
        colorFromOperatingRule = _testAbstractDesyConnectionBehavior.getColorFromOperatingRule(_anyDataMock);
        assertEquals("${Betrieb}", colorFromOperatingRule);

        when(_anyDataMock.numberValue()).thenReturn(-1);
        colorFromOperatingRule = _testAbstractDesyConnectionBehavior.getColorFromOperatingRule(_anyDataMock);
        assertEquals("${Illegal}", colorFromOperatingRule);

        when(_anyDataMock.numberValue()).thenReturn(3);
        colorFromOperatingRule = _testAbstractDesyConnectionBehavior.getColorFromOperatingRule(_anyDataMock);
        assertEquals("${Illegal}", colorFromOperatingRule);

        when(_anyDataMock.numberValue()).thenReturn(1234);
        colorFromOperatingRule = _testAbstractDesyConnectionBehavior.getColorFromOperatingRule(_anyDataMock);
        assertEquals("${Illegal}", colorFromOperatingRule);
    }

    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyConnectionBehavior#hasValue(org.csstudio.dal.simple.AnyDataChannel)}.
     */
    @Test
    public void testHasValue() {
        when(_dynamicValuePropertyMock.getCondition()).thenReturn(new DynamicValueCondition(DynamicValueState.ERROR));
        boolean hasValue = _testAbstractDesyConnectionBehavior.hasValue(_anyDataChannelMock);
        assertFalse(hasValue);
        when(_dynamicValuePropertyMock.getCondition()).thenReturn(new DynamicValueCondition(DynamicValueState.HAS_LIVE_DATA));
        hasValue = _testAbstractDesyConnectionBehavior.hasValue(_anyDataChannelMock);
        assertTrue(hasValue);
    }

}
