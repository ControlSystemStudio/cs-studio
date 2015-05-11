/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.dal;

import java.util.EnumSet;

import org.csstudio.dal.simple.Severity;
import org.csstudio.dal.simple.impl.DynamicValueConditionConverterUtil;


/**
 * Condition of remote dynamic value. Contains set of states, timestamp when condition first occurred and description.
 *
 * @author ikriznar
 */
public final class DynamicValueCondition implements Severity
{

    public static final String NA_MESSAGE="N/A";
    public static final String CONNECTION_STATE_UPDATE_MESSAGE = "Connection state update.";
    public static final String METADATA_AVAILABLE_MESSAGE = "Metadata available.";

    private final EnumSet<DynamicValueState> states;
    private final Timestamp timestamp;
    private final String description;


    /**
     * Creates new condition.
     * @param states a set of stated defining the condition
     * @param timestamp the tiemstamp of condition, if <code>null</code> a timestamp will be created with current time
     * @param description a short description of condition, can be <code>null</code>
     */
    public DynamicValueCondition(final EnumSet<DynamicValueState> states,
        Timestamp timestamp, final String description)
    {
        super();

        if (timestamp == null) {
            timestamp = new Timestamp();
        }

        this.states = states;
        this.timestamp = timestamp;
        this.description = description;
    }

    /**
     * Creates new condition, with current time for timestamp and null message.
     * @param state a state defining the condition
     */
    public DynamicValueCondition(final DynamicValueState state)
    {
        this(EnumSet.of(state));
    }

    /**
     * Creates new condition, with current time for timestamp and null message.
     * @param states a set of stated defining the condition
     */
    public DynamicValueCondition(final EnumSet<DynamicValueState> states)
    {
        this(states,null,null);
    }

    /**
     * Returns description.
     *
     * @return Returns the description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Returns set of states.
     *
     * @return Returns the states.
     */
    public EnumSet<DynamicValueState> getStates()
    {
        return states;
    }

    /**
     * Returns timestamp of condition.
     *
     * @return Returns the timestamp.
     */
    public Timestamp getTimestamp()
    {
        return timestamp;
    }

    /**
     * Returns <code>true</code> if states contains ERROR state.
     *
     * @return <code>true</code> if states contains ERROR state.
     */
    public boolean isError()
    {
        return states.contains(DynamicValueState.ERROR);
    }

    /**
     * Returns <code>true</code> if states contains ALARM state.
     *
     * @return <code>true</code> if states contains ALARM state.
     */
    public boolean isAlarm()
    {
        return states.contains(DynamicValueState.ALARM);
    }

    /**
     * Returns <code>true</code> if states contains WARNING state.
     *
     * @return <code>true</code> if states contains WARNING state.
     */
    public boolean isWarning()
    {
        return states.contains(DynamicValueState.WARNING);
    }

    /**
     * Returns <code>true</code> if states contains TIMEOUT state.
     *
     * @return <code>true</code> if states contains TIMEOUT state.
     */
    public boolean isTimeout()
    {
        return states.contains(DynamicValueState.TIMEOUT);
    }

    /**
     * Returns <code>true</code> if states contains TIMELAG state.
     *
     * @return <code>true</code> if states contains TIMELAG state.
     */
    public boolean isTimelag()
    {
        return states.contains(DynamicValueState.TIMELAG);
    }

    /**
     * Returns <code>true</code> if states contains LINK_NOT_AVAILABLE state.
     *
     * @return <code>true</code> if states contains LINK_NOT_AVAILABLE state.
     */
    public boolean isLinkNotAvailable()
    {
        return states.contains(DynamicValueState.LINK_NOT_AVAILABLE);
    }

    /**
     * Returns <code>true</code> if states contains NORMAL state.
     *
     * @return <code>true</code> if states contains NORMAL state.
     */
    public boolean isNormal()
    {
        return states.contains(DynamicValueState.NORMAL);
    }

