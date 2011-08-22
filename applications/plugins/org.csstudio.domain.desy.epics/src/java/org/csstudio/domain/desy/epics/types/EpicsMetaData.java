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

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * TODO (bknerr) : Consider a hierarchical data structure for meta data
 * instead of this data bag.
 *
 * @author bknerr
 * @since Mar 4, 2011
 */
public final class EpicsMetaData {

    /**
     * Null object/flyweight pattern (there are a lot of channels in which states array is empty for
     * enum types or display ranges, or alarms are not present.
     */
    private static final EpicsMetaData EMPTY_DATA =
        new EpicsMetaData(null, null, null, null);

    private final EpicsGraphicsData<? extends Comparable<?>> _graphicsData;
    private final IControlLimits<? extends Comparable<?>> _ctrlLimits;
    private final Short _precision;
    private final EpicsAlarm _alarm;
    private final ImmutableList<EpicsEnum> _states;



    /**
     * Constructor.
     */
    private EpicsMetaData(@Nonnull final String[] states) {
        _states = initStateList(states);

        _alarm = null;
        _graphicsData = null;
        _ctrlLimits = null;
        _precision = null;
    }

    /**
     * Constructor.
     */
    private EpicsMetaData(@Nullable final EpicsAlarm alarm,
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

        _states  = ImmutableList.of();
    }

    @Nonnull
    public static EpicsMetaData create(@Nonnull final String[] states) {
        if (states.length == 0) {
            return EMPTY_DATA;
        }
        return new EpicsMetaData(states);
    }

    @Nonnull
    public static EpicsMetaData create(@Nullable final EpicsAlarm alarm,
                                       @Nullable final EpicsGraphicsData<? extends Comparable<?>> gr,
                                       @Nullable final IControlLimits<? extends Comparable<?>> ctrl,
                                       @Nullable final Short precision) {
        if (alarm == null && gr == null && ctrl == null && precision == null) {
            return EMPTY_DATA;
        }
        return new EpicsMetaData(alarm, gr, ctrl, precision);
    }

    @Nonnull
    private ImmutableList<EpicsEnum> initStateList(@Nonnull final String[] states) {
        if (states.length == 0) {
            // throw new IllegalArgumentException("States array for enumerated values is empty.");
            return ImmutableList.of();
        }
        final List<EpicsEnum> enumList = Lists.newArrayListWithExpectedSize(states.length);
        int i = 0;
        for (final String state : states) {
            if (Strings.isNullOrEmpty(state)) {
                enumList.add(EpicsEnum.createFromState(EpicsEnum.UNSET_STATE_STR, i));
            } else {
                enumList.add(EpicsEnum.createFromState(state, i));
            }
            i++;
        }
        return ImmutableList.copyOf(enumList);
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
    public ImmutableList<EpicsEnum> getStates() {
        return _states;
    }

    /**
     * Three cases possible:<br/>
     * <ul>
     *   <li> a list of states exists and index is within bounds -> return the enum holding the state
     *   <li> a list of states exists, but index is out of bounds -> return a newly created enum
     *        from index (==raw value)
     *   <li> a list of states doesn't exist -> return a newly created enum from index (==raw value)
     * </ul>
     * @param index
     * @return
     */
    @Nonnull
    public EpicsEnum getOrCreateState(final int index) {
        if (index >= 0 && index < _states.size()) {
            return _states.get(index);
        }
        return EpicsEnum.createFromRaw(index);
    }
}
