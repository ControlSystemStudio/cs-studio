/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.simplepv;

import static org.diirt.vtype.ValueFactory.newAlarm;
import static org.diirt.vtype.ValueFactory.newDisplay;
import static org.diirt.vtype.ValueFactory.newTime;
import static org.diirt.vtype.ValueFactory.newVDouble;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
import java.util.Collection;
import java.time.Instant;

import org.csstudio.simplepv.VTypeHelper;
import org.diirt.util.text.NumberFormats;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.Time;
import org.diirt.vtype.VDouble;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/** JUnit test for VTypeHelper methods
 *
 */
@RunWith(Parameterized.class)
public class VTypeHelperTest
{

    private Time testTime;
    private static final Alarm alarmNone = newAlarm(AlarmSeverity.NONE, "NONE");
    private static final Display displayNone = newDisplay(Double.NaN, Double.NaN,
            Double.NaN, "", NumberFormats.toStringFormat(), Double.NaN, Double.NaN,
            Double.NaN, Double.NaN, Double.NaN);


    private VType testValue;
    private VTypeHelperBean expectedData;

    public VTypeHelperTest(VType value, VTypeHelperBean data) {
        this.testValue = value;
        this.expectedData = data;
    }

    @Before
    public void setUp() throws Exception {
        testTime = newTime(Instant.ofEpochSecond(1354719441, 521786982));
    }

    @Test
    public void testGetTimestamp() {
        VDouble value = newVDouble(1.0, alarmNone, testTime, displayNone);
        assertEquals(VTypeHelper.getTimestamp(value), testTime.getTimestamp());
    }

    @Test
    public void testGetTimestampWhenNull() {
        VType value = new VType(){}; // Create instance of empty interface
        assertEquals(VTypeHelper.getTimestamp(value), null);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> vtypeInstances() {
       return Arrays.asList(new Object[][] {
          { null,
               new VTypeHelperBean(BasicDataType.UNKNOWN, Double.NaN, true) },
          { ValueFactory.newVByte(new Byte("4"),
                   ValueFactory.alarmNone(),
                   ValueFactory.timeNow(),
                   ValueFactory.displayNone()),
               new VTypeHelperBean(BasicDataType.BYTE, 4.0, true)},
          { ValueFactory.newVDouble(1.0),
               new VTypeHelperBean(BasicDataType.DOUBLE, 1.0, true)},
          { ValueFactory.newVEnum(1, Arrays.asList(new String[] {"zero", "one"}),
                  ValueFactory.alarmNone(),
                  ValueFactory.timeNow()),
               new VTypeHelperBean(BasicDataType.ENUM, 1.0, true) },
          { ValueFactory.newVFloat(0.5f,
                  ValueFactory.alarmNone(),
                  ValueFactory.timeNow(),
                  ValueFactory.displayNone()),
               new VTypeHelperBean(BasicDataType.FLOAT, 0.5, true) },
          { ValueFactory.newVInt(42,
                  ValueFactory.alarmNone(),
                  ValueFactory.timeNow(),
                  ValueFactory.displayNone()),
               new VTypeHelperBean(BasicDataType.INT, 42.0, true) },
          { ValueFactory.newVShort(new Short("21"),
                  ValueFactory.alarmNone(),
                  ValueFactory.timeNow(),
                  ValueFactory.displayNone()),
              new VTypeHelperBean(BasicDataType.SHORT, 21.0, true) },
          { ValueFactory.newVString("test",
                  ValueFactory.alarmNone(),
                  ValueFactory.timeNow()),
              new VTypeHelperBean(BasicDataType.STRING, Double.NaN, true) },
       });
    }

    @Test
    public void getBasicTypeDataTest() {
        assertThat(VTypeHelper.getBasicDataType(testValue), is(expectedData.btype));
    }

    @Test
    public void getDoubleReturnsDoubleReprOfValueIfNotSTRINGorUNKNOWN() {
        if (expectedData.btype != BasicDataType.UNKNOWN &&
                expectedData.btype != BasicDataType.STRING) {
            assertThat(VTypeHelper.getDouble(testValue), is(expectedData.dval));
        }
    }

    @Test
    public void getDoubleReturnsNaNIfTypeUNKNOWN() {
        if (expectedData.btype == BasicDataType.UNKNOWN) {
            assertThat(VTypeHelper.getDouble(testValue), is(Double.NaN));
        }
    }

    @Test
    public void getDoubleReturnsNaNIfTypeSTRING() {
        if (expectedData.btype == BasicDataType.STRING) {
            assertThat(VTypeHelper.getDouble(testValue), is(Double.NaN));
        }
    }

    @Test
    public void getSizeIsOneForScalarOtherwiseArrayLength() {
        if (expectedData.isScalar) {
            assertThat(VTypeHelper.getSize(testValue), is(1));
        }
    }

}
