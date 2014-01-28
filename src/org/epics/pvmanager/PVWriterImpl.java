/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
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
    
    // Immutable part, no need to synchronize
    private final boolean syncWrite;

    // Thread-safe, no need to synchronize
    private List<PVWriterListener<T>> pvWriterListeners = new CopyOnWriteArrayList<>();
    
    // Atomocity in the callback is guaranteed by how the PVWriterDirector
    //     prepares the PVWriter before the notification
    
    // Thread-safety is guaranteed by the following rule:
    //  - any variable declared after the locked should be read or written
    //    only while holding the lock
    // Potential deadlocks or livelocks are prevented by the following rule:
    //  - never call outside this object, except for something small and understood,
    //    while holding the lock
    
    private final Object lock = new Object();
    // guarded by lock
    private boolean closed = false;
    private boolean writeConnected = false;
    private Exception lastWriteException;
    private PVWriterDirector<T> writeDirector;
    private PVWriter<T> writerForNotification = this;
    private boolean needsConnectionNotification = false;
    private boolean needsExceptionNotification = false;

    PVWriterImpl(boolean syncWrite, boolean notifyFirstListener) {
        this.syncWrite = syncWrite;
    }
    
    void firePvWritten() {
        PVWriterEvent<T> event;
        synchronized(lock) {
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
            event = new PVWriterEvent<>(mask, writerForNotification);
        }
        for (PVWriterListener<T> listener : pvWriterListeners) {
            listener.pvChanged(event);
        }
    }
    
    void fireWriteSuccess() {
        PVWriterEvent<T> event;
        synchronized(lock) {
            event = new PVWriterEvent<>(PVWriterEvent.WRITE_SUCCEEDED_MASK, writerForNotification);
        }
        for (PVWriterListener<T> listener : pvWriterListeners) {
            listener.pvChanged(event);
        }
    }
    
    void fireWriteFailure(Exception ex) {
        setLastWriteException(ex);
        PVWriterEvent<T> event;
        synchronized(lock) {
            event = new PVWriterEvent<>(PVWriterEvent.WRITE_FAILED_MASK, writerForNotification);
        }
        for (PVWriterListener<T> listener : pvWriterListeners) {
            listener.pvChanged(event);
        }
    }

    void setWriteDirector(PVWriterDirector<T> writeDirector) {
        synchronized(lock) {
            this.writeDirector = writeDirector;
        }
    }

    /**
     * Adds a listener to the value. This method is thread safe.
     *
     * @param listener a new listener
     */
    @Override
    public void addPVWriterListener(PVWriterListener<? extends T> listener) {
        if (isClosed()) {
            throw new IllegalStateException("Can't add listeners to a closed PV");
        }

        @SuppressWarnings("unchecked")
        PVWriterListener<T> convertedListener = (PVWriterListener<T>) listener;
        pvWriterListeners.add(convertedListener);
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
        synchronized(lock) {
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
    public void close() {
        synchronized(lock) {
            if (closed) {
                return;
            }
            closed = true;
        }
        pvWriterListeners.clear();
        PVWriterDirector<T> director;
        synchronized(lock) {
            director = writeDirector;
        }
        director.close();
    }

    /**
     * True if no more notifications are going to be sent for this PV.
     *
     * @return true if closed
     */
    @Override
    public boolean isClosed() {
        synchronized(lock) {
            return closed;
        }
    }
    
    /**
     * Changes the last exception associated with write operations.
     * 
     * @param ex the new exception
     */
    void setLastWriteException(Exception ex) {
        synchronized(lock) {
            if (!Objects.equals(ex, lastWriteException)) {
                lastWriteException = ex;
                needsExceptionNotification = true;
            }
        }
    }

    /**
     * Returns the last exception that was generated by write operations
     * and clears it (subsequent call will return null).
     *
     * @return the last generated exception or null
     */
    @Override
    public Exception lastWriteException() {
        synchronized(lock) {
            Exception exception = lastWriteException;
            lastWriteException = null;
            return exception;
        }
    }

    @Override
    public boolean isWriteConnected() {
        synchronized(lock) {
            return writeConnected;
        }
    }
    
    public void setWriteConnected(boolean writeConnected) {
        synchronized(lock) {
            if (this.writeConnected != writeConnected) {
                this.writeConnected = writeConnected;
                needsConnectionNotification = true;
            }
        }
    }
    
    void setWriterForNotification(PVWriter<T> writerForNotification) {
        synchronized(lock) {
            this.writerForNotification = writerForNotification;
        }
    }
}
