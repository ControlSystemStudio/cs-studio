package org.csstudio.dal.simple.impl;

import org.csstudio.dal.DynamicValueProperty;

public final class LongAnyDataImpl extends AbstractAnyDataImpl<Long> {

    public static final Long UNINITIALIZED_VALUE = Long.MIN_VALUE;

    public LongAnyDataImpl(DynamicValueProperty<Long> property, long beamID) {
        super(property,beamID);
    }
    public LongAnyDataImpl(DynamicValueProperty<Long> property) {
        super(property,Long.MIN_VALUE);
    }

    public Object[] anySeqValue() {
        return new Object[]{response.getValue()};
    }

    public Object anyValue() {
        return response.getValue();
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
    protected Long confirmValue(Long value) {
        if (value != null) return value;
        return UNINITIALIZED_VALUE;
    }

}
