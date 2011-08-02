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

import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.typesupport.TypeSupportException;

/**
 * Type conversions for {@link Collection}.
 *
 * @author bknerr
 * @since 16.12.2010
 */
@SuppressWarnings("rawtypes")
final class CollectionTypeConversionSupport extends ArchiveTypeConversionSupport<Collection> {
    /**
     * Constructor.
     * @param type
     */
    CollectionTypeConversionSupport() {
        super(Collection.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    protected String convertToArchiveString(@Nonnull final Collection values) throws TypeSupportException {
        if (values.isEmpty()) {
            return "";
        }
        return convertFromMultiScalarToArchiveString(values);
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
    protected Collection convertFromArchiveString(@Nonnull final String value) throws TypeSupportException {
        throw new TypeSupportException("This method shall not be invoked for type Collection.class." +
                                       " Use .class type of T for Collection<T>! as parameter." , null);
    }

    /**
     * {@inheritDoc}
     *
     * ATTENTION: guaranteed to throw {@link TypeSupportException}.<br>
     *            Use {@link ArchiveTypeConversionSupport#fromMultiScalarArchiveString(Class<T>, String)}
     *            with Class<T> for Collection<T> instead of Collection.class.
     */
    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    protected Collection convertFromArchiveStringToMultiScalar(@Nonnull final Class<?> collectionClass,
                                                               @Nonnull final String values) throws TypeSupportException {
        throw new TypeSupportException("This method shall not be invoked for class type Collection.class." +
                                       " Use .class type of T for a Collection<T> as parameter." , null);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected Collection convertFromDouble(@Nonnull final Double value) throws TypeSupportException {
        throw new TypeSupportException("This method is not defined (yet?) for Collection.class.\n" +
                                       "Perhaps it will make sense for archiving the magnitudes of numerical vectors?" , null);
    }
}
