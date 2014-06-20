/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An object representing the PVReader. It contains all elements that are common
 * to all PVs of all type. The payload is specified by the generic type,
 * and is returned by {@link #getValue()}. Changes in
 * values are notified through the {@link PVReaderListener}. Listeners
 * can be registered from any thread.
 * <p>
 * The value/connection/exception can be accessed from any the thread,
 * but there is no guarantee on the atomicity. The only way to work on a consistent
 * snapshot is to use a listener.
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

    // PVReader state
    
    // Immutable part, no need to syncronize
    private final String name;
    private final boolean notifyFirstListener;

    // ReaderListener have their own syncronization, which allows
    // adding/removing listeners while iterating.
    private List<PVReaderListener<T>> pvReaderListeners = new CopyOnWriteArrayList<>();
    
    // Atomocity in the callback is guaranteed by how the PVReaderDirector
    //     prepares the PVReader before the notification
    
    // Thread-safety is guaranteed by the following rule:
    //  - any variable declared after the locked should be read or written
    //    only while holding the lock
    // Potential deadlocks or livelocks are prevented by the following rule:
    //  - never call outside this object, except for something small and understood,
    //    while holding the lock

    private final Object lock = new Object();
    
    // guarded by lock
    private boolean closed = false;
    private boolean paused = false;
    private boolean connected = false;
    private T value;
    private PVReader<T> readerForNotification = this;
    private Exception lastException;
    private boolean exceptionToNotify = false;
    private boolean connectionToNotify = false;
    private boolean valueToNotify = false;
    private boolean sentFirstEvent = false;
    private PVReaderDirector<T> director = null;

    void setReaderForNotification(PVReader<T> readerForNotification) {
        synchronized(lock) {
            this.readerForNotification = readerForNotification;
        }
    }

    void firePvValueChanged() {
        int notificationMask = 0;
        PVReaderEvent<T> event;
        synchronized(lock) {
            if (connectionToNotify) {
                notificationMask += PVReaderEvent.CONNECTION_MASK;
            }
            if (valueToNotify) {
                notificationMask += PVReaderEvent.VALUE_MASK;
            }
            if (exceptionToNotify) {
                notificationMask += PVReaderEvent.EXCEPTION_MASK;
            }
            connectionToNotify = false;
            valueToNotify = false;
            exceptionToNotify = false;
            sentFirstEvent = true;
            event = new PVReaderEvent<>(notificationMask, readerForNotification);
        }
        
        for (PVReaderListener<T> listener : pvReaderListeners) {
            listener.pvChanged(event);
        }
    }

    /**
     * Adds a listener to the value. This method is thread safe.
     *
     * @param listener a new listener
     */
    @Override
    public void addPVReaderListener(PVReaderListener<? super T> listener) {
        if (isClosed())
            throw new IllegalStateException("Can't add listeners to a closed PV");
        
        @SuppressWarnings("unchecked")
        PVReaderListener<T> convertedListener = (PVReaderListener<T>) listener;
        pvReaderListeners.add(convertedListener);
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

    private class ListenerDelegate<E> implements PVReaderListener<T> {

        private Class<?> clazz;
        private PVReaderListener delegate;

        public ListenerDelegate(Class<?> clazz, PVReaderListener delegate) {
            this.clazz = clazz;
            this.delegate = delegate;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void pvChanged(PVReaderEvent<T> event) {
            // forward the change if the value is of the right type
            if (clazz.isInstance(getValue()))
                delegate.pvChanged(event);
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
    public void removePVReaderListener(PVReaderListener<? super T> listener) {
        // Removing a delegate will cause the proper comparisons
        // so that it removes either the direct or the delegate
        pvReaderListeners.remove(new ListenerDelegate<Object>(Object.class, listener));
    }


    /**
     * Returns the name of the PVReader. This method is thread safe.
     *
     * @return the value of name
     */
    @Override
    public String getName() {
        return name;
    }


    /**
     * Returns the value of the PVReader. Not thread safe: can be safely accessed only
     * as part of the {@link PVReaderListener}.
     *
     * @return the value of value
     */
    @Override
    public T getValue() {
        synchronized(lock) {
            return value;
        }
    }

    void setValue(T value) {
        synchronized(lock) {
            this.value = value;
            valueToNotify = true;
        }
        firePvValueChanged();
    }

    public void setDirector(PVReaderDirector<T> director) {
        synchronized(this) {
            this.director = director;
        }
    }
    
    

    /**
     * De-registers all listeners, stops all notifications and closes all
     * connections from the data sources needed by this. Once the PVReader
     * is closed, it can't be re-opened. Subsequent calls to close do not
     * do anything.
     */
    @Override
    public void close() {
        pvReaderListeners.clear();
        synchronized(lock) {
            director.close();
            closed = true;
        }
    }

    /**
     * True if no more notifications are going to be sent for this PVReader.
     *
     * @return true if closed
     */
    @Override
    public boolean isClosed() {
        synchronized(lock) {
            return closed;
        }
    }

    @Override
    public void setPaused(boolean paused) {
        synchronized(lock) {
            this.paused = paused;
        }
    }

    @Override
    public boolean isPaused() {
        synchronized(lock) {
            return paused;
        }
    }

    /**
     * Whether there is an exception that needs to be notified to the client.
     * This is used to throttle back exceptions.
     * 
     * @return true if this pvReader needs to notify an exception
     */
    boolean isLastExceptionToNotify() {
        synchronized(lock) {
            return exceptionToNotify;
        }
    }
    
    /**
     * Whether there is a connection state that needs to be notified.
     * This is used to throttle back connection notifications.
     * 
     * @return true if this pvReader needs to notify a connection state
     */
    boolean isReadConnectionToNotify() {
        synchronized(lock) {
            return connectionToNotify;
        }
    }
    
    /**
     * Changes the last exception associated with the PVReader.
     * 
     * @param ex the new exception
     */
    void setLastException(Exception ex) {
        synchronized(lock) {
            lastException = ex;
            exceptionToNotify = true;
        }
    }

    /**
     * Returns the last exception that was generated preparing the value
     * for this PVReader and clears it (subsequent call will return null).
     *
     * @return the last generated exception or null
     */
    @Override
    public Exception lastException() {
        synchronized(lock) {
            Exception ex = lastException;
            lastException = null;
            return ex;
        }
    }
    
    void setConnected(boolean connected) {
        synchronized(lock) {
            if (this.connected == connected) {
                return;
            }

            this.connected = connected;
            connectionToNotify = true;
        }
    }

    @Override
    public boolean isConnected() {
        synchronized(lock) {
            return connected;
        }
    }

    public boolean isSentFirsEvent() {
        synchronized(lock) {
            return sentFirstEvent;
        }
    }
}
