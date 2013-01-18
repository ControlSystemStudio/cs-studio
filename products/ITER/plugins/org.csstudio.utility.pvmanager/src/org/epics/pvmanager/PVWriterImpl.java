/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation class for {@link PVWriter}.
 *
 * @author carcassi
 */
class PVWriterImpl<T> implements PVWriter<T> {
    
    static <T> PVWriterImpl<T> implOf(PVWriter<T> pvWriter) {
        if (pvWriter instanceof PVWriterImpl) {
            return (PVWriterImpl<T>) pvWriter;
        }
        
        throw new IllegalArgumentException("PVWriter must be implemented using PVWriterImpl");
    }

    // PVWriter state
    
    // Immutable part, no need to syncronize
    private final boolean syncWrite;
    private final boolean notifyFirstListener; 
    
    // guarded by this
    private boolean closed = false;
    private boolean writeConnected = false;
    private Exception lastWriteException;
    private List<PVWriterListener<T>> pvWriterListeners = new CopyOnWriteArrayList<PVWriterListener<T>>();
    private PVWriterDirector<T> writeDirector;
    private PVWriter<T> writerForNotification = this;
    private boolean needsConnectionNotification = false;
    private boolean needsExceptionNotification = false;

    PVWriterImpl(boolean syncWrite, boolean notifyFirstListener) {
        this.syncWrite = syncWrite;
        this.notifyFirstListener = notifyFirstListener;
    }
    
    synchronized void firePvWritten() {
        int mask = 0;
        if (needsConnectionNotification) {
            mask += PVWriterEvent.CONNECTION_MASK;
        }
        if (needsExceptionNotification) {
            mask += PVWriterEvent.EXCEPTION_MASK;
        }
        // Nothing to notify
        if (mask == 0) {
            return;
        }
        needsConnectionNotification = false;
        needsExceptionNotification = false;
        PVWriterEvent<T> event = new PVWriterEvent(mask, writerForNotification);
        for (PVWriterListener<T> listener : pvWriterListeners) {
            listener.pvChanged(event);
        }
    }
    
    synchronized void fireWriteSuccess() {
        PVWriterEvent<T> event = new PVWriterEvent(PVWriterEvent.WRITE_SUCCEEDED_MASK, writerForNotification);
        for (PVWriterListener<T> listener : pvWriterListeners) {
            listener.pvChanged(event);
        }
    }
    
    synchronized void fireWriteFailure(Exception ex) {
        setLastWriteException(ex);
        PVWriterEvent<T> event = new PVWriterEvent(PVWriterEvent.WRITE_FAILED_MASK, writerForNotification);
        for (PVWriterListener<T> listener : pvWriterListeners) {
            listener.pvChanged(event);
        }
    }

    synchronized void setWriteDirector(PVWriterDirector<T> writeDirector) {
        this.writeDirector = writeDirector;
    }

    /**
     * Adds a listener to the value. This method is thread safe.
     *
     * @param listener a new listener
     */
    @Override
    public synchronized void addPVWriterListener(PVWriterListener<? extends T> listener) {
        if (isClosed())
            throw new IllegalStateException("Can't add listeners to a closed PV");
        
        // Check whether to notify when the first listener is added.
        // This is done to make sure that exceptions thrown at pv creation
        // are not lost since the listener is added after the pv is created.
        // If the notification is done on a separate thread, the context switch
        // is enough to make sure the listener is registerred before the event
        // arrives, but if the notification is done on the same thread
        // the notification would be lost.
        boolean notify = pvWriterListeners.isEmpty() && notifyFirstListener &&
                lastWriteException != null;
        @SuppressWarnings("unchecked")
        PVWriterListener<T> convertedListener = (PVWriterListener<T>) listener;
        pvWriterListeners.add(convertedListener);
        if (notify)
            listener.pvChanged(null);
    }

    /**
     * Removes a listener to the value. This method is thread safe.
     *
     * @param listener the old listener
     */
    @Override
    public void removePVWriterListener(PVWriterListener<? extends T> listener) {
        @SuppressWarnings("unchecked")
        PVWriterListener<T> convertedListener = (PVWriterListener<T>) listener;
        pvWriterListeners.remove(convertedListener);
    }
    
    
    @Override
    public void write(T newValue) {
        // Safely taking the write directory
        // the whole method can't be in a synchronized block, or
        // it would block the notifications in case of syncWrite
        // and would deadlock
        PVWriterDirector<T> director;
        synchronized(this) {
            director = writeDirector;
        }
        if (syncWrite) {
            director.syncWrite(newValue, this);
        } else {
            director.write(newValue, this);
        }
    }

    /**
     * De-registers all listeners, stops all notifications and closes all
     * connections from the data sources needed by this. Once the PV
     * is closed, it can't be re-opened. Subsequent calls to close do not
     * do anything.
     */
    @Override
    public synchronized void close() {
        if (!closed) {
            closed = true;
            pvWriterListeners.clear();
            writeDirector.close();
        }
    }

    /**
     * True if no more notifications are going to be sent for this PV.
     *
     * @return true if closed
     */
    @Override
    public synchronized boolean isClosed() {
        return closed;
    }
    
    /**
     * Changes the last exception associated with write operations.
     * 
     * @param ex the new exception
     */
    synchronized void setLastWriteException(Exception ex) {
        if (!Objects.equals(ex, lastWriteException)) {
            lastWriteException = ex;
            needsExceptionNotification = true;
        }
    }

    /**
     * Returns the last exception that was generated by write operations
     * and clears it (subsequent call will return null).
     *
     * @return the last generated exception or null
     */
    @Override
    public synchronized Exception lastWriteException() {
        Exception exception = lastWriteException;
        lastWriteException = null;
        return exception;
    }

    @Override
    public synchronized boolean isWriteConnected() {
        return writeConnected;
    }
    
    public synchronized void setWriteConnected(boolean writeConnected) {
        if (this.writeConnected != writeConnected) {
            this.writeConnected = writeConnected;
            needsConnectionNotification = true;
        }
    }
    
    void setWriterForNotification(PVWriter<T> writerForNotification) {
        this.writerForNotification = writerForNotification;
    }
}
