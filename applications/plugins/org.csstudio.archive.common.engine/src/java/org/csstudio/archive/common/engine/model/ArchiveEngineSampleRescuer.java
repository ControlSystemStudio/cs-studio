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

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.util.AbstractToFileDataRescuer;
import org.csstudio.archive.common.service.util.DataRescueException;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;

/**
 * Implements a data rescue functionality in case the archive services are unavailable.
 * Writes the samples serialized to the file system and notifies the staff of the action.
 *
 * @author bknerr
 * @since Mar 28, 2011
 */
class ArchiveEngineSampleRescuer extends AbstractToFileDataRescuer {

    private final List<IArchiveSample<Object, ISystemVariable<Object>>> _samples;

    private TimeInstant _timeStamp;

    @Nonnull
    public static ArchiveEngineSampleRescuer with(@Nonnull final List<IArchiveSample<Object, ISystemVariable<Object>>> samples) {
        return new ArchiveEngineSampleRescuer(samples);
    }

    /**
     * Constructor.
     */
    ArchiveEngineSampleRescuer(@Nonnull final List<IArchiveSample<Object, ISystemVariable<Object>>> samples) {
        super();
        _samples = samples;
        _timeStamp = TimeInstantBuilder.fromNow();
    }

    @Nonnull
    public ArchiveEngineSampleRescuer at(@Nonnull final TimeInstant time) {
        _timeStamp = time;
        return this;
    }

    @Override
    protected void writeToFile(@Nonnull final ObjectOutput output) throws IOException {
        output.writeObject(_samples);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected String composeRescueFileName() {
        return "rescue_" + _timeStamp.formatted(TimeInstant.STD_TIME_FMT_FOR_FS) + "_S" + _samples.size()+ ".ser";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleException(@Nonnull final IOException e) throws DataRescueException {
        throw new DataRescueException("Mmh", e);
    }
}
