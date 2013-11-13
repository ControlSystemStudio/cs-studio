/*******************************************************************************
* Copyright (c) 2010-2013 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.actions;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.notifier.ItemInfo;
import org.csstudio.alarm.beast.notifier.PVSnapshot;
import org.csstudio.alarm.beast.notifier.PVSummary;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;
import org.csstudio.email.JavaxMailSender;
import org.eclipse.osgi.util.NLS;

public abstract class AbstractMailActionImpl implements IAutomatedAction {
	
	/** Information from {@link AlarmTreeItem} providing the automated action */
	protected ItemInfo item;
	
	protected List<PVSnapshot> pvs;
	
	protected boolean manuallyExecuted = false;

	protected JavaxMailSender mailSender;

	final private static Pattern NLSPattern = Pattern.compile("\\{\\ *\\d+\\ *\\}");
    final private static Pattern PrefixPattern = Pattern.compile("^\\*(.*)$");
    
    protected String buildSubject() {
		String subject = mailSender.getSubject().trim();
		StringBuilder builder = new StringBuilder();

		// Global rule => prefix with "ACK:" if the alarm is an acknowledge
		if (!manuallyExecuted) {
			boolean acknowledge = false;
			for (PVSnapshot pv : pvs)
				if (pv.isAcknowledge())
					acknowledge = true;
			if (acknowledge)
				builder.append("ACK: ");
		}

		// Subject undefined => build from PV
		if (subject.isEmpty()) {
			if (item.isPV()) {
				PVSummary summary = PVSummary.buildFromSnapshot(pvs.get(0));
				builder.append(summary.getSummary());
			} else {
				builder.append(buildAlarmCount());
				builder.append(": ");
				builder.append(item.getName());
			}
		} else {
			// Handle NLS
			subject = fillNLS(subject);
			// Handle prefix
			Matcher prefixMatcher = PrefixPattern.matcher(subject);
			if (prefixMatcher.matches()) { // Defined subject only
				subject = prefixMatcher.group(1).trim();
				builder.append(subject);
				return builder.toString();
			}
			if (item.isPV()) {
				PVSnapshot snapshot = pvs.get(0);
				builder.append(snapshot.getCurrentSeverity().name());
				builder.append(" alarm: ");
				builder.append(subject);
			} else {
				builder.append(buildAlarmCount());
				builder.append(": ");
				builder.append(subject);
			}
		}
		return builder.toString();
	}
	
	protected String buildBody() {
		String body = mailSender.getBody().trim();
		StringBuilder builder = new StringBuilder();

		// Body undefined => build from PV
		if (body.isEmpty()) {
			for (PVSnapshot pv : pvs) {
				PVSummary summary = PVSummary.buildFromSnapshot(pv);
				builder.append(summary.getDetails());
				builder.append("\n\n");
			}
		} else {
			// Handle NLS
			body = fillNLS(body);
			// Handle prefix
			Matcher prefixMatcher = PrefixPattern.matcher(body);
			if (prefixMatcher.matches()) { // Defined body only
				body = prefixMatcher.group(1).trim();
				builder.append(body);
				return builder.toString();
			}
			builder.append(body);
			builder.append("\n");
			for (PVSnapshot pv : pvs) {
				PVSummary summary = PVSummary.buildFromSnapshot(pv);
				builder.append(summary.getDetails());
				builder.append("\n");
			}
		}
		return builder.toString();
	}
	
	// Handle NLS
	private String fillNLS(final String message) {
		Matcher nlsMatcher = NLSPattern.matcher(message);
		if (!nlsMatcher.find())
			return message;
		String filledMessage = "";
		if (item.isPV()) {
			PVSnapshot snapshot = pvs.get(0);
			String[] bindings = { snapshot.getCurrentSeverity().name(), snapshot.getValue() };
			filledMessage = NLS.bind(message, bindings);
		} else {
			String[] bindings = { buildAlarmCount(), "" };
			filledMessage = NLS.bind(message, bindings);
		}
		return filledMessage;
	}
	
	// Build a summary of underlying alarms
	private String buildAlarmCount() {
		StringBuilder builder = new StringBuilder();
		// count alarms by severity
		int okCount = 0;
		int minorCount = 0;
		int majorCount = 0;
		int invalidCount = 0;
		int undefinedCount = 0;
		for (PVSnapshot pv : pvs) {
			switch (pv.getCurrentSeverity()) {
			case OK: okCount++; break;
			case MINOR: minorCount++; break;
			case MAJOR: majorCount++; break;
			case INVALID: invalidCount++; break;
			case UNDEFINED: undefinedCount++; break;
			default: break;
			}
		}
		// nb MINOR alarms ... nb MINOR alarms - nb INVALID alarms
		boolean isFirst = true;
		if (undefinedCount > 0) {
			isFirst = false;
			builder.append(undefinedCount + " UNDEFINED alarm(s)");
		}
        if (invalidCount > 0) {
            if (!isFirst) builder.append(" - ");
            isFirst = false;
            builder.append(invalidCount + " INVALID alarm(s)");
        }
		if (majorCount > 0) {
			if (!isFirst) builder.append(" - ");
			isFirst = false;
			builder.append(majorCount + " MAJOR alarm(s)");
		}
		if (minorCount > 0) {
			if (!isFirst) builder.append(" - ");
			isFirst = false;
			builder.append(minorCount + " MINOR alarm(s)");
		}
		if (okCount > 0) {
			if (!isFirst) builder.append(" - ");
			isFirst = false;
			builder.append(okCount + " OK alarm(s)");
		}
		return builder.toString();
	}

}
