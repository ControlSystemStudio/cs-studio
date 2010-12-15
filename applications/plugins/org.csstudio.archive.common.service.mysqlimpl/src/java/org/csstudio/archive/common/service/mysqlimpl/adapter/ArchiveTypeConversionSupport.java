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
import org.csstudio.domain.desy.types.AbstractTypeSupport;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.logging.CentralLogger;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 15.12.2010
 * @param <T> the supported class type
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class statically is accessed, hence the name should be short and descriptive!

 */
public abstract class ArchiveTypeConversionSupport<T> extends AbstractTypeSupport<T> {
    // CHECKSTYLE ON : AbstractClassName

    protected static final Logger LOG =
        CentralLogger.getInstance().getLogger(ArchiveTypeConversionSupport.class);

    protected static final String ARCHIVE_COLLECTION_ELEM_SEP = "\\,";

    private static boolean INSTALLED = false;

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
        AbstractTypeSupport.addTypeSupport(String.class, new StringArchiveTypeConversionSupport());
        AbstractTypeSupport.addTypeSupport(Byte.class, new ByteArchiveTypeConversionSupport());

        INSTALLED = true;
    }

    /**
     * Tries to convert the value data/datum into a string representation suitable for archiving purposes.
     * @param valueData
     * @return
     * @throws TypeSupportException
     */
    @CheckForNull
    public static <T> String toArchiveString(@Nonnull final T value) throws TypeSupportException {
        @SuppressWarnings("unchecked")
        final Class<T> typeClass = (Class<T>) value.getClass();
        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) cachedTypeSupportFor(typeClass);
        if (support == null) {
            throw new TypeSupportException("No conversion type support registered.", null);
        }
        return support.convertScalarToArchiveString(value);
    }

    /**
     * Tries to convert the value data/datum into a string representation suitable for archiving purposes.
     * @param valueData
     * @return
     * @throws TypeSupportException
     */
    @CheckForNull
    public static <T> String toArchiveString(@Nonnull final Collection<T> values) throws TypeSupportException {
        if (values.isEmpty()) {
            return "";
        }
        @SuppressWarnings("unchecked")
        final Class<T> typeClass = (Class<T>) values.iterator().next().getClass();
        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) cachedTypeSupportFor(typeClass);
        if (support == null) {
            throw new TypeSupportException("No conversion type support registered.", null);
        }
        return support.convertMultiScalarToArchiveString(values);
    }

    /**
     * Tries to convert the archive string value data/datum into a the correct type representation.
     * @param the type of the value
     * @param valueData the string representation of the value
     * @return
     * @throws TypeSupportException
     */
    @CheckForNull
    public static <T> T fromScalarArchiveString(final Class<T> typeClass, @Nonnull final String value) throws TypeSupportException {
        final ArchiveTypeConversionSupport<T> support =
            (ArchiveTypeConversionSupport<T>) cachedTypeSupportFor(typeClass);
        if (support == null) {
            throw new TypeSupportException("No conversion type support registered.", null);
        }
        return support.convertScalarFromArchiveString(value);
    }

    /**
     * Tries to convert the archive string value data (supposed to represent an iterable)
     * into a typed collection. Whether the collection shall support a {@link java.util.Set}, {@link List}, or
     * any other subtype of collection has to be handled by the invoker.
     *
     * @param elemClass the type of the elements for which the type support has to exist
     * @param values the string representation for the values
     * @return the Collection
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


    @CheckForNull
    public abstract String convertScalarToArchiveString(@Nonnull final T value) throws TypeSupportException;
    @CheckForNull
    public abstract T convertScalarFromArchiveString(@Nonnull final String value);

    @CheckForNull
    public String convertMultiScalarToArchiveString(@Nonnull final Iterable<T> values) throws TypeSupportException {
      final Iterable<String> items = Iterables.transform(values, new Function<T, String>() {
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
      });
      if (Iterables.size(values) != Iterables.size(items)) {
          throw new TypeSupportException("Number of converted elements (" + Iterables.size(items) +
                                                   " does not match the number of passed elements (" + Iterables.size(values) + "!", null);
      }
      final String result = Joiner.on(ARCHIVE_COLLECTION_ELEM_SEP).join(items);
      return result;
    }

    @CheckForNull
    public abstract Collection<T> convertMultiScalarFromArchiveString(@Nonnull final String values) throws TypeSupportException;
    @CheckForNull
    public abstract Double convertToDouble(@Nonnull final T value) throws TypeSupportException;

}
