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
package org.csstudio.domain.desy.epics.typesupport;

import javax.annotation.Nonnull;

import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.domain.desy.epics.types.EpicsGraphicsData;
import org.csstudio.domain.desy.epics.types.EpicsMetaData;
import org.csstudio.domain.desy.types.Limits;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Conversion support for {@link Number} & {@link Comparable} types.
 *
 * @author bknerr
 * @since 10.08.2011
 * @param <T> the comparable number type
 */
public abstract class AbstractNumberIMetaDataTypeSupport<T extends Number & Comparable<T>> extends EpicsIMetaDataTypeSupport<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractNumberIMetaDataTypeSupport.class);
    /**
     * Constructor.
     */
    public AbstractNumberIMetaDataTypeSupport(@Nonnull final Class<T> type) {
        super(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected EpicsMetaData convertToMetaData(@Nonnull final IMetaData data) throws TypeSupportException {
        final INumericMetaData numMeta = checkAndConvertToNumeric(data, getType());
        final Limits<T> alarmLims = createLimits(toNumber(numMeta.getAlarmHigh()),
                                                 toNumber(numMeta.getAlarmLow()));
        final Limits<T> warnLims = createLimits(toNumber(numMeta.getWarnHigh()),
                                                toNumber(numMeta.getWarnLow()));
        final Limits<T> oprLims = createLimits(toNumber(numMeta.getDisplayHigh()),
                                               toNumber(numMeta.getDisplayLow()));
        final EpicsGraphicsData<T> gr =
            new EpicsGraphicsData<T>(alarmLims, warnLims, oprLims);
        return EpicsMetaData.create(null, gr, null, null);

    }

    @Nonnull
    protected abstract T toNumber(final double d);


    @Nonnull
    private Limits<T> createLimits(@Nonnull final T high,
                                   @Nonnull final T low) {
        try {
            return Limits.<T>create(low, high);
        } catch (final IllegalArgumentException e) {
            LOG.warn("Limits info invalid in meta data. Set limits to zero.", e);
        }
        return Limits.<T>create(toNumber(0.0), toNumber(0.0));
    }
}
