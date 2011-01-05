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
package org.csstudio.domain.desy.epics.types;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.AbstractTypeSupport;
import org.csstudio.domain.desy.types.BaseTypeConversionSupport;
import org.csstudio.domain.desy.types.ICssAlarmValueType;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * And more conversion support, now from CssValues to IValues.
 *
 * @author bknerr
 * @since 22.12.2010
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class statically is accessed, hence the name should be short and descriptive!
 */
public abstract class EpicsCssValueTypeSupport<T> extends AbstractTypeSupport<T> {
// CHECKSTYLE ON : AbstractClassName

    protected static Map<Class<?>, AbstractTypeSupport<?>> TYPE_SUPPORTS =
        Maps.newHashMap();
    protected static Map<Class<?>, AbstractTypeSupport<?>> CALC_TYPE_SUPPORTS =
        new ConcurrentHashMap<Class<?>, AbstractTypeSupport<?>>();

    /**
     * @author bknerr
     * @since 22.12.2010
     */
    private static final class CssStringValueTypeSupport extends EpicsCssValueTypeSupport<String> {
        /**
         * Constructor.
         */
        public CssStringValueTypeSupport() {
            // TODO Auto-generated constructor stub
        }

        @Override
        @Nonnull
        protected IValue convertToIValue(@Nonnull final String data,
                                         @Nonnull final EpicsAlarm alarm,
                                         @Nonnull final TimeInstant timestamp) {
            return ValueFactory.createStringValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                  EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                  alarm.getStatus().toString(),
                                                  null,
                                                  new String[] {data});
        }

