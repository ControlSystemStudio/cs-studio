/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.csstudio.saverestore.Utilities;
import org.csstudio.saverestore.Utilities.VTypeComparison;
import org.csstudio.saverestore.data.Threshold;
import org.diirt.util.array.ArrayBoolean;
import org.diirt.util.array.ArrayDouble;
import org.diirt.util.array.ArrayLong;
import org.diirt.util.array.ListBoolean;
import org.diirt.util.array.ListDouble;
import org.diirt.util.array.ListLong;
import org.diirt.util.array.ListNumber;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.Time;
import org.diirt.vtype.VBooleanArray;
import org.diirt.vtype.VByte;
import org.diirt.vtype.VDouble;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VFloat;
import org.diirt.vtype.VInt;
import org.diirt.vtype.VLong;
import org.diirt.vtype.VLongArray;
import org.diirt.vtype.VShort;
import org.diirt.vtype.VStringArray;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.diirt.vtype.VString;
import org.junit.Test;

/**
 *
 * <code>UtilitiesTest</code> tests methods from the {@link Utilities} class.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class UtilitiesTest {

    /**
     * Tests {@link Utilities#valueToString(VType)} and {@link Utilities#valueToString(VType, int)}.
     */
    @Test
    public void testValueToString() {
        Alarm alarm = ValueFactory.alarmNone();
        Display display = ValueFactory.displayNone();
        Time time = ValueFactory.timeNow();

        VType val = ValueFactory.newVDouble(5d,alarm,time,display);
        String result = Utilities.valueToString(val);
        assertEquals("5.0", result);

        val = ValueFactory.newVFloat(5f,alarm,time,display);
        result = Utilities.valueToString(val);
        assertEquals("5.0", result);

        val = ValueFactory.newVLong(5L,alarm,time,display);
        result = Utilities.valueToString(val);
        assertEquals("5", result);

        val = ValueFactory.newVInt(5,alarm,time,display);
        result = Utilities.valueToString(val);
        assertEquals("5", result);

        val = ValueFactory.newVShort((short)5,alarm,time,display);
        result = Utilities.valueToString(val);
        assertEquals("5", result);

        val = ValueFactory.newVByte((byte)5,alarm,time,display);
        result = Utilities.valueToString(val);
        assertEquals("5", result);

        val = ValueFactory.newVEnum(1, Arrays.asList("first", "second", "third"),alarm, time);
        result = Utilities.valueToString(val);
        assertEquals("second", result);

        val = ValueFactory.newVString("third",alarm, time);
        result = Utilities.valueToString(val);
        assertEquals("third", result);

        ListDouble data = new ArrayDouble(1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9);
        val = ValueFactory.newVDoubleArray(data,alarm,time,display);
        result = Utilities.valueToString(val);
        assertEquals("[1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0,...]", result);

        val = ValueFactory.newVDoubleArray(data,alarm,time,display);
        result = Utilities.valueToString(val,3);
        assertEquals("[1.0, 2.0, 3.0,...]", result);

        val = ValueFactory.newVDoubleArray(data,alarm,time,display);
        result = Utilities.valueToString(val,100);
        assertEquals("[1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0]", result);

        val = ValueFactory.newVStringArray(Arrays.asList("first", "second", "third"),alarm, time);
        result = Utilities.valueToString(val);
        assertEquals("[first, second, third]", result);

        ListLong ldata = new ArrayLong(1,2,3,4,5);
        val = ValueFactory.newVLongArray(ldata,alarm,time,display);
        result = Utilities.valueToString(val,3);
        assertEquals("[1, 2, 3,...]", result);

        ListBoolean bdata = new ArrayBoolean(true,true,false,true);
        val = ValueFactory.newVBooleanArray(bdata,alarm,time);
        result = Utilities.valueToString(val,4);
        assertEquals("[true, true, false, true]", result);
    }

    /**
     * Tests {@link Utilities#valueFromString(String, VType)}.
     */
    @Test
    public void testValueFromString() {
        Alarm alarm = ValueFactory.alarmNone();
        Display display = ValueFactory.displayNone();
        Time time = ValueFactory.timeNow();

        VType val = ValueFactory.newVDouble(5d,alarm,time,display);
        VType result = Utilities.valueFromString("5.0", val);
        assertTrue(result instanceof VDouble);
        assertEquals(5.0, ((VDouble)result).getValue().doubleValue(),0);

        val = ValueFactory.newVFloat(5f,alarm,time,display);
        result = Utilities.valueFromString("5.0", val);
        assertTrue(result instanceof VFloat);
        assertEquals(5.0f, ((VFloat)result).getValue().floatValue(),0);

        val = ValueFactory.newVLong(5L,alarm,time,display);
        result = Utilities.valueFromString("5", val);
        assertTrue(result instanceof VLong);
        assertEquals(5L, ((VLong)result).getValue().longValue());

        val = ValueFactory.newVInt(5,alarm,time,display);
        result = Utilities.valueFromString("5", val);
        assertTrue(result instanceof VInt);
        assertEquals(5, ((VInt)result).getValue().intValue());

        val = ValueFactory.newVShort((short)5,alarm,time,display);
        result = Utilities.valueFromString("5", val);
        assertTrue(result instanceof VShort);
        assertEquals((short)5, ((VShort)result).getValue().shortValue());

        val = ValueFactory.newVByte((byte)5,alarm,time,display);
        result = Utilities.valueFromString("5", val);
        assertTrue(result instanceof VByte);
        assertEquals((byte)5, ((VByte)result).getValue().byteValue());

        val = ValueFactory.newVEnum(1, Arrays.asList("first", "second", "third"),alarm, time);
        result = Utilities.valueFromString("second", val);
        assertTrue(result instanceof VEnum);
        assertEquals("second", ((VEnum)result).getValue());

        val = ValueFactory.newVString("third",alarm, time);
        result = Utilities.valueFromString("third", val);
        assertTrue(result instanceof VString);
        assertEquals("third", ((VString)result).getValue());

        try {
            ListDouble data = new ArrayDouble(1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9);
            val = ValueFactory.newVDoubleArray(data,alarm,time,display);
            result = Utilities.valueFromString("[1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0,...]", val);
            fail("Exception should happen, because the number of elements is wrong");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        val = ValueFactory.newVStringArray(Arrays.asList("first", "second", "third"),alarm, time);
        result = Utilities.valueFromString("[first, second, third]", val);
        assertTrue(result instanceof VStringArray);
        assertArrayEquals(new String[]{"first","second","third"}, ((VStringArray)result).getData().toArray(new String[0]));


        ListLong ldata = new ArrayLong(1,2,3,4,5);
        val = ValueFactory.newVLongArray(ldata,alarm,time,display);
        result = Utilities.valueFromString("1, 2, 3, 4, 5", val);
        assertTrue(result instanceof VLongArray);
        assertEquals(ldata, ((VLongArray)result).getData());

        ListBoolean bdata = new ArrayBoolean(true,true,false,true);
        val = ValueFactory.newVBooleanArray(bdata,alarm,time);
        result = Utilities.valueFromString("[true, true, false, true]", val);
        assertTrue(result instanceof VBooleanArray);
        assertEquals(bdata, ((VBooleanArray)result).getData());
    }

    /**
     * Tests {@link Utilities#toRawValue(VType)}.
     */
    @Test
    public void testToRawValue() {
        Alarm alarm = ValueFactory.alarmNone();
        Display display = ValueFactory.displayNone();
        Time time = ValueFactory.timeNow();

        VType val = ValueFactory.newVDouble(5d,alarm,time,display);
        Object d = Utilities.toRawValue(val);
        assertTrue(d instanceof Double);
        assertEquals(5.0,d);

        val = ValueFactory.newVFloat(5f,alarm,time,display);
        d = Utilities.toRawValue(val);
        assertTrue(d instanceof Float);
        assertEquals(5.0f,d);

        val = ValueFactory.newVLong(5L,alarm,time,display);
        d = Utilities.toRawValue(val);
        assertTrue(d instanceof Long);
        assertEquals(5L,d);

        val = ValueFactory.newVInt(5,alarm,time,display);
        d = Utilities.toRawValue(val);
        assertTrue(d instanceof Integer);
        assertEquals(5,d);

        val = ValueFactory.newVShort((short)5,alarm,time,display);
        d = Utilities.toRawValue(val);
        assertTrue(d instanceof Short);
        assertEquals((short)5,d);

        val = ValueFactory.newVByte((byte)5,alarm,time,display);
        d = Utilities.toRawValue(val);
        assertTrue(d instanceof Byte);
        assertEquals((byte)5,d);

        val = ValueFactory.newVEnum(1, Arrays.asList("first", "second", "third"),alarm, time);
        d = Utilities.toRawValue(val);
        assertTrue(d instanceof String);
        assertEquals("second",d);

        val = ValueFactory.newVString("third",alarm, time);
        d = Utilities.toRawValue(val);
        assertTrue(d instanceof String);
        assertEquals("third",d);

        ListDouble data = new ArrayDouble(1,2,3,4,5);
        val = ValueFactory.newVDoubleArray(data,alarm,time,display);
        d = Utilities.toRawValue(val);
        assertTrue(d instanceof ListNumber);
        ListNumber l = (ListNumber)d;
        for (int i = 0; i < l.size(); i++) {
            assertEquals(data.getDouble(i), l.getDouble(i), 0);
        }
    }

    /**
     * Tests {@link Utilities#toRawStringValue(VType)}.
     */
    @Test
    public void testToRawStringValue() {
        Alarm alarm = ValueFactory.alarmNone();
        Display display = ValueFactory.displayNone();
        Time time = ValueFactory.timeNow();

        VType val = ValueFactory.newVDouble(5d,alarm,time,display);
        String result = Utilities.toRawStringValue(val);
        assertEquals("5.0", result);

        val = ValueFactory.newVFloat(5f,alarm,time,display);
        result = Utilities.toRawStringValue(val);
        assertEquals("5.0", result);

        val = ValueFactory.newVLong(5L,alarm,time,display);
        result = Utilities.toRawStringValue(val);
        assertEquals("5", result);

        val = ValueFactory.newVInt(5,alarm,time,display);
        result = Utilities.toRawStringValue(val);
        assertEquals("5", result);

        val = ValueFactory.newVShort((short)5,alarm,time,display);
        result = Utilities.toRawStringValue(val);
        assertEquals("5", result);

        val = ValueFactory.newVByte((byte)5,alarm,time,display);
        result = Utilities.toRawStringValue(val);
        assertEquals("5", result);

        val = ValueFactory.newVEnum(1, Arrays.asList("first", "second", "third"),alarm, time);
        result = Utilities.toRawStringValue(val);
        assertEquals("second~[first;second;third]", result);

        val = ValueFactory.newVString("third",alarm, time);
        result = Utilities.toRawStringValue(val);
        assertEquals("third", result);

        ListDouble data = new ArrayDouble(1,2,3,4,5);
        val = ValueFactory.newVDoubleArray(data,alarm,time,display);
        result = Utilities.toRawStringValue(val);
        assertEquals("[1.0;2.0;3.0;4.0;5.0]", result);

         val = ValueFactory.newVStringArray(Arrays.asList("first", "second", "third"),alarm, time);
        result = Utilities.toRawStringValue(val);
        assertEquals("[first;second;third]", result);

        ListLong ldata = new ArrayLong(1,2,3,4,5);
        val = ValueFactory.newVLongArray(ldata,alarm,time,display);
        result = Utilities.toRawStringValue(val);
        assertEquals("[1;2;3;4;5]", result);

        ListBoolean bdata = new ArrayBoolean(true,true,false,true);
        val = ValueFactory.newVBooleanArray(bdata,alarm,time);
        result = Utilities.toRawStringValue(val);
        assertEquals("[true;true;false;true]", result);
    }

    /**
     * Tests {@link Utilities#valueToCompareString(VType, VType, Optional)}. The test doesn't cover all possible
     * combinations, but it does cover a handful of them.
     */
    @Test
    public void testValueToCompareString() {
        Alarm alarm = ValueFactory.alarmNone();
        Display display = ValueFactory.displayNone();
        Time time = ValueFactory.timeNow();

        Optional<Threshold<?>> threshold = Optional.of(new Threshold<>(5d,-5d));

        VType val1 = ValueFactory.newVDouble(5d,alarm,time,display);
        VType val2 = ValueFactory.newVDouble(6d,alarm,time,display);
        VTypeComparison result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("5.0 \u0394-1.0", result.getString());
        assertTrue(result.getValuesEqual() < 0);
        assertTrue(result.isWithinThreshold());

        val1 = ValueFactory.newVDouble(15d,alarm,time,display);
        val2 = ValueFactory.newVDouble(6d,alarm,time,display);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("15.0 \u0394+9.0", result.getString());
        assertTrue(result.getValuesEqual() > 0);
        assertFalse(result.isWithinThreshold());

        val1 = ValueFactory.newVDouble(6d,alarm,time,display);
        val2 = ValueFactory.newVDouble(6d,alarm,time,display);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("6.0 \u03940.0", result.getString());
        assertEquals(0,result.getValuesEqual());
        assertTrue(result.isWithinThreshold());

        val1 = ValueFactory.newVLong(15L,alarm,time,display);
        val2 = ValueFactory.newVDouble(6d,alarm,time,display);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("15 \u0394+9", result.getString());
        assertTrue(result.getValuesEqual() > 0);
        assertFalse(result.isWithinThreshold());

        val1 = ValueFactory.newVDouble(15d,alarm,time,display);
        val2 = ValueFactory.newVLong(6L,alarm,time,display);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("15.0 \u0394+9.0", result.getString());
        assertTrue(result.getValuesEqual() > 0);
        assertFalse(result.isWithinThreshold());

        val1 = ValueFactory.newVDouble(15d,alarm,time,display);
        val2 = ValueFactory.newVLong(6L,alarm,time,display);
        result = Utilities.valueToCompareString(val1, val2, Optional.empty());
        assertEquals("15.0 \u0394+9.0", result.getString());
        assertTrue(result.getValuesEqual() > 0);
        assertFalse(result.isWithinThreshold());

        val1 = ValueFactory.newVDouble(15d,alarm,time,display);
        val2 = ValueFactory.newVLong(15L,alarm,time,display);
        result = Utilities.valueToCompareString(val1, val2, Optional.empty());
        assertEquals("15.0 \u03940.0", result.getString());
        assertEquals(0,result.getValuesEqual());
        assertTrue(result.isWithinThreshold());

        val1 = ValueFactory.newVString("first",alarm,time);
        val2 = ValueFactory.newVLong(15L,alarm,time,display);
        result = Utilities.valueToCompareString(val1, val2, Optional.empty());
        assertEquals("first", result.getString());
        assertNotEquals(0, result.getValuesEqual());
        assertFalse(result.isWithinThreshold());

        val1 = ValueFactory.newVDoubleArray(new ArrayDouble(1,2,3),alarm,time,display);
        val2 = ValueFactory.newVDoubleArray(new ArrayDouble(1,2,3),alarm,time,display);
        result = Utilities.valueToCompareString(val1, val2, Optional.empty());
        assertEquals("[1.0, 2.0, 3.0]", result.getString());
        assertEquals(0,result.getValuesEqual());
        assertTrue(result.isWithinThreshold());

        val1 = ValueFactory.newVDoubleArray(new ArrayDouble(1,2,3),alarm,time,display);
        val2 = ValueFactory.newVLongArray(new ArrayLong(1,2,3),alarm,time,display);
        result = Utilities.valueToCompareString(val1, val2, Optional.empty());
        assertEquals("[1.0, 2.0, 3.0]", result.getString());
        assertNotEquals(0, result.getValuesEqual());
        assertFalse(result.isWithinThreshold());

        //compare string array values: equal, non equal
        val1 = ValueFactory.newVStringArray(Arrays.asList("value1","value2"),alarm,time);
        val2 = ValueFactory.newVStringArray(Arrays.asList("value1","value2"),alarm,time);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("[value1, value2]", result.getString());
        assertEquals(0, result.getValuesEqual());
        assertTrue(result.isWithinThreshold());

        val1 = ValueFactory.newVStringArray(Arrays.asList("value1","value2"),alarm,time);
        val2 = ValueFactory.newVStringArray(Arrays.asList("value1","value3"),alarm,time);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("[value1, value2]", result.getString());
        assertTrue(result.getValuesEqual() > 0);
        assertFalse(result.isWithinThreshold());

        //compare string values: equal, first less than second, second less than first
        val1 = ValueFactory.newVString("value1",alarm,time);
        val2 = ValueFactory.newVString("value1",alarm,time);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("value1", result.getString());
        assertEquals(0, result.getValuesEqual());
        assertTrue(result.isWithinThreshold());

        val1 = ValueFactory.newVString("value1",alarm,time);
        val2 = ValueFactory.newVString("value2",alarm,time);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("value1", result.getString());
        assertTrue(result.getValuesEqual() < 0);
        assertFalse(result.isWithinThreshold());

        val1 = ValueFactory.newVString("value2",alarm,time);
        val2 = ValueFactory.newVString("value1",alarm,time);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("value2", result.getString());
        assertTrue(result.getValuesEqual() > 0);
        assertFalse(result.isWithinThreshold());

        //compare long values: equal, first less than second, second less than first
        val1 = ValueFactory.newVLong(6L,alarm,time,display);
        val2 = ValueFactory.newVLong(6L,alarm,time, display);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("6 \u03940", result.getString());
        assertEquals(0, result.getValuesEqual());
        assertTrue(result.isWithinThreshold());

        val1 = ValueFactory.newVLong(5L,alarm,time,display);
        val2 = ValueFactory.newVLong(6L,alarm,time, display);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("5 \u0394-1", result.getString());
        assertTrue(result.getValuesEqual() < 0);
        assertTrue(result.isWithinThreshold());

        val1 = ValueFactory.newVLong(6L,alarm,time,display);
        val2 = ValueFactory.newVLong(5L,alarm,time, display);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("6 \u0394+1", result.getString());
        assertTrue(result.getValuesEqual() > 0);
        assertTrue(result.isWithinThreshold());

        //compare int values: equal, first less than second, second less than first
        val1 = ValueFactory.newVInt(6,alarm,time,display);
        val2 = ValueFactory.newVInt(6,alarm,time, display);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("6 \u03940", result.getString());
        assertEquals(0, result.getValuesEqual());
        assertTrue(result.isWithinThreshold());

        val1 = ValueFactory.newVInt(5,alarm,time,display);
        val2 = ValueFactory.newVInt(6,alarm,time, display);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("5 \u0394-1", result.getString());
        assertTrue(result.getValuesEqual() < 0);
        assertTrue(result.isWithinThreshold());

        val1 = ValueFactory.newVInt(6,alarm,time,display);
        val2 = ValueFactory.newVInt(5,alarm,time, display);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("6 \u0394+1", result.getString());
        assertTrue(result.getValuesEqual() > 0);
        assertTrue(result.isWithinThreshold());

        //compare short values: equal, first less than second, second less than first
        val1 = ValueFactory.newVShort((short)6,alarm,time,display);
        val2 = ValueFactory.newVShort((short)6,alarm,time, display);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("6 \u03940", result.getString());
        assertEquals(0, result.getValuesEqual());
        assertTrue(result.isWithinThreshold());

        val1 = ValueFactory.newVShort((short)5,alarm,time,display);
        val2 = ValueFactory.newVShort((short)6,alarm,time, display);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("5 \u0394-1", result.getString());
        assertTrue(result.getValuesEqual() < 0);
        assertTrue(result.isWithinThreshold());

        val1 = ValueFactory.newVShort((short)6,alarm,time,display);
        val2 = ValueFactory.newVShort((short)5,alarm,time, display);
        result = Utilities.valueToCompareString(val1, val2, threshold);
        assertEquals("6 \u0394+1", result.getString());
        assertTrue(result.getValuesEqual() > 0);
        assertTrue(result.isWithinThreshold());

        //compare enum values: equal, first less than second, second less than first
        List<String> labels = Arrays.asList("val1","val2","val3");

        val1 = ValueFactory.newVEnum(1,labels,alarm,time);
        val2 = ValueFactory.newVEnum(1,labels,alarm,time);
        result = Utilities.valueToCompareString(val1, val2, Optional.empty());
        assertEquals("val2", result.getString());
        assertEquals(0, result.getValuesEqual());
        assertTrue(result.isWithinThreshold());

        val1 = ValueFactory.newVEnum(1,labels,alarm,time);
        val2 = ValueFactory.newVEnum(2,labels,alarm,time);
        result = Utilities.valueToCompareString(val1, val2, Optional.empty());
        assertEquals("val2", result.getString());
        assertTrue(result.getValuesEqual() < 0);
        assertFalse(result.isWithinThreshold());

        val1 = ValueFactory.newVEnum(2,labels,alarm,time);
        val2 = ValueFactory.newVEnum(1,labels,alarm,time);
        result = Utilities.valueToCompareString(val1, val2, Optional.empty());
        assertEquals("val3", result.getString());
        assertTrue(result.getValuesEqual() > 0);
        assertFalse(result.isWithinThreshold());
    }

    /**
     * Tests {@link Utilities#areVTypesIdentical(VType, VType)} method.
     *
     * @throws InterruptedException
     */
    @Test
    public void testVTypesIdentical() throws InterruptedException {
        Alarm alarm = ValueFactory.newAlarm(AlarmSeverity.INVALID, "name");
        Alarm alarm2 = ValueFactory.newAlarm(AlarmSeverity.INVALID, "name");
        Display display = ValueFactory.displayNone();
        Time time = ValueFactory.timeNow();
        Thread.sleep(1);
        Time time2 = ValueFactory.timeNow();
        VType val1 = ValueFactory.newVDouble(5d,alarm,time,display);
        VType val2 = ValueFactory.newVDouble(6d,alarm2,time2,display);

        assertFalse(Utilities.areVTypesIdentical(val1, val2, true));
        assertFalse(Utilities.areVTypesIdentical(val1, val2, false));
        val2 = ValueFactory.newVDouble(5d,alarm2,time,display);
        assertTrue(Utilities.areVTypesIdentical(val1, val2, true));
        assertTrue(Utilities.areVTypesIdentical(val1, val2, false));

        val2 = ValueFactory.newVDouble(5d,ValueFactory.alarmNone(),time,display);
        assertFalse(Utilities.areVTypesIdentical(val1, val2, true));
        assertTrue(Utilities.areVTypesIdentical(val1, val2, false));

        val2 = ValueFactory.newVDouble(5d,alarm2,time2,display);
        assertFalse(Utilities.areVTypesIdentical(val1, val2, true));
        assertTrue(Utilities.areVTypesIdentical(val1, val2, false));

        val2 = ValueFactory.newVLong(5L,alarm2,time,display);
        assertFalse(Utilities.areVTypesIdentical(val1, val2, true));
        assertFalse(Utilities.areVTypesIdentical(val1, val2, false));

        val1 = ValueFactory.newVDoubleArray(new ArrayDouble(1,2,3),alarm,time,display);
        val2 = ValueFactory.newVDoubleArray(new ArrayDouble(1,2,3),alarm,time,display);
        assertTrue(Utilities.areVTypesIdentical(val1, val2, true));
        assertTrue(Utilities.areVTypesIdentical(val1, val2, false));

        val2 = ValueFactory.newVDoubleArray(new ArrayDouble(1,2,3,4),alarm,time,display);
        assertFalse(Utilities.areVTypesIdentical(val1, val2, true));


    }
}
