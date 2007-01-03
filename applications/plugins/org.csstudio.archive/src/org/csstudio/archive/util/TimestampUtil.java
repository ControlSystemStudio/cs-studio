package org.csstudio.archive.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.csstudio.platform.util.ITimestamp;
import org.csstudio.platform.util.TimestampFactory;

/** Helper for ITimestamp.
 *  @author Kay Kasemir
 *  @see ITimestamp
 */
public class TimestampUtil
{
    private static SimpleDateFormat time_parser = null;

    /** Create a time stamp from fractional seconds. */
    public static ITimestamp fromDouble(final double seconds)
    {
        long secs = (long) seconds;
        long nano = (long)((seconds - secs)*1e9);
        return TimestampFactory.createTimestamp(secs, nano);
    }

    /** Create a time stamp from pieces. */
    public static ITimestamp fromPieces(
                    final int year, final int month, final int day,
                    final int hours, final int minutes, final int seconds,
                    final long nano)
    {
        Calendar cal =
            new GregorianCalendar(year, month-1, day, hours, minutes, seconds);
        final long millis = cal.getTimeInMillis();
        final long secs = millis / 1000L;
        return TimestampFactory.createTimestamp(secs, nano);
    }

    /** Create a time stamp from pieces.
     *  <p>
     *  Order of array elements is the same as returned by 
     *  ITimestamp.toPieces().
 */
    public static ITimestamp fromPieces(final long pieces[])
    {
        return fromPieces((int)pieces[0], (int)pieces[1],
                        (int)pieces[2], (int)pieces[3],
                        (int)pieces[4], (int)pieces[5], pieces[6]);
    }
    
    /** Parse time stamp from string "yyyy/MM/dd HH:mm:ss" */
    @SuppressWarnings("nls")
    public static ITimestamp fromString(final String text) throws Exception
    {
        if (time_parser == null)
            time_parser = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date d;
        try
        {
            if (text.length() == 10) // "yyyy/MM/dd" -> append "00:00:00"
                d = time_parser.parse(text + " 00:00:00");
            else if (text.length() == 16) // "yyyy/MM/dd HH:mm" -> append ":00"
                    d = time_parser.parse(text + ":00");
            else
                d = time_parser.parse(text.substring(0, 19));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception("Date has to match '2006-01-18',"
                    + " and time has to match '15:42'", e);
        }
        long millis = d.getTime();
        long secs = millis / 1000;
        long nano = (millis - secs*1000) * 1000000;
        return TimestampFactory.createTimestamp(secs, nano);
    }
    
    /** Parse time string, return seconds.
     *  <p>
     *  Allowed formats:
     *  <ul>
     *  <li>"1234" - results in 1234 seconds
     *  <li>"1:30" - results in 1 minute, 30 seconds = 90 seconds
     *  <li>"1:20:30" - results in 1 hour, 20 minutes, 30 seconds = ...
     *  </ul>
     *  Special cases:
     *  One overall '-' is allowed to specify negative seconds.
     *  
     *  @return The seconds parsed from the string.
     *  @throws Exception on parse error.
     */
    @SuppressWarnings("nls")
    public static double parseSeconds(String text) throws Exception
    {
        final char sep = ':';
        text = text.trim();
        if (text.length() <= 1)
            throw new Exception("Empty String");

        double sign;
        if (text.charAt(0) == '-')
        {
            text = text.substring(1);
            sign = -1.0;
        }
        else
            sign = +1.0;
        // Get the first number out of the string
        int i = text.indexOf(sep);
        if (i < 0) // That's it.
            return sign * Double.parseDouble(text);
        
        // There's more; these are the hours or minutes
        double secs = 60 * Double.parseDouble(text.substring(0, i));

        int j = text.indexOf(sep, i+1);
        if (j < 0) // That's it: Minutes and hours
            return sign * (secs + Double.parseDouble(text.substring(i+1)));
        // Hours, minutes, and seconds
        secs = 60 * (secs + Double.parseDouble(text.substring(i+1, j)));
        
        // More than two ':'?
        if (text.indexOf(sep, j+1) >= 0)
            throw new Exception("Invalid format");
        // Get the seconds
        return sign * (secs + Double.parseDouble(text.substring(j+1)));
    }
}
