package org.csstudio.simplepv;

public class VTypeHelperBean {

    BasicDataType btype;
    Double dval;
    boolean isScalar;

    public VTypeHelperBean(BasicDataType expectedType, Double dval, boolean isScalar) {
        this.btype = expectedType;
        this.dval = dval;
        this.isScalar = true;
    }
}

