/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import java.util.regex.Pattern;

import org.eclipse.osgi.util.NLS;

/** Formatter for alarm system annunciations
 *
 *  <p>Used by the alarm server to create the annuncation,
 *  also used by the GUI to display what the annunciation
 *  should have been.
 *
 *  @author Kay Kasemir
 */
public class AnnunciationFormatter
{
    final private static Pattern priority_pattern = Pattern.compile(Messages.PriorityAnnunciationPattern);

    /** Determine if the format describes a 'priority' message
     *  @param format Format
     *  @return <code>true</code> for priority message
     */
    public static boolean hasPriority(final String format)
    {
        return priority_pattern.matcher(format).matches();
    }

    /** Create message for annunciation
     *  @param format Format to use, either the plain description or a format
     *                that starts with '*' and may then include {0} for the severity
     *                and {1} for the value
     *  @param severity Alarm severity
     *  @param value Alarm value
     *  @return Annunciation text
     */
    public static String format(String format, final String severity, String value)
    {
        // Priority flag at start of format?
        format = format.trim();
        boolean priority = format.startsWith(Messages.PriorityAnnunciationPrefix);
        if (priority)
            format = format.substring(Messages.PriorityAnnunciationPrefix.length()).trim();

        String message;
        // Custom format?
        if (format.startsWith(Messages.FormattedAnnunciationPrefix))
        {
            format = format.substring(Messages.FormattedAnnunciationPrefix.length()).trim();

            // Priority flag at start of custom format?
            if (format.startsWith(Messages.PriorityAnnunciationPrefix))
            {
                priority = true;
                format = format.substring(Messages.PriorityAnnunciationPrefix.length()).trim();
            }
            if (value == null)
                value = "null"; //$NON-NLS-1$
            // Use custom format
            message = NLS.bind(format, severity, value);
        }
        else// Use default format
            message = NLS.bind(Messages.AnnunciationFmt, severity, format);

        message = message.trim();
        // Re-apply priority flag as needed
        if (priority)
            message = Messages.PriorityAnnunciationPrefix + message;

        return message;
    }
}
