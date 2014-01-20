/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager;

/**
 * Matches and fills a cache with the data from connection and message payloads.
 * This optional class helps the writer of a datasource to manage the
 * type matching and conversions.
 *
 * @param <ConnectionPayload> the type of payload given at connection
 * @param <MessagePayload> the type of payload for each message
 * @author carcassi
 */
public interface DataSourceTypeAdapter<ConnectionPayload, MessagePayload> {
    
    /**
     * Determines whether the converter can take values from the channel
     * described by the connection payload and transform them in a 
     * type required by the cache.
     * 
     * @param cache the cache where data will need to be written
     * @param connection the connection information
     * @return zero if there is no match, or the position of the type matched
     */
    int match(ValueCache<?> cache, ConnectionPayload connection);
    
    /**
     * The parameters required to open a monitor for the channel. The
     * type of the parameters will be datasource specific
     * <p>
     * For channels multiplexed on a single subscription, this method
     * is never used.
     * 
     * @param cache the cache where data will need to be written
     * @param connection the connection information
     * @return datasource specific subscription information
     */
    Object getSubscriptionParameter(ValueCache<?> cache, ConnectionPayload connection);
    
    /**
     * Takes the information in the message and updates the cache. 
     * 
     * @param cache cache to be updated
     * @param connection the connection information
     * @param message the payload of each message
     * @return true if a new value was stored
     */
    boolean updateCache(ValueCache<?> cache, ConnectionPayload connection, MessagePayload message);
}
