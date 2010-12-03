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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.platform.data.IValue;

import com.google.common.collect.Maps;

/**
 * Type support after carcassi's pattern: {@link org.epics.pvmanager.TypeSupport}
 *
 * @author bknerr
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
    }



    /**
     * Calculates and caches the type support for a particular class, so that
     * introspection does not occur at every call.
     *
     * @param typeClass the class of the type
     * @return the support for the type or null
     * @param <T> the type to retrieve support for
     */
    @CheckForNull
    static <T> TypeSupport<T> cachedTypeSupportFor(@Nonnull final Class<T> typeClass) {
        @SuppressWarnings("unchecked")
		final TypeSupport<T> support = (TypeSupport<T>) TYPE_SUPPORTS.get(typeClass);
//        if (support == null) {
//            support = recursiveTypeSupportFor(typeClass);
//            if (support == null)
//                throw new RuntimeException("No support found for type " + typeClass);
//            calculatedTypeSupport.put(typeClass, support);
//        }
        return support;
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
    public static <V, T extends ICssValueType<V>> CssDouble toCssDouble(final T value) throws ConversionTypeSupportException {
		final Class<T> typeClass = (Class<T>) value.getClass();
        final AbstractCssValueConversionTypeSupport<V, T> support =
            (AbstractCssValueConversionTypeSupport<V, T>) cachedTypeSupportFor(typeClass);
        return support.convertToDDouble(value);
    }

    /**
     * Tries to convert the given IValue type to its basic value type.
     * @param value the value to be converted
     * @return the conversion result
     * @throws ConversionTypeSupportException when conversion failed.
     * @param <R>
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public static <R, T extends IValue> R toBasicType(final T value) throws ConversionTypeSupportException {
        final Class<T> typeClass = (Class<T>) value.getClass();
        final AbstractIValueConversionTypeSupport<R, T> support =
            (AbstractIValueConversionTypeSupport<R, T>) cachedTypeSupportFor(typeClass);
        return support.convertToBasicType(value);
    }


}
