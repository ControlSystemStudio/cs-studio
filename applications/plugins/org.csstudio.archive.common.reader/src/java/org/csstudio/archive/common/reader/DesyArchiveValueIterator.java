/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.data.values.IValue;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Raw value iterator for service infrastructure.
 *
 * @author bknerr
 * @since 21.12.2010
 * @param <V> the base type of this channel
 */
public class DesyArchiveValueIterator<V extends Serializable> extends AbstractValueIterator<V> {

    @SuppressWarnings("unused")
    private static final Logger LOG =
        LoggerFactory.getLogger(DesyArchiveValueIterator.class);

    /**
     * Constructor.
     */
    DesyArchiveValueIterator(@Nonnull final Collection<IArchiveSample<V, ISystemVariable<V>>> samples,
                             @Nonnull final String channelName,
                             @Nonnull final TimeInstant start,
                             @Nonnull final TimeInstant end) {
        super(samples, channelName, start, end);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return getIterator().hasNext();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public IValue next() throws Exception {
        final IValue value = ARCH_SAMPLE_2_IVALUE_FUNC.apply(getIterator().next());
        if (value == null) {
            throw new TypeSupportException("Sample could not be converted to " + IValue.class.getName() + " type.", null);
        }
        return value;
    }
}
