/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.logbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.notifier.model.IActionHandler;

/**
 * Handler for Log command: Parse {@link AADataStructure} details to extract information.
 * Example: log:CODAC,Sandbox&body={0} Alarm raised - Water below {1} m3
 *
 * @author Fred Arnaud (Sopra Group) - ITER
 *
 */
public class LogCommandHandler implements IActionHandler {

    private final static String DELIMITERS = ",;";
    private final static Pattern NLSPattern = Pattern.compile("\\{\\ *[01]\\ *\\}");

    private enum ParamType {
        Logbooks("logbooks"), Level("level"), Tags("tags"), Body("body");

        private final String name;

        ParamType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private final String details;
    private List<String> logbooks = Collections.emptyList();
    private List<String> tags = Collections.emptyList();
    private String level;
    private String body;

    public LogCommandHandler(String details) {
        this.details = details;
    }

    public void parse() throws Exception {
        if (details == null || "".equals(details))
            return;
        Matcher mainMatcher = Pattern.compile("^log:(.*)$").matcher(details);
        if (mainMatcher.matches())
            parseDataType(mainMatcher.group(1).trim(), ParamType.Logbooks);
    }

    // Recursive method to retrieve pattern parameters
    private void parseDataType(String data, ParamType type) throws Exception {
        boolean found = false;
        for (ParamType param : ParamType.values()) {
            Matcher matcher = Pattern.compile("^(.*)(?:\\?|&)(?i:" + param.getName() + ")=(.*)$").matcher(data);
            if (matcher.matches()) {
                found = true;
                parseDataType(matcher.group(1).trim(), type);
                parseDataType(matcher.group(2).trim(), param);
            }
        }
        // no pattern found => final data
        if (!found) handleParameter(data, type);
    }

    private void handleParameter(String data, ParamType type) throws Exception {
        if (type == null || data == null)
            return;
        switch (type) {
        case Logbooks:
            logbooks = parseList(data);
            break;
        case Tags:
            tags = parseList(data);
            break;
        case Level:
            level = data.trim();
            break;
        case Body:
            body = data.trim();
            validateNSF(body);
            break;
        }
    }

    private List<String> parseList(String data) throws Exception {
        StringTokenizer st = new StringTokenizer(data, DELIMITERS);
        List<String> list = new ArrayList<String>();
        while (st.hasMoreElements()) {
            String token = st.nextToken().trim();
            list.add(token);
        }
        return list;
    }

    protected void validateNSF(String data) throws Exception {
        String dataCopy = new String(data);
        int beginIndex = 0, endIndex = 0;
        while (true) {
            beginIndex = dataCopy.indexOf('{');
            if (beginIndex == -1)
                break;
            endIndex = dataCopy.indexOf('}');
            if (endIndex == -1)
                throw new Exception("Invalid field: " + dataCopy.substring(beginIndex));
            String nls = dataCopy.substring(beginIndex, endIndex + 1);
            Matcher nlsMatcher = NLSPattern.matcher(nls);
            if (!nlsMatcher.matches())
                throw new Exception("Invalid field: " + nls);
            dataCopy = dataCopy.substring(endIndex + 1);
        }
    }

    public List<String> getLogbooks() {
        return logbooks;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getLevel() {
        if (level == null)
            return "";
        return level;
    }

    public String getBody() {
        if (body == null)
            return "";
        return body;
    }

    @Override
    public String toString() {
        return "LogCommandHandler [details=" + details + ", logbooks="
                + logbooks + ", level=" + level + ", body=" + body + "]";
    }

}
