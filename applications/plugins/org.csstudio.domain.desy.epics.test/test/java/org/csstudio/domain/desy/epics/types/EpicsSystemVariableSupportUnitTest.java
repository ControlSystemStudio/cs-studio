/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.desy.epics.types;

import java.util.Collection;

import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.IEnumeratedValue;
import org.csstudio.data.values.ILongValue;
import org.csstudio.data.values.IValue;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.csstudio.domain.desy.epics.typesupport.EpicsSystemVariableSupport;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.system.SystemVariableSupport;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test for css value type conversions.
 *
 * @author bknerr
 * @since 22.12.2010
 */
public class EpicsSystemVariableSupportUnitTest {

    @Before
    public void setup() {
        EpicsSystemVariableSupport.install();
    }



    @Test
    public void testFloat() throws TypeSupportException {
        final Float input = Float.valueOf(1.0F);
        final IAlarmSystemVariable<Float> cssVal =
            new EpicsSystemVariable<Float>("NONE",
                                            input,
                                            ControlSystem.EPICS_DEFAULT,
                                            TimeInstantBuilder.fromNow(),
                                            new EpicsAlarm(EpicsAlarmSeverity.NO_ALARM, EpicsAlarmStatus.BADSUB));

        final IValue iVal = SystemVariableSupport.toIValue(cssVal);

        Assert.assertTrue(iVal instanceof IDoubleValue);
        final double value = ((IDoubleValue) iVal).getValue();
        Assert.assertTrue(input.equals(Double.valueOf(value).floatValue()));

        Assert.assertTrue(iVal.getSeverity().isOK());
    }

    @Test
    public void testDouble() throws TypeSupportException {
        final Double input = Double.valueOf(1.0);
        final IAlarmSystemVariable<Double> cssVal =
            new EpicsSystemVariable<Double>("NONE",
                    input,
                    ControlSystem.EPICS_DEFAULT,
                    TimeInstantBuilder.fromNow(),
                    new EpicsAlarm(EpicsAlarmSeverity.NO_ALARM, EpicsAlarmStatus.BADSUB));


        final IValue iVal = SystemVariableSupport.toIValue(cssVal);

        Assert.assertTrue(iVal instanceof IDoubleValue);
        final double value = ((IDoubleValue) iVal).getValue();
        Assert.assertTrue(input.equals(Double.valueOf(value)));

        Assert.assertTrue(iVal.getSeverity().isOK());
    }

    @Test
    public void testByte() throws TypeSupportException {
        final Byte input = Byte.valueOf((byte) 12);
        final IAlarmSystemVariable<Byte> cssVal =
        new EpicsSystemVariable<Byte>("NONE",
                input,
                ControlSystem.EPICS_DEFAULT,
                TimeInstantBuilder.fromNow(),
                new EpicsAlarm(EpicsAlarmSeverity.NO_ALARM, EpicsAlarmStatus.BADSUB));

        final IValue iVal = SystemVariableSupport.toIValue(cssVal);

        Assert.assertTrue(iVal instanceof ILongValue);
        final long value = ((ILongValue) iVal).getValue();
        Assert.assertTrue(Long.valueOf(input.longValue()).equals(Long.valueOf(value)));

        Assert.assertTrue(iVal.getSeverity().isOK());
    }

    @Test
    public void testInteger() throws TypeSupportException {
        final Integer input = Integer.valueOf(1);
        final IAlarmSystemVariable<Integer> cssVal =
        new EpicsSystemVariable<Integer>("NONE",
                input,
                ControlSystem.EPICS_DEFAULT,
                TimeInstantBuilder.fromNow(),
                new EpicsAlarm(EpicsAlarmSeverity.NO_ALARM, EpicsAlarmStatus.BADSUB));
        final IValue iVal = SystemVariableSupport.toIValue(cssVal);

        Assert.assertTrue(iVal instanceof ILongValue);
        final long value = ((ILongValue) iVal).getValue();
        Assert.assertTrue(Long.valueOf(input.longValue()).equals(Long.valueOf(value)));

        Assert.assertTrue(iVal.getSeverity().isOK());
    }

    @Test
    public void testLong() throws TypeSupportException {
        final Long input = Long.valueOf(1L);
        final IAlarmSystemVariable<Long> cssVal =

        new EpicsSystemVariable<Long>("NONE",
                input,
                ControlSystem.EPICS_DEFAULT,
                TimeInstantBuilder.fromNow(),
                new EpicsAlarm(EpicsAlarmSeverity.NO_ALARM, EpicsAlarmStatus.BADSUB));

        final IValue iVal = SystemVariableSupport.toIValue(cssVal);

        Assert.assertTrue(iVal instanceof ILongValue);
        final long value = ((ILongValue) iVal).getValue();
        Assert.assertTrue(input.equals(Long.valueOf(value)));

        Assert.assertTrue(iVal.getSeverity().isOK());
    }

    @Test
    public void testDoubleCollection() throws TypeSupportException {
        final IAlarmSystemVariable<Collection<Double>> cssVal =
        new EpicsSystemVariable<Collection<Double>>("NONE",
                Lists.newArrayList(Double.valueOf(1.0), Double.valueOf(2.0)),
                ControlSystem.EPICS_DEFAULT,
                TimeInstantBuilder.fromNow(),
                new EpicsAlarm(EpicsAlarmSeverity.NO_ALARM, EpicsAlarmStatus.BADSUB));
        final IValue iVal = SystemVariableSupport.toIValue(cssVal);

        Assert.assertTrue(iVal instanceof IDoubleValue);
        final double[] values = ((IDoubleValue) iVal).getValues();
        Assert.assertEquals(2, values.length);
        Assert.assertEquals(Double.valueOf(1.0), Double.valueOf(values[0]));
        Assert.assertEquals(Double.valueOf(2.0), Double.valueOf(values[1]));
    }

    @Test
    public void testEpicsEnumCollection() throws TypeSupportException {
        final IAlarmSystemVariable<Collection<EpicsEnum>> cssVal =
            new EpicsSystemVariable<Collection<EpicsEnum>>("NONE",
                                                           Lists.newArrayList(EpicsEnum.createFromState("ON"),
                                                                              EpicsEnum.createFromState("OFF")),
                ControlSystem.EPICS_DEFAULT,
                TimeInstantBuilder.fromNow(),
                new EpicsAlarm(EpicsAlarmSeverity.NO_ALARM, EpicsAlarmStatus.BADSUB));

        final IValue iVal = SystemVariableSupport.toIValue(cssVal);

        Assert.assertTrue(iVal instanceof IEnumeratedValue);
        final int[] values = ((IEnumeratedValue) iVal).getValues();
        Assert.assertEquals(2, values.length);
        Assert.assertEquals(Integer.valueOf(1), Integer.valueOf(values[0]));
        Assert.assertEquals(Integer.valueOf(0), Integer.valueOf(values[1]));
        
        final IEnumeratedMetaData meta = ((IEnumeratedValue) iVal).getMetaData();
        Assert.assertNotNull(meta);

    }
}
