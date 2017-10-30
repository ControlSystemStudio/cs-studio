/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.csstudio.java.time.TimestampFormats;

/** Parse an absolute date/time string.
 *  The logic of this date/time parser is not speed optimized. This should not be used for consecutive parses. It should be used in
 *  cases of parsing inputs from user.
 *
 *  @see #parse(Calendar, String)
 *
 *  @author Borut Terpinc
 *          Changed the class to use DateTimeFormatter for parsing the date. Formats with zone are accepted.
 *          Added parsers for additional time-stamp formats.
 *
 *  @author Sergei Chevtsov developed the original code for the
 *          Java Archive Viewer, from which this code heavily borrows.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AbsoluteTimeParser
{

    private AbsoluteTimeParser(){}

    /** The accepted date formats for absolute times. */
    private static final DateTimeFormatter[] parsers = new DateTimeFormatter[]
    {   // Most complete version first
        TimestampFormats.FULL_FORMAT,
        TimestampFormats.MILLI_FORMAT,
        TimestampFormats.SECONDS_FORMAT,
        TimestampFormats.TIME_FORMAT,
        TimestampFormats.DATESHORT_FORMAT,
        TimestampFormats.DATE_FORMAT,
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnnnnnX"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmX"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HHX"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };


    /** Like parse(), using a base calendar of 'now', 'current time zone'.
     *  @see #parse(Calendar, String)
     *  @return Calendar initialized from parsed text.
     */
    public static Calendar parse(String text)
    {
        Calendar cal = Calendar.getInstance();
        return parse(cal, text);
    }

    /** Adjust given calendar to the date and time parsed from the text.
     *  <p>
     *  The date/time text should follow the formats defined in {@link org.csstudio.java.time.TimestampFormats}.
     *
     *  The milliseconds (sss), seconds(ss), minutes(mm), hours(hh)
     *  might be left off, and will then default to zero.
     *  <p>
     *  When omitting the year, the year from the passed-in calendar is used.
     *  When omitting the whole date (YYYY/MM/DD), the values from the passed-in
     *  calendar are used.
     *  It is not possible to provide <i>only</i> the month <i>without</i>
     *  the day or vice versa.
     *  <p>
     *  An empty text leaves the provided calendar unchanged.
     *  <p>
     *  All other cases result in an exception.
     *
     *  @param cal Base calendar, defines the time zone as well as
     *             the year, in case the text doesn't include a year.
     *  @param text The text to parse.
     *  @return Adjusted Calendar.
     *
     */
    public static Calendar parse(Calendar cal, String text)
    {

        String cooked = text.trim();
        // Empty string? Pass cal as is back, since we didn't change it?
        if (cooked.length() < 1)
            return cal;

        //remove the spaces in between
        cooked = cooked.replaceAll("\\s+"," ");
        //if the ISO time is with T, we leave remove the empty space after the T
        cooked = cooked.replaceAll("T\\s","T");
        // Provide missing year from given cal

        cooked = addDateIfNotProvided(cal, cooked);

        for (DateTimeFormatter parser : parsers)
        {
            try
            {
                ZonedDateTime parsedDateTime =  ZonedDateTime.parse(cooked, parser);
                return GregorianCalendar.from(parsedDateTime);
            }
            catch (DateTimeParseException e){} // Ignore, try the next one

            //if only local datetime is provided as local we pass the system zone
            try
            {
                LocalDateTime localDateTime =  LocalDateTime.parse(cooked, parser);
                ZonedDateTime parsedDateTime =  ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
                return GregorianCalendar.from(parsedDateTime);
            }
            catch (DateTimeParseException e){}// Ignore, try the next one
            //if just date is provided we parse the date and add start of day time and the system zone

            try
            {
                LocalDate localDate =  LocalDate.parse(cooked, parser);
                ZonedDateTime zonedDateTime =ZonedDateTime.of(localDate, LocalTime.MIN, ZoneId.systemDefault());
                return GregorianCalendar.from(zonedDateTime);
            }
            catch (DateTimeParseException e){}// Ignore, try the next one
        }
        throw new DateTimeParseException("Cannot parse date and time!" , text, 0);
    }

    /***
     * If there is no date, we use the one from the param calendar.
     *
     * @param cal used for date
     * @param dateTimeText date-time string to check and change
     * @return reformatted date-time string.
     */

    private static String addDateIfNotProvided(Calendar cal, String dateTimeText) {
        int datesep = dateTimeText.indexOf('-');
        if (datesep < 0){ // No date at all provided? Use the one from cal.
            dateTimeText = String.format("%04d-%02d-%02d %s",
                                   cal.get(Calendar.YEAR),
                                   cal.get(Calendar.MONTH) + 1,
                                   cal.get(Calendar.DAY_OF_MONTH),
                                   dateTimeText);
        }
        else
        {   // Are there two date separators?
            datesep = dateTimeText.indexOf('-', datesep + 1);
            // If not, assume that we have MM-DD, and add the YYYY.
            if (datesep < 0)
                dateTimeText = String.format("%04d-%s",
                                       cal.get(Calendar.YEAR), dateTimeText);
        }
        return dateTimeText;
    }

    /** Format given calendar value into something that this parser would handle.
     *  @return Date and time string.
     */
    public static String format(Calendar cal)
    {
        return TimestampFormats.FULL_FORMAT.format(cal.toInstant());
    }
}
