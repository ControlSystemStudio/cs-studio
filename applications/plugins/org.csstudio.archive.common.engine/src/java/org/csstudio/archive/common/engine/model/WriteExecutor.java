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

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.csstudio.archive.common.engine.Activator;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveWriterService;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.calc.CumulativeAverageCache;
import org.csstudio.domain.desy.types.ITimedCssAlarmValueType;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/** Thread that writes values from multiple <code>SampleBuffer</code>s
 *  to an <code>RDBArchiveServer</code>.
 *  <p>
 *  When there are write errors, it sets the sample buffer error state
 *  and tries to reconnect to the database and write again until successful.
 *  Since the Oracle batch mechanism doesn't tell us what exactly failed
 *  in a batch, all the samples that were part of the batch might
 *  be lost.
 *  The channels that add samples to the sample buffer supposedly notice
 *  the error condition and add a special indicator once we recover.
 *
 *  @author Kay Kasemir
 */
public class WriteExecutor {

    private static final Logger LOG = CentralLogger.getInstance().getLogger(WriteExecutor.class);

    /** Minimum write period [seconds] */
    private static final long MIN_WRITE_PERIOD_S = 5;

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
        private final long _periodInS;

        /** Average number of values per write run */
        final CumulativeAverageCache _avgWriteCount = new CumulativeAverageCache();
        /** Average duration of write run */
        final CumulativeAverageCache _avgWriteDurationInMS = new CumulativeAverageCache();

        ITimestamp _lastTimeWrite;


        /**
         * Constructor.
         * @param string
         * @param values
         */
        public WriteWorker(@Nonnull final WriteExecutor exec,
                           @Nonnull final String name,
                           @Nonnull final Collection<ArchiveChannel<Object, ITimedCssAlarmValueType<Object>>> channels,
                           final long periodInS) {
            _exec = exec;
            _name = name;
            WORKER_LOG.info(_name + " started with period " + periodInS + "s");
            _channels = channels;
            _periodInS = periodInS;
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
                _lastTimeWrite = TimestampFactory.now();

                final long durationInMS = timer.getMilliseconds();
                if (durationInMS >= _periodInS) {
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

        public long getPeriodInS() {
            return _periodInS;
        }
    }

    private final ConcurrentMap<String, ArchiveChannel<Object, ITimedCssAlarmValueType<Object>>> _channelMap =
        Maps.newConcurrentMap();
    private ScheduledExecutorService _executor = Executors.newSingleThreadScheduledExecutor();
    private WriteWorker _writeWorker;
    private long _writePeriodInS;


    /**
     * Construct thread for writing to server
     */
    public WriteExecutor() {
        // EMPTY
    }

    void enhanceWriterThroughput(@Nonnull final WriteWorker writeWorker) throws InterruptedException {
        final long currentPeriod = writeWorker.getPeriodInS();
        if (currentPeriod > MIN_WRITE_PERIOD_S) {
            _executor.shutdown();
            _executor.awaitTermination(currentPeriod, TimeUnit.SECONDS);
            _executor.shutdownNow();

            _executor = Executors.newSingleThreadScheduledExecutor();

            _writePeriodInS = Math.max(currentPeriod>>1, MIN_WRITE_PERIOD_S);
            _writeWorker = submitAndScheduleWriteWorker(_writePeriodInS,
                                                        0L);
        } else {
            LOG.warn("Archive writer duration exceeds minimum period.");
            LOG.warn("Consider starting another writer.");
            // TODO (bknerr) : handle writeWorker exhaustion
        }
    }

    @Nonnull
    private WriteWorker submitAndScheduleWriteWorker(final long writePeriodInS,
                                                     final long delayInS) {
        final WriteWorker writeWorker = new WriteWorker(this,
                                                        "Periodic Archive Engine Writer",
                                                        _channelMap.values(),
                                                        writePeriodInS);
        _executor.scheduleAtFixedRate(writeWorker,
                                 delayInS,
                                 writeWorker.getPeriodInS(),
                                 TimeUnit.SECONDS);
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
            LOG.warn("Worker has already been submitted with period (s): " + _writeWorker.getPeriodInS());
            return;
        }
        long writePeriod = pWritePeriod;
        if (writePeriod < MIN_WRITE_PERIOD_S) {
            LOG.warn("Adjusting write period from "
                    + pWritePeriod + " to " + MIN_WRITE_PERIOD_S);
            writePeriod = MIN_WRITE_PERIOD_S;
        }
        _writePeriodInS = writePeriod;

        submitAndScheduleWriteWorker(_writePeriodInS,
                                     MIN_WRITE_PERIOD_S);

    }

    /** Reset statistics */
    public void reset() {
        _writeWorker._avgWriteCount.clear();
        _writeWorker._avgWriteDurationInMS.clear();
    }

    /** @return Timestamp of end of last write run */
    @Nonnull
    public ITimestamp getLastWriteTime() {
        return _writeWorker._lastTimeWrite;
    }

    /** @return Average number of values per write run */
    public double getAvgWriteCount() {
        return _writeWorker._avgWriteCount.getValue();
    }

    /** @return  Average duration of write run in seconds */
    public double getAvgWriteDuration() {
        return _writeWorker._avgWriteDurationInMS.getValue();
    }

    /** Stop the write thread, after performing a final write. */
    public void shutdown() throws Exception {
        _executor.execute(new WriteWorker(this, "Shutdown worker", _channelMap.values(), 0L)); // execute immediately
        _executor.shutdown();
    }
}
