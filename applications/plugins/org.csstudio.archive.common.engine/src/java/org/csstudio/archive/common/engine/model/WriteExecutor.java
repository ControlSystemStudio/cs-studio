/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.archive.common.engine.Activator;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveWriterService;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.calc.CumulativeAverageCache;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.ITimedCssAlarmValueType;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.joda.time.Duration;

import com.google.common.collect.Lists;
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

    private static final Logger LOG = CentralLogger.getInstance().getLogger(WriteExecutor.class);

    /** Minimum write period [seconds] */
    private static final long MIN_WRITE_PERIOD_MS = 5000;

    /**
     * The independent worker to write/submit the samples to the persistence layer.
     *
     * @author bknerr
     * @since 14.02.2011
     */
    protected static final class WriteWorker implements Runnable {

        private static final Logger WORKER_LOG =
                CentralLogger.getInstance().getLogger(WriteExecutor.WriteWorker.class);

        private final WriteExecutor _exec;
        private final String _name;
        private final Collection<ArchiveChannel<Object, ITimedCssAlarmValueType<Object>>> _channels;
        private final long _periodInMS;

        /** Average number of values per write run */
        final CumulativeAverageCache _avgWriteCount = new CumulativeAverageCache();
        /** Average duration of write run */
        final CumulativeAverageCache _avgWriteDurationInMS = new CumulativeAverageCache();

        TimeInstant _lastTimeWrite;

        /**
         * Constructor.
         * @param exec
         * @param name
         * @param channels
         * @param periodInMS
         */
        public WriteWorker(@Nonnull final WriteExecutor exec,
                           @Nonnull final String name,
                           @Nonnull final Collection<ArchiveChannel<Object, ITimedCssAlarmValueType<Object>>> channels,
                           final long periodInMS) {
            _exec = exec;
            _name = name;
            WORKER_LOG.info(_name + " started with period " + periodInMS + "ms");
            _channels = channels;
            _periodInMS = periodInMS;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            final BenchmarkTimer timer = new BenchmarkTimer();
            try {
                timer.start();
                // In case of a network problem, we can hang in here for a long time...
                final long written = write();

                timer.stop();
                _lastTimeWrite = TimeInstantBuilder.buildFromNow();

                final long durationInMS = timer.getMilliseconds();
                if (durationInMS >= _periodInMS) {
                    _exec.enhanceWriterThroughput(this);
                }

                _avgWriteCount.accumulate(Double.valueOf(written));
                _avgWriteDurationInMS.accumulate(Double.valueOf(durationInMS));
            } catch (final OsgiServiceUnavailableException e) {
                // FIXME (bknerr) : OSGi service unavailable - handle data rescue here
            } catch (final ArchiveServiceException e) {
                // Error within the service, which is responsible for data rescueing
                WORKER_LOG.error("Exception during write.", e);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }


        }
        /**
         * Drain all data from the buffers to the service interface.
         *  @return number of samples drained/written to the service
         * @throws OsgiServiceUnavailableException
         * @throws ArchiveServiceException
         */
        private long write() throws OsgiServiceUnavailableException, ArchiveServiceException {
            int totalCount = 0;

            final LinkedList<IArchiveSample<Object, ITimedCssAlarmValueType<Object>>> allSamples = Lists.newLinkedList();

            for (final ArchiveChannel<Object, ITimedCssAlarmValueType<Object>> channel : _channels) {
                final SampleBuffer<Object,
                                   ITimedCssAlarmValueType<Object>,
                                   IArchiveSample<Object, ITimedCssAlarmValueType<Object>>> buffer = channel.getSampleBuffer();

                buffer.updateStats();
                totalCount += buffer.size();
                buffer.drainTo(allSamples);
            }
            final IArchiveWriterService writerService = Activator.getDefault().getArchiveWriterService();
            writerService.writeSamples(allSamples);

            return totalCount;
        }

        public long getPeriodInMS() {
            return _periodInMS;
        }
    }

    private final ConcurrentMap<String, ArchiveChannel<Object, ITimedCssAlarmValueType<Object>>> _channelMap =
        Maps.newConcurrentMap();
    private ScheduledExecutorService _executor = Executors.newSingleThreadScheduledExecutor();
    private WriteWorker _writeWorker;
    private long _writePeriodInMS;


    /**
     * Construct thread for writing to server
     */
    public WriteExecutor() {
        // EMPTY
    }

    void enhanceWriterThroughput(@Nonnull final WriteWorker writeWorker) throws InterruptedException {
        final long currentPeriodInMS = writeWorker.getPeriodInMS();
        if (currentPeriodInMS > MIN_WRITE_PERIOD_MS) {
            _executor.shutdown();
            _executor.awaitTermination(currentPeriodInMS, TimeUnit.MILLISECONDS);
            _executor.shutdownNow();

            _executor = Executors.newSingleThreadScheduledExecutor();

            _writePeriodInMS = Math.max(currentPeriodInMS>>1, MIN_WRITE_PERIOD_MS);
            _writeWorker = submitAndScheduleWriteWorker(_writePeriodInMS,
                                                        0L);
        } else {
            LOG.warn("Archive writer duration exceeds minimum period.");
            LOG.warn("Consider starting another writer.");
            // TODO (bknerr) : handle writeWorker exhaustion
        }
    }

    @Nonnull
    private WriteWorker submitAndScheduleWriteWorker(final long writePeriodInMS,
                                                     final long delayInMS) {
        final WriteWorker writeWorker = new WriteWorker(this,
                                                        "Periodic Archive Engine Writer",
                                                        _channelMap.values(),
                                                        writePeriodInMS);
        _executor.scheduleAtFixedRate(writeWorker,
                                      delayInMS,
                                      writeWorker.getPeriodInMS(),
                                      TimeUnit.MILLISECONDS);
        return writeWorker;
    }

    /** Add a channel's buffer that this thread reads */
    public void addChannel(@Nonnull final ArchiveChannel<Object, ITimedCssAlarmValueType<Object>> channel) {
        _channelMap.putIfAbsent(channel.getName(), channel);
    }

    /** Start the write worker.
     *  @param pWritePeriod Period between writes in seconds
     */
    public void start(final long pWritePeriod) {
        if (_writeWorker != null) {
            LOG.warn("Worker has already been submitted with period (ms): " + _writeWorker.getPeriodInMS());
            return;
        }
        long writePeriod = pWritePeriod;
        if (writePeriod < MIN_WRITE_PERIOD_MS) {
            LOG.warn("Adjusting write period from "
                    + pWritePeriod + " to " + MIN_WRITE_PERIOD_MS);
            writePeriod = MIN_WRITE_PERIOD_MS;
        }
        _writePeriodInMS = writePeriod;

        _writeWorker = submitAndScheduleWriteWorker(_writePeriodInMS,
                                                    0L);

    }

    /** Reset statistics */
    public void reset() {
        if (_writeWorker != null) {
            _writeWorker._avgWriteCount.clear();
            _writeWorker._avgWriteDurationInMS.clear();
        }
    }

    /** @return Timestamp of end of last write run */
    @CheckForNull
    public TimeInstant getLastWriteTime() {
        return _writeWorker != null ? _writeWorker._lastTimeWrite : null;
    }

    /** @return Average number of values per write run */
    @CheckForNull
    public Double getAvgWriteCount() {
        return _writeWorker != null ? _writeWorker._avgWriteCount.getValue() : null;
    }

    /** @return  Average duration of write run */
    @CheckForNull
    public Duration getAvgWriteDuration() {
        final Double doubleDurMS = _writeWorker._avgWriteDurationInMS.getValue();
        return _writeWorker != null ? new Duration(doubleDurMS.longValue()) : null;
    }

    /**
     * Stop all write workers, after having submitted a directly executed shutdown worker that
     * shall empty the channels' sample buffers.
     * Gracefully shutdown of the executor.
     */
    public void shutdown() {
        _executor.execute(new WriteWorker(this, "Shutdown worker", _channelMap.values(), 0L));
        _executor.shutdown();
    }
}
