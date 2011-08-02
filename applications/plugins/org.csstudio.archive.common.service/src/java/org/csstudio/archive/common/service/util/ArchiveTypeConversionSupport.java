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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.service.channel.ArchiveChannel;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.controlsystem.IArchiveControlSystem;
import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.AbstractTypeSupport;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.epics.pvmanager.TypeSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

/**
 * Archive type conversion for system variables.
 *
 * @author bknerr
 * @since 15.12.2010
 * @param <T> the supported class type
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class is accessed statically, hence the name should be short and descriptive!
 *
 */
public abstract class ArchiveTypeConversionSupport<T> extends AbstractTypeSupport<T> {
    // CHECKSTYLE ON : AbstractClassName

    protected static final String ARCHIVE_COLLECTION_ELEM_SEP = "\\,";
    protected static final String ARCHIVE_COLLECTION_PREFIX = "[";
    protected static final String ARCHIVE_COLLECTION_SUFFIX = "]";
    protected static final String ARCHIVE_TUPLE_PREFIX = "(";
    protected static final String ARCHIVE_TUPLE_SUFFIX = ")";
    protected static final String ARCHIVE_TUPLE_SEP = "\\|";

    protected static final String ARCHIVE_NULL_ENTRY = "<null>";

    private static final String[] SCALAR_TYPE_PACKAGES =
        new String[]{
                     "java.lang",
                     "org.csstudio.domain.desy.epics.types",
                     };
    private static final String[] MULTI_SCALAR_TYPE_PACKAGES =
        new String[]{
                     "java.util",
                     "org.csstudio.domain.desy.epics.types",
                     };

    private static boolean INSTALLED;

    /**
     * Constructor.
     */
    protected ArchiveTypeConversionSupport(@Nonnull final Class<T> type) {
        super(type, ArchiveTypeConversionSupport.class);
    }

    public static void install() {
        if (INSTALLED) {
            return;
        }
        BaseTypeConversionSupport.install();
        TypeSupport.addTypeSupport(new DoubleArchiveTypeConversionSupport());
        TypeSupport.addTypeSupport(new FloatArchiveTypeConversionSupport());
        TypeSupport.addTypeSupport(new IntegerArchiveTypeConversionSupport());
        TypeSupport.addTypeSupport(new LongArchiveTypeConversionSupport());
        TypeSupport.addTypeSupport(new StringArchiveTypeConversionSupport());
        TypeSupport.addTypeSupport(new ByteArchiveTypeConversionSupport());
        TypeSupport.addTypeSupport(new EnumArchiveTypeConversionSupport());
        TypeSupport.addTypeSupport(new CollectionTypeConversionSupport());

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
            (ArchiveTypeConversionSupport<T>) findTypeSupportForOrThrowTSE(ArchiveTypeConversionSupport.class,
                                                                   typeClass);
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
    @Nonnull
    public static <T> T fromArchiveString(@Nonnull final Class<T> typeClass,
                                          @Nonnull final String value) throws TypeSupportException {
        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) findTypeSupportForOrThrowTSE(ArchiveTypeConversionSupport.class,
                                                                   typeClass);
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
    @Nonnull
    public static <T> Collection<T>
    fromMultiScalarArchiveString(@Nonnull final Class<?> collectionClass,
                                 @Nonnull final Class<T> elemClass,
                                 @Nonnull final String values) throws TypeSupportException {
        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) findTypeSupportForOrThrowTSE(ArchiveTypeConversionSupport.class,
                                                                           elemClass);
        return support.convertFromArchiveStringToMultiScalar(collectionClass, values);
    }


