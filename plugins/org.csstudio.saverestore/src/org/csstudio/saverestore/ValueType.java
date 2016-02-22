package org.csstudio.saverestore;

import org.csstudio.saverestore.data.VDisconnectedData;
import org.diirt.vtype.VBoolean;
import org.diirt.vtype.VBooleanArray;
import org.diirt.vtype.VByte;
import org.diirt.vtype.VByteArray;
import org.diirt.vtype.VDouble;
import org.diirt.vtype.VDoubleArray;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VEnumArray;
import org.diirt.vtype.VFloat;
import org.diirt.vtype.VFloatArray;
import org.diirt.vtype.VInt;
import org.diirt.vtype.VIntArray;
import org.diirt.vtype.VLong;
import org.diirt.vtype.VLongArray;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VShort;
import org.diirt.vtype.VShortArray;
import org.diirt.vtype.VString;
import org.diirt.vtype.VStringArray;
import org.diirt.vtype.VType;

/**
 * <code>ValueType</code> defines all possible value types that are supported by this data provider. The enumeration
 * provides a mapping between the VType instance and the string representation of the type.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public enum ValueType {
    DOUBLE_ARRAY("double_array", VDoubleArray.class),
    FLOAT_ARRAY("float_array", VFloatArray.class),
    LONG_ARRAY("long_array", VLongArray.class),
    INT_ARRAY("int_array", VIntArray.class),
    SHORT_ARRAY("short_array", VShortArray.class),
    BYTE_ARRAY("byte_array", VByteArray.class),
    ENUM_ARRAY("enum_array", VEnumArray.class),
    STRING_ARRAY("string_array", VStringArray.class),
    BOOLEAN_ARRAY("boolean_array", VBooleanArray.class),
    NUMBER_ARRAY("number_array", VNumberArray.class),
    DOUBLE("double", VDouble.class),
    FLOAT("float", VFloat.class),
    LONG("long", VLong.class),
    INT("int", VInt.class),
    SHORT("short", VShort.class),
    BYTE("byte", VByte.class),
    BOOLEAN("boolean", VBoolean.class),
    STRING("string", VString.class),
    ENUM("enum", VEnum.class),
    NODATA("na", VDisconnectedData.class),
    NUMBER("number", VNumber.class);

    private final String typeName;
    private final Class<? extends VType> type;

    private ValueType(String typeName, Class<? extends VType> type) {
        this.typeName = typeName;
        this.type = type;
    }

    /**
     * Returns the interface that represents this value type.
     *
     * @return the interface class
     */
    public Class<? extends VType> getType() {
        return type;
    }

    /**
     * Return the name of the value type.
     *
     * @return the name
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Returns whether the vtype is an instance of this value type.
     *
     * @param type the type that which instance needs to be checked
     * @return true if vtype is instance of this or false if not
     */
    public boolean instanceOf(VType type) {
        return this.type.isAssignableFrom(type.getClass());
    }

    /**
     * Returns true if this value type represents an array of false otherwise.
     *
     * @return true if it is an array or false otherwise
     */
    public boolean isArray() {
        return ordinal() < DOUBLE.ordinal();
    }

    /**
     * Returns the value type that matches the given name.
     *
     * @param name the name to match
     * @return the value type, where {@link ValueType#name} is equals to <code>name</code> parameter
     */
    public static ValueType forName(String typeName) {
        ValueType[] values = values();
        for (ValueType v : values) {
            if (v.typeName.equalsIgnoreCase(typeName)) {
                return v;
            }
        }
        return NODATA;
    }

    /**
     * Transforms the vtype to a string representing the type of the vtype (e.g. double, string_array etc.).
     *
     * @param type the type to transform
     * @return the value type as string
     */
    public static String vtypeToStringType(VType type) {
        for (ValueType t : ValueType.values()) {
            if (t.instanceOf(type)) {
                return t.typeName;
            }
        }
        throw new IllegalArgumentException("Unknown data type " + type.getClass() + ".");
    }
}
