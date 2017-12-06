/*******************************************************************************
 * Copyright (c) 2010-2017 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.model;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.beast.msghist.Messages;
import org.eclipse.osgi.util.NLS;

/**
 * Utility class that provides an interface between message history filtering queries and proper {@link MessagePropertyFilter}
 * setup on {@link Model}.
 *
 * @author Borut Terpinc
 */
public class FilterQuery {
    private static final String FILTER_EQUALS = "=";
    private static final String FILTER_SEPARATOR = " AND ";
    private static final String ALIAS_START_TIME = "START_TIME";
    private static final String ALIAS_END_TIME = "END_TIME";

    private FilterQuery() {
    }

    /**
     *
     * @return null if given string is a valid filtering query or an error string otherwise.
     */
    public static String validateQuery(String query) {
        if (query == null || query.isEmpty())
            return null;

        String[] values = query.split(FILTER_SEPARATOR);
        for (String value : values) {
            String[] split = value.split(FILTER_EQUALS);
            if (split.length != 2 || split[0].trim().isEmpty() || split[1].trim().isEmpty())
                return NLS.bind(Messages.FilterInputError, FILTER_SEPARATOR, FILTER_EQUALS);
        }

        return null;
    }

    /**
     * Construct syntactically correct part of query.
     */
    private static String buildClausePart(String property, String value) {
        return String.format("%s%s%s%s", property, FILTER_EQUALS, value, FILTER_SEPARATOR);
    }

    /**
     * @return filtering query constructed from current state of the model.
     */
    public static String fromModel(Model model) {
        MessagePropertyFilter[] filters = model.getFilters();
        StringBuilder query = new StringBuilder();
        String timeQuery = fromTimeSpec(model.getStartSpec(), model.getEndSpec());
        if (!timeQuery.isEmpty())
            query.append(String.format("%s%s", timeQuery, FILTER_SEPARATOR));

        for (MessagePropertyFilter filter : filters)
            query.append(buildClausePart(filter.getProperty(), filter.getPattern()));
        if (query.length() > FILTER_SEPARATOR.length())
            query.setLength(query.length() - FILTER_SEPARATOR.length());

        return query.toString();
    }

    /**
     * @return filtering query constructed from message history start spec and end spec time preferences.
     */
    public static String fromTimeSpec(String startSpec, String endSpec) {
        StringBuilder query = new StringBuilder();
        if (!endSpec.isEmpty())
            query.append(buildClausePart(ALIAS_START_TIME, startSpec));
        if (!startSpec.isEmpty())
            query.append(buildClausePart(ALIAS_END_TIME, endSpec));
        if (query.length() > FILTER_SEPARATOR.length())
            query.setLength(query.length() - FILTER_SEPARATOR.length());
        return query.toString();
    }

    /**
     * Applies filter query to a given model.
     *
     * @param query
     *            query to apply
     * @param model
     *            model to apply this query to
     * @throws Exception
     *             on invalid query format or model error when setting filters
     */
    public static void apply(String query, Model model) throws Exception {
        String validationError = validateQuery(query);
        if (validationError != null)
            throw new IllegalArgumentException(validationError);

        MessagePropertyFilter[] filters;
        String startSpec = "";
        String endSpec = "";

        if (query == null || query.isEmpty())
            filters = new MessagePropertyFilter[0];
        else {
            String[] values = query.split(FILTER_SEPARATOR);
            List<MessagePropertyFilter> filterList = new ArrayList<>();

            for (String value : values) {
                String[] split = value.split(FILTER_EQUALS);

                String property = split[0].trim();
                String pattern = split[1].trim();

                if (property.equals(ALIAS_START_TIME))
                    startSpec = pattern;

                else if (property.equals(ALIAS_END_TIME))
                    endSpec = pattern;
                else
                    filterList.add(new MessagePropertyFilter(property, pattern));
            }

            filters = filterList.toArray(new MessagePropertyFilter[filterList.size()]);
        }

        model.setTimerange(startSpec, endSpec);
        model.setFilters(filters);
    }
}
