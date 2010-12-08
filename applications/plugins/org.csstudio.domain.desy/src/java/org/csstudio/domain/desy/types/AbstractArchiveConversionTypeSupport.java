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

import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * Archive type conversion support
 *
 * @author bknerr
 * @since 07.12.2010
 * @param <T>
 */
public abstract class AbstractArchiveConversionTypeSupport<T> extends TypeSupport<T> {

    static final Logger LOG =
        CentralLogger.getInstance().getLogger(AbstractArchiveConversionTypeSupport.class);

    private static boolean INSTALLED = false;

    /**
     * Constructor.
     */
    AbstractArchiveConversionTypeSupport() {
        // Don't instantiate outside this class
    }

    @SuppressWarnings("rawtypes")
    public static void install() {
        if (INSTALLED) {
            return;
        }
        TypeSupport.addTypeSupport(Number.class, new AbstractArchiveConversionTypeSupport<Number>() {
            @Override
            @Nonnull
            public String convertToArchiveString(@Nonnull final Number value) throws ConversionTypeSupportException {
                return value.toString();
            }
            @Override
            @Nonnull
            public Double convertToDouble(@Nonnull final Number d) {
                return d.doubleValue();
            }
        });
        TypeSupport.addTypeSupport(Collection.class, new AbstractArchiveConversionTypeSupport<Collection>() {
            @Override
            @SuppressWarnings("unchecked")
            @Nonnull
            public String convertToArchiveString(@Nonnull final Collection values) throws ConversionTypeSupportException {
                final Iterable items = Iterables.transform(values, new Function<Object, String>() {
                    @Override
                    @CheckForNull
                    public String apply(@Nonnull final Object from) {
                        try {
                            return TypeSupport.toArchiveString(from);
                        } catch (final ConversionTypeSupportException e) {
                            LOG.warn("No type conversion to archive string for " + from.getClass().getName() + " registered.");
                            return null;
                        }
                    }
                });
                final String result = "[" + Joiner.on(",").join(items) + "]";
                return result;
            }
            @Override
            @Nonnull
            public Double convertToDouble(@Nonnull final Collection l) throws ConversionTypeSupportException {
                if (l.size() == 1) {
                    final Object value = l.iterator().next();
                    return TypeSupport.toDouble(value);
                }
                return Double.NaN;
            }
        });
        INSTALLED = true;
    }

    @CheckForNull
    public abstract String convertToArchiveString(@Nonnull final T value) throws ConversionTypeSupportException;
    @Nonnull
    public abstract Double convertToDouble(@Nonnull final T value) throws ConversionTypeSupportException;

}
