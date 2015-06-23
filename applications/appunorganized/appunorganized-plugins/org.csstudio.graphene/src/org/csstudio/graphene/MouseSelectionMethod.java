package org.csstudio.graphene;

public enum MouseSelectionMethod {

    HOVER, CLICK;

    public static String[] labels() {
        return new String[] {"HOVER", "CLICK"};
    }

}
