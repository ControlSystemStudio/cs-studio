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
package org.csstudio.domain.desy.epics.alarm;

import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

/**
 * TODO (bknerr) : Consider a hierarchical data structure for meta data
 * instead of this data bag.
 *
 * @author bknerr
 * @since Mar 4, 2011
 */
public class EpicsMetaData {

    private final EpicsGraphicsData<? extends Comparable<?>> _grData;
    private final IControlLimits<? extends Comparable<?>> _ctrlLimits;
    private final Short _precision;
    private final EpicsAlarm _alarm;
    private final ImmutableSet<String> _states;


    /**
     * Constructor.
     */
    public EpicsMetaData(@Nonnull final Set<String> states) {
        _states = ImmutableSet.<String>builder().addAll(states).build();

        _alarm = null;
        _grData = null;
        _ctrlLimits = null;
        _precision = null;
    }

    /**
     * Constructor.
     */
    public EpicsMetaData(@Nullable final EpicsAlarm alarm,
                         @Nullable final EpicsGraphicsData<? extends Comparable<?>> gr,
                         @Nullable final IControlLimits<? extends Comparable<?>> ctrl,
                         @Nullable final Short precision) {
        _alarm = alarm;
        _grData = gr;
        _ctrlLimits = ctrl;
        if (_grData != null && _ctrlLimits != null &&
            !gr.getAlarmHigh().getClass().equals(_ctrlLimits.getCtrlHigh().getClass())) {
                throw new IllegalArgumentException("Type mismatch on object construction. Meta data for ctrl limits and graphics don't have the same class type.");
        }
        _precision = precision;

        _states = null;
    }

    @CheckForNull
    public EpicsGraphicsData<?> getGrData() {
        return _grData;
    }

    @CheckForNull
    public IControlLimits<?> getCtrlLimits() {
        return _ctrlLimits;
    }

    @CheckForNull
    public Short getPrecision() {
        return _precision;
    }
    @CheckForNull
    public EpicsAlarm getAlarm() {
        return _alarm;
    }

    @CheckForNull
    public ImmutableSet<String> getStates() {
        return _states;
    }
}
