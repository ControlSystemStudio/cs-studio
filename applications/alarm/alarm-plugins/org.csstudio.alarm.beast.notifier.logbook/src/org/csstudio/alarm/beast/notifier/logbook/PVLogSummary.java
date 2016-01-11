/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.logbook;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.TimestampHelper;
import org.csstudio.alarm.beast.notifier.PVSnapshot;
import org.csstudio.alarm.beast.notifier.util.NotifierUtils;
import org.eclipse.osgi.util.NLS;

/**
 * Helper to build log entry message from PV.
 *
 * @author Fred Arnaud (Sopra Group) - ITER
 *
 */
public class PVLogSummary {

    private String name, description;
    private final String current_severity, severity;
    private final String current_message, message;
    private final String value;
    private final String timestamp;
    private final String duration;

    private final char prefix;
    private final static Pattern NLSPattern = Pattern.compile("\\{\\ *\\d+\\ *\\}");

    /** Pattern for description prefixes */
    final private static Pattern PrefixPattern = Pattern.compile("^(\\*|\\!)(.*)");

    public static PVLogSummary buildFromSnapshot(PVSnapshot pv) {
        char prefix = '-';
        String description = pv.getDescription().trim();
        boolean isNLSMessage = false;
        Matcher NLSMatcher = NLSPattern.matcher(description);
        if (NLSMatcher.find()) {
            isNLSMessage = true;
        }
        // Clean description
        Matcher prefixMatcher = PrefixPattern.matcher(description);
        if (prefixMatcher.matches()) {
            prefix = prefixMatcher.group(1).charAt(0);
            description = prefixMatcher.group(2).trim();
        }
        String name = pv.getName();
        String current_severity = pv.getCurrentSeverity().name();
        String severity = pv.getSeverity().name();
        String current_message = pv.getCurrentMessage();
        String message = pv.getMessage();
        String value = pv.getValue();
        String timestamp = pv.getTimestamp() == null ? "(no time)"
                : TimestampHelper.format(pv.getTimestamp());
        String duration = NotifierUtils.getDurationString(pv.getTimestamp());
        return new PVLogSummary(description, name, current_severity, severity,
                current_message, message, value, timestamp, duration, prefix,
                isNLSMessage);
    }

    public PVLogSummary(String description, String name,
            String current_severity, String severity, String current_message,
            String message, String value, String timestamp, String duration,
            char prefix, boolean isNLSMessage) {
        this.name = name;
        this.current_severity = current_severity;
        this.severity = severity;
        this.current_message = current_message;
        this.message = message;
        this.value = value;
        this.timestamp = timestamp;
        this.duration = duration;
        this.prefix = prefix;
        this.description = description;
        if (isNLSMessage) {
            String[] bindings = { current_severity, value };
            this.description = NLS.bind(description, bindings);
        }
    }

    public String getSeverity() {
        StringBuilder builder = new StringBuilder();
        builder.append(timestamp);
        builder.append(": ");
        builder.append(current_severity);
        return builder.toString();
    }

    public String getAlarmTime() {
        StringBuilder builder = new StringBuilder();
        builder.append("Alarm Time: ");
        builder.append(timestamp);
        if (!duration.isEmpty()) {
            builder.append(" (Time since event: ");
            builder.append(duration);
            builder.append(")");
        }
        builder.append("\n");
        return builder.toString();
    }

    public String getHeader() {
        StringBuilder builder = new StringBuilder();
        if (prefix == '*') {
            builder.append(description);
        } else {
            builder.append(current_severity);
            builder.append(" alarm");
            if (description != null && !description.isEmpty())
                builder.append(": " + description);
        }
        builder.append("\n");
        return builder.toString();
    }

    public String getLog() {
        StringBuilder builder = new StringBuilder();

        builder.append("PV                      : ");
        builder.append(name);
        builder.append("\n");

        builder.append("Alarm Time              : ");
        builder.append(timestamp);
        if (!duration.isEmpty()) {
            builder.append(" (Time since event: ");
            builder.append(duration);
            builder.append(")");
        }
        builder.append("\n");

        builder.append("Alarm Severity/Message  : ");
        builder.append(severity);
        builder.append("/");
        builder.append(message);
        builder.append("\n");

        builder.append("Alarm Value             : ");
        builder.append(value);
        builder.append("\n");

        builder.append("Current Severity/Message: ");
        builder.append(current_severity);
        builder.append("/");
        builder.append(current_message);
        builder.append("\n");

        return builder.toString();
    }
}