    @Nonnull
    public static <T> T fromDouble(@Nonnull final String dataType, @Nonnull final Double value) throws TypeSupportException {
        final Class<?> typeClass = BaseTypeConversionSupport.createTypeClassFromString(dataType,
                                                             SCALAR_TYPE_PACKAGES);
        if (typeClass == null) {
            throw new TypeSupportException("Class object for data type " + dataType +
                                           " could not be loaded from packages " +
                                           Joiner.on(", ").join(SCALAR_TYPE_PACKAGES), null);
        }
        @SuppressWarnings("unchecked")
        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) findTypeSupportForOrThrowTSE(ArchiveTypeConversionSupport.class,
                                                                   typeClass);
        return support.convertFromDouble(value);
    }


    /**
     * Returns whether the given data type as contained in the archive db is optimizable by averaging.
     * This datatype has to have a registered type support with convertibility to Double type.
     * @return true if optimizable by averaging, false otherwise
     * @throws TypeSupportException
     */
    @Nonnull
    public static Boolean isDataTypeOptimizable(@Nonnull final String dataType) throws TypeSupportException {
        final Class<?> typeClass =
            BaseTypeConversionSupport.createTypeClassFromString(dataType,
                                                                SCALAR_TYPE_PACKAGES);
        if (typeClass == null) {
            throw new TypeSupportException("Class object for data type " + dataType +
                                           " could not be loaded from packages " +
                                           Joiner.on(", ").join(SCALAR_TYPE_PACKAGES), null);
        }
        return isDataTypeOptimizable(typeClass);
    }
    /**
     * Returns whether the given data type is optimizable by averaging.
     * This datatype has to have a registered type support with convertibility to Double type.
     * @return true if optimizable by averaging, false otherwise
     * @throws TypeSupportException
     */
    @Nonnull
    public static <T> Boolean isDataTypeOptimizable(@Nonnull final Class<T> dataType) throws TypeSupportException {
        final ArchiveTypeConversionSupport<?> support =
            (ArchiveTypeConversionSupport<?>) findTypeSupportForOrThrowTSE(ArchiveTypeConversionSupport.class,
                                                                 dataType);
        return support.isOptimizableByAveraging();
    }


    @Nonnull
    public static <T> T fromArchiveString(@Nonnull final String datatype,
                                          @Nonnull final String value) throws TypeSupportException {
        try {
            final Class<T> typeClass = BaseTypeConversionSupport.createTypeClassFromString(datatype,
                                                                                           SCALAR_TYPE_PACKAGES);
            return fromArchiveString(typeClass, value);
        } catch (final TypeSupportException e) {
            return multiScalarSupport(datatype, value);
        }
    }

    // CHECKSTYLE OFF : ParameterNumber
    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> IArchiveChannel createArchiveChannel(@Nonnull final ArchiveChannelId id,
                                                           @Nonnull final String name,
                                                           @Nonnull final String datatype,
                                                           @Nonnull final ArchiveChannelGroupId archiveChannelGroupId,
                                                           @Nullable final TimeInstant time,
                                                           @Nonnull final IArchiveControlSystem cs,
                                                           @CheckForNull final String low,
                                                           @CheckForNull final String high) throws TypeSupportException {
        // CHECKSTYLE ON : ParameterNumber
        if (Strings.isNullOrEmpty(low) || Strings.isNullOrEmpty(high)) {
            return new ArchiveChannel(id,
                                      name,
                                      datatype,
                                      archiveChannelGroupId,
                                      time,
                                      cs);
        }
        final Class<Object> typeClass = BaseTypeConversionSupport.createTypeClassFromString(datatype, SCALAR_TYPE_PACKAGES);
        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) findTypeSupportForOrThrowTSE(ArchiveTypeConversionSupport.class,
                                                                           typeClass);
        return support.createChannel(id, name, datatype, archiveChannelGroupId, time, cs,
                                     (T) fromArchiveString(datatype, low),
                                     (T) fromArchiveString(datatype, high));
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static <T> T multiScalarSupport(@Nonnull final String datatype,
                                            @Nonnull final String value) throws TypeSupportException {


        final Class<T> collClass =
            BaseTypeConversionSupport.createCollectionClassFromMultiScalarString(datatype,
                                                                                 MULTI_SCALAR_TYPE_PACKAGES);

        final Class<T> typeClass =
            BaseTypeConversionSupport.createTypeClassFromMultiScalarString(datatype,
                                                                           SCALAR_TYPE_PACKAGES);

        if (typeClass != null) {
            return (T) fromMultiScalarArchiveString(collClass, typeClass, value);
        }

        throw new TypeSupportException("Either class unknown or conversion type support not registered for " + datatype, null);
    }

    @Nonnull
    protected static <T> Collection<T> createCollectionFromIterable(@Nonnull final Class<?> collectionClass,
                                                                    @Nonnull final Iterable<T> values) throws TypeSupportException {
        try {
            @SuppressWarnings("unchecked")
            final Collection<T> collection = (Collection<T>) collectionClass.newInstance();
            for (final T elem : values) {
                collection.add(elem);
            }
            return collection;
        } catch (final InstantiationException e) {
            throw new TypeSupportException("Collection class " + collectionClass.getName() + " could not be created.", e);
        } catch (final IllegalAccessException e) {
            throw new TypeSupportException("Collection class " + collectionClass.getName() + " could not be created.", e);
        }
    }

    protected static <T> void checkInputVsOutputSize(@Nonnull final Iterable<String> strings,
                                                     @Nonnull final Iterable<T> typedValues) throws TypeSupportException {
        int size;
        try {
            size = Iterables.size(typedValues);
        } catch (final NumberFormatException e) {
            throw new TypeSupportException("Values representation is not convertible to " + EpicsEnum.class.getName(), e);
        }
        if (Iterables.size(strings) != size) {
            throw new TypeSupportException("Number of values in string representation does not match the size of the result collection..", null);
        }
    }

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
     * Type to archive string converter function for guava collection transforming.
     *
     * @author bknerr
     * @since 22.12.2010
     */
    private final class Type2StringFunction implements Function<T, String> {
        private final Logger _log =
                LoggerFactory.getLogger(ArchiveTypeConversionSupport.Type2StringFunction.class);
        private final ArchiveTypeConversionSupport<T> _support;

        /**
         * Constructor.
         * @param support
         */
        public Type2StringFunction(@Nonnull final ArchiveTypeConversionSupport<T> support) {
            _support = support;
        }

        @Override
        @CheckForNull
        public String apply(@Nonnull final T from) {
            try {
                return _support.convertToArchiveString(from);
            } catch (final TypeSupportException e) {
                _log.warn("No type conversion to archive string for {}", from.getClass().getName() + " registered.");
                return null;
            }
        }
    }


    @Nonnull
    protected abstract String convertToArchiveString(@Nonnull final T value) throws TypeSupportException;
    @Nonnull
    protected abstract T convertFromArchiveString(@Nonnull final String value) throws TypeSupportException;
    @Nonnull
    protected abstract Collection<T> convertFromArchiveStringToMultiScalar(@Nonnull Class<?> collectionClass,
                                                                           @Nonnull final String values)
                                                                           throws TypeSupportException;
    @Nonnull
    protected abstract T convertFromDouble(@Nonnull final Double value) throws TypeSupportException;

    /**
     * A type support overriding this method with return value <code>Boolean.TRUE</code> has to
     * implement a valid 'convertToDouble' method.
     *
     * @return true if the data type is optimizable by averaging (i.e. convertible to Double)
     */
    @Nonnull
    protected Boolean isOptimizableByAveraging() {
        return Boolean.FALSE;
    }

    /**
     * Has to be overriden for all types that support display ranges in the channel abstraction
     */
    @Nonnull
    // CHECKSTYLE OFF : ParameterNumber
    protected IArchiveChannel createChannel(@Nonnull final ArchiveChannelId id,
                                            @Nonnull final String name,
                                            @Nonnull final String datatype,
                                            @Nonnull final ArchiveChannelGroupId archiveChannelGroupId,
                                            @Nonnull final TimeInstant time,
                                            @Nonnull final IArchiveControlSystem cs,
                                            @SuppressWarnings("unused") @Nonnull final T low,
                                            @SuppressWarnings("unused") @Nonnull final T high) {
        // CHECKSTYLE ON : ParameterNumber
        return new ArchiveChannel(id,
                                  name,
                                  datatype,
                                  archiveChannelGroupId,
                                  time,
                                  cs);
    }

    @Nonnull
    protected String convertFromMultiScalarToArchiveString(@Nonnull final Collection<T> values) throws TypeSupportException {
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
}
