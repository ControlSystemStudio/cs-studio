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
package org.csstudio.domain.desy.epics.pvmanager;

import gov.aps.jca.dbr.CTRL;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.PRECISION;
import gov.aps.jca.dbr.STS;
import gov.aps.jca.dbr.TIME;

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
import org.csstudio.domain.desy.epics.typesupport.EpicsSystemVariableSupport;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.Limits;
import org.epics.pvmanager.jca.TypeFactory;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 08.12.2011
 * @param <V> The desired value into which to convert the scalars or multiscalar elements.
 * @param <EV> the epics value with time information
 * @param <EM> the epics value with sev and status information
 */
public abstract class AbstractDesyJCATypeFactory<V,
                                                 EV extends DBR & TIME,
                                                 EM extends DBR & STS>
    implements TypeFactory<EpicsSystemVariable<V>, EV, EM> {

    private final Class<V> _valueType;
    private final DBRType _epicsValueType;
    private final DBRType _epicsMetaType;
    private final DBRType _channelFieldType;

    public AbstractDesyJCATypeFactory(@Nonnull final Class<V> valueType,
                                      @Nonnull final DBRType epicsValueType,
                                      @Nonnull final DBRType epicsMetaType,
                                      @Nonnull final DBRType channelFieldType) {

        _valueType = valueType;
        _epicsValueType = epicsValueType;
        _epicsMetaType = epicsMetaType;
        _channelFieldType = channelFieldType;
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

    @Override
    @Nonnull
    public EpicsSystemVariable<V> createValue(@Nonnull final EV value,
                                              @CheckForNull final EM metadata,
                                              final boolean connected) {
        throw new UnsupportedOperationException("DESY type factory does not support variable creation without channel name.");
    }

    /**
     * The {@link Channel#getFieldType} serving as key in the type factory provider maps.
     * @return
     */
    @Nonnull
    public DBRType getChannelFieldType() {
        return _channelFieldType;
    }

    /**
     * Creates DESY specific system variable from JCA layer, incl. time stamp conversion from
     * epoch 1990-01-01 to 1970-01-01.
     * @param channelName
     * @param eVal
     * @param eMeta
     * @param dMeta
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Nonnull
    public EpicsSystemVariable<V> createValue(@Nonnull final String channelName,
                                              @Nonnull final EV eVal,
                                              @Nonnull final EM eMeta,
                                              @Nonnull final EpicsMetaData dMeta) {

        final TimeInstant timestamp = EpicsSystemVariableSupport.toTimeInstant(eVal);

        final Object data = toData(eVal, eMeta);

        return new EpicsSystemVariable(channelName, data, ControlSystem.EPICS_DEFAULT, timestamp, dMeta);
    }


    @Nonnull
    public <W extends Comparable<? super W>>
    EpicsMetaData createMetaData(@Nonnull final STS eMeta) {
        final EpicsAlarm alarm = new EpicsAlarm(EpicsAlarmSeverity.valueOf(eMeta.getSeverity()),
                                                EpicsAlarmStatus.valueOf(eMeta.getStatus()));
        Short prec = null;
        if (eMeta instanceof PRECISION) {
            prec = Short.valueOf(((PRECISION) eMeta).getPrecision());
        }
        EpicsGraphicsData<W> gr = null;
        IControlLimits<W> cr = null;
        if (eMeta instanceof CTRL) {
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
    public abstract V toData(@Nonnull final DBR eVal, @CheckForNull final EM eMeta);

}
