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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.service.channel.ArchiveChannel;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.ArchiveLimitsChannel;
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
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * Archive type conversion for system variables.
 *
 * @author bknerr
 * @since 15.12.2010
 * @param <T> the supported class type
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class is accessed statically, hence the name should be short and descriptive!
 */
public abstract class ArchiveTypeConversionSupport<T extends Serializable> extends AbstractTypeSupport<T> {
    // CHECKSTYLE ON : AbstractClassName
    protected static final String ARCHIVE_COLLECTION_ELEM_SEP = "\\,";
    protected static final String ARCHIVE_COLLECTION_PREFIX = "[";
    protected static final String ARCHIVE_COLLECTION_SUFFIX = "]";
    protected static final String ARCHIVE_TUPLE_PREFIX = "(";
    protected static final String ARCHIVE_TUPLE_SUFFIX = ")";
    protected static final String ARCHIVE_TUPLE_SEP = "\\|";

    protected static final String ARCHIVE_NULL_ENTRY = "<null>";

    private static final String[] ADDITIONAL_TYPE_PACKAGES =
        new String[]{
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

        TypeSupport.addTypeSupport(new ShortArchiveTypeConversionSupport());
        TypeSupport.addTypeSupport(new DoubleArchiveTypeConversionSupport());
        TypeSupport.addTypeSupport(new FloatArchiveTypeConversionSupport());
        TypeSupport.addTypeSupport(new IntegerArchiveTypeConversionSupport());
        TypeSupport.addTypeSupport(new LongArchiveTypeConversionSupport());
        TypeSupport.addTypeSupport(new StringArchiveTypeConversionSupport());
        TypeSupport.addTypeSupport(new ByteArchiveTypeConversionSupport());
        TypeSupport.addTypeSupport(new EnumArchiveTypeConversionSupport());

        CollectionTypeConversionSupport.install();

        INSTALLED = true;
    }

