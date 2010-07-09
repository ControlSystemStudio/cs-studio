package org.epics.css.dal.simple.impl;

import org.epics.css.dal.DynamicValueProperty;

public final class DoubleAnyDataImpl extends AbstractAnyDataImpl<Double> {
	
	public DoubleAnyDataImpl(DynamicValueProperty<Double> property) {
		super(property,Long.MIN_VALUE);
	}
	public DoubleAnyDataImpl(DynamicValueProperty<Double> property, long beamID) {
		super(property, beamID);
	}

	public Object[] anySeqValue() {
		return new Object[]{response.getNumber()};
	}

	public Object anyValue() {
		return response.getNumber();
	}

	public double[] doubleSeqValue() {
		return new double[]{response.getValue()};
	}

	public double doubleValue() {
		return response.getValue();
	}

	public long[] longSeqValue() {
		return new long[]{response.getNumber().longValue()};
	}

	public long longValue() {
		return response.getNumber().longValue();
	}

	public Number[] numberSeqValue() {
		return new Number[]{response.getNumber()};
	}

	public Number numberValue() {
		return response.getNumber();
	}

	public String[] stringSeqValue() {
		return new String[]{response.getNumber().toString()};
	}

	public String stringValue() {
		return response.getNumber().toString();
	}
	
	@Override
	protected Double confirmValue(Double value) {
		if (value != null) return value;
		return Double.NaN;
	}

}
