/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.archive.common.engine.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.engine.ArchiveEnginePreference;
import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.calc.CumulativeAverageCache;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.time.StopWatch;
import org.csstudio.domain.desy.time.StopWatch.RunningStopWatch;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;

import com.google.common.collect.Lists;

/**
 * The independent worker to write/submit the samples to the persistence layer.
 *
 * @author bknerr
 * @since 14.02.2011
 */
final class WriteWorker implements Runnable {

    private static final Logger WORKER_LOG =
            CentralLogger.getInstance().getLogger(WriteWorker.class);

    private final String _name;
    private final Collection<ArchiveChannel<Object, IAlarmSystemVariable<Object>>> _channels;

    private final long _periodInMS;
    /** Average number of values per write run */
    private final CumulativeAverageCache _avgWriteCount = new CumulativeAverageCache();
    /** Average duration of write run */
    private final CumulativeAverageCache _avgWriteDurationInMS = new CumulativeAverageCache();


    private final IServiceProvider _provider;

    private final ArchiveEngineId _engineId;

    private TimeInstant _lastTimeWrite;
    /**
     * Constructor.
     */
    public WriteWorker(@Nonnull final ArchiveEngineId engineId,
                       @Nonnull final IServiceProvider provider,
                       @Nonnull final String name,
                       @Nonnull final Collection<ArchiveChannel<Object, IAlarmSystemVariable<Object>>> channels,
                       final long periodInMS) {
        _engineId = engineId;
        _provider = provider;
        _name = name;
        _channels = channels;
        _periodInMS = periodInMS;

        WORKER_LOG.info(_name + " created with period " + periodInMS + "ms");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        WORKER_LOG.info("RUN: " + TimeInstantBuilder.buildFromNow().formatted());

        try {
            final RunningStopWatch watch = StopWatch.start();

            final long written = collectAndWriteSamples(_provider, _channels);
            _lastTimeWrite = TimeInstantBuilder.buildFromNow();
            updateEngineHeartBeat(_provider, _engineId, _lastTimeWrite);

            final long durationInMS = watch.getElapsedTimeInMillis();
            if (durationInMS >= _periodInMS) {
                // FIXME (bknerr) : this won't work, stupid
                //_writeExec.enhanceWriterThroughput(this);
            }

            _avgWriteCount.accumulate(Double.valueOf(written));
            _avgWriteDurationInMS.accumulate(Double.valueOf(durationInMS));
        } catch (final OsgiServiceUnavailableException e) {
            WORKER_LOG.error("Archive service unavailable - rescue data.", e);
            rescueData();
        } catch (final ArchiveServiceException e) {
            WORKER_LOG.error("Exception within service impl. Data rescue should be handled there.", e);
        }
//        catch (final InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
    }

    private void updateEngineHeartBeat(@Nonnull final IServiceProvider provider,
                                       @Nonnull final ArchiveEngineId engineId,
                                       @Nonnull final TimeInstant lastTimeWrite)
                                       throws ArchiveServiceException,
                                       OsgiServiceUnavailableException {
        final IArchiveEngineFacade engineFacade = provider.getEngineFacade();
        engineFacade.updateEngineIsAlive(engineId, lastTimeWrite);

    }

    private void rescueData() {
        final URL rescueDir = ArchiveEnginePreference.DATA_RESCUE_DIR.getValue();
        final LinkedList<IArchiveSample<Object, IAlarmSystemVariable<Object>>> samples =
                collectSamplesFromBuffers(_channels);

        final String fileName = "rescue_" + TimeInstantBuilder.buildFromNow() + "_S" + samples.size()+ ".ser";
        final File file = new File(rescueDir.getFile(), fileName);

        ObjectOutput output = null;
        try {
            final OutputStream ostream = new FileOutputStream(file);
            final OutputStream buffer = new BufferedOutputStream(ostream);
            output = new ObjectOutputStream(buffer);
            output.writeObject(samples);
        } catch (final IOException e) {
            WORKER_LOG.error("Rescue of data failed.", e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (final IOException e) {
                    WORKER_LOG.warn("Closing of output stream for data rescue file failed.", e);
                }
            }
        }
        // READ IN FROM FILE
//        ObjectInputStream objectIn = null;
//        int objectCount = 0;
//        Junk object = null;
//
//        objectIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(
//            "C:/JunkObjects.bin")));
//
//        // Read from the stream until we hit the end
//        while (objectCount < 3) {
//          object = (Junk) objectIn.readObject();
//          objectCount++;
//          System.out.println(object);
//        }
//
//        objectIn.close();


    }

    /**
     * Drains all data from the buffers and writes them to the persistence service.
     * @param provider the provider for the service to write to
     * @param channels the channels of which the buffers should be drained to the persistence layer
     *  @return number of samples drained/written to the service
     * @throws OsgiServiceUnavailableException
     * @throws ArchiveServiceException
     */
    private long collectAndWriteSamples(@Nonnull final IServiceProvider provider,
                              @Nonnull final Collection<ArchiveChannel<Object, IAlarmSystemVariable<Object>>> channels)
                              throws OsgiServiceUnavailableException, ArchiveServiceException {

        final LinkedList<IArchiveSample<Object, IAlarmSystemVariable<Object>>> allSamples =
                collectSamplesFromBuffers(channels);

        final IArchiveEngineFacade service = provider.getEngineFacade();
        service.writeSamples(allSamples);

        return allSamples.size();
    }

    @Nonnull
    private LinkedList<IArchiveSample<Object, IAlarmSystemVariable<Object>>>
    collectSamplesFromBuffers(@Nonnull final Collection<ArchiveChannel<Object, IAlarmSystemVariable<Object>>> channels) {

        final LinkedList<IArchiveSample<Object, IAlarmSystemVariable<Object>>> allSamples = Lists.newLinkedList();

        for (final ArchiveChannel<Object, IAlarmSystemVariable<Object>> channel : channels) {
            final SampleBuffer<Object,
            IAlarmSystemVariable<Object>,
                               IArchiveSample<Object, IAlarmSystemVariable<Object>>> buffer = channel.getSampleBuffer();

            buffer.updateStats();
            buffer.drainTo(allSamples);
        }
        return allSamples;
    }

    public long getPeriodInMS() {
        return _periodInMS;
    }

    @Nonnull
    protected CumulativeAverageCache getAvgWriteCount() {
        return _avgWriteCount;
    }

    @Nonnull
    protected CumulativeAverageCache getAvgWriteDurationInMS() {
        return _avgWriteDurationInMS;
    }

    @CheckForNull
    protected TimeInstant getLastTimeWrite() {
        return _lastTimeWrite;
    }

    public void clear() {
        _avgWriteCount.clear();
        _avgWriteDurationInMS.clear();
        _lastTimeWrite = null;
    }
}
