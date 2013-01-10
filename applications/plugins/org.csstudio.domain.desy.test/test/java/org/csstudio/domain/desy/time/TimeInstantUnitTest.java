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
package org.csstudio.domain.desy.time;

import java.util.regex.Pattern;

import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link TimeInstant}.
 *
 * @author bknerr
 * @since 18.11.2010
 */
public final class TimeInstantUnitTest {

    @Test(expected=IllegalArgumentException.class)
    public void constructorFactoryMethodFromNanosNegative() {
        TimeInstantBuilder.fromNanos(-1L);
    }
    @Test(expected=IllegalArgumentException.class)
    public void constructorFactoryMethodFromMillisNegative() {
        TimeInstantBuilder.fromMillis(-1L);
    }
    @Test(expected=IllegalArgumentException.class)
    public void constructorFactoryMethodFromSecondsNegative() {
        TimeInstantBuilder.fromSeconds(-1L);
    }
    @Test(expected=IllegalArgumentException.class)
    public void constructorFactoryMethodFromTooManySeconds() {
        TimeInstantBuilder.fromSeconds(TimeInstant.MAX_SECONDS + 1);
    }


    @Test
    public void constructorFactoryMethodsValidZero() {

        TimeInstant ts = TimeInstantBuilder.fromNanos(0L);

        Assert.assertEquals(ts.getFractalMillisInNanos(), 0L);
        Assert.assertEquals(ts.getMillis(), 0L);
        Assert.assertEquals(ts.getSeconds(), 0L);

        ts = TimeInstantBuilder.fromMillis(0L);

        Assert.assertEquals(ts.getFractalMillisInNanos(), 0L);
        Assert.assertEquals(ts.getMillis(), 0L);
        Assert.assertEquals(ts.getSeconds(), 0L);

        ts = TimeInstantBuilder.fromSeconds(0L);

        Assert.assertEquals(ts.getFractalMillisInNanos(), 0L);
        Assert.assertEquals(ts.getMillis(), 0L);
        Assert.assertEquals(ts.getSeconds(), 0L);
    }

    @Test
    public void constructorFactoryMethodsValid() {

        TimeInstant ts = TimeInstantBuilder.fromNanos(999123456789L);

        Assert.assertEquals(456789L, ts.getFractalMillisInNanos());
        Assert.assertEquals(123456789L, ts.getFractalSecondsInNanos());
        Assert.assertEquals(999123456789L, ts.getNanos());
        Assert.assertEquals(999123L, ts.getMillis());
        Assert.assertEquals(999L, ts.getSeconds());

        ts = TimeInstantBuilder.fromMillis(999123L);

        Assert.assertEquals(0L, ts.getFractalMillisInNanos());
        Assert.assertEquals(123000000L, ts.getFractalSecondsInNanos());
        Assert.assertEquals(999123000000L, ts.getNanos());
        Assert.assertEquals(999123L, ts.getMillis());
        Assert.assertEquals(999L, ts.getSeconds());

        ts = TimeInstantBuilder.fromSeconds(999L);

        Assert.assertEquals(0L, ts.getFractalMillisInNanos());
        Assert.assertEquals(0L, ts.getFractalSecondsInNanos());
        Assert.assertEquals(999000000000L, ts.getNanos());
        Assert.assertEquals(999000L, ts.getMillis());
        Assert.assertEquals(999L, ts.getSeconds());


    }

    @Test
    public void compareTimeTest() {
        final TimeInstant first = TimeInstantBuilder.fromMillis(0L);
        final TimeInstant now = TimeInstantBuilder.fromNow();

        Assert.assertEquals(0, first.compareTo(first));
        Assert.assertEquals(-1, first.compareTo(now));
        Assert.assertEquals(1, now.compareTo(first));
        Assert.assertFalse(first.equals(now));
        Assert.assertFalse(first.equals(now));
        Assert.assertFalse(first.getInstant().equals(now.getInstant()));

        final TimeInstant t1 = TimeInstantBuilder.fromMillis(123456L);
        final TimeInstant t2 = TimeInstantBuilder.fromMillis(123456L);
        Assert.assertEquals(0, t1.compareTo(t2));
        Assert.assertEquals(0, t2.compareTo(t1));
        Assert.assertTrue(t1.equals(t2));
        Assert.assertTrue(t2.equals(t1));
        Assert.assertTrue(t1.getInstant().equals(t2.getInstant()));
        Assert.assertFalse(t1.isAfter(t2));
        Assert.assertFalse(t2.isAfter(t1));
        Assert.assertFalse(t1.isBefore(t2));
        Assert.assertFalse(t2.isBefore(t1));

        final TimeInstant t3 = TimeInstantBuilder.fromNanos(123456000000L);
        final TimeInstant t4 = TimeInstantBuilder.fromNanos(123456000001L);
        Assert.assertEquals(-1, t3.compareTo(t4));
        Assert.assertEquals(1, t4.compareTo(t3));
        Assert.assertFalse(t3.equals(t4));
        Assert.assertFalse(t4.equals(t3));
        Assert.assertTrue(t3.getInstant().equals(t4.getInstant()));
        Assert.assertFalse(t3.isAfter(t4));
        Assert.assertTrue(t4.isAfter(t3));
        Assert.assertTrue(t3.isBefore(t4));
        Assert.assertFalse(t4.isBefore(t3));
    }


