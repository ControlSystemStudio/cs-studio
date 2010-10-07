/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An object representing the PV. It contains all elements that are common
 * to all PVs of all type. The payload is specified by the generic type,
 * and is returned by {@link #getValue()}. Changes in
 * values are notified through the {@link PVValueChangeListener}. Listeners
 * can be registered from any thread. The value can only be accessed on the
 * thread on which the listeners is called.
 *
 * @author carcassi
 * @param <T> the type of the PV.
 */
public final class PV<T> {

    //Static factory should be substituted by constructor? Should factory
    // be public or package private? Should PV name also be final?

    private PV(String name) {
        this.name = name;
    }

    /**
     * Factory methods for PV objects. The class is used to initialize
     * the value of the PV.
     *
     * @param <E> type of the new PV
     * @param clazz type of the new PV
     * @return a new PV
     */
    static <E> PV<E> createPv(String name, Class<E> clazz) {
        return new PV<E>(name);
    }

    private List<PVValueChangeListener> valueChangeListeners = new CopyOnWriteArrayList<PVValueChangeListener>();

    void firePvValueChanged() {
        for (PVValueChangeListener listener : valueChangeListeners) {
            listener.pvValueChanged();
        }
    }

    /**
     * Adds a listener to the value. This method is thread safe.
     *
     * @param listener a new listener
     */
    public void addPVValueChangeListener(PVValueChangeListener listener) {
        if (isClosed())
            throw new IllegalStateException("Can't add listeners to a closed PV");
        valueChangeListeners.add(listener);
    }

    /**
     * Adds a listener to the value. This method is thread safe.
     *
     * @param listener a new listener
     */
    public void removePVValueChangeListener(PVValueChangeListener listener) {
        valueChangeListeners.remove(listener);
    }

    private final String name;

    /**
     * Returns the name of the PV. This method is thread safe.
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    private T value;

    /**
     * Returns the value of the PV. Not thread safe: can be safely accessed only
     * as part of the {@link PVValueChangeListener}.
     *
     * @return the value of value
     */
    public T getValue() {
        return value;
    }

    void setValue(T value) {
        this.value = value;
        firePvValueChanged();
    }

    // This needs to be modified on client thread (i.e. UI) and
    // read on the timer thread (so that actual closing happens in the
    // background)
    private volatile boolean closed = false;

    /**
     * De-registers all listeners, stops all notifications and closes all
     * connections from the data sources needed by this. Once the PV
     * is closed, it can't be re-opened. Subsequent calls to close do not
     * do anything.
     */
    public void close() {
        valueChangeListeners.clear();
        closed = true;
    }

    /**
     * True if no more notifications are going to be sent for this PV.
     *
     * @return true if closed
     */
    public boolean isClosed() {
        return closed;
    }
}