    /**
     * Creates new condition with no description and with set, which is copy of provided state only with included requested states.
     * @param states the states to be included in copy
     * @return new condition
     */
    public DynamicValueCondition deriveConditionWithStates(
        final DynamicValueState... states)
    {
        return new DynamicValueCondition(DynamicValueState.deriveSetWithStates(getStates(), states));
    }

    /**
     * Creates new condition with no description and with set, which is copy of provided state only with excluded requested states.
     * @param states the states to be excluded from copy
     * @return new condition
     */
    public DynamicValueCondition deriveConditionWithoutStates(
        final DynamicValueState... states)
    {
        return new DynamicValueCondition(DynamicValueState.deriveSetWithoutStates(getStates(), states));
    }

    /**
     * Returns <code>true</code> only of all states are inside this condition.
     * @param states the states to be checked for inclusion
     * @return <code>true</code> only of all states are inside this condition
     */
    public boolean containsAllStates(final DynamicValueState... states)
    {
        return DynamicValueState.containsAllStates(getStates(), states);
    }

    /**
     * Returns <code>true</code> only of all states are inside this condition.
     * @param states the states to be checked for inclusion
     * @return <code>true</code> only of all states are inside this condition
     */
    public boolean containsAllStates(final EnumSet<DynamicValueState> states)
    {
        return DynamicValueState.containsAllStates(getStates(), states);
    }

    /**
     * Returns <code>true</code> if at least one of provided states is inside this condition.
     * @param states the states to be checked for inclusion
     * @return <code>true</code> if at least one of provided states is inside this condition
     */
    public boolean containsAnyOfStates(final DynamicValueState... states)
    {
        return DynamicValueState.containsAnyOfStates(getStates(), states);
    }

    /**
     * Returns <code>true</code> if at least one of provided states is inside this condition.
     * @param states the states to be checked for inclusion
     * @return <code>true</code> if at least one of provided states is inside this condition
     */
    public boolean containsAnyOfStates(final EnumSet<DynamicValueState> states)
    {
        return DynamicValueState.containsAnyOfStates(getStates(), states);
    }

    /**
     * Return <code>true</code> if provided condition has same states.
     * @param condition the condition with set to be tested
     * @return <code>true</code> if provided condition has same states
     */
    public boolean areStatesEqual(final DynamicValueCondition condition)
    {
        if (condition == null) {
            return false;
        }

        return DynamicValueState.areSetsEqual(states, condition.states);
    }

    @Override
    public String getSeverityInfo() {
        return DynamicValueConditionConverterUtil.extractSeverityInfo(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb= new StringBuilder(256);
        sb.append(states.toString());
        if (timestamp!=null) {
            sb.append(", ");
            sb.append(timestamp);
        } else {
            sb.append(", no-time");
        }
        if (description!=null) {
            sb.append(", ");
            sb.append(description);
        }
        return sb.toString();
    }

    @Override
    public String descriptionToString() {
        final String result;

        if (description != null) {
            result = description;
        } else {
            result = "NO_STATUS";
        }
        return result;
    }

    public boolean containsDescription() {
        return description != null;
    }

    @Override
    public boolean hasValue() {
        return !containsAnyOfStates(new DynamicValueState[]{DynamicValueState.LINK_NOT_AVAILABLE, DynamicValueState.NO_VALUE, DynamicValueState.TIMEOUT, DynamicValueState.ERROR});
    }

    @Override
    public boolean isInvalid() {
        // TODO is this OK?
        return containsAllStates(DynamicValueState.ERROR);
    }

    @Override
    public boolean isMajor() {
        return containsAllStates(DynamicValueState.ALARM);
    }

    @Override
    public boolean isMinor() {
        return containsAllStates(DynamicValueState.WARNING);
    }

    @Override
    public boolean isOK() {
        return !containsAnyOfStates(new DynamicValueState[]{DynamicValueState.WARNING, DynamicValueState.ALARM, DynamicValueState.ERROR, DynamicValueState.LINK_NOT_AVAILABLE, DynamicValueState.TIMELAG, DynamicValueState.TIMEOUT});
    }
}

/* __oOo__ */