    @Test(expected=IllegalArgumentException.class)
    public void invalidMinusTest1() {
        final TimeInstant t1 = TimeInstantBuilder.fromNanos(0L);
        t1.minusMillis(1L);
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidMinusTest2() {
        final TimeInstant t1 = TimeInstantBuilder.fromNanos(0L);
        t1.minusMillis(-1L);
    }

    @Test
    public void minusSecondsTest() {
        TimeInstant t = TimeInstantBuilder.fromSeconds(222L);
        TimeInstant result = t.minusSeconds(200L);
        Assert.assertTrue(result.getSeconds() == 22L);

        t = TimeInstantBuilder.fromNanos(5123456789L);
        result = t.minusSeconds(5L);
        Assert.assertTrue(result.getSeconds() == 0L);
        Assert.assertTrue(result.getFractalSecondsInNanos() == 123456789L);
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidMinusSecondsTest() {
        final TimeInstant t1 = TimeInstantBuilder.fromSeconds(10L);
        t1.minusSeconds(12L);
    }

    @Test
    public void minusMillisTest() {
        TimeInstant t = TimeInstantBuilder.fromMillis(222L);
        TimeInstant result = t.minusMillis(200L);
        Assert.assertTrue(result.getMillis() == 22L);

        t = TimeInstantBuilder.fromNanos(123456789L);
        result = t.minusMillis(123L);
        Assert.assertTrue(result.getMillis() == 0L);
        Assert.assertTrue(result.getFractalSecondsInNanos() == 456789L);
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidMinusMillisTest() {
        final TimeInstant t1 = TimeInstantBuilder.fromNanos(1L);
        t1.minusMillis(2L);
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidPlusTest1() {
        final TimeInstant t1 = TimeInstantBuilder.fromNanos(0L);
        t1.plusMillis(-1L);
    }

    @Test(expected=IllegalArgumentException.class)
    public void invalidPlusTest2() {
        final TimeInstant t1 = TimeInstantBuilder.fromNanos(0L);
        t1.plusNanosPerSecond(-1L);
    }

    @Test
    public void plusTest() {
        final TimeInstant t1 = TimeInstantBuilder.fromNanos(0L);
        final TimeInstant t2 = t1.plusMillis(0L);
        Assert.assertTrue(t1.equals(t2));

        final TimeInstant t3 = t1.plusMillis(1L);
        Assert.assertTrue(t3.isAfter(t2));
        Assert.assertEquals(1L, t3.getMillis());

        final TimeInstant t4 = t3.plusNanosPerSecond(999000000);
        Assert.assertTrue(t4.isAfter(t3));
        Assert.assertEquals(1000L, t4.getMillis());

        final TimeInstant t5 = t1.plusNanosPerSecond(1L);
        Assert.assertTrue(t4.isAfter(t1));
        Assert.assertEquals(TimeInstantBuilder.fromNanos(1L), t5);

        final TimeInstant t6 = t1.plusMillis(123L).plusNanosPerSecond(456789L);
        Assert.assertEquals(TimeInstantBuilder.fromNanos(123456789L), t6);
        Assert.assertEquals(TimeInstantBuilder.fromNanos(123000000L).plusNanosPerSecond(456789L), t6);
    }

    @Test
    public void testFormats() {
        final TimeInstant now = TimeInstantBuilder.fromNow();

        final String date = now.formatted(TimeInstant.STD_DATE_FMT);
        Assert.assertTrue(Pattern.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d", date));

        final String time = now.formatted(TimeInstant.STD_TIME_FMT);
        Assert.assertTrue(Pattern.matches("\\d\\d:\\d\\d:\\d\\d", time));

        final String timeFS = now.formatted(TimeInstant.STD_TIME_FMT_FOR_FS);
        Assert.assertTrue(Pattern.matches("\\d\\d_\\d\\d_\\d\\d", timeFS));

        final String dateTime = now.formatted(TimeInstant.STD_DATETIME_FMT);
        Assert.assertTrue(Pattern.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d", dateTime));

        final String dateTimeWithMS = now.formatted(TimeInstant.STD_DATETIME_FMT_WITH_MILLIS);
        Assert.assertTrue(Pattern.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d", dateTimeWithMS));

        final String dateTimeFS = now.formatted(TimeInstant.STD_DATETIME_FMT_FOR_FS);
        Assert.assertTrue(Pattern.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d_\\d\\dh\\d\\dm\\d\\ds", dateTimeFS));
    }

    @Test
    public void testPlusDuration() {
        final TimeInstant ti = TimeInstantBuilder.fromMillis(0L);
        TimeInstant plusTi = ti.plusDuration(Duration.ZERO);
        Assert.assertEquals(ti, plusTi);

        plusTi = ti.plusDuration(Duration.standardSeconds(1L));
        Assert.assertEquals(TimeInstantBuilder.fromSeconds(1L), plusTi);

        plusTi = ti.plusDuration(Duration.standardHours(1L)).plusNanosPerSecond(123L);

        Assert.assertEquals(TimeInstantBuilder.fromNanos(3600000000123L), plusTi);
    }
}
