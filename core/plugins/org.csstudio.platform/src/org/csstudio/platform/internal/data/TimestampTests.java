/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.platform.internal.data;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.csstudio.platform.data.ITimestamp;
import org.junit.Test;

/** Tests of the {@link Timestamp} class.
 *  @author Kay Kasemir
 */
public final class TimestampTests
{
    /** Basic timestamp construction. */
    @SuppressWarnings("nls")
    @Test
    public void createStamps()
    {
        ITimestamp stamp;
        
        // long, long
        stamp = new Timestamp(0L, 0L);
        assertEquals(0L, stamp.seconds());
        assertEquals(0L, stamp.nanoseconds());
        assertEquals(false, stamp.isValid());
        
        // double
        stamp = new Timestamp(0.0);
        assertEquals(0L, stamp.seconds());
        assertEquals(0L, stamp.nanoseconds());
        assertEquals(false, stamp.isValid());

        // nanos roll over
        stamp = new Timestamp(1L, 2500000000L);
        assertEquals(3L, stamp.seconds());
        assertEquals(500000000L, stamp.nanoseconds());
        assertEquals(true, stamp.isValid());
        
        // To string
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2007, 1, 18);
        stamp = new Timestamp(cal.getTimeInMillis()/1000L, 0L);
        // Note: Cannot check seconds, since those would be based on UTC,
        //       while the calendar is in 'local' time.
        // But the end result, the local time string, should
        // match what we put in.
        System.out.println(stamp.toString());
        assertEquals("2007/02/18 00:00:00.000000000", stamp.toString());

        // Down to seconds
        cal.set(2007, 1, 18, 13, 45, 59);
        stamp = new Timestamp(cal.getTimeInMillis()/1000L, 0L);
        System.out.println(stamp.toString());
        assertEquals("2007/02/18 13:45:59.000000000", stamp.toString());

        // Down to nanoseconds
        stamp = new Timestamp(cal.getTimeInMillis()/1000L, 123456789L);
        System.out.println(stamp.toString());
        assertEquals("2007/02/18 13:45:59.123456789", stamp.toString());
    }
    
    /**
     * Test method for {@link Timestamp#equals(Object)}.
     * 
    @Test
    public void testEquality()
    {
        // Basic conversions from/to pieces and strings
        ITimestamp a = Timestamp.fromPieces(1990, 1, 18, 13, 30, 20, 0);
        ITimestamp b = Timestamp.fromPieces(1990, 1, 18, 13, 30, 20, 0);
        ITimestamp c = TimestampFactory.now();

        assertTrue(a.equals(a));
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));

        assertTrue(a.isGreaterOrEqual(a));
        assertTrue(a.isGreaterOrEqual(b));
        assertTrue(b.isGreaterOrEqual(a));

        assertTrue(c.isGreaterOrEqual(a));
        assertTrue(c.isGreaterOrEqual(b));
        assertTrue(c.isGreaterOrEqual(c));

        assertTrue(c.isGreaterThan(a));
        assertTrue(c.isGreaterThan(b));
        assertFalse(c.isGreaterThan(c));

        assertFalse(a.equals(c));
        assertFalse(b.equals(c));
        assertFalse(c.equals(a));
    }
         */

}
