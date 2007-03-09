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


/**
 * Condition ov remote dynamic value. Contains set of states, timestam when condition first occured and description.
 *
 * @author ikriznar
 */
public class DynamicValueCondition
{
	private EnumSet<DynamicValueState> states;
	private long timestamp;
	private String description;

	/**
	 * Creates new condition.
	 */
	public DynamicValueCondition(EnumSet<DynamicValueState> states,
	    long timestamp, String description)
	{
		super();

		if (timestamp == 0) {
			timestamp = System.currentTimeMillis();
		}

		this.states = states;
		this.timestamp = timestamp;
		this.description = description;
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
	public long getTimestamp()
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
	    DynamicValueState... states)
	{
		EnumSet<DynamicValueState> s = EnumSet.copyOf(this.states);

		for (int i = 0; i < states.length; i++) {
			s.add(states[i]);
		}

		return new DynamicValueCondition(s, 0, description);
	}

	public DynamicValueCondition deriveConditionWithoutStates(
	    DynamicValueState... states)
	{
		EnumSet<DynamicValueState> s = EnumSet.copyOf(this.states);

		for (int i = 0; i < states.length; i++) {
			s.remove(states[i]);
		}

		return new DynamicValueCondition(s, 0, description);
	}

	public boolean containsStates(DynamicValueState... states)
	{
		for (int i = 0; i < states.length; i++) {
			if (!this.states.contains(states[i])) {
				return false;
			}
		}

		return true;
	}

	public boolean areStatesEqual(DynamicValueCondition condition)
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
}

/* __oOo__ */
