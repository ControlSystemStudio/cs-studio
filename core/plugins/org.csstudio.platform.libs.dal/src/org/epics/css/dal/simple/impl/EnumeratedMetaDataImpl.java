package org.epics.css.dal.simple.impl;

import org.epics.css.dal.simple.MetaData;

public final class EnumeratedMetaDataImpl implements MetaData {
	
	private final String[] states;
	
	public EnumeratedMetaDataImpl(String[] states) {
		this.states = states;
	}

	public double getAlarmHigh() {
		// default value
		return Double.NaN;
	}

	public double getAlarmLow() {
		// default value
		return Double.NaN;
	}

	public double getDisplayHigh() {
		// default value
		return Double.NaN;
	}

	public double getDisplayLow() {
		// default value
		return Double.NaN;
	}

	public int getPrecision() {
		// default value
		return 0;
	}

	public String getState(int state) {
		return states[state];
	}

	public String[] getStates() {
		String[] s= new String[states.length];
		System.arraycopy(states, 0, s, 0, s.length);
		return s;
	}

	public String getUnits() {
		return null;
	}

	public double getWarnHigh() {
		// default value
		return Double.NaN;
	}

	public double getWarnLow() {
		// default value
		return Double.NaN;
	}

}
