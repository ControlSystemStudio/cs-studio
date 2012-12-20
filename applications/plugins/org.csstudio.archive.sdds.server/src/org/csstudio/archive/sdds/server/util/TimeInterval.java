
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

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.annotation.Nonnull;

/**
 * @author Markus Moeller
 *
 */
public class TimeInterval {

    /** */
    private final DateType dateType;

    /** */
    private long startTime;

    /** */
    private long endTime;

    /** */
    private int[] years;

    /** */
    private int startYear;

    /** */
    private int endYear;

    /** */
    private int startMonth;

    /** */
    private int endMonth;

    /**
     *
     */
    public TimeInterval() {
        this(-1, -1, DateType.UNIX);
    }

    /**
     *
     * @param begin
     * @param end
     */
    public TimeInterval(final long begin, final long end) {
        this(begin, end, DateType.UNIX);
    }

    public TimeInterval(final long begin, final long end, @Nonnull final DateType type) {
        this.startTime = begin;
        this.endTime = end;
        this.dateType = type;

        init();
    }

    /**
     *
     */
    private void init() {

        GregorianCalendar calStart = null;
        GregorianCalendar calEnd = null;
        long temp;

        if(startTime == -1 || endTime == -1) {
            years = new int[0];
            return;
        }

        if(startTime > endTime) {
            temp = endTime;
            endTime = startTime;
            startTime = temp;
        }

        // Convert from UNIX timestamp to Java timestamp (seconds + miliseconds)
        if(dateType == DateType.UNIX) {
            startTime *= 1000;
            endTime *= 1000;
        }

        calStart = new GregorianCalendar();
        calStart.setTimeInMillis(startTime);

        calEnd = new GregorianCalendar();
        calEnd.setTimeInMillis(endTime);

        years = new int[calEnd.get(Calendar.YEAR) - calStart.get(Calendar.YEAR) + 1];

        for(int i = calStart.get(Calendar.YEAR); i <= calEnd.get(Calendar.YEAR); i++)
        {
            years[i - calStart.get(Calendar.YEAR)] = i;
        }

        startMonth = calStart.get(Calendar.MONTH) + 1;
        endMonth = calEnd.get(Calendar.MONTH) + 1;

        startYear = calStart.get(Calendar.YEAR);
        endYear = calEnd.get(Calendar.YEAR);
    }

    @Nonnull
    public final int[] getYears() {
        return years;
    }

    public final int getStartYear() {
        return startYear;
    }

    public final int getEndYear() {
        return endYear;
    }

    public final int getStartMonth() {
        return startMonth;
    }

    public final int getEndMonth() {
        return endMonth;
    }

    @Nonnull
    public final String getStartMonthAsString() {
        String result = null;
        final int m = getStartMonth();

        result = m > 9 ? Integer.toString(m) : new String("0" + m);

        return result;
    }

    @Nonnull
    public final String getEndMonthAsString() {
        String result = null;
        final int m = getEndMonth();

        result = m > 9 ? Integer.toString(m) : new String("0" + m);

        return result;
    }

    /**
     *
     * TODO (mmoeller) :
     *
     * @author mmoeller
     * @since 04.11.2011
     */
    public enum DateType {
        UNIX,
        JAVA
    }
}
