package org.csstudio.domain.desy.epics.types;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.TypeSupportException;
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
    protected IValue convertToIValue(@Nonnull final Collection data,
                                     @Nonnull final EpicsAlarm alarm,
                                     @Nonnull final TimeInstant timestamp) throws TypeSupportException {
        if (data.isEmpty()) {
            throw new TypeSupportException("Collection of data is empty. Type cannot be determined.", null);
        }

        return toIValue(data.iterator().next().getClass(),
                        data,
                        alarm,
                        timestamp);
    }

    @Override
    @Nonnull
    protected IValue convertCollectionToIValue(@Nonnull final Collection<Collection> data,
                                               @Nonnull final EpicsAlarm alarm,
                                               @Nonnull final TimeInstant timestamp) throws TypeSupportException {
        throw new TypeSupportException("This method should not be invoked on itself", null);
    }
}