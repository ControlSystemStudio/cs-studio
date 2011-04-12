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
package org.csstudio.domain.desy.system;

import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.data.values.IValue;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.AbstractTypeSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;

import com.google.common.collect.Maps;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 17.02.2011
 * @param <T> the supported class type
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class is accessed statically, hence the name should be short and descriptive!
 */
public abstract class SystemVariableSupport<T> extends AbstractTypeSupport<T> {
    // CHECKSTYLE ON : AbstractClassName

    /**
     * Discriminates the type support families by their system type (Epics, Doocs, Tango).
     */
    private static final Map<ControlSystemType, Class<? extends SystemVariableSupport<?>>> SYSTEM_DISCRIMINATOR =
        Maps.newEnumMap(ControlSystemType.class);

    /**
     * Constructor.
     */
    public SystemVariableSupport(@Nonnull final Class<T> type,
                                 @Nonnull final Class<? extends SystemVariableSupport<T>> typeSupportFamily) {
        super(type, typeSupportFamily);

        SYSTEM_DISCRIMINATOR.put(getControlSystemType(), typeSupportFamily);
    }

    /**
     * Creates a system and value type specific variable from the given values.
     * Dispatches the value creation to the suitable type support family by the control system
     * type.
     *
     * @param name the variable's name
     * @param value the value of the variable
     * @param system the control system (serves as discriminator to spawn the correct type family)
     * @param time the timestamp
     * @return the system and value type specific variable.
     * @throws TypeSupportException
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> ISystemVariable<T> create(@Nonnull final String name,
                                                @Nonnull final T value,
                                                @Nonnull final ControlSystem system,
                                                @Nonnull final TimeInstant time) throws TypeSupportException {

        final Class<? extends SystemVariableSupport<?>> familyClass = SYSTEM_DISCRIMINATOR.get(system.getType());
        if (familyClass != null) {
            final Class<T> typeClass = (Class<T>) value.getClass();
            final SystemVariableSupport<T> support =
                (SystemVariableSupport<T>) findTypeSupportForOrThrowTSE(familyClass, typeClass);
            return support.createVariable(name, value, system, time);
        }
        throw new TypeSupportException("System variable support for system " + system.getType() + " unknown.", null);
    }
    @Nonnull
    protected abstract ISystemVariable<T> createVariable(@Nonnull final String name,
                                                         @Nonnull final T value,
                                                         @Nonnull final ControlSystem system,
                                                         @Nonnull final TimeInstant timestamp) throws TypeSupportException;
    @Nonnull
    protected abstract ControlSystemType getControlSystemType();


    @CheckForNull
    public static <T>
    IValue toIMinMaxDoubleValue(@Nonnull final IAlarmSystemVariable<T> sysVar,
                                @Nonnull final T min,
                                @Nonnull final T max) throws TypeSupportException {

        final Class<? extends SystemVariableSupport<?>> familyClass = SYSTEM_DISCRIMINATOR.get(sysVar.getOrigin().getType());
        if (familyClass != null) {
            final T value = sysVar.getData().getValueData();
            @SuppressWarnings("unchecked")
            final Class<T> typeClass = (Class<T>) value.getClass();
            final SystemVariableSupport<T> support =
                (SystemVariableSupport<T>) findTypeSupportForOrThrowTSE(familyClass, typeClass);
            return support.convertToIMinMaxDoubleValue(sysVar, min, max);
        }
        throw new TypeSupportException("System variable support for system " + sysVar.getOrigin().getType() + " unknown.", null);
    }
    /**
     * To be overridden by implementing classes.
     * @param sysVar
     * @param min
     * @param max
     * @return
     * @throws TypeSupportException
     */
    @Nonnull
    protected IValue convertToIMinMaxDoubleValue(@Nonnull final IAlarmSystemVariable<T> sysVar,
                                                 @SuppressWarnings("unused") @Nonnull final T min,
                                                 @SuppressWarnings("unused") @Nonnull final T max) throws TypeSupportException {
        throw new TypeSupportException("Type " + sysVar.getData().getValueData().getClass() + " cannot be converted to IMinMaxDoubleValue!", null);
    }


    @CheckForNull
    public static <T> IValue toIValue(@Nonnull final IAlarmSystemVariable<T> sysVar) throws TypeSupportException {
        final Class<? extends SystemVariableSupport<?>> familyClass = SYSTEM_DISCRIMINATOR.get(sysVar.getOrigin().getType());
        if (familyClass != null) {
            final T value = sysVar.getData().getValueData();
            @SuppressWarnings("unchecked")
            final Class<T> typeClass = (Class<T>) value.getClass();
            final SystemVariableSupport<T> support =
                (SystemVariableSupport<T>) findTypeSupportForOrThrowTSE(familyClass, typeClass);
            return support.convertToIValue(sysVar);
        }
        throw new TypeSupportException("System variable support for system " + sysVar.getOrigin().getType() + " unknown.", null);
    }

    @Nonnull
    protected abstract IValue convertToIValue(@Nonnull final IAlarmSystemVariable<T> sysVar) throws TypeSupportException;
}
