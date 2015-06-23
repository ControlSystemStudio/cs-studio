package org.csstudio.dal.simple.impl;

import java.util.Arrays;

import org.csstudio.dal.DynamicValueProperty;

public final class StringSeqAnyDataImpl extends AbstractAnyDataImpl<String[]> {

    public static final String[] UNINITIALIZED_VALUE = new String[]{""};

    public StringSeqAnyDataImpl(DynamicValueProperty<String[]> property, long beamID) {
        super(property, beamID);
    }
    public StringSeqAnyDataImpl(DynamicValueProperty<String[]> property) {
        super(property, Long.MIN_VALUE);
    }

    public Object[] anySeqValue() {
        return response.getValue();
    }

    public Object anyValue() {
        return response.getValue();
    }

    public double[] doubleSeqValue() {
        return DataUtil.toDoubleSeq(response.getValue());
    }

    public double doubleValue() {
        try {
            return new Double(response.getValue()[0]);
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    public long[] longSeqValue() {
        return DataUtil.toLongSeq(response.getValue());
    }

    public long longValue() {
        try {
            return DataUtil.castTo(response.getValue()[0], Long.class);
        } catch (Exception e) {
            return Long.MIN_VALUE; // TODO any better idea?
        }
    }

    public Number[] numberSeqValue() {
        return DataUtil.toNumberSeq(response.getValue());
    }

    public Number numberValue() {
        return DataUtil.castTo(response.getValue()[0], Double.class);
    }

    public String[] stringSeqValue() {
        return response.getValue();
    }

    public String stringValue() {
        return Arrays.toString(response.getValue());
    }
    @Override
    protected String[] confirmValue(String[] value) {
        if (value != null) return value;
        return UNINITIALIZED_VALUE;
    }

}
