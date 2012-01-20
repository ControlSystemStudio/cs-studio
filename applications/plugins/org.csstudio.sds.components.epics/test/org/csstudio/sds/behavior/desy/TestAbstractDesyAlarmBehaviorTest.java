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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 03.11.2011
 */
public class TestAbstractDesyAlarmBehaviorTest {

    private TestAbstractDesyAlarmBehavior _testAbstractDesyAlarmBehavior;
    private AnyDataChannel _anyDataChannelMock;
    private AbstractWidgetModel _abstractWidgetModelMock;
    private AnyData _anyDataMock;
    private DynamicValueProperty _dynamicValuePropertyMock;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        _testAbstractDesyAlarmBehavior = new TestAbstractDesyAlarmBehavior();
        _abstractWidgetModelMock = mock(AbstractWidgetModel.class);
        _anyDataChannelMock = mock(AnyDataChannel.class);
        _anyDataMock = mock(AnyData.class);
        _dynamicValuePropertyMock = mock(DynamicValueProperty.class);
        when(_anyDataChannelMock.getData()).thenReturn(_anyDataMock);
        when(_anyDataChannelMock.getProperty()).thenReturn(_dynamicValuePropertyMock);
    }

    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyAlarmBehavior#doProcessConnectionStateChange(org.csstudio.sds.model.AbstractWidgetModel, org.csstudio.dal.simple.AnyDataChannel)}.
     */
    @Test
    public void testDoProcessConnectionStateChange() {
        // null
        when(_anyDataMock.getSeverity()).thenReturn(null);
        _testAbstractDesyAlarmBehavior.doProcessConnectionStateChange(_abstractWidgetModelMock, _anyDataChannelMock);
        verify(_abstractWidgetModelMock, times(1)).setPropertyValue(eq(AbstractWidgetModel.PROP_COLOR_BACKGROUND),anyString());
//        verify(_abstractWidgetModelMock, times(1)).setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND,"${Initial}");
        verify(_abstractWidgetModelMock, never()).setPropertyValue(eq(AbstractWidgetModel.PROP_BORDER_COLOR),anyString());
        verify(_abstractWidgetModelMock, never()).setPropertyValue(eq(AbstractWidgetModel.PROP_BORDER_STYLE),anyObject());
        verify(_abstractWidgetModelMock, never()).setPropertyValue(eq(AbstractWidgetModel.PROP_BORDER_WIDTH),anyInt());
        // ALARM
        when(_anyDataMock.getSeverity()).thenReturn(new DynamicValueCondition(DynamicValueState.ALARM));
        _testAbstractDesyAlarmBehavior.doProcessConnectionStateChange(_abstractWidgetModelMock, _anyDataChannelMock);
        verify(_abstractWidgetModelMock, times(2)).setPropertyValue(eq(AbstractWidgetModel.PROP_COLOR_BACKGROUND),anyString());
        verify(_abstractWidgetModelMock, times(1)).setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR,"${Major}");
        verify(_abstractWidgetModelMock, times(1)).setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE,BorderStyleEnum.LINE.getIndex());
        verify(_abstractWidgetModelMock, times(1)).setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH,3);
        // WARNING
        when(_anyDataMock.getSeverity()).thenReturn(new DynamicValueCondition(DynamicValueState.WARNING));
        _testAbstractDesyAlarmBehavior.doProcessConnectionStateChange(_abstractWidgetModelMock, _anyDataChannelMock);
        verify(_abstractWidgetModelMock, times(1)).setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR,"${Minor}");
        verify(_abstractWidgetModelMock, times(2)).setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE,BorderStyleEnum.LINE.getIndex());
        verify(_abstractWidgetModelMock, times(2)).setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH,3);
        // NORMAL
        when(_anyDataMock.getSeverity()).thenReturn(new DynamicValueCondition(DynamicValueState.NORMAL));
        _testAbstractDesyAlarmBehavior.doProcessConnectionStateChange(_abstractWidgetModelMock, _anyDataChannelMock);
        verify(_abstractWidgetModelMock, times(1)).setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR,"${NoAlarm}");
        verify(_abstractWidgetModelMock, times(1)).setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE,BorderStyleEnum.NONE.getIndex());
        verify(_abstractWidgetModelMock, times(1)).setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH,0);
        // ERROR
        when(_anyDataMock.getSeverity()).thenReturn(new DynamicValueCondition(DynamicValueState.ERROR));
        _testAbstractDesyAlarmBehavior.doProcessConnectionStateChange(_abstractWidgetModelMock, _anyDataChannelMock);
        verify(_abstractWidgetModelMock, times(1)).setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR,"${Invalid}");
        verify(_abstractWidgetModelMock, times(2)).setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE,BorderStyleEnum.NONE.getIndex());
        verify(_abstractWidgetModelMock, times(2)).setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH,0);
    }

    /**
     * Test method for {@link org.csstudio.sds.behavior.desy.AbstractDesyAlarmBehavior#AbstractDesyAlarmBehavior()}.
     */
    @Test
    public void testAbstractDesyAlarmBehavior() {
        final String[] invisiblePropertyIds = _testAbstractDesyAlarmBehavior.getInvisiblePropertyIds();
        final List<String> asList = Arrays.asList(invisiblePropertyIds);
        assertTrue(asList.contains(AbstractWidgetModel.PROP_BORDER_COLOR));
        assertTrue(asList.contains(AbstractWidgetModel.PROP_BORDER_STYLE));
        assertTrue(asList.contains(AbstractWidgetModel.PROP_BORDER_WIDTH));
    }

}
