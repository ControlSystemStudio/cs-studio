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
package org.csstudio.archive.common.engine.pvmanager;

import gov.aps.jca.dbr.CTRL;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.PRECISION;
import gov.aps.jca.dbr.STS;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.dbr.TimeStamp;

import java.util.ArrayList;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.csstudio.domain.desy.epics.types.ControlLimits;
import org.csstudio.domain.desy.epics.types.EpicsGraphicsData;
import org.csstudio.domain.desy.epics.types.EpicsMetaData;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.epics.types.IControlLimits;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.Limits;
import org.epics.pvmanager.jca.TypeFactory;

import com.google.common.collect.Lists;


/**
 * Dedicated type factory to create {@link EpicsSystemVariable}s from DBR types.
 *
 * Take care - from API present it cannot be ensured that
 *
 * @author bknerr
 * @since 30.08.2011
 * @param <V> The desired value into which to convert the scalars or multiscalar elements.
 * @param <EV> the epics value with time information
 * @param <EM> the epics value with sev and status information
 */
// CHECKSTYLE OFF : AbstractClassName
@SuppressWarnings("rawtypes")
public abstract class DesyTypeFactory<V,
                                      EV extends DBR & TIME,
                                      EM extends DBR & STS>
    implements TypeFactory<EpicsSystemVariable<V>, EV, EM> {
// CHECKSTYLE ON : AbstractClassName

    private final Class<V> _valueType;
    private final DBRType _epicsValueType;
    private final DBRType _epicsMetaType;
    private boolean _isArray;

    public DesyTypeFactory(@Nonnull final Class<V> valueType,
                           @Nonnull final DBRType epicsValueType,
                           @Nonnull final DBRType epicsMetaType) {

        _valueType = valueType;
        _epicsValueType = epicsValueType;
        _epicsMetaType = epicsMetaType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Class<V> getValueType() {
        return _valueType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public DBRType getEpicsMetaType() {
        return _epicsMetaType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public DBRType getEpicsValueType() {
        return _epicsValueType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isArray() {
        return _isArray; // TODO (bknerr) : is not always an array - better isCollection or isMultiScalar or the like
    }

    @Override
    @Nonnull
    public EpicsSystemVariable<V> createValue(@Nonnull final EV value,
                                              @CheckForNull final EM metadata,
                                              final boolean connected) {
        throw new UnsupportedOperationException("DESY type factory does not support variable creation without channel name.");
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public EpicsSystemVariable<V> createValue(@Nonnull final String channelName,
                                              @Nonnull final EV eVal,
                                              @Nonnull final EM eMeta,
                                              @Nonnull final EpicsMetaData dMeta) {

        final TimeStamp ts = eVal.getTimeStamp();
        final TimeInstant timestamp =
            TimeInstantBuilder.fromNanos((long) (1e9*ts.secPastEpoch() + ts.nsec()));

        final Object data;
        if (isArray()) {
            data = toMultiScalarData(eVal, eMeta);
        } else {
            data = toScalarData(eVal, eMeta);
        }
        return new EpicsSystemVariable(channelName, data, ControlSystem.EPICS_DEFAULT, timestamp, dMeta);
    }

    @Nonnull
    protected <W extends Comparable<? super W>>
    EpicsMetaData createMetaData(@Nonnull final STS eMeta) {
        final EpicsAlarm alarm = new EpicsAlarm(EpicsAlarmSeverity.valueOf(eMeta.getSeverity()),
                                                EpicsAlarmStatus.valueOf(eMeta.getStatus()));
        Short prec = null;
        if (PRECISION.class.isAssignableFrom(eMeta.getClass())) {
            prec = Short.valueOf(((PRECISION) eMeta).getPrecision());
        }
        EpicsGraphicsData<W> gr = null;
        IControlLimits<W> cr = null;
        if (CTRL.class.isAssignableFrom(eMeta.getClass())) {
            final CTRL ctrl = (CTRL) eMeta;
            gr = createGraphics(ctrl);
            cr = createControlLimits(ctrl);
        }
        return EpicsMetaData.create(alarm, gr, cr, prec);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    protected <W extends Comparable<? super W>>
    EpicsGraphicsData<W> createGraphics(@Nonnull final CTRL ctrl) {
        final Limits<W> aLimits = Limits.create((W) ctrl.getLowerAlarmLimit(), (W) ctrl.getUpperAlarmLimit());
        final Limits<W> wLimits = Limits.create((W) ctrl.getLowerWarningLimit(), (W) ctrl.getUpperWarningLimit());
        final Limits<W> oLimits = Limits.create((W) ctrl.getLowerDispLimit(), (W) ctrl.getUpperDispLimit());
        return new EpicsGraphicsData<W>(aLimits, wLimits, oLimits);
    }
    @SuppressWarnings("unchecked")
    @Nonnull
    protected <W extends Comparable<? super W>>
    IControlLimits<W> createControlLimits(@Nonnull final CTRL ctrl) {
        return new ControlLimits<W>((W) ctrl.getLowerCtrlLimit(), (W) ctrl.getUpperCtrlLimit());
    }

    @Nonnull
    protected V toScalarData(@Nonnull final DBR eVal, @CheckForNull final EM eMeta) {
        return toScalarData(eVal, eMeta, 0);
    }
    @Nonnull
    protected abstract V toScalarData(@Nonnull final DBR eVal,
                                      @CheckForNull final EM eMeta,
                                      final int index);

    @SuppressWarnings("unchecked")
    @Nonnull
    private ArrayList toMultiScalarData(@Nonnull final DBR eVal,
                                        @CheckForNull final EM eMeta) {
        final int nelm = eVal.getCount();
        final ArrayList array = Lists.newArrayList(nelm);
        for (int i = 0; i < nelm; i++) {
            array.add(toScalarData(eVal, eMeta, i));
        }
        return array;
    }

    public void setIsArray(final boolean b) {
        _isArray = b;

    }
}
