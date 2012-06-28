/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.util;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.epics.util.time.TimestampFormat;

/**
 * Implements a formatting class to format timestamps. The format String
 * is exactly the one supported by the {@link SimpleDateFormat}, except
 * that it supports the nanosecond field identified by N.
 * <p>
 * For example, the pattern "yyyy-MM-dd'T'HH:mm:ss.NNNNNNNNNZ" formats to 2001-07-04T12:08:56.012345678-0700
 * <p>
 * Parsing is not currently supported.
 *
 * @deprecated This class is being retired in favor of {@link TimestampFormat}
 * @author carcassi
 */
@Deprecated
public class TimeStampFormat extends Format {

    private final DateFormat dateFormat;
    private String convertedPattern;
    // This regex pattern matches first escaped quotes and text strings,
    // to get them out of the way, and then it matches strings of Ns
    private Pattern matchNanoField = Pattern.compile("(?:(?:'')+|(?:'[^']+')|(N+))");
    private List<NumberFormat> nanoFieldFormats = new ArrayList<NumberFormat>();
    private List<String> nanoFieldStrings = new ArrayList<String>();


    /**
     * Creates a new TimeStampFormat using the current Locale.
     *
     */
    public TimeStampFormat() {
        dateFormat = new SimpleDateFormat();
    }

    /**
     * Creates a new TimeStampFormat using the given pattern and the symbols
     * from the current Locale.
     *
     * @param pattern the pattern describing the date and time format
     */
    public TimeStampFormat(String pattern) {
        initPattern(pattern);
        dateFormat = new SimpleDateFormat(convertedPattern);
    }

    /**
     * Creates a new TimeStampFormat using the given pattern and symbols.
     *
     * @param pattern the pattern describing the date and time format
     * @param symbols symbols to be used in formatting
     */
    public TimeStampFormat(String pattern, DateFormatSymbols symbols) {
        initPattern(pattern);
        dateFormat = new SimpleDateFormat(convertedPattern, symbols);
    }

    /**
     * Creates a new TimeStampFormat using the given pattern and the symbols
     * from the current Locale.
     *
     * @param pattern the pattern describing the date and time format
     * @param locale the Locale to be used
     */
    public TimeStampFormat(String pattern, Locale locale) {
        initPattern(pattern);
        dateFormat = new SimpleDateFormat(convertedPattern, locale);
    }

    private void initPattern(String pattern) {
        Matcher m = matchNanoField.matcher(pattern);
        StringBuffer newPattern = new StringBuffer();
        while (m.find()) {
            String match = m.group();
            if (!match.startsWith("'")) {
                int nField = nanoFieldFormats.size() + 1;
                String nanoFieldString = "MS" + nField;
                nanoFieldFormats.add(new DecimalFormat(match.replace('N', '0')));
                nanoFieldStrings.add(nanoFieldString);
                m.appendReplacement(newPattern, "'" + nanoFieldString + "'");
            }
        }
        m.appendTail(newPattern);
        convertedPattern = newPattern.toString();
    }

    /**
     * Changes the time zone used to format the timestamp.
     *
     * @param zone a new time zone
     */
    public void setTimeZome(TimeZone zone) {
        dateFormat.setTimeZone(zone);
    }

    /**
     * Returns the time zone used to format the timestamp.
     *
     * @return the current time zone
     */
    public TimeZone getTimeZone() {
        return dateFormat.getTimeZone();
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj instanceof TimeStamp) {
            TimeStamp time = (TimeStamp) obj;
            int begin = toAppendTo.length();
            StringBuffer buf =  dateFormat.format(time.asDate(), toAppendTo, pos);

            for (int nField = 0; nField < nanoFieldStrings.size(); nField++) {
                String tag = nanoFieldStrings.get(nField);
                int start = buf.indexOf(tag, begin);
                if (start != -1)
                    buf.replace(start, start + tag.length(), nanoFieldFormats.get(nField).format(time.getNanoSec()));
            }

            return buf;
        } else {
            throw new IllegalArgumentException("Cannot format given Object as TimeStamp");
        }
    }

    /**
     * Formats the given TimeStamp.
     *
     * @param timeStamp a TimeStamp
     * @return the formatted String
     */
    public String format(TimeStamp timeStamp) {
        return format((Object) timeStamp);
    }

    /**
     * NB: Not supported
     */
    @Override
    public Object parseObject(String source, ParsePosition pos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
