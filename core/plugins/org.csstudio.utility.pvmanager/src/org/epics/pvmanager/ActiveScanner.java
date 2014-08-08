/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.pvmanager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.epics.util.time.TimeDuration;

/**
 *
 * @author carcassi
 */
class ActiveScanner implements Scanner {
    /** Executor used to scan the connection/exception queues */
    private final ScheduledExecutorService scannerExecutor;
    private volatile ScheduledFuture<?> scanTaskHandle;
    private final PVReaderDirector readerDirector;
    private final TimeDuration maxDuration;

    ActiveScanner(ScheduledExecutorService scannerExecutor, PVReaderDirector readerDirector, TimeDuration maxDuration,
            TimeDuration timeout, final String timeoutMessage) {
        this.scannerExecutor = scannerExecutor;
        this.readerDirector = readerDirector;
        this.maxDuration = maxDuration;
        if (timeout != null) {
            scannerExecutor.schedule(new Runnable() {
                @Override
                public void run() {
                    ActiveScanner.this.readerDirector.processTimeout(timeoutMessage);
                }
            }, timeout.toNanosLong(), TimeUnit.NANOSECONDS);
        }
    }
    
    @Override
    public void start() {
        scanTaskHandle = scannerExecutor.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                if (readerDirector.isActive()) {
                    // If paused, simply skip without stopping the scan
                    if (!readerDirector.isPaused()) {
                        readerDirector.notifyPv();
                    }
                } else {
                    stop();
                }
            }
        }, 0, maxDuration.toNanosLong(), TimeUnit.NANOSECONDS);
    }
    
    @Override
    public void stop() {
        readerDirector.close();
        readerDirector.disconnect();
        if (scanTaskHandle != null) {
            scanTaskHandle.cancel(false);
            scanTaskHandle = null;
        } else {
            throw new IllegalStateException("Scan was never started");
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
        // Do nothing
    }

    @Override
    public void notifiedPv() {
        // Do nothing
    }
    
}
