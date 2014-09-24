/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.vtype.json;

import java.util.List;
import javax.json.JsonObject;
import org.epics.util.array.ListNumber;
import org.epics.vtype.VBoolean;
import org.epics.vtype.VBooleanArray;
import org.epics.vtype.VEnum;
import org.epics.vtype.VEnumArray;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;
import static org.epics.vtype.ValueFactory.*;
import org.epics.vtype.table.VTableFactory;

/**
 * 
 * @author carcassi
 */
class VTypeToJsonV1 {

    static VType toVType(JsonObject json) {
        switch(typeNameOf(json)) {
            case "VDouble":
            case "VFloat":
            case "VLong":
            case "VInt":
            case "VShort":
            case "VByte":
                return toVNumber(json);
            case "VDoubleArray":
            case "VFloatArray":
            case "VLongArray":
            case "VIntArray":
            case "VShortArray":
            case "VByteArray":
                return toVNumberArray(json);
            case "VBoolean":
                return toVBoolean(json);
            case "VBooleanArray":
                return toVBooleanArray(json);
            case "VString":
                return toVString(json);
            case "VStringArray":
                return toVStringArray(json);
            case "VEnum":
                return toVEnum(json);
            case "VEnumArray":
                return toVEnumArray(json);
            case "VTable":
                return toVTable(json);
            default:
                throw new UnsupportedOperationException("Not implemented yet");
        }
    }
    
    static String typeNameOf(JsonObject json) {
        JsonObject type = json.getJsonObject("type");
        if (type == null) {
            return null;
        }
        return type.getString("name");
    }
    
