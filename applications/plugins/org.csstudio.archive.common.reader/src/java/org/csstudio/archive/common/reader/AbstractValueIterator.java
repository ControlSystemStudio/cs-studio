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
package org.csstudio.archive.common.reader;

import java.util.Iterator;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.IValue;
import org.csstudio.domain.desy.time.TimeInstant;

/**
 * Abstract super class for value iterators talking to the DESY archive service interface.
 *
 * @author bknerr
 * @since 22.06.2011
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractValueIterator implements ValueIterator {

    protected static final ArchiveSampleToIValueFunction ARCH_SAMPLE_2_IVALUE_FUNC =
        new ArchiveSampleToIValueFunction();

    private final String _channelName;
    private final TimeInstant _start;
    private final TimeInstant _end;


    private final Iterable<IArchiveSample> _samples;
    private final Iterator<IArchiveSample> _samplesIter;


    /**
     * Constructor.
     */
    protected AbstractValueIterator(@Nonnull final Iterable<IArchiveSample> samples,
                                    @Nonnull final String channelName,
                                    @Nonnull final TimeInstant start,
                                    @Nonnull final TimeInstant end) {
        _channelName = channelName;
        _start = start;
        _end = end;

        _samples = samples;
        _samplesIter = _samples.iterator();
    }

    @Nonnull
    protected TimeInstant getStart() {
        return _start;
    }

    @Nonnull
    protected TimeInstant getEnd() {
        return _end;
    }

    @Nonnull
    protected String getChannelName() {
        return _channelName;
    }

    @Nonnull
    protected Iterator<IArchiveSample> getIterator() {
        return _samplesIter;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean hasNext();

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public abstract IValue next() throws Exception;

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // A stub, not used here
    }
}
