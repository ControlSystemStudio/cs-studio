/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.platform.logging.CentralLogger;
import org.joda.time.Duration;

import com.google.common.collect.Maps;

/**
 * Executor wrapper to handle the archive writer pool.
 * The write worker is periodically scheduled to submit all samples from the channels' samplebuffers
 * to the persistence layer via the archive service.
 *
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 */
public class WriteExecutor {

    private static final int MAX_AWAIT_TERMINATION_TIME_S = 2;

    private static final Logger LOG = CentralLogger.getInstance().getLogger(WriteExecutor.class);

    /** Minimum write period [seconds] */
    private static final long MIN_WRITE_PERIOD_MS = 5000;

    private final ConcurrentMap<String, ArchiveChannel<Object, ISystemVariable<Object>>> _channelMap =
        Maps.newConcurrentMap();

    private final ScheduledExecutorService _heartBeatExecutor =
        Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService _writeSamplesExecutor =
        Executors.newSingleThreadScheduledExecutor();

    private WriteWorker _writeWorker;
    private long _writePeriodInMS;

    private final IServiceProvider _provider;

    private final ArchiveEngineId _engineId;

    /**
     * Construct thread for writing to server
     * @param provider provider for the service
     */
    public WriteExecutor(@Nonnull final IServiceProvider provider,
                         @Nonnull final ArchiveEngineId engineId) {
        _provider = provider;
        _engineId = engineId;
    }


    public void addChannel(@Nonnull final ArchiveChannel<Object, ISystemVariable<Object>> channel) {
        _channelMap.putIfAbsent(channel.getName(), channel);
    }

    public void start(final long pHeartBeatPeriodInMS, final long pWritePeriodInMS) {

        if (_writeWorker != null) {
            LOG.warn("Worker has already been submitted with period (ms): " + _writeWorker.getPeriodInMS());
            return;
        }

        _writePeriodInMS = adjustWritePeriod(pWritePeriodInMS, MIN_WRITE_PERIOD_MS);


        _writeWorker = submitAndScheduleWriteWorker(_provider,
                                                    _writePeriodInMS);
        submitAndScheduleHeartBeatWorker(_engineId,
                                         _provider,
                                         pHeartBeatPeriodInMS);

    }


    @Nonnull
    private HeartBeatWorker submitAndScheduleHeartBeatWorker(@Nonnull final ArchiveEngineId engineId,
                                                             @Nonnull final IServiceProvider provider,
                                                             final long period) {
        final HeartBeatWorker heartBeatWorker = new HeartBeatWorker(engineId,
                                                                    provider);
        _heartBeatExecutor.scheduleAtFixedRate(heartBeatWorker,
                                               0L,
                                               period,
                                               TimeUnit.MILLISECONDS);
        return heartBeatWorker;

    }
    @Nonnull
    private WriteWorker submitAndScheduleWriteWorker(@Nonnull final IServiceProvider provider,
                                                     final long writePeriodInMS) {
        final WriteWorker writeWorker = new WriteWorker(provider,
                                                        "Periodic Archive Engine Writer",
                                                        _channelMap.values(),
                                                        writePeriodInMS);
        _writeSamplesExecutor.scheduleAtFixedRate(writeWorker,
                                                  500L,
                                                  writePeriodInMS,
                                                  TimeUnit.MILLISECONDS);
        return writeWorker;
    }

    private long adjustWritePeriod(final long pWritePeriod, final long minWritePeriodMs) {
        long writePeriod = pWritePeriod;
        if (writePeriod < minWritePeriodMs) {
            LOG.warn("Adjusting write period from "
                    + pWritePeriod + " to " + minWritePeriodMs);
            writePeriod = minWritePeriodMs;
        }
        return writePeriod;
    }

    /** Reset statistics */
    public void reset() {
        if (_writeWorker != null) {
            _writeWorker.clear();
        }
    }

    @CheckForNull
    public TimeInstant getLastWriteTime() {
        return _writeWorker != null ? _writeWorker.getLastWriteTime() : null;
    }

    /** @return Average number of values per write run */
    @CheckForNull
    public Double getAvgWriteCount() {
        return _writeWorker != null ? _writeWorker.getAvgWriteCount().getValue() : null;
    }

    /** @return  Average duration of write run */
    @CheckForNull
    public Duration getAvgWriteDuration() {
        final Double doubleDurMS = _writeWorker.getAvgWriteDurationInMS().getValue();
        return _writeWorker != null ? new Duration(doubleDurMS.longValue()) : null;
    }

    /**
     * Stop the write and heartbeat worker, after having submitted a directly executed shutdown worker that
     * shall drain the channels' sample buffers.
     * Then gracefully shutdown the executor.
     */
    public void shutdown() {
        if (!_writeSamplesExecutor.isShutdown()) {
            _writeSamplesExecutor.shutdown();
        }
        if (!_heartBeatExecutor.isShutdown()) {
            _heartBeatExecutor.shutdown();
        }
        performFinalWriteBeforeShutdown();
    }

    private void performFinalWriteBeforeShutdown() {
        final ExecutorService finalWriteExecutor = Executors.newSingleThreadExecutor();
        finalWriteExecutor.execute(new WriteWorker(_provider,
                                                   "Shutdown worker",
                                                   _channelMap.values(),
                                                   0L));


        final Duration dur = computeAwaitTerminationTime();
        // Await termination (either the average duration or the max termination time.
        try {
            finalWriteExecutor.awaitTermination(dur.getMillis(), TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Nonnull
    private Duration computeAwaitTerminationTime() {
        Duration dur = getAvgWriteDuration();
        if (dur == null || dur.getMillis() < Duration.standardSeconds(MAX_AWAIT_TERMINATION_TIME_S).getMillis()) {
            dur = Duration.standardSeconds(MAX_AWAIT_TERMINATION_TIME_S);
        }
        return dur;
    }
}
