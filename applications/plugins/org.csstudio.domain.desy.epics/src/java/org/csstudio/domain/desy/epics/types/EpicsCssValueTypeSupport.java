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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.alarm.IAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.BaseTypeConversionSupport;
import org.csstudio.domain.desy.types.ICssAlarmValueType;
import org.csstudio.domain.desy.types.TypeSupport;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
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
public abstract class EpicsCssValueTypeSupport<T> extends TypeSupport<T> {
// CHECKSTYLE ON : AbstractClassName

    /**
     * @author bknerr
     * @since 22.12.2010
     */
    private static final class CssStringValueTypeSupport extends EpicsCssValueTypeSupport<String> {

        /**
         * Constructor.
         */
        public CssStringValueTypeSupport() {
            // EMPTY
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
    @SuppressWarnings("rawtypes")
    private static final class CssCollectionValueTypeSupport extends EpicsCssValueTypeSupport<Collection> {
        /**
         * Constructor.
         */
        public CssCollectionValueTypeSupport() {
            // EMPTY
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
            // EMPTY
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

        @Override
        @Nonnull
        protected IValue convertToIMinMaxDoubleValue(@Nonnull final ICssAlarmValueType<Byte> cssValue, 
                                                     @Nonnull final Byte min, 
                                                     @Nonnull final Byte max) throws TypeSupportException {
            return createMinMaxDoubleValueFromNumber(cssValue.getTimestamp(), cssValue.getAlarm(), cssValue.getValueData(), min, max);
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
            // EMPTY
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

        @Override
        @Nonnull
        protected IValue convertToIMinMaxDoubleValue(@Nonnull final ICssAlarmValueType<Long> cssValue, 
                                                     @Nonnull final Long min, 
                                                     @Nonnull final Long max) throws TypeSupportException {
            return createMinMaxDoubleValueFromNumber(cssValue.getTimestamp(), cssValue.getAlarm(), cssValue.getValueData(), min, max);
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
            // Empty
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

        @Override
        @Nonnull
        protected IValue convertToIMinMaxDoubleValue(@Nonnull final ICssAlarmValueType<Integer> cssValue, 
                                                     @Nonnull final Integer min, 
                                                     @Nonnull final Integer max) throws TypeSupportException {
            return createMinMaxDoubleValueFromNumber(cssValue.getTimestamp(), cssValue.getAlarm(), cssValue.getValueData(), min, max);
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
            // EMPTY
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

        @Override
        @Nonnull
        protected IValue convertToIMinMaxDoubleValue(@Nonnull final ICssAlarmValueType<Double> cssValue, 
                                                     @Nonnull final Double min, 
                                                     @Nonnull final Double max) throws TypeSupportException {
            return createMinMaxDoubleValueFromNumber(cssValue.getTimestamp(), cssValue.getAlarm(), cssValue.getValueData(), min, max);
        }
    }
    private static final class CssFloatValueTypeSupport extends EpicsCssValueTypeSupport<Float> {
        /**
         * Constructor.
         */
        public CssFloatValueTypeSupport() {
            // EMPTY
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

        @Override
        @Nonnull
        protected IValue convertToIMinMaxDoubleValue(@Nonnull final ICssAlarmValueType<Float> cssValue, 
                                                     @Nonnull final Float min, 
                                                     @Nonnull final Float max) throws TypeSupportException {
            return createMinMaxDoubleValueFromNumber(cssValue.getTimestamp(), cssValue.getAlarm(), cssValue.getValueData(), min, max);
        }
    }
    private static final class CssEpicsEnumValueTypeSupport extends EpicsCssValueTypeSupport<EpicsEnumTriple> {
        /**
         * Constructor.
         */
        public CssEpicsEnumValueTypeSupport() {
            // EMPTY
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
        TypeSupport.addTypeSupport(Double.class, new CssDoubleValueTypeSupport());
        TypeSupport.addTypeSupport(Float.class, new CssFloatValueTypeSupport());
        TypeSupport.addTypeSupport(Long.class, new CssLongValueTypeSupport());
        TypeSupport.addTypeSupport(Integer.class, new CssIntegerValueTypeSupport());
        TypeSupport.addTypeSupport(String.class, new CssStringValueTypeSupport());
        TypeSupport.addTypeSupport(Byte.class, new CssByteValueTypeSupport());

        TypeSupport.addTypeSupport(EpicsEnumTriple.class, new CssEpicsEnumValueTypeSupport());

        TypeSupport.addTypeSupport(Collection.class, new CssCollectionValueTypeSupport());

        INSTALLED = true;
    }

    @CheckForNull
    public static <T> IValue toIValue(@Nonnull final ICssAlarmValueType<T> cssValue) throws TypeSupportException {
        final T valueData = cssValue.getValueData();
        @SuppressWarnings("unchecked")
        final Class<T> typeClass = (Class<T>) valueData.getClass();
        final EpicsCssValueTypeSupport<T> support =
            (EpicsCssValueTypeSupport<T>) cachedTypeSupportFor(EpicsCssValueTypeSupport.class, typeClass);
        // TODO (bknerr) : This is definitely an epics alarm, choose an appropriate abstraction
        return support.convertToIValue(valueData, (EpicsAlarm) cssValue.getAlarm(), cssValue.getTimestamp());
    }

    /**
     * @param data
     * @param min
     * @param max
     * @return
     * @throws TypeSupportException 
     */
    public static <T> IValue toIMinMaxDoubleValue(@Nonnull final ICssAlarmValueType<T> cssValue,
                                                  @Nonnull final T min,
                                                  @Nonnull final T max) throws TypeSupportException {
        final T valueData = cssValue.getValueData();
        @SuppressWarnings("unchecked")
        final Class<T> typeClass = (Class<T>) valueData.getClass();
        final EpicsCssValueTypeSupport<T> support =
            (EpicsCssValueTypeSupport<T>) cachedTypeSupportFor(EpicsCssValueTypeSupport.class, typeClass);
        // TODO (bknerr) : This is definitely an epics alarm, choose an appropriate abstraction
        return support.convertToIMinMaxDoubleValue(cssValue, min, max);
    }

    protected IValue toIValue(@Nonnull final Class<?> typeClass,
                              @Nonnull final Collection<T> data,
                              @Nonnull final EpicsAlarm alarm,
                              @Nonnull final TimeInstant timestamp) throws TypeSupportException {
        @SuppressWarnings("unchecked")
        final EpicsCssValueTypeSupport<T> support =
            (EpicsCssValueTypeSupport<T>) cachedTypeSupportFor(EpicsCssValueTypeSupport.class, typeClass);
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
    @CheckForNull
    protected IValue convertToIMinMaxDoubleValue(@Nonnull final ICssAlarmValueType<T> cssValue,
                                                 @SuppressWarnings("unused") @Nonnull final T min,
                                                 @SuppressWarnings("unused") @Nonnull final T max) throws TypeSupportException {
        throw new TypeSupportException("Type " + cssValue.getValueData().getClass() + " cannot be converted to IMinMaxDoubleValue!", null);
    }

    @Nonnull 
    static IValue createMinMaxDoubleValueFromNumber(@Nonnull final TimeInstant timestamp,
                                                            @Nonnull final IAlarm ialarm,
                                                            @Nonnull final Number valueData,
                                                            @Nonnull final Number min,
                                                            @Nonnull final Number max) {
        // TODO (bknerr) : well thats not quite right again, Epics specifics shouldn't be here
        EpicsAlarm alarm = (EpicsAlarm) ialarm;
        return ValueFactory.createMinMaxDoubleValue(BaseTypeConversionSupport.toTimestamp(timestamp), 
                                                    EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                    alarm.getStatus().toString(),
                                                    null,
                                                    null,
                                                    new double[]{ valueData.doubleValue() },
                                                    min.doubleValue(),
                                                    max.doubleValue());
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Class<? extends TypeSupport<T>> getTypeSupportFamily() {
        return (Class<? extends TypeSupport<T>>) EpicsCssValueTypeSupport.class;
    }
}
