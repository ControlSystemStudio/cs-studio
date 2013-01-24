/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import org.epics.pvmanager.data.Alarm;
import org.epics.pvmanager.data.VType;

/**
 * An object representing the PVReader. It contains all elements that are common
 * to all PVs of all type. The payload is specified by the generic type,
 * and is returned by {@link #getValue()}. Changes in
 * values are notified through the {@link PVReaderListener}. Listeners
 * can be registered from any thread. The value can only be accessed on the
 * thread on which the listeners is called.
 *
 * @author carcassi
 * @param <T> the type of the PVReader.
 */
public interface PVReader<T> {

    /**
     * Adds a listener to the value. This method is thread safe.
     *
     * @param listener a new listener
     */
    public void addPVReaderListener(PVReaderListener listener);

    /**
     * Adds a listener to the value, which is notified only if the value is
     * of a given type. This method is thread safe.
     *
     * @param clazz type to filter notifications for
     * @param listener a new listener
     */
    public void addPVReaderListener(final Class<?> clazz, final PVReaderListener listener);

    /**
     * Removes a listener to the value. This method is thread safe.
     *
     * @param listener the old listener
     */
    public void removePVReaderListener(PVReaderListener listener);

    /**
     * Returns the name of the PVReader. This method is thread safe.
     *
     * @return the value of name
     */
    public String getName();

    /**
     * Returns the value of the PVReader. Not thread safe: can be safely accessed only
     * as part of the {@link PVReaderListener}.
     *
     * @return the value of value
     */
    public T getValue();

    /**
     * De-registers all listeners, stops all notifications and closes all
     * connections from the data sources needed by this. Once the PVReader
     * is closed, it can't be re-opened. Subsequent calls to close do not
     * do anything.
     */
    public void close();

    /**
     * True if no more notifications are going to be sent for this PVReader.
     *
     * @return true if closed
     */
    public boolean isClosed();

    /**
     * Returns the last exception that was generated preparing the value
     * for this PVReader and clears it (subsequent call will return null).
     *
     * @return the last generated exception or null
     */
    public Exception lastException();

    /**
     * Pauses or resumes the reader notifications.
     * <p>
     * Note that since notifications may still be in flight, you may receive
     * notifications after setting the pause state to on. The paused flag on the
     * reader, though, is changed immediately.
     * 
     * @param paused whether the reader should be paused or not
     */
    public void setPaused(boolean paused);
    
    /**
     * Whether the reader is paused. If a reader is paused, all the notifications
     * are skipped. While the channels remains open, and data is still being collected,
     * the computation after the collectors is suspended, which saves computation
     * resources.
     * 
     * @return true if it is paused
     */
    public boolean isPaused();
    
    /**
     * True if the reader is connected. <b>Do not use this method to display connection status
     * if using vTypes defined in org.epics.pvmanager.data.</b>
     * <p>
     * Currently, a reader is defined as connected if <b>all</b> the channels
     * are connected. This means that you still may get updates even if
     * this method returns false. You can use this method to determine whether
     * your notification comes from a complete set.
     * <p>
     * When using {@link VType}s, you should use the {@link Alarm} interface to
     * get the connection status. This scales when you get aggregates, such
     * as lists or maps of channels. This method does obviously not scale functionally
     * since, in an aggregate, it can't tell you which channel of the set
     * is connected or not.
     * 
     * @return 
     */
    public boolean isConnected();
}