    static JsonObject toJson(VType vType) {
        if (vType instanceof VNumber) {
            return toJson((VNumber) vType);
        } else if (vType instanceof VNumberArray) {
            return toJson((VNumberArray) vType);
        } else if (vType instanceof VBoolean) {
            return toJson((VBoolean) vType);
        } else if (vType instanceof VBooleanArray) {
            return toJson((VBooleanArray) vType);
        } else if (vType instanceof VString) {
            return toJson((VString) vType);
        } else if (vType instanceof VStringArray) {
            return toJson((VStringArray) vType);
        } else if (vType instanceof VEnum) {
            return toJson((VEnum) vType);
        } else if (vType instanceof VEnumArray) {
            return toJson((VEnumArray) vType);
        } else if (vType instanceof VTable) {
            return toJson((VTable) vType);
        }
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    static VNumber toVNumber(JsonObject json) {
        VTypeJsonMapper mapper = new VTypeJsonMapper(json);
        Number value;
        switch(mapper.getTypeName()) {
            case "VDouble":
                value = mapper.getJsonNumber("value").doubleValue();
                break;
            case "VFloat":
                value = (float) mapper.getJsonNumber("value").doubleValue();
                break;
            case "VLong":
                value = (long) mapper.getJsonNumber("value").longValue();
                break;
            case "VInt":
                value = (int) mapper.getJsonNumber("value").intValue();
                break;
            case "VShort":
                value = (short) mapper.getJsonNumber("value").intValue();
                break;
            case "VByte":
                value = (byte) mapper.getJsonNumber("value").intValue();
                break;
            default:
                throw new UnsupportedOperationException("Not implemented yet");
        }
        return newVNumber(value, mapper.getAlarm(), mapper.getTime(), mapper.getDisplay());
    }
    
    static VString toVString(JsonObject json) {
        VTypeJsonMapper mapper = new VTypeJsonMapper(json);
        return newVString(mapper.getString("value"), mapper.getAlarm(), mapper.getTime());
    }
    
    static VStringArray toVStringArray(JsonObject json) {
        VTypeJsonMapper mapper = new VTypeJsonMapper(json);
        return newVStringArray(mapper.getListString("value"), mapper.getAlarm(), mapper.getTime());
    }
    
    static VEnum toVEnum(JsonObject json) {
        VTypeJsonMapper mapper = new VTypeJsonMapper(json);
        List<String> labels = mapper.getJsonObject("enum").getListString("labels");
        return newVEnum(mapper.getInt("value"), labels, mapper.getAlarm(), mapper.getTime());
    }
    
    static VEnumArray toVEnumArray(JsonObject json) {
        VTypeJsonMapper mapper = new VTypeJsonMapper(json);
        return newVEnumArray(mapper.getListInt("value"), mapper.getJsonObject("enum").getListString("labels"), mapper.getAlarm(), mapper.getTime());
    }
    
    static VTable toVTable(JsonObject json) {
        VTypeJsonMapper mapper = new VTypeJsonMapper(json);
        List<Class<?>> types = mapper.getColumnTypes("columnTypes");
        return newVTable(types, mapper.getListString("columnNames"), mapper.getColumnValues("columnValues", types));
    }
    
    static VNumberArray toVNumberArray(JsonObject json) {
        VTypeJsonMapper mapper = new VTypeJsonMapper(json);
        ListNumber value;
        switch(mapper.getTypeName()) {
            case "VDoubleArray":
                value = mapper.getListDouble("value");
                break;
            case "VFloatArray":
                value = mapper.getListFloat("value");
                break;
            case "VLongArray":
                value = mapper.getListLong("value");
                break;
            case "VIntArray":
                value = mapper.getListInt("value");
                break;
            case "VShortArray":
                value = mapper.getListShort("value");
                break;
            case "VByteArray":
                value = mapper.getListByte("value");
                break;
            default:
                throw new UnsupportedOperationException("Not implemented yet");
        }
        return newVNumberArray(value, mapper.getAlarm(), mapper.getTime(), mapper.getDisplay());
    }
    
    static VBoolean toVBoolean(JsonObject json) {
        VTypeJsonMapper mapper = new VTypeJsonMapper(json);
        return newVBoolean(mapper.getBoolean("value"), mapper.getAlarm(), mapper.getTime());
    }
    
    static VBooleanArray toVBooleanArray(JsonObject json) {
        VTypeJsonMapper mapper = new VTypeJsonMapper(json);
        return newVBooleanArray(mapper.getListBoolean("value"), mapper.getAlarm(), mapper.getTime());
    }
    
    static JsonObject toJson(VNumber vNumber) {
        return new JsonVTypeBuilder()
                .addType(vNumber)
                .addObject("value", vNumber.getValue())
                .addAlarm(vNumber)
                .addTime(vNumber)
                .addDisplay(vNumber)
                .build();
    }
    
    static JsonObject toJson(VNumberArray vNumberArray) {
        return new JsonVTypeBuilder()
                .addType(vNumberArray)
                .addObject("value", vNumberArray.getData())
                .addAlarm(vNumberArray)
                .addTime(vNumberArray)
                .addDisplay(vNumberArray)
                .build();
    }
    
    static JsonObject toJson(VBoolean vBoolean) {
        return new JsonVTypeBuilder()
                .addType(vBoolean)
                .add("value", vBoolean.getValue())
                .addAlarm(vBoolean)
                .addTime(vBoolean)
                .build();
    }
    
    static JsonObject toJson(VBooleanArray vBooleanArray) {
        return new JsonVTypeBuilder()
                .addType(vBooleanArray)
                .addObject("value", vBooleanArray.getData())
                .addAlarm(vBooleanArray)
                .addTime(vBooleanArray)
                .build();
    }
    
    static JsonObject toJson(VString vString) {
        return new JsonVTypeBuilder()
                .addType(vString)
                .add("value", vString.getValue())
                .addAlarm(vString)
                .addTime(vString)
                .build();
    }
    
    static JsonObject toJson(VStringArray vStringArray) {
        return new JsonVTypeBuilder()
                .addType(vStringArray)
                .addListString("value", vStringArray.getData())
                .addAlarm(vStringArray)
                .addTime(vStringArray)
                .build();
    }
    
    static JsonObject toJson(VEnum vEnum) {
        return new JsonVTypeBuilder()
                .addType(vEnum)
                .add("value", vEnum.getIndex())
                .addAlarm(vEnum)
                .addTime(vEnum)
                .addEnum(vEnum)
                .build();
    }
    
    static JsonObject toJson(VEnumArray vEnum) {
        return new JsonVTypeBuilder()
                .addType(vEnum)
                .addListNumber("value", vEnum.getIndexes())
                .addAlarm(vEnum)
                .addTime(vEnum)
                .addEnum(vEnum)
                .build();
    }
    
    static JsonObject toJson(VTable vTable) {
        return new JsonVTypeBuilder()
                .addType(vTable)
                .addListString("columnNames", VTableFactory.columnNames(vTable))
                .addListColumnType("columnTypes", VTableFactory.columnTypes(vTable))
                .addListColumnValues("columnValues", vTable)
                .build();
    }
}
