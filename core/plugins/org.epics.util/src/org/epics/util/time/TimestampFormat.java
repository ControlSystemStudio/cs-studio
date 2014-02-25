/**
 * Copyright (C) 2012-14 epics-util developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.util.time;

import java.text.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements a formatting class to format Timestamps. The format String
 * is exactly the one supported by the {@link SimpleDateFormat}, except
 * that it supports the nanosecond field identified by N.
 * <p>
 * For example, the pattern "yyyy-MM-dd'T'HH:mm:ss.NNNNNNNNNZ" formats to 2001-07-04T12:08:56.012345678-0700
 * <p>
 * Parsing is not currently supported if nanosecond field is used.
 *
 * @author carcassi
 */
public class TimestampFormat extends Format {

    private final DateFormat dateFormat;
    private String convertedPattern;
    // This regex pattern matches first escaped quotes and text strings,
    // to get them out of the way, and then it matches strings of Ns
    private Pattern matchNanoField = Pattern.compile("(?:(?:'')+|(?:'[^']+')|(N+))");
    private List<NumberFormat> nanoFieldFormats = new ArrayList<NumberFormat>();
    private List<String> nanoFieldStrings = new ArrayList<String>();
    private boolean nanoPattern = false;


    /**
     * Creates a new TimestampFormat using the current Locale.
     *
     */
    public TimestampFormat() {
        dateFormat = new SimpleDateFormat();
    }

    /**
     * Creates a new TimestampFormat using the given pattern and the symbols
     * from the current Locale.
     *
     * @param pattern the pattern describing the date and time format
     */
    public TimestampFormat(String pattern) {
        initPattern(pattern);
        dateFormat = new SimpleDateFormat(convertedPattern);
    }

    /**
     * Creates a new TimestampFormat using the given pattern and symbols.
     *
     * @param pattern the pattern describing the date and time format
     * @param symbols symbols to be used in formatting
     */
    public TimestampFormat(String pattern, DateFormatSymbols symbols) {
        initPattern(pattern);
        dateFormat = new SimpleDateFormat(convertedPattern, symbols);
    }

    /**
     * Creates a new TimestampFormat using the given pattern and the symbols
     * from the current Locale.
     *
     * @param pattern the pattern describing the date and time format
     * @param locale the Locale to be used
     */
    public TimestampFormat(String pattern, Locale locale) {
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
                nanoPattern = true;
            }
        }
        m.appendTail(newPattern);
        convertedPattern = newPattern.toString();
    }

    /**
     * Changes the time zone used to format the Timestamp.
     *
     * @param zone a new time zone
     */
    public void setTimeZome(TimeZone zone) {
        dateFormat.setTimeZone(zone);
    }

    /**
     * Returns the time zone used to format the Timestamp.
     *
     * @return the current time zone
     */
    public TimeZone getTimeZone() {
        return dateFormat.getTimeZone();
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj == null) {
            return new StringBuffer("null");
        }
        
        if (obj instanceof Timestamp) {
            Timestamp time = (Timestamp) obj;
            int begin = toAppendTo.length();
            StringBuffer buf =  dateFormat.format(time.toDate(), toAppendTo, pos);

            for (int nField = 0; nField < nanoFieldStrings.size(); nField++) {
                String tag = nanoFieldStrings.get(nField);
                int start = buf.indexOf(tag, begin);
                if (start != -1)
                    buf.replace(start, start + tag.length(), nanoFieldFormats.get(nField).format(time.getNanoSec()));
            }

            return buf;
        } else {
            throw new IllegalArgumentException("Cannot format given Object as Timestamp");
        }
    }

    /**
     * Formats the given Timestamp.
     *
     * @param Timestamp a Timestamp
     * @return the formatted String
     */
    public String format(Timestamp Timestamp) {
        return format((Object) Timestamp);
    }
    
    /**
     * Parses the source at the given position.
     * 
     * @param source text to parse
     * @param pos the position
     * @return the parsed timestamp
     */
    public Timestamp parse(String source, ParsePosition pos) {
        if (nanoPattern)
            throw new UnsupportedOperationException("Not supporting parsing of nanosecond field.");
        return Timestamp.of(dateFormat.parse(source, pos));
    }
    
    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return parse(source, pos);
    }
    
    /**
     * Parses a String and converts it to a Timestamp.
     * 
     * @param source the string to parse
     * @return the parsed object
     * @throws ParseException if the string does not match the pattern
     */
    public Timestamp parse(String source) throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        Timestamp result = parse(source, pos);
        if (pos.getIndex() == 0)
            throw new ParseException("Unparseable date: \"" + source + "\"" ,
                pos.getErrorIndex());
        return result;
    }

}
