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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;

/**
 * Type conversion necessary as long as there are these other classes around.
 *
 * @author bknerr
 * @since 17.12.2010
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class statically is accessed, hence the name should be short and descriptive!
 *
 */
public abstract class BaseTypeConversionSupport {
    // CHECKSTYLE ON : AbstractClassName
    /**
     * Constructor.
     */
    private BaseTypeConversionSupport() {
        // Empty
    }

    @Nonnull
    public static TimeInstant toTimeInstant(@Nonnull final ITimestamp ts) {
        return TimeInstantBuilder.buildFromSeconds(ts.seconds()).plusNanosPerSecond(ts.nanoseconds());
    }

    @Nonnull
    public static ITimestamp toTimestamp(@Nonnull final TimeInstant ti) {
        return TimestampFactory.createTimestamp(ti.getSeconds(), ti.getFractalSecondsInNanos());
    }

    /**
     * Tries to create a {@link Class<?>} object for the given dataType string, iteratively
     * over the given array of package names.
     *
     * Note that the utilized <code>Class.forName(package + class)</code> does only work when a
     * class loader buddy is registered for this package. Unless it is a basic package like
     * "java.lang" or "java.util" you have to add in the manifest.mf of the plugin that exports the
     * passed package(s) the line "Eclipse-RegisterBuddy: org.csstudio.domain.desy".
     *
     * This method does not propagate a ClassNotFoundException but return <code>null</code>, if
     * class creation is not possible.
     *
     * @param <T>
     * @param datatype the name of the class
     * @param packages the array of package names to try
     * @return a {@link Class} object or <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    @CheckForNull
    public static <T> Class<T> createTypeClassFromString(@Nonnull final String datatype,
                                                         @Nonnull final String... packages) {
        Class<T> typeClass = null;
        for (final String pkg : packages) {
            try {
                typeClass = (Class<T>) Class.forName(pkg + "." + datatype);
                break;
                // CHECKSTYLE OFF: EmptyBlock
            } catch (final ClassNotFoundException e) {
                // Ignore
                // CHECKSTYLE ON: EmptyBlock
            }
        }
        return typeClass;
    }

    /**
     * Tries to create a {@link Class} object for the element type for a generic {@link java.util.Collection},
     * such as "Set&lt;Byte&gt;" as {@param datatype} shall return Class&lt;Byte&gt;.<br/>
     * Recognized patterns for collection describing strings are Collection<*>, List<*>, Set<*>, and
     * Vector<*>
     *
     * @param <T>
     * @param datatype the string for the generic collection type, e.g. List&lt;Double&gt;.
     * @param packages the packages to try for the element type, e.g. typically "java.lang".
     * @return the class object or <code>null</code>
     */
    @CheckForNull
    public static <T> Class<T> createTypeClassFromMultiScalarString(@Nonnull final String datatype,
                                                                    @Nonnull final String... packages) {
        final Pattern p = Pattern.compile("^(Collection|List|Set|Vector)<(.+)>$");
        final Matcher m = p.matcher(datatype);
        if (m.matches()) {
            final String elementType = m.group(2); // e.g. Byte from List<Byte>
            return createTypeClassFromString(elementType, packages);
        }
        return null;
    }


}
