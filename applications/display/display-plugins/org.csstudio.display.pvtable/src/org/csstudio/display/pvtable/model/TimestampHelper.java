/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


/**
 * Time stamp gymnastics
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimestampHelper {
    final public static String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.nnnnnnnnn";
    final public static String FORMAT_PARSE = "yyyy-MM-dd HH:mm:ss.";

    private static ZoneId zone = ZoneId.systemDefault();

    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(FORMAT_FULL);
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(FORMAT_PARSE);

    /**
     * @param timestamp
     *            {@link Instant}, may be <code>null</code>
     * @return Time stamp formatted as string
     */
    public static String format(final Instant timestamp) {
        if (timestamp == null) {
            return "null";
        }
        return timeFormat.format(ZonedDateTime.ofInstant(timestamp, zone));
    }

    /**
     * Take a String and return a Timestamp Should be implemented in
     * TimestampFormat
     *
     * @param sTimestamp
     * @return
     * @throws ParseException
     * @author A.PHILIPPE, L.PHILIPPE GANIL/FRANCE
     */
    public static Instant parse(final String sTimestamp) throws ParseException {
        if (sTimestamp == "" || sTimestamp == null) {
            return null;
        }

        Instant t = null;
        try {
        	t = ZonedDateTime.parse(sTimestamp, timeFormat).toInstant();
        }
        catch (DateTimeParseException ex) {
        	ex.printStackTrace();
        }

        if (t == null) {
        	try {
        		t = ZonedDateTime.parse(sTimestamp, dateFormat).toInstant();
        	}
            catch (DateTimeParseException ex) {
            	ex.printStackTrace();
            }
        }

        return t;
    }
}