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

import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.AbstractTypeSupport;
import org.csstudio.domain.desy.types.BaseTypeConversionSupport;
import org.csstudio.domain.desy.types.ICssAlarmValueType;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 22.12.2010
 */
public abstract class EpicsCssValueConversionTypeSupport<T> extends AbstractTypeSupport<T> {

    private static boolean INSTALLED = false;

    /**
     * Constructor.
     */
    EpicsCssValueConversionTypeSupport() {
     // Don't instantiate outside this class
    }

    @SuppressWarnings("rawtypes")
    public static void install() {
        if (INSTALLED) {
            return;
        }
        AbstractTypeSupport.addTypeSupport(Double.class, new EpicsCssValueConversionTypeSupport<Double>() {

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
            protected IValue convertCollectionToIValue(final Collection<Double> data,
                                                       final EpicsAlarm alarm,
                                                       final TimeInstant timestamp) {
                return ValueFactory.createDoubleValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                      EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                      alarm.getStatus().toString(),
                                                      null,
                                                      null,
                                                      Doubles.toArray(data));
            }
        });
//        AbstractTypeSupport.addTypeSupport(Float.class, new AbstractCssValueConversionTypeSupport<Float>() {
//
//            @Override
//            @Nonnull
//            protected IValue convertToIValue(@Nonnull final Float data,
//                                             @Nonnull final EpicsAlarm alarm,
//                                             @Nonnull final TimeInstant timestamp) {
//                return ValueFactory.createDoubleValue(BaseTypeConversionSupport.toTimestamp(timestamp),
//                                                      EpicsTypeSupport.toSeverity(alarm.getSeverity()),
//                                                      alarm.getStatus().toString(),
//                                                      null,
//                                                      null,
//                                                      new double[] {data.doubleValue()});
//            }
//
//        });
//        AbstractTypeSupport.addTypeSupport(Integer.class, new AbstractCssValueConversionTypeSupport<Integer>(){
//
//            @Override
//            protected IValue convertToIValue(@Nonnull final Integer data,
//                                             @Nonnull final EpicsAlarm alarm,
//                                             @Nonnull final TimeInstant timestamp) {
//                return ValueFactory.createLongValue(BaseTypeConversionSupport.toTimestamp(timestamp),
//                                                    EpicsTypeSupport.toSeverity(alarm.getSeverity()),
//                                                    alarm.getStatus().toString(),
//                                                    null,
//                                                    null,
//                                                    new long[] {data.longValue()});
//            }
//
//        });
        AbstractTypeSupport.addTypeSupport(Long.class, new EpicsCssValueConversionTypeSupport<Long>(){

            @Override
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
            protected IValue convertCollectionToIValue(final Collection<Long> data,
                                                       final EpicsAlarm alarm,
                                                       final TimeInstant timestamp) {
                return ValueFactory.createLongValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                                    EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                                    alarm.getStatus().toString(),
                                                    null,
                                                    null,
                                                    Longs.toArray(data));
            }

        });
//        AbstractTypeSupport.addTypeSupport(String.class, new AbstractCssValueConversionTypeSupport<String>(){
//
//            @Override
//            protected IValue convertToIValue(@Nonnull final String data,
//                                             @Nonnull final EpicsAlarm alarm,
//                                             @Nonnull final TimeInstant timestamp) {
//                return ValueFactory.createStringValue(BaseTypeConversionSupport.toTimestamp(timestamp),
//                                                      EpicsTypeSupport.toSeverity(alarm.getSeverity()),
//                                                      alarm.getStatus().toString(),
//                                                      null,
//                                                      new String[] {data});
//            }
//
//        });
//        AbstractTypeSupport.addTypeSupport(Byte.class, new AbstractCssValueConversionTypeSupport<Byte>(){
//
//            @Override
//            protected IValue convertToIValue(@Nonnull final Byte data,
//                                             @Nonnull final EpicsAlarm alarm,
//                                             @Nonnull final TimeInstant timestamp) {
//                return ValueFactory.createLongValue(BaseTypeConversionSupport.toTimestamp(timestamp),
//                                                    EpicsTypeSupport.toSeverity(alarm.getSeverity()),
//                                                    alarm.getStatus().toString(),
//                                                    null,
//                                                    null,
//                                                    new long[] {data.longValue()});
//            }
//
//        });
//        AbstractTypeSupport.addTypeSupport(Enum.class, new AbstractCssValueConversionTypeSupport<Enum>(){
//
//            @Override
//            protected IValue convertToIValue(@Nonnull final Enum data,
//                                             @Nonnull final EpicsAlarm alarm,
//                                             @Nonnull final TimeInstant timestamp) {
//                // TODO Auto-generated method stub
//                return null;
//            }
//
//        });
        AbstractTypeSupport.addTypeSupport(Collection.class, new EpicsCssValueConversionTypeSupport<Collection>(){

            @SuppressWarnings("unchecked")
            @Override
            protected IValue convertToIValue(@Nonnull final Collection data,
                                             @Nonnull final EpicsAlarm alarm,
                                             @Nonnull final TimeInstant timestamp) throws TypeSupportException {
                if (data.isEmpty()) {
                    throw new TypeSupportException("Collection of data is empty. Type cannot be determined.", null);
                }
                return convertCollectionToIValue(data, alarm, timestamp);
            }

            @Override
            protected IValue convertCollectionToIValue(final Collection<Collection> data,
                                                       final EpicsAlarm alarm,
                                                       final TimeInstant timestamp) throws TypeSupportException {
                throw new TypeSupportException("This method should not be invoked on itself", null);
            }

        });

        INSTALLED = true;
    }

    @CheckForNull
    public static <T> IValue toIValue(@Nonnull final ICssAlarmValueType<T> cssValue) throws TypeSupportException {
        final T valueData = cssValue.getValueData();
        @SuppressWarnings("unchecked")
        final Class<T> typeClass = (Class<T>) valueData.getClass();
        final EpicsCssValueConversionTypeSupport<T> support =
            (EpicsCssValueConversionTypeSupport<T>) cachedTypeSupportFor(typeClass);
        if (support == null) {
            throw new TypeSupportException("No conversion type support registered.", null);
        }
        // TODO (bknerr) : This is definitily an epics alarm, choose an appropriate abstraction
        return support.convertToIValue(valueData, (EpicsAlarm) cssValue.getAlarm(), cssValue.getTimestamp());
    }

    protected IValue toIValue(@Nonnull final Class<T> typeClass,
                              @Nonnull final Collection<T> data,
                              @Nonnull final EpicsAlarm alarm,
                              @Nonnull final TimeInstant timestamp) throws TypeSupportException {
        final EpicsCssValueConversionTypeSupport<T> support =
            (EpicsCssValueConversionTypeSupport<T>) cachedTypeSupportFor(typeClass);
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
