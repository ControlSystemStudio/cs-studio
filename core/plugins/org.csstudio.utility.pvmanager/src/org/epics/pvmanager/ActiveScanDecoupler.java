/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.pvmanager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.epics.pvmanager.*;
import org.epics.util.time.TimeDuration;

/**
 *
 * @author carcassi
 */
class ActiveScanDecoupler extends SourceDesiredRateDecoupler {
    
    private volatile ScheduledFuture<?> scanTaskHandle;

    public ActiveScanDecoupler(ScheduledExecutorService scannerExecutor,
            TimeDuration maxDuration, DesiredRateEventListener listener) {
        super(scannerExecutor, maxDuration, listener);
    }

    @Override
    void onStart() {
        scanTaskHandle = getScannerExecutor().scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                if (!isStopped() && !isPaused() && !isEventProcessing()) {
                    DesiredRateEvent event = new DesiredRateEvent();
                    event.addType(DesiredRateEvent.Type.READ_CONNECTION);
                    event.addType(DesiredRateEvent.Type.READ_EXCEPTION);
                    event.addType(DesiredRateEvent.Type.VALUE);
                    event.addType(DesiredRateEvent.Type.WRITE_CONNECTION);
                    event.addType(DesiredRateEvent.Type.WRITE_EXCEPTION);
                    sendDesiredRateEvent(event);
                }
            }
        }, 0, getMaxDuration().toNanosLong(), TimeUnit.NANOSECONDS);
    }

    @Override
    void onStop() {
        if (scanTaskHandle != null) {
            scanTaskHandle.cancel(false);
            scanTaskHandle = null;
        } else {
            throw new IllegalStateException("Scan was never started");
        }
    }
    
    @Override
    void newReadConnectionEvent() {
        // Do nothing
    }

    @Override
    void newWriteConnectionEvent() {
        // Do nothing
    }

    @Override
    void newValueEvent() {
        // Do nothing
    }

    @Override
    void newReadExceptionEvent() {
        // Do nothing
    }

    @Override
    void newWriteExceptionEvent() {
        // Do nothing
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
