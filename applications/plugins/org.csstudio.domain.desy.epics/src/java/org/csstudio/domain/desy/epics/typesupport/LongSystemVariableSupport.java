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
import org.csstudio.data.values.ValueFactory;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;

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
                                            new long[] {sysVar.getData().longValue()});
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
                                                 sysVar.getData(),
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
        return new EpicsSystemVariable<Long>(name, value, system, timestamp, EpicsAlarm.UNKNOWN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected EpicsSystemVariable<Collection<Long>> createCollectionEpicsVariable(@Nonnull final String name,
                                                                                  @Nonnull final Class<?> typeClass,
                                                                                  @Nonnull final Collection<Long> values,
                                                                                  @Nonnull final ControlSystem system,
                                                                                  @Nonnull final TimeInstant timestamp) throws TypeSupportException {
        try {
            @SuppressWarnings("unchecked")
            final Collection<Long> newCollection = (Collection<Long>) typeClass.newInstance();
            for (final Long v : values) {
                newCollection.add(v);
            }
            return new EpicsSystemVariable<Collection<Long>>(name, newCollection, system, timestamp, EpicsAlarm.UNKNOWN);
        } catch (final InstantiationException e) {
            throw new TypeSupportException("Collection type could not be instantiated from Class<?> object.", e);
        } catch (final IllegalAccessException e) {
            throw new TypeSupportException("Collection type could not be instantiated from Class<?> object.", e);
        }
    }
}