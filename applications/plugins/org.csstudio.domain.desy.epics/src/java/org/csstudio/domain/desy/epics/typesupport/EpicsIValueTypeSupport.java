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

import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.epics.types.EpicsMetaData;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.SystemVariableSupport;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.AbstractTypeSupport;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.epics.pvmanager.TypeSupport;

import com.google.common.collect.ImmutableList;


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

    private static boolean INSTALLED;
    /**
     * Constructor for a new EpicsIValue support.
     *
     * @param type the supported type
     */
    public EpicsIValueTypeSupport(@Nonnull final Class<T> type) {
        super(type, EpicsIValueTypeSupport.class);
    }

    // CHECKSTYLE OFF : MethodLength
    public static void install() {
        // CHECKSTYLE ON : MethodLength
        if (INSTALLED) {
            return;
        }
        TypeSupport.addTypeSupport(new IDoubleValueConversionTypeSupport());
        TypeSupport.addTypeSupport(new IEnumeratedValueConversionTypeSupport());
        TypeSupport.addTypeSupport(new ILongValueConversionTypeSupport());
        TypeSupport.addTypeSupport(new IStringValueConversionTypeSupport());

        TypeSupport.addTypeSupport(new AbstractIValueDataToTargetTypeSupport<Byte>(Byte.class) {
            @Override
            @Nonnull
            protected Byte fromLongValue(@Nonnull final Long val) {
                return val.byteValue();
            }
            @Override
            @Nonnull
            protected Byte fromDoubleValue(@Nonnull final Double val) {
                return val.byteValue();
            }
        });
        TypeSupport.addTypeSupport(new AbstractIValueDataToTargetTypeSupport<Short>(Short.class) {
            @Override
            @Nonnull
            protected Short fromLongValue(@Nonnull final Long val) {
                return val.shortValue();
            }
            @Override
            @Nonnull
            protected Short fromDoubleValue(@Nonnull final Double val) {
                return val.shortValue();
            }
        });
        TypeSupport.addTypeSupport(new AbstractIValueDataToTargetTypeSupport<Float>(Float.class) {
            @Override
            @Nonnull
            protected Float fromLongValue(@Nonnull final Long val) {
                return val.floatValue();
            }
            @Override
            @Nonnull
            protected Float fromDoubleValue(@Nonnull final Double val) {
                return val.floatValue();
            }
        });
        TypeSupport.addTypeSupport(new AbstractIValueDataToTargetTypeSupport<Integer>(Integer.class) {
            @Override
            @Nonnull
            protected Integer fromLongValue(@Nonnull final Long val) {
                return val.intValue();
            }
            @Override
            @Nonnull
            protected Integer fromDoubleValue(@Nonnull final Double val) {
                return val.intValue();
            }
        });
        TypeSupport.addTypeSupport(new AbstractIValueDataToTargetTypeSupport<Double>(Double.class) {
            @Override
            @Nonnull
            protected Double fromLongValue(@Nonnull final Long val) {
                return val.doubleValue();
            }
            @Override
            @Nonnull
            protected Double fromDoubleValue(@Nonnull final Double val) {
                return val;
            }
        });
        TypeSupport.addTypeSupport(new AbstractIValueDataToTargetTypeSupport<Long>(Long.class) {
            @Override
            @Nonnull
            protected Long fromLongValue(@Nonnull final Long val) {
                return val;
            }
            @Override
            @Nonnull
            protected Long fromDoubleValue(@Nonnull final Double val) {
                return val.longValue();
            }
        });
        TypeSupport.addTypeSupport(new AbstractIValueDataToTargetTypeSupport<String>(String.class) {
            @Override
            @Nonnull
            protected String fromStringValue(@Nonnull final String val) {
                return val;
            }
        });
        TypeSupport.addTypeSupport(new AbstractIValueDataToTargetTypeSupport<EpicsEnum>(EpicsEnum.class) {
            @Override
            @Nonnull
            protected EpicsEnum fromEnumValue(final int index,
                                              @CheckForNull final EpicsMetaData meta) {
                if (meta == null) {
                    return EpicsEnum.createFromRaw(index);
                }
                final ImmutableList<EpicsEnum> states = meta.getStates();
                if (states.isEmpty() || index < 0 || index >= states.size()) {
                    return EpicsEnum.createFromRaw(index);
                }
                return meta.getStates().get(index);
            }
        });

        INSTALLED = true;
    }

    @Nonnull
    public static <T extends IValue>
    EpicsSystemVariable<?> toSystemVariable(@Nonnull final String name,
                                            @Nonnull final T value,
                                            @Nonnull final ControlSystem cs,
                                            @Nonnull final Class<?> elemClass) throws TypeSupportException {
        return toSystemVariable(name, value, cs, null, elemClass);
    }
    @SuppressWarnings("rawtypes")
    @Nonnull
    public static <T extends IValue>
    EpicsSystemVariable<?> toSystemVariable(@Nonnull final String name,
                                            @Nonnull final T value,
                                            @Nonnull final ControlSystem cs,
                                            @CheckForNull final Class<? extends Collection> collClass,
                                            @Nonnull final Class<?> elemClass) throws TypeSupportException {
        EpicsMetaData meta = null;
        if (value.getMetaData() != null) {
            meta = EpicsIMetaDataTypeSupport.toMetaData(value.getMetaData(), elemClass);
        }
        return toSystemVariable(name, value, meta, cs, collClass, elemClass);
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Nonnull
    public static <T extends IValue>
    EpicsSystemVariable<?> toSystemVariable(@Nonnull final String name,
                                            @Nonnull final T value,
                                            @CheckForNull final EpicsMetaData meta,
                                            @Nonnull final ControlSystem cs,
                                            @CheckForNull final Class<? extends Collection> collClass,
                                            @Nonnull final Class<?> elemClass) throws TypeSupportException {
        final Class<T> typeClass = (Class<T>) value.getClass();
        final AbstractIValueConversionTypeSupport<T> support =
            (AbstractIValueConversionTypeSupport<T>) findTypeSupportForOrThrowTSE(EpicsIValueTypeSupport.class,
                                                                                  typeClass);

        final EpicsAlarm alarm = toEpicsAlarm(value.getSeverity(), value.getStatus());
        final TimeInstant time = BaseTypeConversionSupport.toTimeInstant(value.getTime());

        final Object data = support.toData(value, elemClass, collClass, meta);
        return (EpicsSystemVariable<?>) SystemVariableSupport.create(name, data, cs, time, alarm);
    }

    @Nonnull
    protected abstract Object toData(@Nonnull final T value,
                                     @Nonnull final Class<?> elemClass,
                                     @SuppressWarnings("rawtypes") @Nonnull final Class<? extends Collection> collClass,
                                     @CheckForNull final EpicsMetaData meta) throws TypeSupportException;

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
        final EpicsAlarmSeverity epicsSeverity = toEpicsSeverity(sev);
        final EpicsAlarmStatus epicsStatus = EpicsAlarmStatus.parseStatus(status);
        return new EpicsAlarm(epicsSeverity, epicsStatus);
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
                throw new IllegalArgumentException("This severity has not been defined.");
        }
    }
}
