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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.csstudio.domain.desy.types.AbstractTypeSupport;
import org.csstudio.domain.desy.types.ICssAlarmValueType;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;

import com.google.common.collect.Maps;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 15.12.2010
 * @param <T> the type to be supported
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class statically is accessed, hence the name should be short and descriptive!
 */
public abstract class EpicsIValueTypeSupport<T> extends AbstractTypeSupport<T> {
// CHECKSTYLE ON : AbstractClassName

    protected static Map<Class<?>, AbstractTypeSupport<?>> TYPE_SUPPORTS =
        Maps.newHashMap();
    protected static Map<Class<?>, AbstractTypeSupport<?>> CALC_TYPE_SUPPORTS =
        new ConcurrentHashMap<Class<?>, AbstractTypeSupport<?>>();

    public static void install() {
        AbstractIValueConversionTypeSupport.install();
    }

    /**
     * Tries to convert the given IValue type and its accompanying parms to the css value type.
     * @param value the value to be converted
     * @return the conversion result
     * @throws TypeSupportException when conversion failed.
     * @param <R>
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    @CheckForNull
    public static <R extends ICssAlarmValueType<?>, T extends IValue>
        R toCssType(@Nonnull final T value) throws TypeSupportException {

        final Class<T> typeClass = (Class<T>) value.getClass();
        final AbstractIValueConversionTypeSupport<R, T> support =
            (AbstractIValueConversionTypeSupport<R, T>) cachedTypeSupportFor(typeClass, TYPE_SUPPORTS, CALC_TYPE_SUPPORTS);
        if (support == null) {
            throw new TypeSupportException("No conversion type support registered.", null);
        }
        return support.convertToCssType(value);
    }

    /**
     * Converts the parameters into a type safe enum class for EPICS alarms.
     * Attention:
     * <li>the parameter severity is not safe, since the interface does
     * not force the is* methods returning true exactly for one of them
     * <li>the parameter status is string based and may therefore not be parseable into
     * the {@link EpicsStatus}
     *
     * @param sev the severity
     * @param status the status
     * @return the epics alarm composite (as of 3.14.12.rc1 in dbStatic/alarm.h)
     */
    @CheckForNull
    public static EpicsAlarm toEpicsAlarm(@CheckForNull final ISeverity sev,
                                          @Nullable final String status) {
        final EpicsAlarmSeverity severity = toEpicsSeverity(sev);
        return new EpicsAlarm(severity, EpicsAlarmStatus.parseStatus(status));
    }

    @CheckForNull
    public static EpicsAlarmSeverity toEpicsSeverity(@CheckForNull final ISeverity sev) {
        // once before...
        if (sev == null) {
            return null;
        }
        EpicsAlarmSeverity severity = null;
        // Unfortunately ISeverity is not an enum (if it were, I would have used it) so dispatch
        // btw from the interface the mutual exclusion of these states is not ensured
        if (sev.isOK()) {
            severity = EpicsAlarmSeverity.NO_ALARM;
        } else if (sev.isMinor()) {
            severity = EpicsAlarmSeverity.MINOR;
        } else if (sev.isMajor()) {
            severity = EpicsAlarmSeverity.MAJOR;
        } else if (sev.isInvalid()) {
            severity = EpicsAlarmSeverity.INVALID;
        }
        // and once after... in case anything was false! This interface is lovely
        if (severity == null) {
            return null;
        }
        return severity;
    }

    @CheckForNull
    public static ISeverity toSeverity(@Nonnull final EpicsAlarmSeverity sev) {
        switch (sev) {
            case UNKNOWN : // unknown->no alarm is apparently wrong, but once you use ISeverity, well, you're wrong.
            case NO_ALARM : return ValueFactory.createOKSeverity();
            case MINOR :    return ValueFactory.createMinorSeverity();
            case MAJOR :    return ValueFactory.createMajorSeverity();
            case INVALID :  return ValueFactory.createInvalidSeverity();
        }
        return null;
    }


    @CheckForNull
    public static <T> EpicsIValueTypeSupport<T> getTypeSupportFor(@Nonnull final Class<T> typeClass) {
        try {
            return (EpicsIValueTypeSupport<T>) cachedTypeSupportFor(typeClass, TYPE_SUPPORTS, CALC_TYPE_SUPPORTS);
        } catch (final TypeSupportException e) {
            return null;
        }
    }
}
