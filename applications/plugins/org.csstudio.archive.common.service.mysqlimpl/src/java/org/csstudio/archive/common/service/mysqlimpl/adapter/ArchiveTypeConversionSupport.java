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
package org.csstudio.archive.common.service.mysqlimpl.adapter;

import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.domain.desy.epics.types.EpicsEnumTriple;
import org.csstudio.domain.desy.types.AbstractTypeSupport;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.logging.CentralLogger;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

/**
 * Archive type conversion for system variables.
 *
 * @author bknerr
 * @since 15.12.2010
 * @param <T> the supported class type
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class statically is accessed, hence the name should be short and descriptive!
 *
 */
public abstract class ArchiveTypeConversionSupport<T> extends AbstractTypeSupport<T> {
    // CHECKSTYLE ON : AbstractClassName

    /**
     * TODO (bknerr) :
     *
     * @author bknerr
     * @since 22.12.2010
     */
    private final class Type2StringFunction implements Function<T, String> {
        /**
         * Constructor.
         */
        public Type2StringFunction() {
            // Empty
        }

        @Override
        @CheckForNull
        public String apply(@Nonnull final T from) {
            try {
                return ArchiveTypeConversionSupport.toArchiveString(from);
            } catch (final TypeSupportException e) {
                LOG.warn("No type conversion to archive string for " + from.getClass().getName() + " registered.");
                return null;
            }
        }
    }
    private final Type2StringFunction _type2StringFunc = new Type2StringFunction();

    protected static final Logger LOG =
        CentralLogger.getInstance().getLogger(ArchiveTypeConversionSupport.class);

    protected static final String ARCHIVE_COLLECTION_ELEM_SEP = "\\,";
    protected static final String ARCHIVE_COLLECTION_PREFIX = "[";
    protected static final String ARCHIVE_COLLECTION_SUFFIX = "]";
    protected static final String ARCHIVE_TUPLE_PREFIX = "(";
    protected static final String ARCHIVE_TUPLE_SUFFIX = ")";
    protected static final String ARCHIVE_TUPLE_SEP = "\\|";

    protected static final String ARCHIVE_NULL_ENTRY = "<null>";

    private static boolean INSTALLED = false;

    @Nonnull
    protected static String collectionEmbrace(@Nonnull final String input) {
        return embrace(ARCHIVE_COLLECTION_PREFIX, input, ARCHIVE_COLLECTION_SUFFIX);
    }
    @Nonnull
    protected static String tupleEmbrace(@Nonnull final String input) {
        return embrace(ARCHIVE_TUPLE_PREFIX, input, ARCHIVE_TUPLE_SUFFIX);
    }
    @Nonnull
    private static String embrace(@Nonnull final String prefix, @Nonnull final String input, @Nonnull final String suffix) {
        return prefix + input + suffix;
    }
    @CheckForNull
    protected static String collectionRelease(@Nonnull final String input) {
        return release(ARCHIVE_COLLECTION_PREFIX, input, ARCHIVE_COLLECTION_SUFFIX);
    }
    @CheckForNull
    protected static String tupleRelease(@Nonnull final String input) {
        return release(ARCHIVE_TUPLE_PREFIX, input, ARCHIVE_TUPLE_SUFFIX);
    }
    @CheckForNull
    private static String release(@Nonnull final String prefix, @Nonnull final String input, @Nonnull final String suffix) {
        if (input.startsWith(prefix) && input.endsWith(suffix)) {
            return input.substring(prefix.length(),
                                   input.length() - suffix.length());
        }
        return null;
    }

    /**
     * Constructor.
     */
    ArchiveTypeConversionSupport() {
        // Don't instantiate outside this class
    }

    public static void install() {
        if (INSTALLED) {
            return;
        }
        AbstractTypeSupport.addTypeSupport(Double.class, new DoubleArchiveTypeConversionSupport());
        AbstractTypeSupport.addTypeSupport(Float.class, new FloatArchiveTypeConversionSupport());
        AbstractTypeSupport.addTypeSupport(Integer.class, new IntegerArchiveTypeConversionSupport());
        AbstractTypeSupport.addTypeSupport(Long.class, new LongArchiveTypeConversionSupport());
        AbstractTypeSupport.addTypeSupport(String.class, new StringArchiveTypeConversionSupport());
        AbstractTypeSupport.addTypeSupport(Byte.class, new ByteArchiveTypeConversionSupport());
        AbstractTypeSupport.addTypeSupport(EpicsEnumTriple.class, new EnumArchiveTypeConversionSupport());
        AbstractTypeSupport.addTypeSupport(Collection.class, new CollectionTypeConversionSupport());

        INSTALLED = true;
    }

