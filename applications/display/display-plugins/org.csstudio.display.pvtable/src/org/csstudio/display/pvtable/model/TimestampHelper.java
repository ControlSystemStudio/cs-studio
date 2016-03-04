/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.diirt.util.time.Timestamp;
import org.diirt.util.time.TimestampFormat;

/** Time stamp gymnastics
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimestampHelper {
    final public static String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.NNNNNNNNN";
    final public static String FORMAT_PARSE = "yyyy-MM-dd HH:mm:ss.";
    final public static String FORMAT_DECIMAL = "'.'000000000";

    /** Time stamp format */
    final private static TimestampFormat time_format = new TimestampFormat(TimestampHelper.FORMAT_FULL);
    final private static DateFormat dateFormat = new SimpleDateFormat(TimestampHelper.FORMAT_PARSE);
    final private static DecimalFormat decimalFormat = new DecimalFormat(TimestampHelper.FORMAT_DECIMAL);
    
    /** @param timestamp {@link Timestamp}, may be <code>null</code>
     *  @return Time stamp formatted as string
     */
    public static String format(final Timestamp timestamp) {
        if (timestamp == null) {
            return "null";
        }
        synchronized (time_format) {
            return time_format.format(timestamp);
        }
    }
       
    /**
     * Take a String and return a Timestamp
     * Should be implemented in TimestampFormat
     * @param sTimestamp
     * @return
     * @throws ParseException
     * @author A.PHILIPPE, L.PHILIPPE GANIL/FRANCE
     */
    public static Timestamp parse(final String sTimestamp) throws ParseException {
    	if(sTimestamp == "" || sTimestamp == null) {
    		return null;
    	}
    	Date date = dateFormat.parse(sTimestamp);
    	Pattern matchNanoField = Pattern.compile("\\.[0-9]{1,9}");
    	Matcher m = matchNanoField.matcher(sTimestamp);
    	Timestamp t = null;
    	if(m.find()){
    		String nanoString = m.group();
    		t = Timestamp.of(date.toInstant().getEpochSecond(), decimalFormat.parse(nanoString).intValue());
    	}else{
    		t = Timestamp.of(date);
    	}
    	return t;
    }
}