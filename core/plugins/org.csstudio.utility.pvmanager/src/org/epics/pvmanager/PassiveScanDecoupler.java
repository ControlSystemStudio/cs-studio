/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.pvmanager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;

/**
 *
 * @author carcassi
 */
class PassiveScanDecoupler extends SourceDesiredRateDecoupler {
    
    private static final Logger log = Logger.getLogger(PassiveScanDecoupler.class.getName());
    // TODO: this could be made configurable between FINEST and OFF, and the if
    // modified so that code elimination would remove the logging completely
    private static final Level logLevel = Level.FINEST;
    
    private DesiredRateEvent queuedEvent;
    private Timestamp lastSubmission;
    private boolean scanActive;

    public PassiveScanDecoupler(ScheduledExecutorService scannerExecutor,
            TimeDuration maxDuration, DesiredRateEventListener listener) {
        super(scannerExecutor, maxDuration, listener);
        synchronized(lock) {
            lastSubmission = Timestamp.now().minus(getMaxDuration());
        }
    }
    
    private final Runnable notificationTask = new Runnable() {

        @Override
        public void run() {
            DesiredRateEvent nextEvent;
            synchronized(lock) {
                nextEvent = queuedEvent;
                queuedEvent = null;
                if (log.isLoggable(logLevel)) {
                    log.log(Level.WARNING, "Submitted {0}", Timestamp.now());
                }
            }
            sendDesiredRateEvent(nextEvent);
        }
    };

    @Override
    void onStart() {
        // When starting, send an event in case the expressions
        // are constants
        newEvent(DesiredRateEvent.Type.READ_CONNECTION);
    }

    @Override
    void onStop() {
        synchronized(lock) {
            queuedEvent = null;
        }
    }

    @Override
    void newReadConnectionEvent() {
        newEvent(DesiredRateEvent.Type.READ_CONNECTION);
    }

    @Override
    void newWriteConnectionEvent() {
        newEvent(DesiredRateEvent.Type.WRITE_CONNECTION);
    }

    @Override
    void newValueEvent() {
        newEvent(DesiredRateEvent.Type.VALUE);
    }

    @Override
    void newReadExceptionEvent() {
        newEvent(DesiredRateEvent.Type.READ_EXCEPTION);
    }

    @Override
    void newWriteExceptionEvent() {
        newEvent(DesiredRateEvent.Type.WRITE_EXCEPTION);
    }

    @Override
    void newWriteSuccededEvent() {
        DesiredRateEvent event = new DesiredRateEvent();
        event.addType(DesiredRateEvent.Type.WRITE_SUCCEEDED);
        scheduleWriteOutcome(event);
    }

    @Override
    void newWriteFailedEvent(Exception ex) {
        DesiredRateEvent event = new DesiredRateEvent();
        event.addWriteFailed(new RuntimeException());
        sendDesiredRateEvent(event);
    }

    @Override
    void onDesiredEventProcessed() {
        TimeDuration delay = null;
        synchronized (lock) {
            if (queuedEvent != null) {
                Timestamp nextSubmission = lastSubmission.plus(getMaxDuration());
                delay = nextSubmission.durationFrom(Timestamp.now());
                lastSubmission = nextSubmission;
                if (log.isLoggable(logLevel)) {
                    log.log(Level.WARNING, "Schedule next {0}", Timestamp.now());
                }
            } else {
                scanActive = false;
                if (log.isLoggable(logLevel)) {
                    log.log(Level.WARNING, "Do not schedule next {0}", Timestamp.now());
                }
            }
        }
        
        if (delay != null) {
            scheduleNext(delay);
        }
    }
    
    private void newEvent(DesiredRateEvent.Type type) {
        boolean submit;
        TimeDuration delay = null;
        
        synchronized (lock) {
            // Add event to the queue
            if (queuedEvent == null) {
                queuedEvent = new DesiredRateEvent();
                if (log.isLoggable(logLevel)) {
                    log.log(Level.WARNING, "Creating queued event {0}", Timestamp.now());
                }
            }
            queuedEvent.addType(type);

            // If scan is not active, submit the next scan
            if (!scanActive) {
                submit = true;
                Timestamp currentTimestamp = Timestamp.now();
                Timestamp nextTimeSlot = lastSubmission.plus(getMaxDuration());
                if (currentTimestamp.compareTo(nextTimeSlot) < 0) {
                    delay = nextTimeSlot.durationFrom(currentTimestamp);
                    lastSubmission = nextTimeSlot;
                    if (log.isLoggable(logLevel)) {
                        log.log(Level.WARNING, "Submit delayed {0}", Timestamp.now());
                    }
                } else {
                    lastSubmission = currentTimestamp;
                    if (log.isLoggable(logLevel)) {
                        log.log(Level.WARNING, "Submit now {0}", Timestamp.now());
                    }
                }
                scanActive = true;
            } else {
                submit = false;
                if (log.isLoggable(logLevel)) {
                    log.log(Level.WARNING, "Do not submit {0}", Timestamp.now());
                }
            }
            
        }
        if (submit) {
            scheduleNext(delay);
        }
    }
    
    private void scheduleNext(TimeDuration delay) {
        if (delay == null || delay.isNegative()) {
            getScannerExecutor().submit(notificationTask);
        } else {
            getScannerExecutor().schedule(notificationTask, delay.toNanosLong(), TimeUnit.NANOSECONDS);
        }
    }
    
    
    
    /**
     * If possible, submit the event right away, otherwise try again later.
     * @param event the event to submit
     */
    private void scheduleWriteOutcome(final DesiredRateEvent event) {
        if (!isEventProcessing()) {
            sendDesiredRateEvent(event);
        } else {
            getScannerExecutor().submit(new Runnable() {

                @Override
                public void run() {
                    scheduleWriteOutcome(event);
                }
            });
        }
    }
    
}
