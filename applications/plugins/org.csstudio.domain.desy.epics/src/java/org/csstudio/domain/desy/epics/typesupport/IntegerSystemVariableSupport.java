package org.csstudio.domain.desy.epics.typesupport;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.primitives.Longs;

/**
 * @author bknerr
 * @since 22.12.2010
 */
final class IntegerSystemVariableSupport extends EpicsSystemVariableSupport<Integer> {
    /**
     * Constructor.
     */
    public IntegerSystemVariableSupport() {
        super(Integer.class);
    }

    @Override
    @Nonnull
    protected IValue convertEpicsSystemVariableToIValue(@Nonnull final EpicsSystemVariable<Integer> sysVar) {
        return ValueFactory.createLongValue(BaseTypeConversionSupport.toTimestamp(sysVar.getTimestamp()),
                                            EpicsIValueTypeSupport.toSeverity(sysVar.getAlarm().getSeverity()),
                                            sysVar.getAlarm().getStatus().toString(),
                                            null,
                                            null,
                                            new long[] {sysVar.getData().longValue()});
    }

    @Override
    @Nonnull
    protected IValue convertCollectionToIValue(@Nonnull final Collection<Integer> data,
                                               @Nonnull final EpicsAlarm alarm,
                                               @Nonnull final TimeInstant timestamp) {
        final Collection<Long> longs =
            Collections2.transform(data,
                                   new Function<Integer, Long> () {
                                       @Override
                                       public Long apply(@Nonnull final Integer from) {
                                           return Long.valueOf(from);
                                       }
                                   });
        return ValueFactory.createLongValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                            EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                            alarm.getStatus().toString(),
                                            null,
                                            null,
                                            Longs.toArray(longs));
    }

    @Override
    @Nonnull
    protected IValue convertToIMinMaxDoubleValue(@Nonnull final IAlarmSystemVariable<Integer> sysVar,
                                                 @Nonnull final Integer min,
                                                 @Nonnull final Integer max) throws TypeSupportException {
        return createMinMaxDoubleValueFromNumber(sysVar.getTimestamp(),
                                                 (EpicsAlarm) sysVar.getAlarm(),
                                                 sysVar.getData(),
                                                 min,
                                                 max);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected EpicsSystemVariable<Integer> createEpicsVariable(@Nonnull final String name,
                                                               @Nonnull final Integer value,
                                                               @Nonnull final ControlSystem system,
                                                               @Nonnull final TimeInstant timestamp) {
        return new EpicsSystemVariable<Integer>(name, value, system, timestamp, EpicsAlarm.UNKNOWN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected EpicsSystemVariable<Collection<Integer>> createCollectionEpicsVariable(@Nonnull final String name,
                                                                                     @Nonnull final Class<?> typeClass,
                                                                                     @Nonnull final Collection<Integer> values,
                                                                                     @Nonnull final ControlSystem system,
                                                                                     @Nonnull final TimeInstant timestamp) throws TypeSupportException {
        try {
            @SuppressWarnings("unchecked")
            final Collection<Integer> newCollection = (Collection<Integer>) typeClass.newInstance();
            for (final Integer v : values) {
                newCollection.add(v);
            }
            return new EpicsSystemVariable<Collection<Integer>>(name, newCollection, system, timestamp, EpicsAlarm.UNKNOWN);
        } catch (final InstantiationException e) {
            throw new TypeSupportException("Collection type could not be instantiated from Class<?> object.", e);
        } catch (final IllegalAccessException e) {
            throw new TypeSupportException("Collection type could not be instantiated from Class<?> object.", e);
        }
    }
}