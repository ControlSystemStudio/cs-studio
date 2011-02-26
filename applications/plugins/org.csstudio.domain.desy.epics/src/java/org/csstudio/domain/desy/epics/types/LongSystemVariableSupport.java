package org.csstudio.domain.desy.epics.types;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.CssValueType;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;

import com.google.common.primitives.Longs;

/**
 * @author bknerr
 * @since 22.12.2010
 */
final class LongSystemVariableSupport extends EpicsSystemVariableSupport<Long> {
    /**
     * Constructor.
     */
    public LongSystemVariableSupport() {
        super(Long.class);
    }

    @Override
    @Nonnull
    protected IValue convertEpicsSystemVariableToIValue(@Nonnull final EpicsSystemVariable<Long> sysVar) {
        return ValueFactory.createLongValue(BaseTypeConversionSupport.toTimestamp(sysVar.getTimestamp()),
                                            EpicsIValueTypeSupport.toSeverity(sysVar.getAlarm().getSeverity()),
                                            sysVar.getAlarm().getStatus().toString(),
                                            null,
                                            null,
                                            new long[] {sysVar.getData().getValueData().longValue()});
    }

    @Override
    @Nonnull
    protected IValue convertCollectionToIValue(@Nonnull final Collection<Long> data,
                                               @Nonnull final EpicsAlarm alarm,
                                               @Nonnull final TimeInstant timestamp) {
        return ValueFactory.createLongValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                            EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                            alarm.getStatus().toString(),
                                            null,
                                            null,
                                            Longs.toArray(data));
    }

    @Override
    @Nonnull
    protected IValue convertToIMinMaxDoubleValue(@Nonnull final IAlarmSystemVariable<Long> sysVar,
                                                 @Nonnull final Long min,
                                                 @Nonnull final Long max) throws TypeSupportException {
        return createMinMaxDoubleValueFromNumber(sysVar.getTimestamp(),
                                                 (EpicsAlarm) sysVar.getAlarm(),
                                                 sysVar.getData().getValueData(),
                                                 min,
                                                 max);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected EpicsSystemVariable<Long> createEpicsVariable(@Nonnull final String name,
                                                              @Nonnull final Long value,
                                                              @Nonnull final ControlSystem system,
                                                              @Nonnull final TimeInstant timestamp) {
        return new EpicsSystemVariable<Long>(name, new CssValueType<Long>(value), system, timestamp, EpicsAlarm.UNKNOWN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected EpicsSystemVariable<Collection<Long>> createCollectionEpicsVariable(final String name,
                                                                                  final Class<?> typeClass,
                                                                                  final Collection<Long> values,
                                                                                  final ControlSystem system,
                                                                                  final TimeInstant timestamp) throws TypeSupportException {
        // TODO Auto-generated method stub
        return null;
    }
}