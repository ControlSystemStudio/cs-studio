/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

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
class PVReaderImpl<T> implements PVReader<T> {

    static <T> PVReaderImpl<T> implOf(PVReader<T> pvReader) {
        if (pvReader instanceof PVReaderImpl) {
            return (PVReaderImpl<T>) pvReader;
        }
        
        throw new IllegalArgumentException("PVReader must be implemented using PVReaderImpl");
    }
    
    /**
     * Factory methods for PVReader objects. The class is used to initialize
     * the value of the PVReader.
     *
     * @param name of the PVReader
     * @param notifyFirstListener true if, when the first listener is registered
     * and a previous event was generated (value or exception),
     * it should be fired so that it is not lost
     */
    PVReaderImpl(String name, boolean notifyFirstListener) {
        this.name = name;
        this.notifyFirstListener = notifyFirstListener;
    }

    private List<PVReaderListener> pvReaderListeners = new CopyOnWriteArrayList<PVReaderListener>();
    private final boolean notifyFirstListener;
    private volatile boolean missedNotification = false;

    void firePvValueChanged() {
        lastExceptionToNotify = false;
        readConnectionToNotify = false;
        boolean missed = true;
        for (PVReaderListener listener : pvReaderListeners) {
            listener.pvChanged();
            missed = false;
        }
        if (missed)
            missedNotification = true;
    }

    /**
     * Adds a listener to the value. This method is thread safe.
     *
     * @param listener a new listener
     */
    @Override
    public void addPVReaderListener(PVReaderListener listener) {
        if (isClosed())
            throw new IllegalStateException("Can't add listeners to a closed PV");
        
        // Check whether to notify when the first listener is added.
        // This is done to make sure that exceptions thrown at pv creation
        // are not lost since the listener is added after the pv is created.
        // If the notification is done on a separate thread, the context switch
        // is enough to make sure the listener is registerred before the event
        // arrives, but if the notification is done on the same thread
        // the notification would be lost.
        boolean notify = notifyFirstListener && missedNotification;
        pvReaderListeners.add(listener);
        if (notify)
            firePvValueChanged();
    }

    /**
     * Adds a listener to the value, which is notified only if the value is
     * of a given type. This method is thread safe.
     *
     * @param listener a new listener
     */
    @Override
    public void addPVReaderListener(final Class<?> clazz, final PVReaderListener listener) {
        if (isClosed())
            throw new IllegalStateException("Can't add listeners to a closed PV");
        pvReaderListeners.add(new ListenerDelegate<T>(clazz, listener));
    }

    private class ListenerDelegate<T> implements PVReaderListener {

        private Class<?> clazz;
        private PVReaderListener delegate;

        public ListenerDelegate(Class<?> clazz, PVReaderListener delegate) {
            this.clazz = clazz;
            this.delegate = delegate;
        }

        @Override
        public void pvChanged() {
            // forward the change if the value is of the right type
            if (clazz.isInstance(getValue()))
                delegate.pvChanged();
        }

        @Override
        public boolean equals(Object obj) {
            // Override equals so that a remove on the user listener
            // will remove this delegate
            if (obj instanceof ListenerDelegate) {
                return delegate.equals(((ListenerDelegate) obj).delegate);
            }

            return delegate.equals(obj);
        }

        @Override
        public int hashCode() {
            // Override hashCode because of equals/hashCode contract
            return delegate.hashCode();
        }
    }

    /**
     * Removes a listener to the value. This method is thread safe.
     *
     * @param listener the old listener
     */
    @Override
    public void removePVReaderListener(PVReaderListener listener) {
        // Removing a delegate will cause the proper comparisons
        // so that it removes either the direct or the delegate
        pvReaderListeners.remove(new ListenerDelegate<T>(Object.class, listener));
    }

    private final String name;

    /**
     * Returns the name of the PVReader. This method is thread safe.
     *
     * @return the value of name
     */
    @Override
    public String getName() {
        return name;
    }

    private T value;

    /**
     * Returns the value of the PVReader. Not thread safe: can be safely accessed only
     * as part of the {@link PVReaderListener}.
     *
     * @return the value of value
     */
    @Override
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
     * connections from the data sources needed by this. Once the PVReader
     * is closed, it can't be re-opened. Subsequent calls to close do not
     * do anything.
     */
    @Override
    public void close() {
        pvReaderListeners.clear();
        closed = true;
    }

    /**
     * True if no more notifications are going to be sent for this PVReader.
     *
     * @return true if closed
     */
    @Override
    public boolean isClosed() {
        return closed;
    }
    
    // This needs to be modified on the client thread (i.e. UI)
    // and read on the timer thread (so it knows to skip)
    private volatile boolean paused = false;

    @Override
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }
    
    
    
    private AtomicReference<Exception> lastException = new AtomicReference<Exception>();
    private volatile boolean lastExceptionToNotify = false;
    private volatile boolean connected = false;
    private volatile boolean readConnectionToNotify = false;

    /**
     * Whether there is an exception that needs to be notified to the client.
     * This is used to throttle back exceptions.
     * 
     * @return true if this pvReader needs to notify an exception
     */
    boolean isLastExceptionToNotify() {
        return lastExceptionToNotify;
    }
    
    /**
     * Whether there is a connection state that needs to be notified.
     * This is used to throttle back connection notifications.
     * 
     * @return true if this pvReader needs to notify a connection state
     */
    boolean isReadConnectionToNotify() {
        return readConnectionToNotify;
    }
    
    /**
     * Changes the last exception associated with the PVReader.
     * 
     * @param ex the new exception
     */
    void setLastException(Exception ex) {
        lastException.set(ex);
        lastExceptionToNotify = true;
    }

    /**
     * Returns the last exception that was generated preparing the value
     * for this PVReader and clears it (subsequent call will return null).
     *
     * @return the last generated exception or null
     */
    @Override
    public Exception lastException() {
        return lastException.getAndSet(null);
    }
    
    void setConnectd(boolean connected) {
        if (this.connected == connected) {
            return;
        }
        
        this.connected = connected;
        readConnectionToNotify = true;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }
}
