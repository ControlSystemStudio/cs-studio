/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.pvmanager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;
import org.epics.util.time.TimeDuration;

/**
 * Represent a strategy to decouple desired rate events from source rate
 * events.
 *
 * @author carcassi
 */
abstract class SourceDesiredRateDecoupler {
    
    private static final Logger log = Logger.getLogger(SourceDesiredRateDecoupler.class.getName());
    private final DesiredRateEventListener listener;
    private final ScheduledExecutorService scannerExecutor;
    private final TimeDuration maxDuration;
    
    protected final Object lock = new Object();
    private boolean eventProcessing = false;
    private boolean paused = false;
    private boolean stopped = false;

    /**
     * Creates a new rate decoupler that will send the events to the
     * given listener.
     * 
     * @param scannerExecutor executor for the scanner tasks
     * @param maxDuration max interval between notifications
     * @param listener the event callback
     */
    public SourceDesiredRateDecoupler(ScheduledExecutorService scannerExecutor, TimeDuration maxDuration,
            DesiredRateEventListener listener) {
        this.listener = listener;
        this.scannerExecutor = scannerExecutor;
        this.maxDuration = maxDuration;
    }

    public ScheduledExecutorService getScannerExecutor() {
        return scannerExecutor;
    }

    public TimeDuration getMaxDuration() {
        return maxDuration;
    }
    
    /**
     * Starts the scanning. From this moment on, source rate events
     * may trigger desired rate events.
     */
    final void start() {
        onStart();
    }
    
    /**
     * Initialization to be done when the pv is started.
     * <p>
     * Empty implementation to be overridden.
     */
    void onStart() {
    }
    
    /**
     * Pause the scanning. Events will be collected and delayed until a resume.
     */
    final void pause() {
        synchronized(lock) {
            if (paused) {
                log.warning("Pausing an already paused scanner");
            }
            paused = true;
        }
    }
    
    /**
     * Resumes the scanning. If events were collected during the pause,
     * they will be sent right away.
     */
    final void resume() {
        synchronized(lock) {
            if (!paused) {
                log.warning("Resuming a non paused scanner");
            }
            paused = false;
        }
    }
    
    /**
     * Stops the scanning. From this moment on, the pv will no longer be
     * notified. Can't be restarted.
     */
    final void stop() {
        synchronized(lock) {
            stopped = true;
        }
        onStop();
    }
    
    /**
     * Cleanup to be done when the pv is stopped.
     * <p>
     * Empty implementation to be overridden.
     */
    void onStop() {
    }
    
    /**
     * Called when a read connection state changes.
     */
    abstract void newReadConnectionEvent();
    
    /**
     * Called when a write connection state changes.
     */
    abstract void newWriteConnectionEvent();
    
    /**
     * Called when a new read value is available.
     */
    abstract void newValueEvent();
    
    /**
     * Called when a reader error is encountered.
     */
    abstract void newReadExceptionEvent();
    
    /**
     * Called when a writer error is encountered.
     */
    abstract void newWriteExceptionEvent();
    
    /**
     * Called when a write operation terminated successfully.
     */
    abstract void newWriteSuccededEvent();
    
    /**
     * Called when a write operation terminated unsuccessfully.
     */
    abstract void newWriteFailedEvent(Exception ex);
    
    /**
     * Call when a new event should be triggered at the desired rate.
     * After calling this method, one should wait for the next {@link #readyForNextEvent() }
     * before calling it again.
     */
    final void sendDesiredRateEvent(DesiredRateEvent event) {
        synchronized(lock) {
            if (isEventProcessing()) {
                throw new RuntimeException("Previous event still in flight");
            }
            eventProcessing = true;
        }
        listener.desiredRateEvent(event);
    }
    
    /**
     * Called after a pv is notified. Once {@link #sendDesiredRateEvent() }
     * is called, it should not be called again before this method is called.
     */
    final void readyForNextEvent() {
        synchronized(lock) {
            if (!isEventProcessing()) {
                log.warning("Event processing is done, but no event was in flight");
            }
            eventProcessing = false;
        }
        onDesiredEventProcessed();
    }
    
    /**
     * Called after an event was successfully processed.
     * <p>
     * Empty implementation to be overridden.
     */
    void onDesiredEventProcessed() {
    }

    /**
     * True if an event was sent, but the ready for next event wasn't receiced.
     * 
     * @return ture if there is still an event in-flight
     */
    public boolean isEventProcessing() {
        synchronized(lock) {
            return eventProcessing;
        }
    }

    /**
     * Whether the scanning is currently paused.
     * @return true if paused
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Whether the scanning is currently stopped.
     * @return true if stopped
     */
    public boolean isStopped() {
        return stopped;
    }
    
}
