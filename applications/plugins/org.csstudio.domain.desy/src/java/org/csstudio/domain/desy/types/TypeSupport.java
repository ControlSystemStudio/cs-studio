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


import com.google.common.collect.Maps;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 26.11.2010
 */
public abstract class TypeSupport<T extends ICssValueType> {

    private static Map<Class<?>, TypeSupport<?>> _typeSupports = Maps.newHashMap();

    /**
     * Adds support for a new type.
     *
     * @param <T> the type to add support for
     * @param typeClass the class of the type
     * @param typeSupport the support for the type
     */
    public static <T extends ICssValueType> void addTypeSupport(final Class<T> typeClass, final TypeSupport<T> typeSupport) {
        _typeSupports.put(typeClass, typeSupport);
    }



    /**
     * Calculates and caches the type support for a particular class, so that
     * introspection does not occur at every call.
     *
     * @param <T> the type to retrieve support for
     * @param typeClass the class of the type
     * @return the support for the type or null
     */
    @CheckForNull
    static <T extends ICssValueType> TypeSupport<T> cachedTypeSupportFor(final Class<T> typeClass) {
        @SuppressWarnings("unchecked")
		final
        TypeSupport<T> support = (TypeSupport<T>) _typeSupports.get(typeClass);;
//        if (support == null) {
//            support = recursiveTypeSupportFor(typeClass);
//            if (support == null)
//                throw new RuntimeException("No support found for type " + typeClass);
//            calculatedTypeSupport.put(typeClass, support);
//        }
        return support;
    }

    public static <T extends ICssValueType> CssDouble toDDouble(final T value) throws ConversionTypeSupportException {
        @SuppressWarnings("unchecked")
		final
        Class<T> typeClass = (Class<T>) value.getClass();
        final ConversionTypeSupport<T> support = (ConversionTypeSupport<T>) cachedTypeSupportFor(typeClass);
        return support.convertToDDouble(value);
    }
}
