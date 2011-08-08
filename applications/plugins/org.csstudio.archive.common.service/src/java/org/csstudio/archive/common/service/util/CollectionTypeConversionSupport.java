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
package org.csstudio.archive.common.service.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.epics.pvmanager.TypeSupport;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

/**
 * Type conversions for {@link T}. Unfortunately, the basic collection type do not shace a common
 * interface combining Collection and Serializable.
 * So, we have to
 *
 * @author bknerr
 * @since 16.12.2010
 * @param <T> the supported type
 */
// CHECKSTYLE OFF : AbstractClassName
abstract class CollectionTypeConversionSupport<T extends Serializable & Collection<T>> extends ArchiveTypeConversionSupport<T> {
// CHECKSTYLE ON : AbstractClassName

    private static final Set<Class<?>> BASIC_COLL_TYPES =
        Sets.<Class<?>>newHashSet(ArrayList.class,
                                  LinkedList.class,
                                  EnumSet.class,
                                  HashSet.class,
                                  TreeSet.class,
                                  Stack.class,
                                  Vector.class);
    /**
     * Concrete implementation for the different types.
     *
     * @author bknerr
     * @since 04.08.2011
     */
    private static final class ConcreteCollectionArchiveTypeConversionSupport<T extends Serializable & Collection<T>>
                               extends CollectionTypeConversionSupport<T> {
        /**
         * Constructor.
         */
        protected ConcreteCollectionArchiveTypeConversionSupport(@Nonnull final Class<T> typeClass) {
            super(typeClass);
        }
    }

    private static boolean INSTALLED;

    /**
     * Constructor.
     */
    CollectionTypeConversionSupport(@Nonnull final Class<T> typeClass) {
        super(typeClass);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void install() {
        if (INSTALLED) {
            return;
        }
        BaseTypeConversionSupport.install();

        for (final Class<?> clazz : BASIC_COLL_TYPES) {
            TypeSupport.addTypeSupport(new ConcreteCollectionArchiveTypeConversionSupport(clazz));
        }

        INSTALLED = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected String convertToArchiveString(@Nonnull final T values) throws TypeSupportException {
        if (values.isEmpty()) {
            return "";
        }
        @SuppressWarnings("unchecked")
        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) findTypeSupportForOrThrowTSE(ArchiveTypeConversionSupport.class,
                                                                           values.iterator().next().getClass());

        final Collection<String> items =
            Collections2.filter(Collections2.transform(values,  new Type2StringFunction(support)),
                                Predicates.<String>notNull());
        if (values.size() != items.size()) {
            throw new TypeSupportException("Number of transformed elements (" + items.size() +
                                           " does not match the number of input elements (" + values.size() + "!", null);
        }

        final String result = Joiner.on(ARCHIVE_COLLECTION_ELEM_SEP).join(items);

        return collectionEmbrace(result);
    }

    /**
     * {@inheritDoc}
     *
     * ATTENTION: guaranteed to throw {@link TypeSupportException}.<br>
     *            Use {@link ArchiveTypeConversionSupport#fromMultiScalarArchiveString(Class<T>, String)}
     *            with Class<T> for Collection<T> instead of Collection.class.
     */
    @Override
    @Nonnull
    protected T convertFromArchiveString(@Nonnull final String value) throws TypeSupportException {
        throw new TypeSupportException("This method cannot be invoked for Collection.class subtypes!" +
                                       " Use ArchiveTypeConversionSupport.fromArchiveString(Class<C>, Class<E>, String) instead," +
                                       " where you specify the Collection type C, and the elements type E to convert the String into.", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected T convertFromDouble(@Nonnull final Double value) throws TypeSupportException {
        throw new TypeSupportException("This method doesn't make sense for collection based types!", null);
    }
}
