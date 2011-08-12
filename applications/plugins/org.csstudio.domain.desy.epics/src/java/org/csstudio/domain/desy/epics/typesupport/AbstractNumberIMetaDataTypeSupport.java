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

/**
 * Conversion support for {@link Number} & {@link Comparable} types.
 *
 * @author bknerr
 * @since 10.08.2011
 * @param <T> the comparable number type
 */
public abstract class AbstractNumberIMetaDataTypeSupport<T extends Number & Comparable<T>> extends EpicsIMetaDataTypeSupport<T> {

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
        final INumericMetaData numData = checkAndConvertToNumeric(data, getType());
        final EpicsGraphicsData<T> gr =
            new EpicsGraphicsData<T>(Limits.<T>create(toNumber(numData.getAlarmLow()),
                                                      toNumber(numData.getAlarmHigh())),
                                     Limits.<T>create(toNumber(numData.getWarnLow()),
                                                      toNumber(numData.getWarnHigh())),
                                     Limits.<T>create(toNumber(numData.getDisplayLow()),
                                                      toNumber(numData.getDisplayHigh())));
        return EpicsMetaData.create(null, gr, null, null);
    }

    @Nonnull
    protected abstract T toNumber(final double d);
}
