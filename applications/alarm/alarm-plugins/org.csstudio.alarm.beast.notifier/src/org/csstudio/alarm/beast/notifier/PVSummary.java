/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.TimestampHelper;
import org.eclipse.osgi.util.NLS;

/**
 * Build a summary of current alarm state for the specified PV.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class PVSummary {

	private String name, description;
	private final String current_severity, severity;
	private final String current_message, message;
	private final String value;
	private final String timestamp;

	private final char prefix;
	private final static Pattern NLSPattern = Pattern.compile("\\{\\ *\\d+\\ *\\}");

	/** Pattern for description prefixes */
    final private static Pattern PrefixPattern = Pattern.compile("^(\\*|\\!)(.*)");

	public static PVSummary buildFromSnapshot(PVSnapshot pv)
	{
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

		String timestamp = pv.getTimestamp() == null
	        ? "(no time)"
            : TimestampHelper.format(pv.getTimestamp());

		return new PVSummary(description, name, current_severity, severity,
				current_message, message, value, timestamp, prefix, isNLSMessage);
	}

	public PVSummary(String description,
			String name,
			String current_severity,
			String severity,
			String current_message,
			String message,
			String value,
			String timestamp,
			char prefix,
			boolean isNLSMessage)
	{
		this.description = description;
		this.name = name;
		this.current_severity = current_severity;
		this.severity = severity;
		this.current_message = current_message;
		this.message = message;
		this.value = value;
		this.timestamp = timestamp;
		this.prefix = prefix;
		if (isNLSMessage) {
			String[] bindings = { current_severity, value };
			this.description = NLS.bind(description, bindings);
		}
	}

	public String getSummary()
	{
		if (prefix == '*') return description;
		StringBuilder builder = new StringBuilder();
		builder.append(current_severity);
		builder.append(" alarm: ");
		builder.append(description);
		return builder.toString();
	}

	public String getDetails()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("PV: ");
		builder.append(name);
		builder.append(" - ");
		
		builder.append("Description: ");
		builder.append(getSummary());
		builder.append(" - ");
		
		builder.append("Alarm Time: ");
		builder.append(timestamp);
		builder.append(" - ");
		
		builder.append("Current Severity: ");
		builder.append(current_severity);
		builder.append(" - ");
		
		builder.append("Current Status: ");
		builder.append(current_message);
		builder.append(" - ");
		
		builder.append("Alarm Severity: ");
		builder.append(severity);
		builder.append(" - ");
		
		builder.append("Alarm Status: ");
		builder.append(message);
		builder.append(" - ");
		
		builder.append("Alarm Value: ");
		builder.append(value);
		return builder.toString();
	}

}
