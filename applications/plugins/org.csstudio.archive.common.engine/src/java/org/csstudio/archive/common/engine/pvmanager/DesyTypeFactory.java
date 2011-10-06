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
import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 30.08.2011
 * @param <V> The desired value into which
 * @param <EV>
 * @param <EM>
 */
// CHECKSTYLE OFF : AbstractClassName
@SuppressWarnings("rawtypes")
public abstract class DesyTypeFactory<V,
                                      EV extends DBR & TIME,
                                      EM extends DBR & STS> // Not CTRL as to metadata for irregular
                                                                    // 'DBR_TIME_String' and 'DBR_LABELS_ENUM'
    implements TypeFactory<EpicsSystemVariable<V>, EV, EM> {
// CHECKSTYLE ON : AbstractClassName

    private final Class<V> _valueType;
    private final Class<? extends Collection> _collType;
    private final DBRType _epicsValueType;
    private final DBRType _epicsMetaType;

    public DesyTypeFactory(@Nonnull final Class<V> valueType,
                           @Nullable final Class<? extends Collection> collType,
                           @Nonnull final DBRType epicsValueType,
                           @Nonnull final DBRType epicsMetaType) {

        _valueType = valueType;
        _collType = collType;
        _epicsValueType = epicsValueType;
        _epicsMetaType = epicsMetaType;
    }

    public DesyTypeFactory(@Nonnull final Class<V> valueType,
                           @Nonnull final DBRType epicsValueType,
                           @Nonnull final DBRType epicsMetaType) {
        this(valueType, null, epicsValueType, epicsMetaType);
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
        return _collType != null; // is not always an array - better isCollection or isMultiScalar or the like
    }

    @Override
    @Nonnull
    public EpicsSystemVariable<V> createValue(@Nonnull final EV value,
                                              @Nonnull final EM metadata,
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
        if (eVal.getCount() <= 1) {
            data = toScalarData(eVal, eMeta);
        } else {
            data = toMultiScalarData(eVal, eMeta);
        }
        return new EpicsSystemVariable(channelName, data, ControlSystem.EPICS_DEFAULT, timestamp, dMeta);

    }

    @SuppressWarnings("unchecked")
    @CheckForNull
    protected <W extends Comparable<? super W>>
    EpicsMetaData createMetaData(@Nonnull final STS eMeta) {
        final EpicsAlarm alarm = new EpicsAlarm(EpicsAlarmSeverity.valueOf(eMeta.getSeverity()),
                                                EpicsAlarmStatus.valueOf(eMeta.getStatus()));
        Short prec = null;
        if (PRECISION.class.isAssignableFrom(eMeta.getClass())) {
            prec = Short.valueOf(((PRECISION) eMeta).getPrecision());
        }
        final CTRL ctrl = (CTRL) eMeta;

        final EpicsGraphicsData gr =
            new EpicsGraphicsData(Limits.create((W) ctrl.getLowerAlarmLimit(), (W) ctrl.getUpperAlarmLimit()),
                                  Limits.create((W) ctrl.getLowerWarningLimit(), (W) ctrl.getUpperWarningLimit()),
                                  Limits.create((W) ctrl.getLowerDispLimit(), (W) ctrl.getUpperDispLimit()));
        final IControlLimits<W> cr =
            new ControlLimits<W>((W) ctrl.getLowerCtrlLimit(),
                                 (W) ctrl.getUpperCtrlLimit());

        return EpicsMetaData.create(alarm, gr, cr, prec);


    }

    @Nonnull
    protected V toScalarData(@Nonnull final DBR eVal, @Nonnull final EM eMeta) {
        return toScalarData(eVal, eMeta, 0);
    }
    @Nonnull
    protected abstract V toScalarData(@Nonnull final DBR eVal, @Nonnull final EM eMeta, final int index);

    @SuppressWarnings("unchecked")
    @Nonnull
    private ArrayList toMultiScalarData(@Nonnull final DBR eVal, @Nonnull final EM eMeta) {
        final DesyTypeFactory elementFactory = DesyTypeFactoryProvider.getMap().get(eVal);
        final int nelm = eVal.getCount();
        final ArrayList array = Lists.newArrayList(nelm);
        for (int i = 0; i < nelm; i++) {
            array.add(elementFactory.toScalarData(eVal, eMeta, i));
        }
        return array;
    }
}
