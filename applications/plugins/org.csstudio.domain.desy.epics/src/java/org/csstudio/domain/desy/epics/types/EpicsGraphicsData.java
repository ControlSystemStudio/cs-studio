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
package org.csstudio.domain.desy.epics.types;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.epics.alarm.IAlarmLimits;
import org.csstudio.domain.desy.types.Limits;

/**
 * jca abstraction <br/>
 * DBR <- DBR_* <- DBR_STS_* <- DBR_Time_* <- DBR_GR_* <- DBR_Ctrl_*
 *
 *        value    status       time          alarmHi/Lo   ctrlHi
 *                 severity                   warnHi/Lo    ctrlLo
 *                                            dispHi/Lo

 *                                         <- DBR_Float|Double_Precision
 *                                            precision
 *
 *     <- DBR_Enum <- DBR_STS_E <- DBR_LABELS_E <- DBR_CTRL_E
 *                                              <- DBR_TIME_LABELS_E
 *                              <- DBR_TIME_E
 *
 * @author bknerr
 * @since Mar 3, 2011
 * @param <V> graphics data originating from epics
 */
public class EpicsGraphicsData<V extends Comparable<? super V>> implements IOperatingRangeLimits<V>,
                                                                           IAlarmLimits<V> {

    private final Limits<V> _alarmLimits;
    private final Limits<V> _warnLimits;
    private final Limits<V> _operatingRange;


    /**
     * Constructor.
     */
    public EpicsGraphicsData(@Nonnull final Limits<V> aLimits,
                             @Nonnull final Limits<V> wLimits,
                             @Nonnull final Limits<V> oLimits) {
        _alarmLimits = aLimits;
        _warnLimits = wLimits;
        _operatingRange = oLimits;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public V getWarnHigh() {
        return _warnLimits.getHigh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public V getWarnLow() {
        return _warnLimits.getLow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public V getAlarmHigh() {
        return _alarmLimits.getHigh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public V getAlarmLow() {
        return _alarmLimits.getLow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public V getHighOperatingRange() {
        return _operatingRange.getHigh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public V getLowOperatingRange() {
        return _operatingRange.getLow();
    }
}