    /**
     * Tries to convert the value data/datum into a string representation suitable for archiving purposes.
     * @param valueData
     * @return
     * @throws TypeSupportException
     */
    @Nonnull
    public static <T> String toArchiveString(@Nonnull final T value) throws TypeSupportException {
        @SuppressWarnings("unchecked")
        final Class<T> typeClass = (Class<T>) value.getClass();
        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) cachedTypeSupportFor(typeClass);
        if (support == null) {
            throw new TypeSupportException("No conversion type support registered.", null);
        }
        return support.convertToArchiveString(value);
    }

    /**
     * Tries to convert the archive string value data/datum into a the given type representation.
     *
     * @param the expected type of the value
     * @param valueData the string representation of the value
     * @return an instance of the given type with the values extracted from the string.
     * @throws TypeSupportException
     */
    @CheckForNull
    public static <T> T fromScalarArchiveString(final Class<T> typeClass, @Nonnull final String value) throws TypeSupportException {
        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) cachedTypeSupportFor(typeClass);
        if (support == null) {
            throw new TypeSupportException("No conversion type support registered.", null);
        }
        return support.convertFromArchiveString(value);
    }

    /**
     * Tries to convert the archive string value data (supposed to represent a collection)
     * into a typed collection. Whether the collection shall support a {@link java.util.Set},
     * {@link List}, or any other subtype of collection has to be handled by the invoker.
     *
     * ATTENTION: Don't put any non-reifiable type like Collection.class as parameter, but its
     * elements' type (Class<T>.class of Collection<T>), since the Collection.class type support
     * lacks the generic type information of its elements.
     *
     * @param elemClass the type of the elements for which the type support has to exist
     * @param values the string representation for the values
     * @return the typed collection
     * @throws TypeSupportException
     */
    @CheckForNull
    public static <T> Collection<T>
    fromMultiScalarArchiveString(@Nonnull final Class<T> elemClass,
                                 @Nonnull final String values) throws TypeSupportException {
        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) cachedTypeSupportFor(elemClass);
        if (support == null) {
            throw new TypeSupportException("No conversion type support registered.", null);
        }
        return support.convertMultiScalarFromArchiveString(values);
    }

    /**
     * TODO (bknerr) : once the type safety is given from the engine front end, we won't need this one
     *
     * Tries to convert the given css value type to CssDouble.
     * @param value the value to be converted
     * @return the conversion result
     * @throws TypeSupportException when conversion failed.
     * @param <V> the basic type of the value(s)
     * @param <T> the css value type
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> Double toDouble(@Nonnull final T value) throws TypeSupportException {
        final Class<T> typeClass = (Class<T>) value.getClass();
        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) cachedTypeSupportFor(typeClass);
        if (support == null) {
            throw new TypeSupportException("No conversion type support registered.", null);
        }
        return support.convertToDouble(value);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> T fromArchiveString(@Nonnull final String datatype,
                                          @Nonnull final String value) throws TypeSupportException {
        // Parse for generic type

        // Extract the surrounding generic type (recursively)

        // try Class creation:
        Class<T> typeClass;
        try {
            typeClass = (Class<T>) Class.forName("java.lang." + datatype);
        } catch (final ClassNotFoundException ce) {
            // Ignore first
        }
        try {
            typeClass = (Class<T>) Class.forName("java.util." + datatype);
        } catch (final ClassNotFoundException ce) {
            // Ignore second
        }
        try {
            typeClass = (Class<T>) Class.forName("org.csstudio.domain.desy.epics.types." + datatype);
        } catch (final ClassNotFoundException ce) {
            throw new TypeSupportException("Class not found for " + datatype, null);
        }

        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) cachedTypeSupportFor(typeClass);
        if (support == null) {
            throw new TypeSupportException("No conversion type support registered.", null);
        }
        return support.convertFromArchiveString(value);
    }

    @Nonnull
    protected String convertMultiScalarToArchiveString(@Nonnull final Collection<T> values) throws TypeSupportException {
        final Collection<String> items =
            Collections2.filter(Collections2.transform(values, _type2StringFunc),
                                Predicates.<String>notNull());
        if (values.size() != items.size()) {
            throw new TypeSupportException("Number of transformed elements (" + items.size() +
                                           " does not match the number of input elements (" + values.size() + "!", null);
        }
        final String result = Joiner.on(ARCHIVE_COLLECTION_ELEM_SEP).join(items);
        return collectionEmbrace(result);
    }

    @Nonnull
    protected abstract String convertToArchiveString(@Nonnull final T value) throws TypeSupportException;
    @CheckForNull
    protected abstract T convertFromArchiveString(@Nonnull final String value) throws TypeSupportException;
    @CheckForNull
    protected abstract Collection<T> convertMultiScalarFromArchiveString(@Nonnull final String values) throws TypeSupportException;
    @Nonnull
    protected abstract Double convertToDouble(@Nonnull final T value) throws TypeSupportException;
}
