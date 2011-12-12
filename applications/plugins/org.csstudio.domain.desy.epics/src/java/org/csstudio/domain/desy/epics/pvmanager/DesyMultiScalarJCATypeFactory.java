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
package org.csstudio.domain.desy.epics.pvmanager;

import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.STS;
import gov.aps.jca.dbr.TIME;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

/**
 * Dedicated type factory to create {@link org.csstudio.domain.desy.epics.types.EpicsSystemVariable}s from DBR types.
 *
 * @author bknerr
 * @since 08.12.2011
 * @param <V> The desired value into which to convert the scalars or multiscalar elements.
 * @param <EV> the epics value with time information
 * @param <EM> the epics value with sev and status information
 */
public class DesyMultiScalarJCATypeFactory<V,
                                           EV extends DBR & TIME,
                                           EM extends DBR & STS>
    extends AbstractDesyJCATypeFactory<V, EV, EM> {
    /**
     * Constructor.
     */
    public DesyMultiScalarJCATypeFactory(@Nonnull final Class<V> valueType,
                                         @Nonnull final DBRType epicsValueType,
                                         @Nonnull final DBRType epicsMetaType,
                                         @Nonnull final DBRType channelFieldType) {
        super(valueType, epicsValueType, epicsMetaType, channelFieldType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isArray() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @Nonnull
    public V toData(@Nonnull final DBR eVal, @Nonnull final EM eMeta) {
        final int nelm = eVal.getCount();
        final ArrayList array = Lists.newArrayListWithCapacity(nelm);

        final DesyScalarJCATypeFactory scalarFac = getScalarFactory(getChannelFieldType());

        for (int i = 0; i < nelm; i++) {
            array.add(scalarFac.toScalarData(eVal, eMeta, i));
        }
        return (V) array;
    }

    @SuppressWarnings("rawtypes")
    @Nonnull
    private DesyScalarJCATypeFactory getScalarFactory(@Nonnull final DBRType dbrType) {
        final Map<DBRType, DesyScalarJCATypeFactory> scalarMap = DesyTypeFactoryProvider.getScalarMap();
        final DesyScalarJCATypeFactory scalarFac = scalarMap.get(dbrType);
        if (scalarFac == null) {
            throw new IllegalStateException("Scalar factory could not be found for multi scalar factory and key: " + dbrType.toString());
        }
        return scalarFac;
    }
}
