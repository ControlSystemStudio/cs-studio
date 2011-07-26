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
package org.csstudio.domain.desy.epics.typesupport;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.ControlSystemType;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.system.SystemVariableSupport;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.epics.pvmanager.TypeSupport;


/**
 * And more conversion support, now from CssValues to IValues.
 *
 * @author bknerr
 * @since 22.12.2010
 * @param <T> the concrete type of the meta data.
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class statically is accessed, hence the name should be short and descriptive!
 */
public abstract class EpicsSystemVariableSupport<T> extends SystemVariableSupport<T> {
// CHECKSTYLE ON : AbstractClassName

    private static boolean INSTALLED;

    /**
     * Constructor.
     */
    @SuppressWarnings("unchecked")
    protected EpicsSystemVariableSupport(@Nonnull final Class<T> type) {
        super(type, (Class<? extends SystemVariableSupport<T>>) EpicsSystemVariableSupport.class);
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

    @Nonnull
    public static <T>
    IValue toIMinMaxDoubleValue(@Nonnull final IAlarmSystemVariable<T> sysVar,
                                @Nonnull final T min,
                                @Nonnull final T max) throws TypeSupportException {
        final T valueData = sysVar.getData();
        @SuppressWarnings("unchecked")
        final Class<T> typeClass = (Class<T>) valueData.getClass();
        final EpicsSystemVariableSupport<T> support =
            (EpicsSystemVariableSupport<T>) findTypeSupportForOrThrowTSE(EpicsSystemVariableSupport.class, typeClass);
        return support.convertToIMinMaxDoubleValue(sysVar, min, max);
    }

    @Nonnull
    protected static IValue createMinMaxDoubleValueFromNumber(@Nonnull final TimeInstant timestamp,
                                                              @Nonnull final EpicsAlarm alarm,
                                                              @Nonnull final Number valueData,
                                                              @Nonnull final Number min,
                                                              @Nonnull final Number max) {
        return ValueFactory.createMinMaxDoubleValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                    EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                    alarm.getStatus().toString(),
                                                    null,
                                                    IValue.Quality.Original,
                                                    new double[]{valueData.doubleValue()},
                                                    min.doubleValue(),
                                                    max.doubleValue());
    }

    @Nonnull
    protected IValue collectionToIValue(@Nonnull final Class<?> typeClass,
                                        @Nonnull final Collection<T> data,
                                        @Nonnull final EpicsAlarm alarm,
                                        @Nonnull final TimeInstant timestamp) throws TypeSupportException {
        @SuppressWarnings("unchecked")
        final EpicsSystemVariableSupport<T> support =
            (EpicsSystemVariableSupport<T>) findTypeSupportForOrThrowTSE(EpicsSystemVariableSupport.class, typeClass);
        return support.convertCollectionToIValue(data, alarm, timestamp);
    }


    @Override
    @Nonnull
    protected EpicsSystemVariable<T> createVariable(@Nonnull final String name,
                                                    @Nonnull final T value,
                                                    @Nonnull final ControlSystem system,
                                                    @Nonnull final TimeInstant timestamp) throws TypeSupportException {
        @SuppressWarnings("unchecked")
        final Class<T> typeClass = (Class<T>) value.getClass();
        final EpicsSystemVariableSupport<T> support =
            (EpicsSystemVariableSupport<T>) findTypeSupportForOrThrowTSE(EpicsSystemVariableSupport.class, typeClass);
        return support.createEpicsVariable(name, value, system, timestamp);
    }


    @Nonnull
    protected EpicsSystemVariable<?>
    createEpicsVariableFromCollection(@Nonnull final String name,
                                      @Nonnull final Class<?> typeClass,
                                      @Nonnull final Collection<T> data,
                                      @Nonnull final ControlSystem system,
                                      @Nonnull final TimeInstant timestamp) throws TypeSupportException {
        @SuppressWarnings("unchecked")
        final EpicsSystemVariableSupport<T> support =
            (EpicsSystemVariableSupport<T>) findTypeSupportForOrThrowTSE(EpicsSystemVariableSupport.class, typeClass);
        return support.createCollectionEpicsVariable(name, typeClass, data, system, timestamp);
    }



    @Nonnull
    protected abstract EpicsSystemVariable<T>
    createEpicsVariable(@Nonnull final String name,
                        @Nonnull final T value,
                        @Nonnull final ControlSystem system,
                        @Nonnull final TimeInstant timestamp) throws TypeSupportException;

    @Nonnull
    protected abstract EpicsSystemVariable<Collection<T>>
    createCollectionEpicsVariable(@Nonnull final String name,
                                  @Nonnull final Class<?> typeClass,
                                  @Nonnull final Collection<T> data,
                                  @Nonnull final ControlSystem system,
                                  @Nonnull final TimeInstant timestamp) throws TypeSupportException;

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected IValue convertToIValue(@Nonnull final IAlarmSystemVariable<T> sysVar) throws TypeSupportException {
        // Safe cast!
        return convertEpicsSystemVariableToIValue((EpicsSystemVariable<T>) sysVar);
    }
    @Nonnull
    protected abstract IValue convertEpicsSystemVariableToIValue(@Nonnull final EpicsSystemVariable<T> sysVar) throws TypeSupportException;

    @Nonnull
    protected abstract IValue convertCollectionToIValue(@Nonnull final Collection<T> data,
                                                        @Nonnull final EpicsAlarm alarm,
                                                        @Nonnull final TimeInstant timestamp) throws TypeSupportException;


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected ControlSystemType getControlSystemType() {
        return ControlSystemType.EPICS_V3;
    }
}
