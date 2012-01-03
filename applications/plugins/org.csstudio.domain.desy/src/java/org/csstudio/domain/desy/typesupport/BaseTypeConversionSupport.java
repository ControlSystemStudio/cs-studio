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
package org.csstudio.domain.desy.typesupport;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.epics.pvmanager.TypeSupport;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Type conversion necessary as long as there are these other classes around.
 *
 * @author bknerr
 * @since 17.12.2010
 * @param <T> the support's type
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class statically is accessed, hence the name should be short and descriptive!
 *
 */
public abstract class BaseTypeConversionSupport<T> extends AbstractTypeSupport<T> {
    // CHECKSTYLE ON : AbstractClassName

    private static final List<String> BASIC_TYPE_PACKAGES =
        Lists.newArrayList("java.lang", "java.util", "java.util.concurrent");

    @SuppressWarnings("rawtypes")
    private static Map<String, Class> CLASSNAME2TYPE = Maps.newConcurrentMap();

    private static boolean INSTALLED;

    /**
     * Constructor.
     */
    protected BaseTypeConversionSupport(@Nonnull final Class<T> type) {
        super(type, BaseTypeConversionSupport.class);
    }

    public static void install() {
        if (INSTALLED) {
            return;
        }
        TypeSupport.addTypeSupport(new NumberBaseTypeConversionSupport());
        TypeSupport.addTypeSupport(new StringBaseTypeConversionSupport());

        INSTALLED = true;
    }

    @Nonnull
    public static TimeInstant toTimeInstant(@Nonnull final ITimestamp ts) {
        return TimeInstantBuilder.fromSeconds(ts.seconds()).plusNanosPerSecond(ts.nanoseconds());
    }

    @Nonnull
    public static ITimestamp toTimestamp(@Nonnull final TimeInstant ti) {
        return TimestampFactory.createTimestamp(ti.getSeconds(), ti.getFractalSecondsInNanos());
    }

    /**
     * Tries to create a {@link Class} object for the given dataType string, iteratively
     * over the given array of package names (basic package names are appended
     * @see {@link #BASIC_TYPE_PACKAGES}).
     *
     * Note that the utilized <code>Class.forName(package + class)</code> does only work when a
     * class loader buddy is registered for the passed packages. You have to add in the manifest.mf
     * of the plugin that exports the passed package(s) the line
     * "Eclipse-RegisterBuddy: org.csstudio.domain.desy".
     *
     * @param <T>
     * @param datatype the name of the class
     * @param addPackages the list of additional (non-basic) package names to try first
     * @return a {@link Class} object or <code>null</code>.
     * @throws TypeSupportException if class object creation failed
     */
    @Nonnull
    public static Class<?> createBaseTypeClassFromString(@Nonnull final String datatype,
                                                         @Nonnull final String... addPackages) throws TypeSupportException {
        final String baseType = datatype.trim().replaceFirst("\\s*<(.+)>$", "");

        final Iterable<String> allPackages = Iterables.concat(Arrays.asList(addPackages),
                                                              BASIC_TYPE_PACKAGES);

        Class<?> typeClass = getClassTypeFromCache(baseType, allPackages);
        if (typeClass == null) {
            typeClass = createClassTypeForNameAndUpdateCache(baseType, allPackages);
        }
        if (typeClass == null) {
            throw new TypeSupportException("Class object for base datatype of " + datatype +
                                           " could not be created from packages:\n" +
                                           Iterables.toString(allPackages), null);
        }
        return typeClass;
    }

    /**
     * Checks whether for the given class name and packages a class type has already been created.
     * @param baseType the simple class name as string
     * @param allPackages the candidate packages to check for
     * @return the class object for the class named after package.baseType or <code>null</code>
     */
    @SuppressWarnings("rawtypes")
    @Nonnull
    private static Class getClassTypeFromCache(@Nonnull final String baseType,
                                               @Nonnull final Iterable<String> allPackages) {
        for (final String pkg : allPackages) {
            final String fullClassNameCandidate = pkg + "." + baseType;
            if (CLASSNAME2TYPE.containsKey(fullClassNameCandidate)) {
                return CLASSNAME2TYPE.get(fullClassNameCandidate);
            }
        }
        return null;
    }
    /**
     * Tries to create a class object from the simple (baseType) string and any of packages.
     *
     * @param <T>
     * @param baseType the simple name of the class
     * @param allPackages the candidate packages from which to load to class
     * @return the class object
     */
    @CheckForNull
    private static Class<?> createClassTypeForNameAndUpdateCache(@Nonnull final String baseType,
                                                                 @Nonnull final Iterable<String> allPackages) {
        for (final String pkg : allPackages) {
            try {
                final String fullClassName = pkg + "." + baseType;
                final Class<?> typeClass = Class.forName(fullClassName);
                CLASSNAME2TYPE.put(fullClassName, typeClass);
                return typeClass;
                // CHECKSTYLE OFF: EmptyBlock
            } catch (final ClassNotFoundException e) {
                // Ignore
                // CHECKSTYLE ON: EmptyBlock
            }
        }
        return null;
    }

