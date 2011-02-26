package org.csstudio.domain.desy.epics.types;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.csstudio.platform.data.IValue;

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