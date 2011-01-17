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

/**
 * Type support after carcassi's pattern: {@link org.epics.pvmanager.TypeSupport}
 *
 * @author carcassi, bknerr
 * @since 26.11.2010
 * @param <T> the type for the support
 */
public abstract class AbstractTypeSupport<T> {

    /**
     * Constructor.
     */
    protected AbstractTypeSupport() {
        // To be invoked by implementing classes only
    }

    /**
     * Adds support for a new type.
     *
     * @param <T> the type to add support for
     * @param typeClass the class of the type
     * @param typeSupport the support for the type
     */
    public static <T> void addTypeSupport(@Nonnull final Class<T> typeClass,
                                          @Nonnull final AbstractTypeSupport<T> typeSupport,
                                          @Nonnull final Map<Class<?>, AbstractTypeSupport<?>> supportMap,
                                          @Nonnull final Map<Class<?>, AbstractTypeSupport<?>> calcSupportMap) {
        supportMap.put(typeClass, typeSupport);
        calcSupportMap.remove(typeClass);
    }

    /**
     * Calculates and caches the type support for a particular class, so that
     * introspection does not occur at every call.
     *
     * @param typeClass the class of the type
     * @return the support for the type or null
     * @param <T> the type to retrieve support for
     * @throws TypeSupportException
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    protected static <T> AbstractTypeSupport<T> cachedTypeSupportFor(@Nonnull final Class<T> typeClass,
                                                                     @Nonnull final Map<Class<?>, AbstractTypeSupport<?>> supportMap,
                                                                     @Nonnull final Map<Class<?>, AbstractTypeSupport<?>> calcSupportMap) throws TypeSupportException {
        AbstractTypeSupport<T> support = (AbstractTypeSupport<T>) calcSupportMap.get(typeClass);
        if (support == null) {
            support = recursiveTypeSupportFor(typeClass, supportMap);
            if (support == null) {
                Class<? super T> superClass = typeClass.getSuperclass();
                while (!superClass.equals(Object.class)) {
                    support = (AbstractTypeSupport<T>) supportMap.get(superClass);
                    if (support != null) {
                        break;
                    }
                    superClass = superClass.getSuperclass();
                }
            }
            if (support == null) {
                throw new TypeSupportException("No type support found for type " + typeClass, null);
            }
            calcSupportMap.put(typeClass, support);
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
    static <T> AbstractTypeSupport<T> recursiveTypeSupportFor(@Nonnull final Class<T> typeClass,
                                                              @Nonnull final Map<Class<?>, AbstractTypeSupport<?>> supportMap) {
        AbstractTypeSupport<T> support = (AbstractTypeSupport<T>) supportMap.get(typeClass);
        if (support == null) {
            for (@SuppressWarnings("rawtypes") final Class clazz : typeClass.getInterfaces()) {
                support = recursiveTypeSupportFor(clazz, supportMap);
                if (support != null) {
                    return support;
                }
            }
        }
        return support;
    }
}
