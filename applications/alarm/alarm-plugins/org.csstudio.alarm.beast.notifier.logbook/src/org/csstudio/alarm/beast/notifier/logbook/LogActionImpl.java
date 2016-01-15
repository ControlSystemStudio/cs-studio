/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.logbook;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.notifier.AAData;
import org.csstudio.alarm.beast.notifier.ItemInfo;
import org.csstudio.alarm.beast.notifier.PVSnapshot;
import org.csstudio.alarm.beast.notifier.model.IActionHandler;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.LogbookBuilder;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.logbook.TagBuilder;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.osgi.util.NLS;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class LogActionImpl implements IAutomatedAction {

    final private static Pattern NLSPattern = Pattern.compile("\\{\\ *\\d+\\ *\\}");
    final private static Pattern PrefixPattern = Pattern.compile("^\\*(.*)$");

    /** Information from {@link AlarmTreeItem} providing the automated action */
    private ItemInfo item;

    private List<PVSnapshot> pvs;

    private LogCommandHandler handler;

    private final String defaultLogbook;
    private final String defaultLevel;

    private boolean manuallyExecuted = false;

    public LogActionImpl() {
        final IPreferencesService prefs = Platform.getPreferencesService();

        defaultLogbook = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.Default_logbook, "", null);
        defaultLevel = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.Default_level, "", null);
    }

    private String buildBody() {
        String body = handler.getBody().trim();
        StringBuilder builder = new StringBuilder();

        // If system => build alarm count
        if (!item.isPV()) {
            builder.append(buildAlarmCount());
            builder.append(": ");
            builder.append(item.getName());
            builder.append("\n\n");
        }
        // Body undefined => build header from alarm configuration
        if (body.isEmpty()) {
            for (PVSnapshot pv : pvs) {
                PVLogSummary summary = PVLogSummary.buildFromSnapshot(pv);
                builder.append(pv.getParentPath());
                builder.append(":\n");
                if (!manuallyExecuted && pv.isAcknowledge())
                    builder.append("ACK: ");
                builder.append(summary.getHeader());
                builder.append(summary.getLog());
                builder.append("\n");
            }
        } else {
            // Handle NLS
            body = fillNLS(body);
            // Handle prefix
            Matcher prefixMatcher = PrefixPattern.matcher(body);
            if (prefixMatcher.matches()) { // Defined body only
                body = prefixMatcher.group(1).trim();
                for (PVSnapshot pv : pvs) {
                    PVLogSummary summary = PVLogSummary.buildFromSnapshot(pv);
                    builder.append(pv.getParentPath());
                    builder.append(":\n");
                    if (!manuallyExecuted && pv.isAcknowledge())
                        builder.append("ACK: ");
                    builder.append(body);
                    builder.append("\n");
                    builder.append(summary.getAlarmTime());
                    builder.append("\n");
                }
            } else {
                for (PVSnapshot pv : pvs) {
                    PVLogSummary summary = PVLogSummary.buildFromSnapshot(pv);
                    builder.append(pv.getParentPath());
                    builder.append(":\n");
                    if (!manuallyExecuted && pv.isAcknowledge())
                        builder.append("ACK: ");
                    builder.append(body);
                    builder.append("\n");
                    builder.append(summary.getLog());
                    builder.append("\n");
                }
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

    @Override
    public void init(ItemInfo item, AAData auto_action, IActionHandler handler)
            throws Exception {
        this.item = item;
        this.handler = (LogCommandHandler) handler;
        this.manuallyExecuted = auto_action.isManual();
    }

    @Override
    public void execute(List<PVSnapshot> pvs) throws Exception {
        this.pvs = pvs;
        Collections.sort(this.pvs);

        // Get client
        final LogbookClient logbookClient = LogbookClientManager
                .getLogbookClientFactory().getClient();

        // Create log
        LogEntryBuilder logBuilder = LogEntryBuilder.withText(buildBody());

        List<String> logbooks = handler.getLogbooks();
        if (logbooks.isEmpty())
            logbooks.add(defaultLogbook);
        for (String logbook : logbooks)
            logBuilder.addLogbook(LogbookBuilder.logbook(logbook));

        String level = handler.getLevel().isEmpty() ? defaultLevel : handler.getLevel();
        logBuilder.setLevel(level);

        if (handler.getTags() != null)
            for (String tag : handler.getTags())
                logBuilder.addTag(TagBuilder.tag(tag));

        logbookClient.createLogEntry(logBuilder.build());
    }

}
