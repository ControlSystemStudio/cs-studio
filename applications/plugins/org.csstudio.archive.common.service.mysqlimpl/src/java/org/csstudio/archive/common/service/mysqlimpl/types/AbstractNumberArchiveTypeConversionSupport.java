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
package org.csstudio.archive.common.service.mysqlimpl.types;

import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.typesupport.TypeSupportException;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * Common type conversions for {@link Number} subtypes.
 *
 * @author bknerr
 * @since 10.12.2010
 * @param <N> the number subtype
 */
public abstract class AbstractNumberArchiveTypeConversionSupport<N extends Number> extends ArchiveTypeConversionSupport<N> {

    /**
     * Number to string convertible function.
     *
     * @author bknerr
     * @since 20.12.2010
     */
    private final class String2NumberFunction implements Function<String, N> {
        /**
         * Constructor.
         */
        public String2NumberFunction() {
            // Empty
        }

        @Override
        @CheckForNull
        public N apply(@Nonnull final String from) {
            try {
                return convertFromArchiveString(from);
            } catch (final TypeSupportException e) {
                return null;
            }
        }
    }
    private final String2NumberFunction _string2NumberFunc = new String2NumberFunction();

    /**
     * Constructor.
     * @param type
     */
    AbstractNumberArchiveTypeConversionSupport(@Nonnull final Class<N> type) {
        super(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String convertToArchiveString(@Nonnull final N value) throws TypeSupportException {
        return value.toString();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Boolean isOptimizableByAveraging() {
        return Boolean.TRUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<N> convertFromArchiveStringToMultiScalar(@Nonnull final Class<?> collectionClass,
                                                               @Nonnull final String archiveValues) throws TypeSupportException {

        final String releasedStr = collectionRelease(archiveValues);
        if (releasedStr == null) {
            throw new TypeSupportException("Values representation does not adhere to multi scalar start and end delimiters.", null);
        }
        final Iterable<String> strings = Splitter.on(ARCHIVE_COLLECTION_ELEM_SEP).split(releasedStr);

        final Iterable<N> typedValues = Iterables.filter(Iterables.transform(strings, _string2NumberFunc),
                                                         Predicates.<N>notNull());
        checkInputVsOutputSize(strings, typedValues);

        return createCollectionFromIterable(collectionClass, typedValues);
    }
}
