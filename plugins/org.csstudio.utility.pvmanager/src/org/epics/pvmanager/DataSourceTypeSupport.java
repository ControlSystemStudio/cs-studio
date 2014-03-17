/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The type support for a datasource. This optional class is provided to help
 * create a more flexible type support in a datasource, so that support
 * for individual types is done through runtime configuration. It provides
 * default implementation for matching typeAdapters from the desired cache
 * and connection payload.
 *
 * @author carcassi
 */
public class DataSourceTypeSupport {
    
    /**
     * Given a collection of type datapters, finds the one that can store
     * data in the cache given the channel information described in the
     * connection payload. If there isn't a unique match, an exception
     * is thrown.
     * 
     * @param <C> type of connection payload
     * @param <T> datasource specific type adapter type
     * @param typeAdapters a collection of type adapters
     * @param cache the cache where to store the data
     * @param connection the connection payload
     * @return 0 if the type was not matched
     */
    protected <C, T extends DataSourceTypeAdapter<? super C,?>> T find(Collection<T> typeAdapters, ValueCache<?> cache, C connection) {
        int matched = 0;
        List<T> matchedConverters = new ArrayList<T>();
        for (T converter : typeAdapters) {
            int match = converter.match(cache, connection);
            if (match != 0) {
                if (match < matched) {
                    matchedConverters.clear();
                }
                matchedConverters.add(converter);
            }
        }
        
        if (matchedConverters.size() != 1) {
            throw new IllegalStateException(formatMessage(cache, connection, matched, matchedConverters));
        }
        
        return matchedConverters.get(0);
    }
    
    /**
     * Formats the error message in case of not unique match. This
     * allows data sources to give more specific error messages.
     * 
     * @param cache the cache used for the match
     * @param connection the connection payload used for the match
     * @param match the result of the match
     * @param matchedConverters the matched converters; will either be 0 (no match)
     * or more than 1 (non unique match)
     * @return the message to be passed with the exception
     */
    protected String formatMessage(ValueCache<?> cache, Object connection, int match, List<? extends DataSourceTypeAdapter<?, ?>> matchedConverters) {
        if (matchedConverters.isEmpty()) {
            return "DataSource misconfiguration: no match found to convert payload to type. ("
                    + cache.getType() + " - " + connection + ")";
        } else {
            return "DataSource misconfiguration: multiple matches found to convert payload to type. ("
                    + cache.getType() + " - " + connection + ": " + matchedConverters + ")";
        }
    }
}
