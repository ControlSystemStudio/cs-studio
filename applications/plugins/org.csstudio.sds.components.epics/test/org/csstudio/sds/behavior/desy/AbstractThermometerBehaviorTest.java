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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.csstudio.sds.components.model.TankModel;
import org.csstudio.sds.components.model.ThermometerModel;
import org.csstudio.sds.eventhandling.AbstractBehavior;
import org.csstudio.sds.eventhandling.AbstractBehaviorTest;
import org.csstudio.sds.model.AbstractWidgetModel;

/**
 * @author hrickens
 * @since 26.10.2011
 */
public abstract class AbstractThermometerBehaviorTest<B extends AbstractBehavior<ThermometerModel>> extends AbstractBehaviorTest<ThermometerModel, B> {


    @Override
    protected ThermometerModel createModelMock() {
        final ThermometerModel mock = mock(ThermometerModel.class);
        when(mock.getColor(ThermometerModel.PROP_FILLBACKGROUND_COLOR)).thenReturn("UserDefinedBGColor");
        when(mock.getColor(ThermometerModel.PROP_FILL_COLOR)).thenReturn("UserDefinedColor");
        return mock;
    }


    @Override
    protected void verifyDoInitialize() {
        getInOrder().verify(getModelMock()).setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND,"${Initial}");
//        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILL_COLOR,"${Initial}");
    }

    @Override
    protected void verifyConnectionStateConnectedWithData() {
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILLBACKGROUND_COLOR,"UserDefinedBGColor");
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILL_COLOR,"UserDefinedColor");
    }

    @Override
    protected void verifyValueChangeConnectedWithData() {
//        getInOrder().verify(getModelMock()).setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE,BorderStyleEnum.NONE);
//        getInOrder().verify(getModelMock()).setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH,0);
//        getInOrder().verify(getModelMock()).setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR,"${NoAlarm}");
//        getInOrder().verify(getModelMock()).setPropertyValue(BargraphModel.PROP_FILLBACKGROUND_COLOR,"UserDefinedColor");
//        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILLBACKGROUND_COLOR,"UserDefinedColor");
//        getInOrder().verify(getModelMock()).setPropertyValue(BargraphModel.PROP_TRANSPARENT,true);
    }

    @Override
    protected void verifyConnectionStateConnectedWithoutData() {
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILLBACKGROUND_COLOR,"${Invalid}");
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILL_COLOR,"${Invalid}");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void verifyValueChangeConnectedWithoutData() {
//        getInOrder().verify(getModelMock()).setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE,BorderStyleEnum.NONE);
//        getInOrder().verify(getModelMock()).setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH,0);
//        getInOrder().verify(getModelMock()).setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR,"${NoAlarm}");
//        getInOrder().verify(getModelMock()).setPropertyValue(BargraphModel.PROP_FILLBACKGROUND_COLOR,"${Invalid}");
//        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILLBACKGROUND_COLOR,"${Invalid}");
//        getInOrder().verify(getModelMock()).setPropertyValue(BargraphModel.PROP_TRANSPARENT,false);
    }

    @Override
    protected void verifyConnectionStateConnecting() {
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILLBACKGROUND_COLOR,"${Initial}");
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILL_COLOR,"${Initial}");
    }

    @Override
    protected void verifyConnectionStateFailed() {
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILLBACKGROUND_COLOR,"${VerbAbbr}");
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILL_COLOR,"${VerbAbbr}");
    }

    @Override
    protected void verifyConnectionStateConnectionLost() {
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILLBACKGROUND_COLOR,"${VerbAbbr}");
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILL_COLOR,"${VerbAbbr}");
    }

    @Override
    protected void verifyConnectionStateDestroyed() {
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILLBACKGROUND_COLOR,"${VerbAbbr}");
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILL_COLOR,"${VerbAbbr}");
    }

    @Override
    protected void verifyConnectionStateDisconnecting() {
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILLBACKGROUND_COLOR,"${VerbAbbr}");
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILL_COLOR,"${VerbAbbr}");
    }

    @Override
    protected void verifyConnectionStateDisconnected() {
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILLBACKGROUND_COLOR,"${VerbAbbr}");
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILL_COLOR,"${VerbAbbr}");
    }

    @Override
    protected void verifyConnectionStateInitial() {
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILLBACKGROUND_COLOR,"${Initial}");
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILL_COLOR,"${Initial}");
    }

    @Override
    protected void verifyConnectionStateOperational() {
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILLBACKGROUND_COLOR,"UserDefinedBGColor");
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILL_COLOR,"UserDefinedColor");
    }

    @Override
    protected void verifyValueChangeOperational() {
//        getInOrder().verify(getModelMock()).setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE,BorderStyleEnum.NONE);
//        getInOrder().verify(getModelMock()).setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH,0);
//        getInOrder().verify(getModelMock()).setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR,"${NoAlarm}");
//        getInOrder().verify(getModelMock()).setPropertyValue(BargraphModel.PROP_FILLBACKGROUND_COLOR,"UserDefinedColor");
//        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILLBACKGROUND_COLOR,"UserDefinedColor");
//        getInOrder().verify(getModelMock()).setPropertyValue(BargraphModel.PROP_TRANSPARENT,true);
    }

    @Override
    protected void verifyConnectionStateReady() {
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILLBACKGROUND_COLOR,"${Initial}");
        getInOrder().verify(getModelMock()).setPropertyValue(TankModel.PROP_FILL_COLOR,"${Initial}");
    }


    //    @Test
    //    public void testname() throws Exception {
    //        when(_dynamicValueProperty.getConnectionState()).thenReturn(ConnectionState.CONNECTED);
    //        when(_anyDataChannel.isRunning()).thenReturn(true);
    //        _polygonAlarmBehavior.doProcessConnectionStateChange(_polygonModelMock, _anyDataChannel);
    //        verify(_polygonModelMock, times(1)).setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, "${Zu}");
    //        _polygonAlarmBehavior.doProcessValueChange(_polygonModelMock, _anyData);
    //        verify(_polygonModelMock).setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, "${Zu}");
    //        when(_anyData.numberValue()).thenReturn(1);
    //        _polygonAlarmBehavior.doProcessValueChange(_polygonModelMock, _anyData);
    //        verify(_polygonModelMock).setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, "${Zu}");
    //        when(_anyData.numberValue()).thenReturn(2);
    //        _polygonAlarmBehavior.doProcessValueChange(_polygonModelMock, _anyData);
    //        verify(_polygonModelMock).setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, "${Zu}");
    //        _polygonAlarmBehavior.doProcessValueChange(_polygonModelMock, _anyData);
    //        when(_anyData.numberValue()).thenReturn(3);
    //        verify(_polygonModelMock).setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, "${Zu}");
    //        _polygonAlarmBehavior.doProcessValueChange(_polygonModelMock, _anyData);
    //        when(_anyData.numberValue()).thenReturn(4);
    //        verify(_polygonModelMock).setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, "${Zu}");
    //        _polygonAlarmBehavior.doProcessValueChange(_polygonModelMock, _anyData);
    //        when(_anyData.numberValue()).thenReturn(15);
    //        verify(_polygonModelMock).setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, "${Zu}");
    //        when(_anyData.numberValue()).thenReturn(16);
    //        verify(_polygonModelMock).setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, "${Zu}");
    //    }

}
