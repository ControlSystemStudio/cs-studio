package org.csstudio.domain.desy.epics.types;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.BaseTypeConversionSupport;
import org.csstudio.domain.desy.types.CssValueType;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;

import com.google.common.primitives.Doubles;

/**
 * @author bknerr
 * @since 22.12.2010
 */
final class DoubleSystemVariableSupport extends EpicsSystemVariableSupport<Double> {
    /**
     * Constructor.
     */
    public DoubleSystemVariableSupport() {
        super(Double.class);
    }

    @Override
    @Nonnull
    protected IValue convertEpicsSystemVariableToIValue(@Nonnull final EpicsSystemVariable<Double> sysVar) {
        return ValueFactory.createDoubleValue(BaseTypeConversionSupport.toTimestamp(sysVar.getTimestamp()),
                                              EpicsIValueTypeSupport.toSeverity(sysVar.getAlarm().getSeverity()),
                                              sysVar.getAlarm().getStatus().toString(),
                                              null,
                                              null,
                                              new double[] {sysVar.getData().getValueData().doubleValue()});
    }

    @Override
    @Nonnull
    protected IValue convertCollectionToIValue(@Nonnull final Collection<Double> data,
                                               @Nonnull final EpicsAlarm alarm,
                                               @Nonnull final TimeInstant timestamp) {
        return ValueFactory.createDoubleValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                              EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                              alarm.getStatus().toString(),
                                              null,
                                              null,
                                              Doubles.toArray(data));
    }

    @Override
    @Nonnull
    protected IValue convertToIMinMaxDoubleValue(@Nonnull final IAlarmSystemVariable<Double> sysVar,
                                                 @Nonnull final Double min,
                                                 @Nonnull final Double max) throws TypeSupportException {
        return createMinMaxDoubleValueFromNumber(sysVar.getTimestamp(),
                                                 (EpicsAlarm)sysVar.getAlarm(),
                                                 sysVar.getData().getValueData(),
                                                 min,
                                                 max);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected EpicsSystemVariable<Double> createEpicsVariable(@Nonnull final String name,
                                                              @Nonnull final Double value,
                                                              @Nonnull final ControlSystem system,
                                                              @Nonnull final TimeInstant timestamp) {
        return new  EpicsSystemVariable<Double>(name, new CssValueType<Double>(value), system, timestamp, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected EpicsSystemVariable<Collection<Double>> createCollectionEpicsVariable(final String name,
                                                                            final Class<?> typeClass,
                                                                            final Collection<Double> values,
                                                                            final ControlSystem system,
                                                                            final TimeInstant timestamp) throws TypeSupportException {
        // TODO Auto-generated method stub
        return null;
    }
}