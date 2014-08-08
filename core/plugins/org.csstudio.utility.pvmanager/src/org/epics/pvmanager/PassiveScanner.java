/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.pvmanager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;

/**
 *
 * @author carcassi
 */
class PassiveScanner implements Scanner {
    /** Executor used to scan the connection/exception queues */
    private final ScheduledExecutorService scannerExecutor;
    private volatile ScheduledFuture<?> scanTaskHandle;
    private final PVReaderDirector readerDirector;
    private final TimeDuration maxDuration;
    private final Object lock = new Object();
    private Timestamp lastSubmission;
    // Keeps track of whether 
    private boolean processingEvent;
    private boolean eventQueued;

    PassiveScanner(ScheduledExecutorService scannerExecutor, PVReaderDirector readerDirector, TimeDuration maxDuration,
            TimeDuration timeout, final String timeoutMessage) {
        this.scannerExecutor = scannerExecutor;
        this.readerDirector = readerDirector;
        this.maxDuration = maxDuration;
        if (timeout != null) {
            scannerExecutor.schedule(new Runnable() {
                @Override
                public void run() {
                    PassiveScanner.this.readerDirector.processTimeout(timeoutMessage);
                }
            }, timeout.toNanosLong(), TimeUnit.NANOSECONDS);
        }
    }
    
    private final Runnable notificationTask = new Runnable() {

        @Override
        public void run() {
            if (readerDirector.isActive()) {
                // If paused, simply skip without stopping the scan
                if (!readerDirector.isPaused()) {
                    synchronized (lock) {
                        processingEvent = true;
                    }
                    readerDirector.notifyPv();
                }
            } else {
                stop();
            }
        }
    };
    
    void scheduleNext() {
        Timestamp now = Timestamp.now();
        if (lastSubmission == null) {
            scannerExecutor.submit(notificationTask);
        } else {
            TimeDuration delay = maxDuration.minus(now.durationBetween(lastSubmission));
            if (delay.isPositive()) {
                scannerExecutor.schedule(notificationTask, delay.toNanosLong(), TimeUnit.NANOSECONDS);
            }
        }
    }
    
    private void newEvent() {
        boolean submit;
        synchronized(lock) {
            if (processingEvent) {
                // If there is already an event processing, we should
                // submit after the previous event is done
                submit = false;
                eventQueued = true;
            } else {
                // If there isn't an event processing,
                // we need to submit anyway. We can clear the event
                // queued at this point: we are going to submit anyway
                submit = true;
                eventQueued = false;
            }
        }
        if (submit) {
            scheduleNext();
        }
    }
    
    @Override
    public void start() {
        newEvent();
    }
    
    @Override
    public void stop() {
        readerDirector.close();
        readerDirector.disconnect();
        synchronized(lock) {
            eventQueued = false;
        }
    }

    @Override
    public void pause() {
        // Do nothing
    }

    @Override
    public void resume() {
        // Do nothing
    }

    @Override
    public void collectorChange() {
        newEvent();
    }

    @Override
    public void notifiedPv() {
        boolean submit;
        synchronized(lock) {
            processingEvent = false;
            if (eventQueued) {
                // If there is an event in queue, we should submit
                // next execution
                submit = true;
                eventQueued = false;
            } else {
                // Do nothing
                submit = false;
            }
        }
        if (submit) {
            scheduleNext();
        }
    }
    
}
