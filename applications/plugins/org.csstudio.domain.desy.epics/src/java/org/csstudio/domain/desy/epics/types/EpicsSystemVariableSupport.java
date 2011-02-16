/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.desy.epics.types;

import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.alarm.IAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.BaseTypeConversionSupport;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;
import org.epics.pvmanager.TypeSupport;


/**
 * And more conversion support, now from CssValues to IValues.
 *
 * @author bknerr
 * @since 22.12.2010
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class statically is accessed, hence the name should be short and descriptive!
 */
public abstract class EpicsSystemVariableSupport<T> extends TypeSupport<T> {
// CHECKSTYLE ON : AbstractClassName

    private static boolean INSTALLED = false;

    /**
     * Constructor.
     */
    protected EpicsSystemVariableSupport(@Nonnull final Class<T> type) {
        super(type, EpicsSystemVariableSupport.class);
    }

    public static void install() {
        if (INSTALLED) {
            return;
        }
        TypeSupport.addTypeSupport(new DoubleSystemVariableSupport());
        TypeSupport.addTypeSupport(new FloatSystemVariableSupport());
        TypeSupport.addTypeSupport(new LongSystemVariableSupport());
        TypeSupport.addTypeSupport(new IntegerSystemVariableSupport());
        TypeSupport.addTypeSupport(new StringSystemVariableSupport());
        TypeSupport.addTypeSupport(new ByteSystemVariableSupport());

        TypeSupport.addTypeSupport(new EpicsEnumSystemVariableSupport());

        TypeSupport.addTypeSupport(new CollectionSystemVariableSupport());

        INSTALLED = true;
    }

    @CheckForNull
    public static <T> IValue toIValue(@Nonnull final IAlarmSystemVariable<T> sysVar) throws TypeSupportException {
        final T valueData = sysVar.getData().getValueData();
        @SuppressWarnings("unchecked")
        final Class<T> typeClass = (Class<T>) valueData.getClass();
        final EpicsSystemVariableSupport<T> support =
            (EpicsSystemVariableSupport<T>) findTypeSupportFor(EpicsSystemVariableSupport.class, typeClass);
        // TODO (bknerr) : This is definitely an epics alarm, choose an appropriate abstraction
        return support.convertToIValue(valueData, (EpicsAlarm) sysVar.getAlarm(), sysVar.getTimestamp());
    }

    @CheckForNull
    public static <T>
    IValue toIMinMaxDoubleValue(@Nonnull final IAlarmSystemVariable<T> sysVar,
                                @Nonnull final T min,
                                @Nonnull final T max) throws TypeSupportException {
        final T valueData = sysVar.getData().getValueData();
        @SuppressWarnings("unchecked")
        final Class<T> typeClass = (Class<T>) valueData.getClass();
        final EpicsSystemVariableSupport<T> support =
            (EpicsSystemVariableSupport<T>) findTypeSupportFor(EpicsSystemVariableSupport.class, typeClass);
        // TODO (bknerr) : This is definitely an epics alarm, choose an appropriate abstraction
        return support.convertToIMinMaxDoubleValue(sysVar, min, max);
    }

    @CheckForNull
    protected IValue toIValue(@Nonnull final Class<?> typeClass,
                              @Nonnull final Collection<T> data,
                              @Nonnull final EpicsAlarm alarm,
                              @Nonnull final TimeInstant timestamp) throws TypeSupportException {
        @SuppressWarnings("unchecked")
        final EpicsSystemVariableSupport<T> support =
            (EpicsSystemVariableSupport<T>) findTypeSupportFor(EpicsSystemVariableSupport.class, typeClass);
        return support.convertCollectionToIValue(data, alarm, timestamp);
    }

    @CheckForNull
    protected abstract IValue convertCollectionToIValue(Collection<T> data,
                                                        EpicsAlarm alarm,
                                                        TimeInstant timestamp) throws TypeSupportException;

    @CheckForNull
    protected abstract IValue convertToIValue(@Nonnull final T data,
                                              @Nonnull final EpicsAlarm alarm,
                                              @Nonnull final TimeInstant timestamp) throws TypeSupportException;
    @CheckForNull
    protected IValue convertToIMinMaxDoubleValue(@Nonnull final IAlarmSystemVariable<T> sysVar,
                                                 @SuppressWarnings("unused") @Nonnull final T min,
                                                 @SuppressWarnings("unused") @Nonnull final T max) throws TypeSupportException {
        throw new TypeSupportException("Type " + sysVar.getData().getValueData().getClass() + " cannot be converted to IMinMaxDoubleValue!", null);
    }

    @Nonnull
    static IValue createMinMaxDoubleValueFromNumber(@Nonnull final TimeInstant timestamp,
                                                    @Nonnull final IAlarm ialarm,
                                                    @Nonnull final Number valueData,
                                                    @Nonnull final Number min,
                                                    @Nonnull final Number max) {
        // TODO (bknerr) : well thats not quite right again, Epics specifics shouldn't be here
        final EpicsAlarm alarm = (EpicsAlarm) ialarm;
        return ValueFactory.createMinMaxDoubleValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                    EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                    alarm.getStatus().toString(),
                                                    null,
                                                    null,
                                                    new double[]{ valueData.doubleValue() },
                                                    min.doubleValue(),
                                                    max.doubleValue());
    }

}
