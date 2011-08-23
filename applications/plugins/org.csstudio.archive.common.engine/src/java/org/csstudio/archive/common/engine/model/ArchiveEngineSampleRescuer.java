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

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.sample.ArchiveSampleProtos;
import org.csstudio.archive.common.service.sample.ArchiveSampleProtos.Samples;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport;
import org.csstudio.archive.common.service.util.DataRescueResult;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a data rescue functionality in case the archive services are unavailable.
 * Writes the samples serialized to the file system and notifies the staff of the action.
 *
 * @author bknerr
 * @since Mar 28, 2011
 */
final class ArchiveEngineSampleRescuer {

    private static final Logger EMAIL_LOG =
        LoggerFactory.getLogger("ErrorPerEmailLogger");
    /**
     * Rolling file appender for serialised data of samples.
     */
    private static final Logger RESCUE_LOG =
        LoggerFactory.getLogger("SerializedSamplesRescueLogger");

    private final Collection<IArchiveSample<Serializable, ISystemVariable<Serializable>>> _samplesToBeSerialized;

    /**
     * Constructor.
     */
    private ArchiveEngineSampleRescuer(@Nonnull final Collection<IArchiveSample<Serializable, ISystemVariable<Serializable>>> samples) {
        super();
        _samplesToBeSerialized = samples;
    }

    @Nonnull
    public static ArchiveEngineSampleRescuer with(@Nonnull final Collection<IArchiveSample<Serializable, ISystemVariable<Serializable>>> samples) {
        return new ArchiveEngineSampleRescuer(samples);
    }

    @Nonnull
    public DataRescueResult rescue() {

        final ArchiveSampleProtos.Samples.Builder gpbSamplesBuilder =
            ArchiveSampleProtos.Samples.newBuilder();

        Samples gpbSamples = null;
        try {
            gpbSamples = buildGPBSamples(gpbSamplesBuilder);
            RESCUE_LOG.info(gpbSamples.toString());
        } catch (final Throwable t) {
            EMAIL_LOG.info("Data rescue for samples failed. Samples lost: {}", _samplesToBeSerialized.size());
            return DataRescueResult.failure("rescue/samples/samples.ser*", TimeInstantBuilder.fromNow());
        }

        return DataRescueResult.success("rescue/samples/samples.ser*", TimeInstantBuilder.fromNow());
    }

    @Nonnull
    private Samples buildGPBSamples(@Nonnull final ArchiveSampleProtos.Samples.Builder gpbSamples) throws TypeSupportException {
        final ArchiveSampleProtos.ArchiveSample.Builder builder =
            ArchiveSampleProtos.ArchiveSample.newBuilder();

        for (final IArchiveSample<Serializable, ISystemVariable<Serializable>> sample : _samplesToBeSerialized) {
            builder.clear();

            final ISystemVariable<Serializable> sysVar = sample.getSystemVariable();
            final ArchiveSampleProtos.ArchiveSample gpbSample =
                builder.setChannelId(sysVar.getName())
                       .setControlSystemId(sysVar.getOrigin().getId())
                       .setNanosSinceEpoch(sysVar.getTimestamp().getNanos())
                       .setData(ArchiveTypeConversionSupport.toArchiveString(sysVar.getData()))
                       .build();
            gpbSamples.addSample(gpbSample);
        }
        return gpbSamples.build();
    }
}
