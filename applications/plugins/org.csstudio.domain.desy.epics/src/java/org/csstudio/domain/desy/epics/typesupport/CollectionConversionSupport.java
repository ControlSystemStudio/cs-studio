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

import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.epics.types.EpicsGraphicsData;
import org.csstudio.domain.desy.epics.types.EpicsMetaData;
import org.csstudio.domain.desy.types.Limits;
import org.csstudio.domain.desy.typesupport.TypeSupportException;

/**
 * Conversion support for {@link Collection}-based types.
 *
 * @author bknerr
 * @since 25.07.2011
 */
@SuppressWarnings("rawtypes")
public class CollectionConversionSupport extends EpicsIMetaDataTypeSupport<Collection> {

    /**
     * Constructor.
     */
    public CollectionConversionSupport() {
        super(Collection.class);
    }

    /**
     * {@inheritDoc}
     *
     * For collection based types, we treat the meta meta data as of numeric {@link Double} type on
     * default. That's EPICS style anyway...
     * If it doesn't work we'll try the enumerated one as well, eventually throwing a
     * {@link TypeSupportException} if it doesn't work out.
     */
    @Override
    @Nonnull
    protected EpicsMetaData convertToMetaData(@Nonnull final IMetaData meta) throws TypeSupportException {


        try {
            final INumericMetaData numMeta = checkAndConvertToNumeric(meta, Double.class);
            final EpicsGraphicsData<Double> gr =
                new EpicsGraphicsData<Double>(Limits.<Double>create(numMeta.getAlarmLow(),
                                                                    numMeta.getAlarmHigh()),
                                            Limits.<Double>create(numMeta.getWarnLow(),
                                                                  numMeta.getWarnHigh()),
                                            Limits.<Double>create(numMeta.getDisplayLow(),
                                                                  numMeta.getDisplayHigh()));
            return EpicsMetaData.create(null, gr, null, null);
        // CHECKSTYLE OFF: EmptyBlock
        } catch (final TypeSupportException e) {
            // Ignore
        }
        // CHECKSTYLE ON: EmptyBlock

        final IEnumeratedMetaData enumData = checkAndConvertToEnumerated(meta, EpicsEnum.class);
        final String[] states = enumData.getStates();
        return EpicsMetaData.create(states);
    }
}
