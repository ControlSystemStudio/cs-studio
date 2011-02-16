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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.primitives.Longs;

/**
 * @author bknerr
 * @since 22.12.2010
 */
final class ByteSystemVariableSupport extends EpicsSystemVariableSupport<Byte> {
    /**
     * Constructor.
     */
    public ByteSystemVariableSupport() {
        super(Byte.class);
    }

    @Override
    @Nonnull
    protected IValue convertToIValue(@Nonnull final Byte data,
                                     @Nonnull final EpicsAlarm alarm,
                                     @Nonnull final TimeInstant timestamp) {
        return ValueFactory.createLongValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                            EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                            alarm.getStatus().toString(),
                                            null,
                                            null,
                                            new long[]{ data.longValue() });
    }

    @Override
    @Nonnull
    protected IValue convertCollectionToIValue(@Nonnull final Collection<Byte> data,
                                               @Nonnull final EpicsAlarm alarm,
                                               @Nonnull final TimeInstant timestamp) throws TypeSupportException {
        final Collection<Long> longs =
            Collections2.transform(data,
                                   new Function<Byte, Long> () {
                @Override
                public Long apply(@Nonnull final Byte from) {
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
    protected IValue convertToIMinMaxDoubleValue(@Nonnull final IAlarmSystemVariable<Byte> sysVar,
                                                 @Nonnull final Byte min,
                                                 @Nonnull final Byte max) throws TypeSupportException {
        return createMinMaxDoubleValueFromNumber(sysVar.getTimestamp(), sysVar.getAlarm(), sysVar.getData().getValueData(), min, max);
    }
}