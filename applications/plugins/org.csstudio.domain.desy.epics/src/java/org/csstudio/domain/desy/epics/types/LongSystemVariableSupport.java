package org.csstudio.domain.desy.epics.types;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.BaseTypeConversionSupport;
import org.csstudio.domain.desy.types.TypeSupportException;
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
    protected IValue convertToIValue(@Nonnull final Long data,
                                     @Nonnull final EpicsAlarm alarm,
                                     @Nonnull final TimeInstant timestamp) {
        return ValueFactory.createLongValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                            EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                            alarm.getStatus().toString(),
                                            null,
                                            null,
                                            new long[] {data.longValue()});
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
                                                 sysVar.getAlarm(),
                                                 sysVar.getData().getValueData(),
                                                 min,
                                                 max);
    }
}