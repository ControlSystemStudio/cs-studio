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
package org.csstudio.domain.desy.types;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.alarm.IAlarm;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.platform.data.IValue;

import com.google.common.collect.Maps;

/**
 * Type support after carcassi's pattern: {@link org.epics.pvmanager.TypeSupport}
 *
 * @author carcassi, bknerr
 * @since 26.11.2010
 * @param <T> the type for the support
 *
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class statically is accessed, hence the name should be short and descriptive!
 */
public abstract class TypeSupport<T> {
// CHECKSTYLE ON: AbstractClassName

    /**
     * Constructor.
     */
    protected TypeSupport() {
        // To be invoked by implementing classes only
    }

    private static Map<Class<?>, TypeSupport<?>> TYPE_SUPPORTS = Maps.newHashMap();
    private static Map<Class<?>, TypeSupport<?>> CALC_TYPE_SUPPORTS = new ConcurrentHashMap<Class<?>, TypeSupport<?>>();

    /**
     * Adds support for a new type.
     *
     * @param <T> the type to add support for
     * @param typeClass the class of the type
     * @param typeSupport the support for the type
     */
    public static <T> void addTypeSupport(@Nonnull final Class<T> typeClass,
                                          @Nonnull final TypeSupport<T> typeSupport) {
        TYPE_SUPPORTS.put(typeClass, typeSupport);
        CALC_TYPE_SUPPORTS.remove(typeClass);
    }

    /**
     * Calculates and caches the type support for a particular class, so that
     * introspection does not occur at every call.
     *
     * @param typeClass the class of the type
     * @return the support for the type or null
     * @param <T> the type to retrieve support for
     */
    @SuppressWarnings("unchecked")
    @CheckForNull
    static <T> TypeSupport<T> cachedTypeSupportFor(@Nonnull final Class<T> typeClass) {
        TypeSupport<T> support = (TypeSupport<T>) CALC_TYPE_SUPPORTS.get(typeClass);
        if (support == null) {
            support = recursiveTypeSupportFor(typeClass);
            if (support == null) {
                final Class<? super T> superclass = typeClass.getSuperclass();
                while (!superclass.equals(Object.class)) {
                    Class<? super T> superClass = superclass;
                    support = (TypeSupport<T>) TYPE_SUPPORTS.get(superClass);
                    if (support != null) {
                        break;
                    }
                    superClass = superclass.getSuperclass();
                }
            }
            if (support == null) {
                throw new RuntimeException("No support found for type " + typeClass);
            }
            CALC_TYPE_SUPPORTS.put(typeClass, support);
        }
        return support;
    }

    /**
     * Retrieve support for the given type and if not found looks at the
     * implemented interfaces.
     * If not found for the interfaces, traverse the superclass hierarchy.
     *
     * @param <T> the type to retrieve support for
     * @param typeClass the class of the type
     * @return the support for the type or null
     */
    @SuppressWarnings("unchecked")
    @CheckForNull
    static <T> TypeSupport<T> recursiveTypeSupportFor(@Nonnull final Class<T> typeClass) {
        TypeSupport<T> support = (TypeSupport<T>) TYPE_SUPPORTS.get(typeClass);
        if (support == null) {
            for (@SuppressWarnings("rawtypes") final Class clazz : typeClass.getInterfaces()) {
                support = recursiveTypeSupportFor(clazz);
                if (support != null) {
                    return support;
                }
            }
        }
        return support;
    }


    /**
     * Tries to convert the given IValue type and its accompanying parms to the css value type.
     * @param value the value to be converted
     * @param alarm the value's alarm state
     * @param timestamp the value's timestamp
     * @return the conversion result
     * @throws ConversionTypeSupportException when conversion failed.
     * @param <R>
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    @CheckForNull
    public static <R extends ICssAlarmValueType<?>, V extends IValue>
        R toCssType(@Nonnull final V value,
                    @Nullable final IAlarm alarm,
                    @Nonnull final TimeInstant timestamp) throws ConversionTypeSupportException {

        final Class<V> typeClass = (Class<V>) value.getClass();
        final AbstractIValueConversionTypeSupport<R, V> support =
            (AbstractIValueConversionTypeSupport<R, V>) cachedTypeSupportFor(typeClass);
        if (support == null) {
            throw new ConversionTypeSupportException("No conversion type support registered.", null);
        }
        return support.convertToCssType(value, alarm, timestamp);
    }

    /**
     * Tries to convert the value data/datum into a string representation suitable for archiving purposes.
     * @param valueData
     * @return
     * @throws ConversionTypeSupportException
     */
    @CheckForNull
    public static <T> String toArchiveString(@Nonnull final T value) throws ConversionTypeSupportException {
        @SuppressWarnings("unchecked")
        final Class<T> typeClass = (Class<T>) value.getClass();
        final AbstractArchiveConversionTypeSupport<T> support =
            (AbstractArchiveConversionTypeSupport<T>) cachedTypeSupportFor(typeClass);
        if (support == null) {
            throw new ConversionTypeSupportException("No conversion type support registered.", null);
        }
        return support.convertToArchiveString(value);
    }

    /**
     * Tries to convert the given css value type to CssDouble.
     * @param value the value to be converted
     * @return the conversion result
     * @throws ConversionTypeSupportException when conversion failed.
     * @param <V> the basic type of the value(s)
     * @param <T> the css value type
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> Double toDouble(@Nonnull final T value) throws ConversionTypeSupportException {
        final Class<T> typeClass = (Class<T>) value.getClass();
        final AbstractArchiveConversionTypeSupport<T> support =
            (AbstractArchiveConversionTypeSupport<T>) cachedTypeSupportFor(typeClass);
        if (support == null) {
            throw new ConversionTypeSupportException("No conversion type support registered.", null);
        }
        return support.convertToDouble(value);
    }
}
