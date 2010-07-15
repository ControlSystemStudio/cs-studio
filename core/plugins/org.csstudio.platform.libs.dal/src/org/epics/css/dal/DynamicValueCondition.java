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

package org.epics.css.dal;

import java.util.EnumSet;

import org.epics.css.dal.simple.Severity;
import org.epics.css.dal.simple.impl.DynamicValueConditionConverterUtil;


/**
 * Condition of remote dynamic value. Contains set of states, timestamp when condition first occurred and description.
 *
 * @author ikriznar
 */
public class DynamicValueCondition implements Severity
{
	private final EnumSet<DynamicValueState> states;
	private final Timestamp timestamp;
	private final String description;

	/**
	 * Creates new condition.
	 * @param states a set of stated defining the condition
	 * @param timestamp the tiemstammp of condition
	 * @param description a short description of condition
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
	 * Creates new condition.
	 * @deprecated use the other constructor: {@link DynamicValueCondition#DynamicValueCondition(EnumSet, Timestamp, String)}
	 */
	@Deprecated
    public DynamicValueCondition(final EnumSet<DynamicValueState> states,
	    final long timestamp, final String description)
	{
		this(states, new Timestamp(timestamp,0),description);
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

	public DynamicValueCondition deriveConditionWithStates(
	    final DynamicValueState... states)
	{
		EnumSet<DynamicValueState> s = EnumSet.copyOf(this.states);

		for (DynamicValueState state : states) {
			s.add(state);
		}

		return new DynamicValueCondition(s, null, description);
	}

	public DynamicValueCondition deriveConditionWithoutStates(
	    final DynamicValueState... states)
	{
		EnumSet<DynamicValueState> s = EnumSet.copyOf(this.states);

		for (DynamicValueState state : states) {
			s.remove(state);
		}

		return new DynamicValueCondition(s, null, description);
	}

	public boolean containsStates(final DynamicValueState... states)
	{
		for (int i = 0; i < states.length; i++) {
			if (!this.states.contains(states[i])) {
				return false;
			}
		}

		return true;
	}

	public boolean containsAnyOfStates(final DynamicValueState... states)
	{
		for (DynamicValueState state : states) {
			if (this.states.contains(state)) {
				return true;
			}
		}

		return false;
	}

	public boolean areStatesEqual(final DynamicValueCondition condition)
	{
		if (condition == null) {
			return false;
		} else if (condition.isAlarm() == isAlarm()
		    && condition.isError() == isError()
		    && condition.isLinkNotAvailable() == isLinkNotAvailable()
		    && condition.isNormal() == isNormal()
		    && condition.isTimelag() == isTimelag()
		    && condition.isTimeout() == isTimeout()
		    && condition.isWarning() == isWarning()) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getSeverityInfo() {
		return DynamicValueConditionConverterUtil.extractSeverityInfo(this);
	}
	
	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder(256);
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

	public boolean hasValue() {
		return !containsAnyOfStates(new DynamicValueState[]{DynamicValueState.LINK_NOT_AVAILABLE, DynamicValueState.NO_VALUE, DynamicValueState.TIMEOUT, DynamicValueState.ERROR});
	}

	public boolean isInvalid() {
		// TODO is this OK?
		return containsStates(DynamicValueState.ERROR);
	}

	public boolean isMajor() {
		return containsStates(DynamicValueState.ALARM);
	}

	public boolean isMinor() {
		return containsStates(DynamicValueState.WARNING);
	}

	public boolean isOK() {
		return !containsAnyOfStates(new DynamicValueState[]{DynamicValueState.WARNING, DynamicValueState.ALARM, DynamicValueState.ERROR, DynamicValueState.LINK_NOT_AVAILABLE, DynamicValueState.TIMELAG, DynamicValueState.TIMEOUT});
	}
}

/* __oOo__ */
