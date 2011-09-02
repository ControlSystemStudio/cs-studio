
/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 */

package org.csstudio.archive.sdds.server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Markus Moeller
 *
 */
public class TimeConverter
{
    /**
     * The method gets a date and time string and returns the time in ms.
     *
     * @param date
     * @param format
     * @return
     */
    public static long convertToLong(final String date, final String format)
    {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        long result = -1;

        try
        {
            result = dateFormat.parse(date).getTime();
        }
        catch(final ParseException pe)
        {
            result = -1;
        }

        return result;
    }

    /**
     *
     * @param start
     * @param end
     * @return
     */
    public static int[] getYears(final long start, final long end) {

        long startTime = start;
        long endTime = end;

        if(startTime > endTime) {
            final long temp = endTime;
            endTime = startTime;
            startTime = temp;
        }

        final GregorianCalendar calStart = new GregorianCalendar();
        calStart.setTimeInMillis(start);

        final GregorianCalendar calEnd = new GregorianCalendar();
        calEnd.setTimeInMillis(end);

        final int[] result = new int[calEnd.get(Calendar.YEAR) - calStart.get(Calendar.YEAR) + 1];

        for(int i = calStart.get(Calendar.YEAR);i <= calEnd.get(Calendar.YEAR);i++) {
            result[i - calStart.get(Calendar.YEAR)] = i;
        }

        return result;
    }

    /**
     *
     * @param time
     * @return
     */
    public static int getMonth(final long time)
    {
        GregorianCalendar calTime = null;

        calTime = new GregorianCalendar();
        calTime.setTimeInMillis(time);

        return calTime.get(Calendar.MONTH) + 1;
    }

    /**
     *
     * @param time
     * @return
     */
    public static String getMonthAsString(final long time)
    {
        GregorianCalendar calTime = null;
        String result = null;
        int m;

        calTime = new GregorianCalendar();
        calTime.setTimeInMillis(time);
        m = calTime.get(Calendar.MONTH) + 1;

        result = m < 10 ? new String("0" + m) : Integer.toString(m);

        return result;
    }

    // ns : 1.000.000 = ms
}
