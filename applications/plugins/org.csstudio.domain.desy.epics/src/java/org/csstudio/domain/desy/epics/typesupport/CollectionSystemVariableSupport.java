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

import org.csstudio.data.values.IValue;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.TypeSupportException;

/**
 * @author bknerr
 * @since 22.12.2010
 */
@SuppressWarnings("rawtypes")
final class CollectionSystemVariableSupport extends EpicsSystemVariableSupport<Collection> {
    /**
     * Constructor.
     */
    public CollectionSystemVariableSupport() {
        super(Collection.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    protected IValue convertEpicsSystemVariableToIValue(@Nonnull final EpicsSystemVariable<Collection> sysVar) throws TypeSupportException {
        final Collection valueData = sysVar.getData().getValueData();
        if (valueData.isEmpty()) {
            throw new TypeSupportException("Collection of data is empty. Type cannot be determined.", null);
        }

        return collectionToIValue(valueData.iterator().next().getClass(),
                                  valueData,
                                  sysVar.getAlarm(),
                                  sysVar.getTimestamp());
    }

    @Override
    @Nonnull
    protected IValue convertCollectionToIValue(@Nonnull final Collection<Collection> data,
                                               @Nonnull final EpicsAlarm alarm,
                                               @Nonnull final TimeInstant timestamp) throws TypeSupportException {
        throw new TypeSupportException("This method should not be invoked on itself", null);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    protected EpicsSystemVariable<Collection> createEpicsVariable(@Nonnull final String name,
                                                                  @Nonnull final Collection values,
                                                                  @Nonnull final ControlSystem system,
                                                                  @Nonnull final TimeInstant timestamp) throws TypeSupportException {
        return (EpicsSystemVariable<Collection>) createEpicsVariableFromCollection(name,
                                                                                   values.iterator().next().getClass(),
                                                                                   values,
                                                                                   system,
                                                                                   timestamp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected EpicsSystemVariable<Collection<Collection>>
    createCollectionEpicsVariable(@Nonnull final String name,
                                  @Nonnull final Class<?> typeClass,
                                  @Nonnull final Collection<Collection> values,
                                  @Nonnull final ControlSystem system,
                                  @Nonnull final TimeInstant timestamp) throws TypeSupportException {
        throw new TypeSupportException("This method should not be invoked ", null);
    }

}