        @Override
        @Nonnull
        protected IValue convertCollectionToIValue(@Nonnull final Collection<String> data,
                                                   @Nonnull final EpicsAlarm alarm,
                                                   @Nonnull final TimeInstant timestamp) throws TypeSupportException {
            return ValueFactory.createStringValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                  EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                  alarm.getStatus().toString(),
                                                  null,
                                                  data.toArray(new String[]{}));
        }
    }

    /**
     * @author bknerr
     * @since 22.12.2010
     */
    private static final class CssCollectionValueTypeSupport extends EpicsCssValueTypeSupport<Collection> {
        /**
         * Constructor.
         */
        public CssCollectionValueTypeSupport() {
            // TODO Auto-generated constructor stub
        }

        @SuppressWarnings("unchecked")
        @Override
        @Nonnull
        protected IValue convertToIValue(@Nonnull final Collection data,
                                         @Nonnull final EpicsAlarm alarm,
                                         @Nonnull final TimeInstant timestamp) throws TypeSupportException {
            if (data.isEmpty()) {
                throw new TypeSupportException("Collection of data is empty. Type cannot be determined.", null);
            }

            return toIValue(data.iterator().next().getClass(),
                            data,
                            alarm,
                            timestamp);
        }

        @Override
        @Nonnull
        protected IValue convertCollectionToIValue(@Nonnull final Collection<Collection> data,
                                                   @Nonnull final EpicsAlarm alarm,
                                                   @Nonnull final TimeInstant timestamp) throws TypeSupportException {
            throw new TypeSupportException("This method should not be invoked on itself", null);
        }
    }


    /**
     * @author bknerr
     * @since 22.12.2010
     */
    private static final class CssByteValueTypeSupport extends EpicsCssValueTypeSupport<Byte> {
        /**
         * Constructor.
         */
        public CssByteValueTypeSupport() {
            // TODO Auto-generated constructor stub
        }

        @Override
        @Nonnull
        protected IValue convertToIValue(@Nonnull final Byte data,
                                         @Nonnull final EpicsAlarm alarm,
                                         @Nonnull final TimeInstant timestamp) {
            return ValueFactory.createLongValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                alarm.getStatus().toString(),
                                                null,
                                                null,
                                                new long[]{ data.longValue() });
        }

        @Override
        @Nonnull
        protected IValue convertCollectionToIValue(@Nonnull final Collection<Byte> data,
                                                   @Nonnull final EpicsAlarm alarm,
                                                   @Nonnull final TimeInstant timestamp) throws TypeSupportException {
            final Collection<Long> longs =
                Collections2.transform(data,
                                       new Function<Byte, Long> () {
                    @Override
                    public Long apply(@Nonnull final Byte from) {
                        return Long.valueOf(from);
                    }
                });
            return ValueFactory.createLongValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                  EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                  alarm.getStatus().toString(),
                                                  null,
                                                  null,
                                                  Longs.toArray(longs));
        }
    }

    /**
     * @author bknerr
     * @since 22.12.2010
     */
    private static final class CssLongValueTypeSupport extends EpicsCssValueTypeSupport<Long> {
        /**
         * Constructor.
         */
        public CssLongValueTypeSupport() {
            // TODO Auto-generated constructor stub
        }

        @Override
        @Nonnull
        protected IValue convertToIValue(@Nonnull final Long data,
                                         @Nonnull final EpicsAlarm alarm,
                                         @Nonnull final TimeInstant timestamp) {
            return ValueFactory.createLongValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                alarm.getStatus().toString(),
                                                null,
                                                null,
                                                new long[] {data.longValue()});
        }

        @Override
        @Nonnull
        protected IValue convertCollectionToIValue(@Nonnull final Collection<Long> data,
                                                   @Nonnull final EpicsAlarm alarm,
                                                   @Nonnull final TimeInstant timestamp) {
            return ValueFactory.createLongValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                alarm.getStatus().toString(),
                                                null,
                                                null,
                                                Longs.toArray(data));
        }
    }

    /**
     * @author bknerr
     * @since 22.12.2010
     */
    private static final class CssIntegerValueTypeSupport extends EpicsCssValueTypeSupport<Integer> {
        /**
         * Constructor.
         */
        public CssIntegerValueTypeSupport() {
            // TODO Auto-generated constructor stub
        }

        @Override
        @Nonnull
        protected IValue convertToIValue(@Nonnull final Integer data,
                                         @Nonnull final EpicsAlarm alarm,
                                         @Nonnull final TimeInstant timestamp) {
            return ValueFactory.createLongValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                alarm.getStatus().toString(),
                                                null,
                                                null,
                                                new long[] {data.longValue()});
        }

        @Override
        @Nonnull
        protected IValue convertCollectionToIValue(@Nonnull final Collection<Integer> data,
                                                   @Nonnull final EpicsAlarm alarm,
                                                   @Nonnull final TimeInstant timestamp) {
            final Collection<Long> longs =
                Collections2.transform(data,
                                       new Function<Integer, Long> () {
                                           @Override
                                           public Long apply(@Nonnull final Integer from) {
                                               return Long.valueOf(from);
                                           }
                                       });
            return ValueFactory.createLongValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                alarm.getStatus().toString(),
                                                null,
                                                null,
                                                Longs.toArray(longs));
        }
    }

    /**
     * @author bknerr
     * @since 22.12.2010
     */
    private static final class CssDoubleValueTypeSupport extends EpicsCssValueTypeSupport<Double> {
        /**
         * Constructor.
         */
        public CssDoubleValueTypeSupport() {
            // TODO Auto-generated constructor stub
        }

        @Override
        @Nonnull
        protected IValue convertToIValue(@Nonnull final Double data,
                                         @Nonnull final EpicsAlarm alarm,
                                         @Nonnull final TimeInstant timestamp) {
            return ValueFactory.createDoubleValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                  EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                  alarm.getStatus().toString(),
                                                  null,
                                                  null,
                                                  new double[] {data.doubleValue()});
        }

        @Override
        @Nonnull
        protected IValue convertCollectionToIValue(@Nonnull final Collection<Double> data,
                                                   @Nonnull final EpicsAlarm alarm,
                                                   @Nonnull final TimeInstant timestamp) {
            return ValueFactory.createDoubleValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                  EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                  alarm.getStatus().toString(),
                                                  null,
                                                  null,
                                                  Doubles.toArray(data));
        }
    }
    private static final class CssFloatValueTypeSupport extends EpicsCssValueTypeSupport<Float> {
        /**
         * Constructor.
         */
        public CssFloatValueTypeSupport() {
            // TODO Auto-generated constructor stub
        }

        @Override
        @Nonnull
        protected IValue convertToIValue(@Nonnull final Float data,
                                         @Nonnull final EpicsAlarm alarm,
                                         @Nonnull final TimeInstant timestamp) {
            return ValueFactory.createDoubleValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                  EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                  alarm.getStatus().toString(),
                                                  null,
                                                  null,
                                                  new double[] {data.doubleValue()});
        }

        @Override
        @Nonnull
        protected IValue convertCollectionToIValue(@Nonnull final Collection<Float> data,
                                                   @Nonnull final EpicsAlarm alarm,
                                                   @Nonnull final TimeInstant timestamp) {
            final Collection<Double> doubles =
                Collections2.transform(data,
                                       new Function<Float, Double> () {
                                           @Override
                                           public Double apply(@Nonnull final Float from) {
                                               return Double.valueOf(from);
                                           }
                                       });
            return ValueFactory.createDoubleValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                  EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                  alarm.getStatus().toString(),
                                                  null,
                                                  null,
                                                  Doubles.toArray(doubles));
        }
    }
    private static final class CssEpicsEnumValueTypeSupport extends EpicsCssValueTypeSupport<EpicsEnumTriple> {
        /**
         * Constructor.
         */
        public CssEpicsEnumValueTypeSupport() {
            // TODO Auto-generated constructor stub
        }

        @Override
        @Nonnull
        protected IValue convertToIValue(@Nonnull final EpicsEnumTriple data,
                                         @Nonnull final EpicsAlarm alarm,
                                         @Nonnull final TimeInstant timestamp) {
            return ValueFactory.createEnumeratedValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                      EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                      alarm.getStatus().toString(),
                                                      null,
                                                      null,
                                                      new int[] {data.getIndex().intValue()});
        }

        @Override
        @Nonnull
        protected IValue convertCollectionToIValue(@Nonnull final Collection<EpicsEnumTriple> data,
                                                   @Nonnull final EpicsAlarm alarm,
                                                   @Nonnull final TimeInstant timestamp) {
            final Collection<Integer> ints =
                Collections2.transform(data,
                                       new Function<EpicsEnumTriple, Integer> () {
                    @Override
                    public Integer apply(@Nonnull final EpicsEnumTriple from) {
                        return from.getIndex();
                    }
                });
            return ValueFactory.createEnumeratedValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                      EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                      alarm.getStatus().toString(),
                                                      null,
                                                      null,
                                                      Ints.toArray(ints));
        }
    }


    private static boolean INSTALLED = false;

    /**
     * Constructor.
     */
    EpicsCssValueTypeSupport() {
     // Don't instantiate outside this class
    }

    public static void install() {
        if (INSTALLED) {
            return;
        }
        AbstractTypeSupport.addTypeSupport(Double.class, new CssDoubleValueTypeSupport(), TYPE_SUPPORTS, CALC_TYPE_SUPPORTS);
        AbstractTypeSupport.addTypeSupport(Float.class, new CssFloatValueTypeSupport(), TYPE_SUPPORTS, CALC_TYPE_SUPPORTS);
        AbstractTypeSupport.addTypeSupport(Long.class, new CssLongValueTypeSupport(), TYPE_SUPPORTS, CALC_TYPE_SUPPORTS);
        AbstractTypeSupport.addTypeSupport(Integer.class, new CssIntegerValueTypeSupport(), TYPE_SUPPORTS, CALC_TYPE_SUPPORTS);
        AbstractTypeSupport.addTypeSupport(String.class, new CssStringValueTypeSupport(), TYPE_SUPPORTS, CALC_TYPE_SUPPORTS);
        AbstractTypeSupport.addTypeSupport(Byte.class, new CssByteValueTypeSupport(), TYPE_SUPPORTS, CALC_TYPE_SUPPORTS);

        AbstractTypeSupport.addTypeSupport(EpicsEnumTriple.class, new CssEpicsEnumValueTypeSupport(), TYPE_SUPPORTS, CALC_TYPE_SUPPORTS);

        AbstractTypeSupport.addTypeSupport(Collection.class, new CssCollectionValueTypeSupport(), TYPE_SUPPORTS, CALC_TYPE_SUPPORTS);

        INSTALLED = true;
    }


    @CheckForNull
    public static <T> IValue toIValue(@Nonnull final ICssAlarmValueType<T> cssValue) throws TypeSupportException {
        final T valueData = cssValue.getValueData();
        @SuppressWarnings("unchecked")
        final Class<T> typeClass = (Class<T>) valueData.getClass();
        final EpicsCssValueTypeSupport<T> support =
            (EpicsCssValueTypeSupport<T>) cachedTypeSupportFor(typeClass, TYPE_SUPPORTS, CALC_TYPE_SUPPORTS);
        if (support == null) {
            throw new TypeSupportException("No conversion type support registered.", null);
        }
        // TODO (bknerr) : This is definitely an epics alarm, choose an appropriate abstraction
        return support.convertToIValue(valueData, (EpicsAlarm) cssValue.getAlarm(), cssValue.getTimestamp());
    }


    protected IValue toIValue(@Nonnull final Class<?> typeClass,
                              @Nonnull final Collection<T> data,
                              @Nonnull final EpicsAlarm alarm,
                              @Nonnull final TimeInstant timestamp) throws TypeSupportException {
        @SuppressWarnings("unchecked")
        final EpicsCssValueTypeSupport<T> support =
            (EpicsCssValueTypeSupport<T>) cachedTypeSupportFor(typeClass, TYPE_SUPPORTS, CALC_TYPE_SUPPORTS);
        if (support == null) {
            throw new TypeSupportException("No conversion type support registered.", null);
        }
        return support.convertCollectionToIValue(data, alarm, timestamp);
    }

    @CheckForNull
    protected abstract IValue convertCollectionToIValue(Collection<T> data,
                                                        EpicsAlarm alarm,
                                                        TimeInstant timestamp) throws TypeSupportException;

    @CheckForNull
    protected abstract IValue convertToIValue(@Nonnull final T data,
                                              @Nonnull final EpicsAlarm alarm,
                                              @Nonnull final TimeInstant timestamp) throws TypeSupportException;

}
