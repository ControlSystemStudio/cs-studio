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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.engine.ArchiveEnginePreference;
import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.util.DataRescueException;
import org.csstudio.domain.desy.calc.CumulativeAverageCache;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.task.AbstractTimeMeasuredRunnable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * The independent worker to write/submit the samples to the persistence layer.
 *
 * @author bknerr
 * @since 14.02.2011
 */
final class WriteWorker extends AbstractTimeMeasuredRunnable {

    private static final Logger WORKER_LOG = LoggerFactory.getLogger(WriteWorker.class);

    private final String _name;
    private final Collection<ArchiveChannel<Object, ISystemVariable<Object>>> _channels;

    private final long _periodInMS;
    /** Average number of values per write run */
    private final CumulativeAverageCache _avgWriteCount = new CumulativeAverageCache();

    private final IServiceProvider _provider;

    private TimeInstant _lastWriteTime;

    /**
     * Constructor.
     */
    public WriteWorker(@Nonnull final IServiceProvider provider,
                       @Nonnull final String name,
                       @Nonnull final Collection<ArchiveChannel<Object, ISystemVariable<Object>>> channels,
                       final long periodInMS) {
        _provider = provider;
        _name = name;
        _channels = channels;
        _periodInMS = periodInMS;

        WORKER_LOG.info("{} created with period {}ms", _name, periodInMS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void measuredRun() {
        try {
            WORKER_LOG.info("WRITE TO SERVICE: {} at {}", _name, TimeInstantBuilder.fromNow().formatted());

            List<IArchiveSample<Object, ISystemVariable<Object>>> samples = Collections.emptyList();

            samples = collectSamplesFromBuffers(_channels);

            final long written = writeSamples(_provider,  samples);

            _lastWriteTime = TimeInstantBuilder.fromNow();
            _avgWriteCount.accumulate(Double.valueOf(written));

        } catch (final ArchiveServiceException e) {
            WORKER_LOG.error("Exception within service impl. Data rescue should be handled there.", e);
        } catch (final Throwable t) {
            WORKER_LOG.error("Unknown throwable. Thread {} is terminated", _name);
            t.printStackTrace();
        }
    }

    private long writeSamples(@Nonnull final IServiceProvider provider,
                              @Nonnull final List<IArchiveSample<Object, ISystemVariable<Object>>> samples)
                              throws ArchiveServiceException {

        try {
            final IArchiveEngineFacade service = provider.getEngineFacade();
            service.writeSamples(samples);
        } catch (final OsgiServiceUnavailableException e) {
            try {
                ArchiveEngineSampleRescuer.with(samples).to(ArchiveEnginePreference.DATA_RESCUE_DIR.getValue()).rescue();
            } catch (final DataRescueException e1) {
                WORKER_LOG.error("Data rescue to file system failed!: {}", e1.getMessage());
                throw new ArchiveServiceException("Data rescue failed.", e1);
            }
        }

        return samples.size();
    }

    @Nonnull
    private LinkedList<IArchiveSample<Object, ISystemVariable<Object>>>
    collectSamplesFromBuffers(@Nonnull final Collection<ArchiveChannel<Object, ISystemVariable<Object>>> channels) {

        final LinkedList<IArchiveSample<Object, ISystemVariable<Object>>> allSamples = Lists.newLinkedList();

        for (final ArchiveChannel<Object, ISystemVariable<Object>> channel : channels) {
            final SampleBuffer<Object, ISystemVariable<Object>, IArchiveSample<Object, ISystemVariable<Object>>> buffer =
                channel.getSampleBuffer();

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

    @CheckForNull
    protected TimeInstant getLastWriteTime() {
        return _lastWriteTime;
    }

    @Override
    public void clear() {
        super.clear();
        _avgWriteCount.clear();
        _lastWriteTime = null;
    }
}
