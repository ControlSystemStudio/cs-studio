package org.csstudio.dal.simple.impl;

import org.csstudio.dal.DynamicValueProperty;

public final class StringAnyDataImpl extends AbstractAnyDataImpl<String> {
	
	public static final String UNINITIALIZED_STRING_VALUE = "";
	public static final Double UNINITIALIZED_DOUBLE_VALUE = Double.NaN;
	
	public StringAnyDataImpl(DynamicValueProperty<String> property, long beamID) {
		super(property,beamID);
	}
	public StringAnyDataImpl(DynamicValueProperty<String> property) {
		super(property, Long.MIN_VALUE);
	}

	public Object[] anySeqValue() {
		return new Object[]{response.getValue()};
	}

	public Object anyValue() {
		return response.getValue();
	}

	public double[] doubleSeqValue() {
		Double d = Double.NaN;
		try {
			d = DataUtil.castTo(response.getValue(), Double.class);
		} catch (Exception e) {}
		return new double[]{d};
	}

	public double doubleValue() {
		Double d = Double.NaN;
		try {
			d = DataUtil.castTo(response.getValue(), Double.class);
		} catch (Exception e) {}
		return d;
	}

	public long[] longSeqValue() {
		try {
			Long d = DataUtil.castTo(response.getValue(), Long.class);
			return new long[]{d};
		} catch (Exception e) {
			return new long[]{Long.MIN_VALUE};
		}
	}

	public long longValue() {
		try {
			return DataUtil.castTo(response.getValue(), Long.class);
		} catch (Exception e) {
			return Long.MIN_VALUE; // TODO any better idea?
		}
	}

	public Number[] numberSeqValue() {
		return new Number[]{response.getNumber()};
	}

	public Number numberValue() {
		return response.getNumber();
	}

	public String[] stringSeqValue() {
		return new String[]{response.getValue()};
	}

	public String stringValue() {
		// return response.toString(); returns the response object 'as String'
		// return response.getValue(); does not work if value is Long!
		try {
			return DataUtil.castTo(response.getValue(), String.class);
		} catch (Exception e) {
			return UNINITIALIZED_STRING_VALUE; // TODO any better idea?
		}
	}
	@Override
	protected String confirmValue(String value) {
		if (value != null) return value;
		return UNINITIALIZED_STRING_VALUE;
	}

}