    /**
     * Tries to convert the value data/datum into a string representation suitable for archiving purposes.
     * @param valueData
     * @return
     * @throws TypeSupportException
     */
    @Nonnull
    public static <T extends Serializable> String toArchiveString(@Nonnull final T value) throws TypeSupportException {
        @SuppressWarnings("unchecked")
        final Class<T> typeClass = (Class<T>) value.getClass();
        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) findTypeSupportForOrThrowTSE(ArchiveTypeConversionSupport.class,
                                                                           typeClass);
        return support.convertToArchiveString(value);
    }

    @Nonnull
    public static <T extends Serializable> byte[] toByteArray(@Nonnull final T values) throws TypeSupportException {

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            final ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(values);
        } catch (final IOException e) {
            throw new TypeSupportException("To byte array conversion failed on serialisation.", e);
        }
        return bos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T extends Serializable> T fromByteArray(@Nonnull final byte[] bytes) throws TypeSupportException {
        final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            final ObjectInput in = new ObjectInputStream(bis);
            return (T) in.readObject();
        } catch (final ClassNotFoundException e) {
            throw new TypeSupportException("Deserialization failed.", e);
        } catch (final IOException e) {
            throw new TypeSupportException("Deserialization failed.", e);
        }
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
    public static <T extends Serializable> T fromArchiveString(@Nonnull final Class<T> typeClass,
                                                               @Nonnull final String value) throws TypeSupportException {
        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) findTypeSupportForOrThrowTSE(ArchiveTypeConversionSupport.class,
                                                                           typeClass);
        return support.convertFromArchiveString(value);
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Nonnull
    public static <T extends Serializable> T fromArchiveString(@Nonnull final String datatype,
                                                               @Nonnull final String value) throws TypeSupportException {
        final Class<T> typeClass = (Class<T>) createTypeClassFromArchiveString(datatype);
        if (!Collection.class.isAssignableFrom(typeClass)) {
            return fromArchiveString(typeClass, value);
        }

        final String elemType =
            BaseTypeConversionSupport.parseForFirstNestedGenericType(datatype);
        final Class<T> elemClass = (Class<T>) createTypeClassFromArchiveString(elemType);
        return (T) fromArchiveString((Class) typeClass, (Class) elemClass, value);
    }

    /**
     * Tries to convert the archive string value data (supposed to represent a serializable collection)
     * into a typed collection. Whether the collection shall support a {@link java.util.HashSet},
     * {@link java.util.ArrayList}, or any other serializable subtype of collection has to be handled by the
     * invoker.
     *
     *
     * @param collectionClass the type of the serializable target collection
     * @param elemClass the type of the target elements in the collection
     * @param values the string representation for the values
     * @return the typed serializable collection
     * @throws TypeSupportException if no support could be found for elemClass type or all or some
     * elements could not be converted from the values string.
     */
    @Nonnull
    public static <T extends Serializable, C extends Collection<T> & Serializable>
    C fromArchiveString(@Nonnull final Class<C> collectionClass,
                        @Nonnull final Class<T> elemClass,
                        @Nonnull final String values) throws TypeSupportException {
        final ArchiveTypeConversionSupport<T> elemSupport =
            (ArchiveTypeConversionSupport<T>) findTypeSupportForOrThrowTSE(ArchiveTypeConversionSupport.class,
                                                                           elemClass);
        final String releasedStr = collectionRelease(values);
        if (releasedStr == null) {
            throw new TypeSupportException("Values representation does not adhere to multi scalar start and end delimiters.", null);
        }
        final Iterable<String> strings = Splitter.on(ARCHIVE_COLLECTION_ELEM_SEP).split(releasedStr);

        final Iterable<T> typedValues = Iterables.filter(Iterables.transform(strings, new String2TypeFunction<T>(elemSupport)),
                                                         Predicates.<T>notNull());
        checkInputVsOutputSize(strings, typedValues);

        return createCollectionFromIterable(collectionClass, typedValues);
    }

    @Nonnull
    protected static <T extends Serializable, C extends Collection<T> & Serializable>
    C createCollectionFromIterable(@Nonnull final Class<C> collectionClass,
                                   @Nonnull final Iterable<T> values) throws TypeSupportException {
        try {
            final C collection = collectionClass.newInstance();
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

    /**
     * Returns the type class that can be loaded from this datatype simple name string.
     * Using the {@link ArchiveTypeConversionSupport#ADDITIONAL_TYPE_PACKAGES}.
     * @param datatype the simple class name
     * @return the class type for this simple name
     * @throws TypeSupportException
     */
    @SuppressWarnings("unchecked")
    @CheckForNull
    public static Class<Serializable> createTypeClassFromArchiveString(@CheckForNull final String datatype) throws TypeSupportException {
        if (Strings.isNullOrEmpty(datatype)) {
            return null;
        }
        return (Class<Serializable>) BaseTypeConversionSupport.createBaseTypeClassFromString(datatype, ADDITIONAL_TYPE_PACKAGES);
    }

    @Nonnull
    public static <T extends Serializable> T fromDouble(@Nonnull final Class<T> typeClass,
                                                        @Nonnull final Double value) throws TypeSupportException {
        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) findTypeSupportForOrThrowTSE(ArchiveTypeConversionSupport.class,
                                                                           typeClass);
        return support.convertFromDouble(value);
    }

    @Nonnull
    public static Boolean isDataTypeSerializableCollection(@Nonnull final String datatype) throws TypeSupportException {
        final Class<?> typeClass = createTypeClassFromArchiveString(datatype);
        if (typeClass == null) {
            return false;
        }
        return isDataTypeSerializableCollection(typeClass);
    }
    @Nonnull
    public static Boolean isDataTypeSerializableCollection(@Nonnull final Class<?> typeClass) {
        return Collection.class.isAssignableFrom(typeClass) && Serializable.class.isAssignableFrom(typeClass);
    }

    /**
     * Returns whether the given data type as contained in the archive db is optimizable by averaging.
     * This datatype has to have a registered type support with convertibility to Double type.
     * @return true if optimizable by averaging, false otherwise
     * @throws TypeSupportException
     */
    @Nonnull
    public static Boolean isDataTypeOptimizable(@Nonnull final String dataType) throws TypeSupportException {
        final Class<?> typeClass = createTypeClassFromArchiveString(dataType);
        if (typeClass == null) {
            throw new TypeSupportException("Class object for data type " + dataType +
                                           " could not be loaded from packages " +
                                           Joiner.on(", ").join(ADDITIONAL_TYPE_PACKAGES), null);
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

    // CHECKSTYLE OFF : ParameterNumber
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Nonnull
    public static <T extends Serializable>
    IArchiveChannel createArchiveChannel(@Nonnull final ArchiveChannelId id,
                                         @Nonnull final String name,
                                         @Nullable final String datatype,
                                         @Nonnull final ArchiveChannelGroupId archiveChannelGroupId,
                                         @Nullable final TimeInstant time,
                                         @Nonnull final IArchiveControlSystem cs,
                                         final boolean enabled,
                                         @CheckForNull final String low,
                                         @CheckForNull final String high) throws TypeSupportException {
        // CHECKSTYLE ON : ParameterNumber

        if (datatype != null) {
            final Class<T> clazz = (Class<T>) createTypeClassFromArchiveString(datatype);
            if (!ArchiveTypeConversionSupport.isDataTypeSerializableCollection(clazz) &&
                !Strings.isNullOrEmpty(low) &&
                !Strings.isNullOrEmpty(high)) {
                return new ArchiveLimitsChannel(id,
                                                name,
                                                datatype,
                                                archiveChannelGroupId,
                                                time,
                                                cs,
                                                enabled,
                                                (Comparable) fromArchiveString(clazz, low),
                                                (Comparable) fromArchiveString(clazz, high));
            }
        }
        return new ArchiveChannel(id,
                                  name,
                                  datatype,
                                  archiveChannelGroupId,
                                  time,
                                  cs,
                                  enabled);
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
    protected final class Type2StringFunction implements Function<T, String> {
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
                _log.warn("No type conversion to archive string for {} registered.", from.getClass().getName());
                return null;
            }
        }
    }
    /**
     * Archive string to type converter function for guava collection transforming.
     *
     * @author bknerr
     * @since 22.12.2010
     */
    protected static final class String2TypeFunction<T extends Serializable> implements Function<String, T> {
        private static final Logger S2T_LOG =
            LoggerFactory.getLogger(ArchiveTypeConversionSupport.String2TypeFunction.class);
        private final ArchiveTypeConversionSupport<T> _support;

        /**
         * Constructor.
         * @param support
         */
        public String2TypeFunction(@Nonnull final ArchiveTypeConversionSupport<T> support) {
            _support = support;
        }

        @Override
        @CheckForNull
        public T apply(@Nonnull final String from) {
            try {
                return _support.convertFromArchiveString(from);
            } catch (final TypeSupportException e) {
                S2T_LOG.warn("Type conversion error in {}", this.getClass().getName());
                return null;
            }
        }
    }


    @Nonnull
    protected abstract String convertToArchiveString(@Nonnull final T value) throws TypeSupportException;
    @Nonnull
    protected abstract T convertFromArchiveString(@Nonnull final String value) throws TypeSupportException;
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

    @SuppressWarnings("rawtypes")
    @Nonnull
    public static String createArchiveTypeStringFromData(@Nonnull final Object data) {

        final Class<? extends Object> clazz = data.getClass();
        if (Collection.class.isAssignableFrom(clazz) && !((Collection) data).isEmpty()) {
            return clazz.getSimpleName() +
                    "<" +
                   createArchiveTypeStringFromData(((Collection) data).iterator().next()) +
                   ">";
        } else {
            return clazz.getSimpleName();
        }
    }



}
