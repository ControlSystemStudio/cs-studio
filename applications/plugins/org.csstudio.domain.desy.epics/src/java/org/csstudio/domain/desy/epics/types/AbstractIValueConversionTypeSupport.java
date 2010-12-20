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

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.AbstractTypeSupport;
import org.csstudio.domain.desy.types.BaseTypeConversionSupport;
import org.csstudio.domain.desy.types.CssAlarmValueType;
import org.csstudio.domain.desy.types.ICssAlarmValueType;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.IEnumeratedValue;
import org.csstudio.platform.data.ILongValue;
import org.csstudio.platform.data.IStringValue;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.util.StringUtil;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;

/**
 * Type Conversion Support for IValue types. Be careful, due to the IValue design there isn't any
 * type safety.
 *
 * @author bknerr
 * @since 02.12.2010
 * @param <R> the basic type of the value(s) of the system variable
 * @param <T> the type of the system variable
 */
public abstract class AbstractIValueConversionTypeSupport<R extends ICssAlarmValueType<?>, T extends IValue>
    extends EpicsTypeSupport<T> {

    /**
     * IStringIValue converstion support
     *
     * @author bknerr
     * @since 15.12.2010
     */
    private static final class IStringValueConversionTypeSupport extends
            AbstractIValueConversionTypeSupport<ICssAlarmValueType<?>, IStringValue> {
        /**
         * Constructor.
         */
        public IStringValueConversionTypeSupport() {
            // Empty
        }

        @Override
        @CheckForNull
        public ICssAlarmValueType<?> convertToCssType(@Nonnull final IStringValue value)  {

            final String[] values = value.getValues();
            if (values == null || values.length == 0) {
                return null;
            }

            final EpicsAlarm alarm = EpicsTypeSupport.toEpicsAlarm(value.getSeverity(), value.getStatus());
            final TimeInstant timestamp = BaseTypeConversionSupport.toTimeInstant(value.getTime());
            if (values.length == 1) {
                return new CssAlarmValueType<String>(values[0],
                                                     alarm,
                                                     timestamp);
            }
            return new CssAlarmValueType<List<String>>(Lists.newArrayList(values),
                                                       alarm,
                                                       timestamp);
        }
    }

    /**
     * ILongValue conversion support.
     *
     * @author bknerr
     * @since 15.12.2010
     */
    private static final class ILongValueConversionTypeSupport extends
            AbstractIValueConversionTypeSupport<ICssAlarmValueType<?>, ILongValue> {
        /**
         * Constructor.
         */
        public ILongValueConversionTypeSupport() {
            // Empty
        }

        @CheckForNull
        @Override
        public ICssAlarmValueType<?> convertToCssType(@Nonnull final ILongValue value)  {

            final long[] values = value.getValues();
            if (values == null || values.length == 0) {
                return null;
            }

            final EpicsAlarm alarm = EpicsTypeSupport.toEpicsAlarm(value.getSeverity(), value.getStatus());
            final TimeInstant timestamp = BaseTypeConversionSupport.toTimeInstant(value.getTime());
            if (values.length == 1) {
                return new CssAlarmValueType<Long>(Long.valueOf(values[0]),
                                                   alarm,
                                                   timestamp);
            }
            return new CssAlarmValueType<List<Long>>(Lists.newArrayList(Longs.asList(values)),
                                                     alarm,
                                                     timestamp);
        }
    }

    /**
     * IDoubleValue conversion support.
     *
     * @author bknerr
     * @since 15.12.2010
     */
    private static final class IDoubleValueConversionTypeSupport extends
            AbstractIValueConversionTypeSupport<ICssAlarmValueType<?>, IDoubleValue> {

        public IDoubleValueConversionTypeSupport() {
            // Empty
        }

        @CheckForNull
        @Override
        public ICssAlarmValueType<?> convertToCssType(@Nonnull final IDoubleValue value) {

            final double[] values = value.getValues();
            if (values == null || values.length == 0) {
                return null;
            }

            final EpicsAlarm alarm = EpicsTypeSupport.toEpicsAlarm(value.getSeverity(), value.getStatus());
            final TimeInstant timestamp = BaseTypeConversionSupport.toTimeInstant(value.getTime());
            if (values.length == 1) {
                return new CssAlarmValueType<Double>(Double.valueOf(values[0]),
                                                     alarm,
                                                     timestamp);
            }
            return new CssAlarmValueType<List<Double>>(Lists.newArrayList(Doubles.asList(values)),
                                                       alarm,
                                                       timestamp);
        }
    }

    /**
     * IEnumeratedValue conversion support.
     *
     * @author bknerr
     * @since 15.12.2010
     */
    private static final class IEnumeratedValueConversionTypeSupport extends
            AbstractIValueConversionTypeSupport<ICssAlarmValueType<?>, IEnumeratedValue> {

        public IEnumeratedValueConversionTypeSupport() {
            // Empty
        }
        /**
         * {@inheritDoc}
         *
         * Kay's enumerated values have to have only a single value element corresponding to the set
         * enumerated value string. We want to archive the string, which yields the information, not the
         * index which doesn't speak for itself or might change.
         *
         * CHECKSTYLE OFF: CyclomaticComplexity (accepted here as we'll get rid of I*Values anyway)
         */
        @CheckForNull
        @Override
        public ICssAlarmValueType<?> convertToCssType(@Nonnull final IEnumeratedValue value)  {
        // CHECKSTYLE ON: CyclomaticComplexity

            // This is a nice example for what happens when physicists 'design' programs.
            // (...and I already got rid of an unsafe cast)
            final int[] values = value.getValues();
            if (values == null || values.length != 1) {
                LOG.warn("EnumeratedValue conversion failed, since IEnumeratedValue is not properly filled!");
                return null;
            }
            final IEnumeratedMetaData metaData = value.getMetaData();
            if (metaData == null) {
                LOG.warn("EnumeratedValue conversion failed, since IEnumeratedValue is not properly filled!");
                return null;
            }
            final String[] states = metaData.getStates();
            final int index = values[0];
            if (states == null || index < 0 || index >= states.length) {
                LOG.warn("EnumeratedValue conversion failed, since IEnumeratedValue is not properly filled!");
                return null;
            }
            final String state = states[index];
            if (StringUtil.isBlank(state)) {
                LOG.warn("EnumeratedValue conversion failed, since IEnumeratedValue is not properly filled!");
                return null;
            }

            // Now I know that IEnumeratedValue has been concisely filled, yeah.
            // (And I already got rid of some boilerplate...)
            // TODO (bknerr) : where's the raw value from epics... couldn't find it in EnumeratedValue
            final EpicsEnumTriple eVal = EpicsEnumTriple.createInstance(index, state, null);

            final EpicsAlarm alarm = EpicsTypeSupport.toEpicsAlarm(value.getSeverity(), value.getStatus());
            final TimeInstant timestamp = BaseTypeConversionSupport.toTimeInstant(value.getTime());

            return new CssAlarmValueType<EpicsEnumTriple>(eVal, alarm, timestamp);
        }
    }


    static final Logger LOG =
        CentralLogger.getInstance().getLogger(AbstractIValueConversionTypeSupport.class);

    private static boolean INSTALLED = false;

    /**
     * Constructor.
     */
    AbstractIValueConversionTypeSupport() {
        // Don't instantiate outside this class
    }

    public static void install() {
        if (INSTALLED) {
            return;
        }
        AbstractTypeSupport.addTypeSupport(IDoubleValue.class,
                                           new IDoubleValueConversionTypeSupport());
        AbstractTypeSupport.addTypeSupport(IEnumeratedValue.class,
                                           new IEnumeratedValueConversionTypeSupport());
        AbstractTypeSupport.addTypeSupport(ILongValue.class,
                                           new ILongValueConversionTypeSupport());
        AbstractTypeSupport.addTypeSupport(IStringValue.class,
                                           new IStringValueConversionTypeSupport());
        INSTALLED = true;
    }

    @CheckForNull
    protected abstract R convertToCssType(@Nonnull final T value) throws TypeSupportException;
}
