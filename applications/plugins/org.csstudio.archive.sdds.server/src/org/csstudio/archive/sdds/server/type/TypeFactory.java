
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.archive.sdds.server.type;

import java.util.Hashtable;

import javax.annotation.Nonnull;

/**
 * TODO (mmoeller) :
 *
 * @author mmoeller
 * @since 16.11.2011
 */
public final class TypeFactory {

    private static final Hashtable<Class<?>, ITypeConverter> CONVERT_TABLE;

    static {
        CONVERT_TABLE = new Hashtable<Class<?>, ITypeConverter>();
        CONVERT_TABLE.put(Integer.class, new IntegerConverter());
        CONVERT_TABLE.put(Long.class, new LongConverter());
        CONVERT_TABLE.put(Float.class, new FloatConverter());
        CONVERT_TABLE.put(Double.class, new DoubleConverter());
    }

    private TypeFactory() {
        // Avoid instantiation of utility class
    }

    @Nonnull
    public static Long toLong(@Nonnull final Object o) throws TypeNotSupportedException {

        final ITypeConverter converter = CONVERT_TABLE.get(o.getClass());
        if (converter == null) {
            throw new TypeNotSupportedException("Class '" + o.getClass().getName() + "' is not supported.");
        }

        return converter.toLong(o);
    }

    @Nonnull
    public static Integer toInteger(@Nonnull final Object o) throws TypeNotSupportedException {

        final ITypeConverter converter = CONVERT_TABLE.get(o.getClass());
        if (converter == null) {
            throw new TypeNotSupportedException("Class '" + o.getClass().getName() + "' is not supported.");
        }

        return converter.toInteger(o);
    }

    @Nonnull
    public static Float toFloat(@Nonnull final Object o) throws TypeNotSupportedException {

        final ITypeConverter converter = CONVERT_TABLE.get(o.getClass());
        if (converter == null) {
            throw new TypeNotSupportedException("Class '" + o.getClass().getName() + "' is not supported.");
        }

        return converter.toFloat(o);
    }

    @Nonnull
    public static Double toDouble(@Nonnull final Object o) throws TypeNotSupportedException {

        final ITypeConverter converter = CONVERT_TABLE.get(o.getClass());
        if (converter == null) {
            throw new TypeNotSupportedException("Class '" + o.getClass().getName() + "' is not supported.");
        }

        return converter.toDouble(o);
    }

    @Nonnull
    public static String toString(@Nonnull final Object o) throws TypeNotSupportedException {

        final ITypeConverter converter = CONVERT_TABLE.get(o.getClass());
        if (converter == null) {
            throw new TypeNotSupportedException("Class '" + o.getClass().getName() + "' is not supported.");
        }

        return converter.toString(o);
    }
}
