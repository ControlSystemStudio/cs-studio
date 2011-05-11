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
package org.csstudio.domain.desy.epics.typesupport;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.csstudio.domain.desy.epics.types.EpicsMetaData;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.typesupport.AbstractTypeSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;


/**
 * Conversion for epics and epics related types.
 *
 * @author bknerr
 * @since 15.12.2010
 * @param <T> the type to be supported
 * CHECKSTYLE OFF: AbstractClassName
 *                 This class statically is accessed, hence the name should be short and descriptive!
 */
public abstract class EpicsIValueTypeSupport<T> extends AbstractTypeSupport<T> {
// CHECKSTYLE ON : AbstractClassName

    /**
     * Constructor for a new EpicsIValue support.
     *
     * @param type the supported type
     */
    public EpicsIValueTypeSupport(@Nonnull final Class<T> type) {
        super(type, EpicsIValueTypeSupport.class);
    }

    public static void install() {
        AbstractIValueConversionTypeSupport.install();
    }

    @Nonnull
    public static <T extends IValue>
    EpicsSystemVariable<?> toSystemVariable(@Nonnull final String name,
                                            @Nonnull final T value) throws TypeSupportException {
        return toSystemVariable(name, value, null);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T extends IValue>
    EpicsSystemVariable<?> toSystemVariable(@Nonnull final String name,
                                            @Nonnull final T value,
                                            @Nullable final EpicsMetaData metaData) throws TypeSupportException {
        final Class<T> typeClass = (Class<T>) value.getClass();
        final AbstractIValueConversionTypeSupport<T> support =
            (AbstractIValueConversionTypeSupport<T>) findTypeSupportForOrThrowTSE(EpicsIValueTypeSupport.class,
                                                                                  typeClass);
        return support.convertToSystemVariable(name, value, metaData);
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
    @Nonnull
    public static EpicsAlarm toEpicsAlarm(@CheckForNull final ISeverity sev,
                                          @Nullable final String status) {
        final EpicsAlarmSeverity severity = toEpicsSeverity(sev);
        return new EpicsAlarm(severity, EpicsAlarmStatus.parseStatus(status));
    }

    @Nonnull
    public static EpicsAlarmSeverity toEpicsSeverity(@CheckForNull final ISeverity sev) {
        if (sev == null) {
            return EpicsAlarmSeverity.UNKNOWN;
        }
        // Unfortunately ISeverity is not an enum (if it were, I would have used it) so dispatch
        // btw from the interface the mutual exclusion of these states is not ensured
        if (sev.isOK()) {
            return EpicsAlarmSeverity.NO_ALARM;
        } else if (sev.isMinor()) {
            return EpicsAlarmSeverity.MINOR;
        } else if (sev.isMajor()) {
            return EpicsAlarmSeverity.MAJOR;
        } else if (sev.isInvalid()) {
            return EpicsAlarmSeverity.INVALID;
        }
        return EpicsAlarmSeverity.UNKNOWN;
    }

    @Nonnull
    public static ISeverity toSeverity(@Nonnull final EpicsAlarmSeverity sev) {
        switch (sev) {
            case UNKNOWN : // unknown fall through to no alarm is apparently wrong, but once you use ISeverity, well, you're wrong.
            case NO_ALARM : return ValueFactory.createOKSeverity();
            case MINOR :    return ValueFactory.createMinorSeverity();
            case MAJOR :    return ValueFactory.createMajorSeverity();
            case INVALID :  return ValueFactory.createInvalidSeverity();
            default:
                throw new IllegalArgumentException("This severity has been defined.");
        }
    }
}
