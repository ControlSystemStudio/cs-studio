package org.csstudio.sds.internal.preferences;

public enum CategorizationType {

    NONE("none"),
    DRAWER("drawer"),
    STACK("stack");

    private final String _id;

    private CategorizationType(String id) {
        _id = id;
    }

    public String getId() {
        return _id;
    }

    public static CategorizationType getTypeForId(String type) {
        for (CategorizationType ctEnum : CategorizationType.values()) {
            if (ctEnum.getId().equals(type)) {
                return ctEnum;
            }
        }
        return null;
    }

}
