package org.csstudio.simplepv;

import static org.diirt.vtype.ValueFactory.newAlarm;
import static org.diirt.vtype.ValueFactory.newDisplay;
import static org.diirt.vtype.ValueFactory.newTime;
import static org.diirt.vtype.ValueFactory.newVDouble;
import static org.junit.Assert.assertEquals;

import java.time.Instant;

import org.csstudio.simplepv.VTypeHelper;
import org.diirt.util.text.NumberFormats;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.Time;
import org.diirt.vtype.VDouble;
import org.diirt.vtype.VType;
import org.junit.Before;
import org.junit.Test;

public class VTypeHelperTest {

    private Time testTime;
    private static final Alarm alarmNone = newAlarm(AlarmSeverity.NONE, "NONE");
    private static final Display displayNone = newDisplay(Double.NaN, Double.NaN,
            Double.NaN, "", NumberFormats.toStringFormat(), Double.NaN, Double.NaN,
            Double.NaN, Double.NaN, Double.NaN);


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

}
