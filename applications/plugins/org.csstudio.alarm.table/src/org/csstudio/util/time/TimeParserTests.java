/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 package org.csstudio.util.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import junit.framework.TestCase;

import org.junit.Test;

@SuppressWarnings("nls") //$NON-NLS-1$
public class TimeParserTests extends TestCase
{
    private static final DateFormat format = 
                      new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //$NON-NLS-1$
    
    @Test
    public void testAbsoluteTimeParser() throws Exception
    {
        Calendar cal;
        
        // Full time, with extra spaces
        cal = AbsoluteTimeParser.parse("   2007/01/18    12:10:13.123     "); //$NON-NLS-1$
        System.out.println(cal.getTime());
        assertEquals(2007, cal.get(Calendar.YEAR));
        assertEquals(1, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals(18, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(10, cal.get(Calendar.MINUTE));
        assertEquals(13, cal.get(Calendar.SECOND));
        assertEquals(123, cal.get(Calendar.MILLISECOND));

        // Empty string should not change the passed in calendar
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(cal.getTimeInMillis());
        Calendar cal3 = AbsoluteTimeParser.parse(cal2, "     "); //$NON-NLS-1$
        // Copy, or same instance?
        // But in any case should the values match what we had in cal.
        assertEquals(cal, cal3);
        
        // Only date, no time
        cal = AbsoluteTimeParser.parse("2007/01/18"); //$NON-NLS-1$
        System.out.println(cal.getTime());
        assertEquals(2007, cal.get(Calendar.YEAR));
        assertEquals(1, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals(18, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));

        // Only time, keep the date
        cal2 = AbsoluteTimeParser.parse(cal, "13:45"); //$NON-NLS-1$
        // Should return new instance, not change the cal we passed in.
        assertNotSame(cal, cal2);
        System.out.println(cal2.getTime());
        assertEquals(2007, cal2.get(Calendar.YEAR));
        assertEquals(1, cal2.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals(18, cal2.get(Calendar.DAY_OF_MONTH));
        assertEquals(13, cal2.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, cal2.get(Calendar.MINUTE));
        assertEquals(0, cal2.get(Calendar.SECOND));

        // Only month and day, but no year
        cal = AbsoluteTimeParser.parse(cal, "02/15 13:45"); //$NON-NLS-1$
        System.out.println(cal.getTime());
        assertEquals(2007, cal.get(Calendar.YEAR));
        assertEquals(2, cal.get(Calendar.MONTH) + 1); // Jan == 0
        assertEquals(15, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(13, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));
    }
    
    @Test
    public void testRelativeTimeParser() throws Exception
    {
        RelativeTimeParserResult result = RelativeTimeParser.parse("-2 Months +5 years"); //$NON-NLS-1$
        System.out.println(result);
        assertEquals(18, result.getOffsetOfNextChar());
        assertEquals(+5, result.getRelativeTime().get(RelativeTime.YEARS));
        assertEquals(-2, result.getRelativeTime().get(RelativeTime.MONTHS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.DAYS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.HOURS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.MINUTES));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.SECONDS));
        
        result = RelativeTimeParser.parse("   30 minutes -2 mon     "); //$NON-NLS-1$
        System.out.println(result);
        assertEquals(20, result.getOffsetOfNextChar());
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.YEARS));
        assertEquals(-2, result.getRelativeTime().get(RelativeTime.MONTHS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.DAYS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.HOURS));
        assertEquals(30, result.getRelativeTime().get(RelativeTime.MINUTES));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.SECONDS));

        result = RelativeTimeParser.parse("+2M 3m 08:00"); //$NON-NLS-1$
        System.out.println(result);
        assertEquals( 6, result.getOffsetOfNextChar());
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.YEARS));
        assertEquals( 2, result.getRelativeTime().get(RelativeTime.MONTHS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.DAYS));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.HOURS));
        assertEquals( 3, result.getRelativeTime().get(RelativeTime.MINUTES));
        assertEquals( 0, result.getRelativeTime().get(RelativeTime.SECONDS));
        
        // now
        result = RelativeTimeParser.parse("   NoW "); //$NON-NLS-1$
        assertEquals(true, result.getRelativeTime().isNow());
    }
    
    @Test
    public void testTimeParser() throws Exception
    {
        // abs, abs
        String start = "2006/01/02 03:04:05"; //$NON-NLS-1$
        String end = "2007/05/06 07:08:09"; //$NON-NLS-1$
        StartEndTimeParser start_end = new StartEndTimeParser(start, end);
        String start_time = format.format(start_end.getStart().getTime());
        String end_time = format.format(start_end.getEnd().getTime());
        System.out.println("   " + start + " ... " + end + "\n-> " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                           start_time + " ... " + end_time); //$NON-NLS-1$
        assertEquals(start, start_time);
        assertEquals(end, end_time);

        // rel, abs
        start = "-2 month +10 min"; //$NON-NLS-1$
        end = "2007/05/06 23:45:09"; //$NON-NLS-1$
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime());
        end_time = format.format(start_end.getEnd().getTime());
        System.out.println("   " + start + " ... " + end + "\n-> " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                           start_time + " ... " + end_time); //$NON-NLS-1$
        assertEquals("2007/03/06 23:55:09", start_time); //$NON-NLS-1$
        assertEquals("2007/05/06 23:45:09", end_time); //$NON-NLS-1$

        // rel, abs with an absolute time in the relative part
        start = "-2 days 08:15"; //$NON-NLS-1$
        end = "2007/05/06 23:45:09"; //$NON-NLS-1$
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime());
        end_time = format.format(start_end.getEnd().getTime());
        System.out.println("   " + start + " ... " + end + "\n-> " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                           start_time + " ... " + end_time); //$NON-NLS-1$
        assertEquals("2007/05/04 08:15:00", start_time); //$NON-NLS-1$
        assertEquals("2007/05/06 23:45:09", end_time); //$NON-NLS-1$
        
        // abs, rel. Also hours that roll over into next day.
        start = "2006/01/29 12:00:00"; //$NON-NLS-1$
        end = "6M 12h"; //$NON-NLS-1$
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime());
        end_time = format.format(start_end.getEnd().getTime());
        System.out.println("   " + start + " ... " + end + "\n-> " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                           start_time + " ... " + end_time); //$NON-NLS-1$
        assertEquals("2006/01/29 12:00:00", start_time); //$NON-NLS-1$
        assertEquals("2006/07/30 00:00:00", end_time); //$NON-NLS-1$

        // rel, rel
        start = "-6h"; //$NON-NLS-1$
        end = "-2h"; //$NON-NLS-1$
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime());
        end_time = format.format(start_end.getEnd().getTime());
        System.out.println("   " + start + " ... " + end + "\n-> " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                           start_time + " ... " + end_time); //$NON-NLS-1$
        long now = Calendar.getInstance().getTimeInMillis() / 1000;
        // Get seconds to 'now'
        double start_diff_sec = now - start_end.getStart().getTimeInMillis()/1000;
        double end_diff_sec = now - start_end.getEnd().getTimeInMillis()/1000;
        assertEquals(8*60*60.0, start_diff_sec, 10.0);
        assertEquals(2*60*60.0, end_diff_sec, 10.0);

        // rel, now
        start = "-6h"; //$NON-NLS-1$
        end = "now"; //$NON-NLS-1$
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime());
        end_time = format.format(start_end.getEnd().getTime());
        System.out.println("   " + start + " ... " + end + "\n-> " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                           start_time + " ... " + end_time); //$NON-NLS-1$
        now = Calendar.getInstance().getTimeInMillis() / 1000;
        // Get seconds to 'now'
        start_diff_sec = now - start_end.getStart().getTimeInMillis()/1000;
        end_diff_sec = now - start_end.getEnd().getTimeInMillis()/1000;
        assertEquals(6*60*60.0, start_diff_sec, 10.0);
        assertEquals(0.0, end_diff_sec, 10.0);
    
        // rel, now
        start = "-6h"; //$NON-NLS-1$
        end = ""; //$NON-NLS-1$
        start_end = new StartEndTimeParser(start, end);
        start_time = format.format(start_end.getStart().getTime());
        end_time = format.format(start_end.getEnd().getTime());
        System.out.println("   " + start + " ... " + end + "\n-> " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                           start_time + " ... " + end_time); //$NON-NLS-1$
        now = Calendar.getInstance().getTimeInMillis() / 1000;
        // Get seconds to 'now'
        start_diff_sec = now - start_end.getStart().getTimeInMillis()/1000;
        end_diff_sec = now - start_end.getEnd().getTimeInMillis()/1000;
        assertEquals(6*60*60.0, start_diff_sec, 10.0);
        assertEquals(0.0, end_diff_sec, 10.0);
    }
}
