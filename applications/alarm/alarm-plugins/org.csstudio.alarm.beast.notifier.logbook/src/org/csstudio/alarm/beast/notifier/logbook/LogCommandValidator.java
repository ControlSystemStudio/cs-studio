/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.logbook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.csstudio.alarm.beast.notifier.model.IActionValidator;
import org.csstudio.logbook.Logbook;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.logbook.Tag;

/**
 * Validator for {@link LogActionImpl}. Uses a {@link LogCommandHandler} to
 * validate an Log command.
 *
 * @author Fred Arnaud (Sopra Group) - ITER
 *
 */
public class LogCommandValidator implements IActionValidator {

    private String details;
    private LogCommandHandler handler;

    // List of all the possible logbooks, levels and tags which may be added to
    // a logEntry.
    private List<String> logbookNames = Collections.emptyList();
    private List<String> tagNames = Collections.emptyList();
    private List<String> levels = Collections.emptyList();

    public LogCommandValidator() {
        try {
            final LogbookClient logbookClient = LogbookClientManager
                    .getLogbookClientFactory().getClient();
            if (logbookClient != null) {
                Collection<Logbook> logbooks = logbookClient.listLogbooks();
                if (logbooks != null && !logbooks.isEmpty()) {
                    logbookNames = new ArrayList<String>();
                    for (Logbook l : logbooks)
                        logbookNames.add(l.getName());
                }
                Collection<Tag> tags = logbookClient.listTags();
                if (tags != null && !tags.isEmpty()) {
                    tagNames = new ArrayList<String>();
                    for (Tag t : tags)
                        tagNames.add(t.getName());
                }
                levels = logbookClient.listLevels();
            }
        } catch (Exception e) {
        }
    }

    public void init(String details) {
        this.details = details == null ? null : details.trim();
        handler = new LogCommandHandler(details);
    }

    /** @return handler for Log command */
    public LogCommandHandler getHandler() {
        return handler;
    }

    @Override
    public boolean validate() throws Exception {
        if (details == null || "".equals(details)) {
            throw new Exception("Missing automated action details");
        }
        handler.parse();
        if (handler.getLogbooks() == null || handler.getLogbooks().isEmpty()) {
            throw new Exception("Missing logbook name");
        }
        for (String s : handler.getLogbooks())
            if (!logbookNames.contains(s))
                throw new Exception("Unknown logbook name '" + s + "'");
        if (handler.getTags() != null)
            for (String s : handler.getTags())
                if (!tagNames.contains(s))
                    throw new Exception("Unknown tag name '" + s + "'");
        if (!handler.getLevel().isEmpty()
                && !levels.contains(handler.getLevel()))
            throw new Exception("Unknown level name '" + handler.getLevel() + "'");
        return true;
    }

}