    /**
     * Parses a generic type name in String form for the first nested element.
     * Typically used to extract to element types of collections...
     * @param fullGenericTypeName the full generic type name as string, e.g. "ArrayList&lt;Foo&lt;...&gt;&gt;"
     * @return the string of the first nested element, e.g. "Foo" from the example above
     * @throws TypeSupportException if the given string was not in a 'generic' form
     */
    @Nonnull
    public static String parseForFirstNestedGenericType(@Nonnull final String fullGenericTypeName) throws TypeSupportException {

        final Pattern p = Pattern.compile("^\\s*[^<> ]+\\s*[<]\\s*([^<> ]+).+\\s*$");
        final Matcher m = p.matcher(fullGenericTypeName);
        if (m.matches()) {
            return m.group(1);
        }
        throw new TypeSupportException("First nested base type of generic expression not found in " + fullGenericTypeName, null);
    }

    /**
     * Checks whether the given String parameter can be loaded as class object for which a toDouble
     * conversion has been registered. Basic packages to load classes from are
     * {@link BaseTypeConversionSupport#BASIC_TYPE_PACKAGES}. Additional packages can be provided.
     *
     * @param <T> the type of the described datatype
     * @param dataType the describing string (a class name)
     * @param additionalPackages the packages from which the class object shall be created.
     * @return true if this dataType is convertible to Double
     * @throws TypeSupportException
     */
    public static boolean isDataTypeConvertibleToDouble(@Nonnull final String dataType,
                                                        @Nonnull final String... additionalPackages) throws TypeSupportException {
        final Class<?> typeClass = createBaseTypeClassFromString(dataType, additionalPackages);
        if (typeClass == null) {
            throw new TypeSupportException("Type is not convertible to " + Double.class.getSimpleName() + "." +
                                           dataType + " could not be loaded as class object from packages.", null);
        }
        try {
            return isDataTypeConvertibleToDouble(typeClass);
        } catch (final TypeSupportException e) {
            throw new TypeSupportException("No type support registered for toDouble conversion of data type " + dataType, e);
        }
    }
    /**
     * Checks whether the given parameter is a type for which a to Double conversion
     * has been registered.
     * @param <T> the type of the described datatype
     * @param type the class type
     * @return true if this class type is convertible to Double
     * @throws TypeSupportException
     */
    public static <T> boolean isDataTypeConvertibleToDouble(@Nonnull final Class<T> type) throws TypeSupportException {
        final BaseTypeConversionSupport<T> support =
            (BaseTypeConversionSupport<T>) findTypeSupportForOrThrowTSE(BaseTypeConversionSupport.class, type);

        return support.isConvertibleToDouble();

    }
    /**
     * To be overridden for types that are convertible to {@link java.lnag.Double}
     * @return true, if so, false otherwise
     */
    protected boolean isConvertibleToDouble() {
        return false;
    }

    /**
     * Tries to convert the given basic value type to Double.
     *
     * @param value the value to be converted
     * @return the conversion result
     * @throws TypeSupportException when conversion failed.
     * @param <V> the basic type of the value(s)
     */
    @Nonnull
    public static <T> Double toDouble(@Nonnull final T value) throws TypeSupportException {
        @SuppressWarnings("unchecked")
        final Class<T> typeClass = (Class<T>) value.getClass();
        final BaseTypeConversionSupport<T> support =
            (BaseTypeConversionSupport<T>) findTypeSupportForOrThrowTSE(BaseTypeConversionSupport.class,
                                                              typeClass);
        return support.convertToDouble(value);
    }


    /**
     * Tries to convert the system variable's data to {@link Double} and returns <code>null</code>
     * if a type support for conversion is not possible or the return value is {@link Double#NaN}.
     * @param <T> the data type
     * @param <V> the system variable type
     * @param sysVar the system variable
     * @return a double value or <code>null</code>
     */
    @CheckForNull
    public static <T, V extends ISystemVariable<T>>
    Double createDoubleFromValueOrNull(@Nonnull final V sysVar) {
        Double newValue = null;
        try {
            newValue = BaseTypeConversionSupport.toDouble(sysVar.getData());
        } catch (final TypeSupportException e) {
            return null; // not convertible. Type support missing.
        }
        if (newValue.equals(Double.NaN)) {
            return null; // not convertible, no data reduction possible
        }
        return newValue;
    }


    /**
     * To be overridden for types that are convertible to {@link java.lnag.Double}
     * @return the corresponding Double value
     * @throws TypeSupportException
     */
    @Nonnull
    protected Double convertToDouble(@Nonnull final T value) throws TypeSupportException {
        throw new TypeSupportException("Type support does not offer to Double convertibility for this type " + value.getClass().getName(), null);
    }


}
