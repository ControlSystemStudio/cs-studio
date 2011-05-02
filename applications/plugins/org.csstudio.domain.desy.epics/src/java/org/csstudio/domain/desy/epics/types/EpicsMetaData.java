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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.platform.util.StringUtil;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * TODO (bknerr) : Consider a hierarchical data structure for meta data
 * instead of this data bag.
 *
 * @author bknerr
 * @since Mar 4, 2011
 */
public class EpicsMetaData {

    private final EpicsGraphicsData<? extends Comparable<?>> _graphicsData;
    private final IControlLimits<? extends Comparable<?>> _ctrlLimits;
    private final Short _precision;
    private final EpicsAlarm _alarm;
    private final Map<Integer, EpicsEnum> _stateMap;



    /**
     * Constructor.
     */
    public EpicsMetaData(@Nonnull final String[] states) {
        _stateMap = initStateMap(states);

        _alarm = null;
        _graphicsData = null;
        _ctrlLimits = null;
        _precision = null;
    }

    private Map<Integer, EpicsEnum> initStateMap(@Nonnull final String[] states) {
        if (states.length == 0) {
            throw new IllegalArgumentException("States array for enumerated values is empty.");
        }
        final LinkedHashMap<Integer, EpicsEnum> stateMap = Maps.newLinkedHashMap();
        int i = 0;
        for (final String state : states) {
            // States may contain a lot of empty strings, as EPICS uses them this way
            if (!StringUtil.isBlank(state)) {
                stateMap.put(Integer.valueOf(i), EpicsEnum.create(i, state, null));
            }
            i++;
        }
        return stateMap;
    }

    /**
     * Constructor.
     */
    public EpicsMetaData(@Nullable final EpicsAlarm alarm,
                         @Nullable final EpicsGraphicsData<? extends Comparable<?>> gr,
                         @Nullable final IControlLimits<? extends Comparable<?>> ctrl,
                         @Nullable final Short precision) {
        _alarm = alarm;
        _graphicsData = gr;
        _ctrlLimits = ctrl;
        if (_graphicsData != null && _ctrlLimits != null &&
            !gr.getAlarmHigh().getClass().equals(_ctrlLimits.getCtrlHigh().getClass())) {
                throw new IllegalArgumentException("Type mismatch on object construction. Meta data for ctrl limits and " +
                		                           "graphics don't have the same class type.");
        }
        _precision = precision;

        _stateMap = Collections.emptyMap();
    }

    @CheckForNull
    public EpicsGraphicsData<?> getGrData() {
        return _graphicsData;
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

    /**
     * Returns an immutable copy of the values of a linked hash map.
     * That means the underlying set of states is ordered according to the order
     * of the array which was used to construct this object.
     * @return an immutable copy of the states.
     */
    @CheckForNull
    public ImmutableSet<EpicsEnum> getStates() {
        return ImmutableSet.<EpicsEnum>builder().addAll(_stateMap.values()).build();
    }

    @CheckForNull
    public EpicsEnum getState(final int index) {
        if (_stateMap.containsKey(Integer.valueOf(index))) {
            return _stateMap.get(Integer.valueOf(index));
        }
        return null;
    }
}
