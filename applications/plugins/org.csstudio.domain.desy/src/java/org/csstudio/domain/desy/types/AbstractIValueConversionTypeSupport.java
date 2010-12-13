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

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.alarm.IAlarm;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.IEnumeratedValue;
import org.csstudio.platform.data.ILongValue;
import org.csstudio.platform.data.IStringValue;
import org.csstudio.platform.data.IValue;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * Type Conversion Support for IValue types
 *
 * @author bknerr
 * @since 02.12.2010
 * @param <R> the basic type of the value(s) of the system variable
 * @param <T> the type of the system variable
 */
public abstract class AbstractIValueConversionTypeSupport<R extends ICssAlarmValueType<?>, T extends IValue> extends TypeSupport<T> {

    private static boolean INSTALLED = false;

    /**
     * Constructor.
     */
    AbstractIValueConversionTypeSupport() {
        // Don't instantiate outside this class
    }

    // CHECKSTYLE OFF: MethodLength
    public static void install() {
    // CHECKSTYLE ON: MethodLength
        if (INSTALLED) {
            return;
        }
        TypeSupport.addTypeSupport(IDoubleValue.class,
                                   new AbstractIValueConversionTypeSupport<ICssAlarmValueType<?>, IDoubleValue>() {
            @CheckForNull
            @Override
            public ICssAlarmValueType<?> convertToCssType(@Nonnull final IDoubleValue value,
                                                          @Nullable final IAlarm alarm,
                                                          @Nonnull final TimeInstant timestamp)  {
                final double[] values = value.getValues();
                if (values.length == 0) {
                    return null;
                }
                if (values.length == 1) {
                    return new CssAlarmValueType<Double>(Double.valueOf(values[0]),
                                                                 alarm,
                                                                 timestamp);
                }
                return new CssAlarmValueType<List<Double>>(Lists.newArrayList(Doubles.asList(values)),
                                                                   alarm,
                                                                   timestamp);
            }
        });
        TypeSupport.addTypeSupport(IEnumeratedValue.class,
                                   new AbstractIValueConversionTypeSupport<ICssAlarmValueType<?>, IEnumeratedValue>() {
                @CheckForNull
                @Override
                public ICssAlarmValueType<?> convertToCssType(@Nonnull final IEnumeratedValue value,
                                                              @Nullable final IAlarm alarm,
                                                              @Nonnull final TimeInstant timestamp)  {
                    final int[] values = value.getValues();
                    if (values.length == 0) {
                        return null;
                    }
                    if (values.length == 1) {
                        return new CssAlarmValueType<Integer>(Integer.valueOf(values[0]),
                                alarm,
                                timestamp);
                    }
                    return new CssAlarmValueType<List<Integer>>(Lists.newArrayList(Ints.asList(values)),
                            alarm,
                            timestamp);
                }
        });
        TypeSupport.addTypeSupport(ILongValue.class,
                                   new AbstractIValueConversionTypeSupport<ICssAlarmValueType<?>, ILongValue>() {
            @CheckForNull
            @Override
            public ICssAlarmValueType<?> convertToCssType(@Nonnull final ILongValue value,
                                                          @Nullable final IAlarm alarm,
                                                          @Nonnull final TimeInstant timestamp)  {
                final long[] values = value.getValues();
                if (values.length == 0) {
                    return null;
                }
                if (values.length == 1) {
                    return new CssAlarmValueType<Long>(Long.valueOf(values[0]),
                                                               alarm,
                                                               timestamp);
                }
                return new CssAlarmValueType<List<Long>>(Lists.newArrayList(Longs.asList(values)),
                                                                 alarm,
                                                                 timestamp);
            }
        });
        TypeSupport.addTypeSupport(IStringValue.class,
                                   new AbstractIValueConversionTypeSupport<ICssAlarmValueType<?>, IStringValue>() {
            @Override
            @CheckForNull
            public ICssAlarmValueType<?> convertToCssType(@Nonnull final IStringValue value,
                                                          @Nullable final IAlarm alarm,
                                                          @Nonnull final TimeInstant timestamp)  {
                final String[] values = value.getValues();
                if (values.length == 0) {
                    return null;
                }
                if (values.length == 1) {
                    return new CssAlarmValueType<String>(values[0],
                                                                 alarm,
                                                                 timestamp);
                }
                return new CssAlarmValueType<List<String>>(Lists.newArrayList(values),
                                                                   alarm,
                                                                   timestamp);
            }
        });
        INSTALLED = true;
    }

    @CheckForNull
    public abstract R convertToCssType(@Nonnull final T value,
                                       @Nullable final IAlarm alarm,
                                       @Nonnull final TimeInstant timestamp) throws ConversionTypeSupportException;

}
