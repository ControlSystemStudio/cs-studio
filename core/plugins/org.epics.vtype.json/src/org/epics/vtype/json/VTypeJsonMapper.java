/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */


package org.epics.vtype.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.epics.util.array.ArrayBoolean;
import org.epics.util.array.ListBoolean;
import org.epics.util.array.ListByte;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListFloat;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListLong;
import org.epics.util.array.ListShort;
import org.epics.util.time.Timestamp;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.Time;
import org.epics.vtype.ValueFactory;
import static org.epics.vtype.json.JsonArrays.*;
/**
 *
 * @author carcassi
 */
class VTypeJsonMapper implements JsonObject {
    
    private final JsonObject json;

    public VTypeJsonMapper(JsonObject json) {
        this.json = json;
    }
    
    public String getTypeName() {
        JsonObject type = json.getJsonObject("type");
        if (type == null) {
            return null;
        }
        return type.getString("name");
    }
    
    public Alarm getAlarm() {
        JsonObject alarm = json.getJsonObject("alarm");
        if (alarm == null) {
            return null;
        }
        return ValueFactory.newAlarm(AlarmSeverity.valueOf(alarm.getString("severity")), alarm.getString("status"));
    }
    
    public Time getTime() {
        VTypeJsonMapper time = getJsonObject("time");
        if (time == null) {
            return null;
        }
        return ValueFactory.newTime(Timestamp.of(time.getInt("unixSec"), time.getInt("nanoSec")), time.getInteger("userTag"), true);
    }
    
    public Display getDisplay() {
        VTypeJsonMapper display = getJsonObject("display");
        if (display == null) {
            return null;
        }
        return ValueFactory.newDisplay(display.getNotNullDouble("lowDisplay"),
                display.getNotNullDouble("lowAlarm"),
                display.getNotNullDouble("lowWarning"),
                display.getString("units"), null,
                display.getNotNullDouble("highWarning"),
                display.getNotNullDouble("highAlarm"),
                display.getNotNullDouble("highDisplay"),
                Double.NaN,
                Double.NaN);
    }
    
    public ListDouble getListDouble(String string) {
        JsonArray array = getJsonArray(string);
        return toListDouble(array);
    }
    
    
    public ListFloat getListFloat(String string) {
        JsonArray array = getJsonArray(string);
        return toListFloat(array);
    }
    
    
    public ListLong getListLong(String string) {
        JsonArray array = getJsonArray(string);
        return toListLong(array);
    }
    
    
    public ListInt getListInt(String string) {
        JsonArray array = getJsonArray(string);
        return toListInt(array);
    }
    
    
    public ListShort getListShort(String string) {
        JsonArray array = getJsonArray(string);
        return toListShort(array);
    }
    
    
    public ListByte getListByte(String string) {
        JsonArray array = getJsonArray(string);
        return toListByte(array);
    }
    

    public ListBoolean getListBoolean(String string) {
        JsonArray array = getJsonArray(string);
        boolean[] values = new boolean[array.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = array.getBoolean(i);
        }
        return new ArrayBoolean(values);
    }
    
    public List<String> getListString(String string) {
        JsonArray array = getJsonArray(string);
        return toListString(array);
    }
    

    public List<Class<?>> getColumnTypes(String string) {
        JsonArray array = getJsonArray(string);
        List<Class<?>> types = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            String type = array.getString(i);
            if ("String".equals(type)) {
                types.add(String.class);
            } else if ("double".equals(type)) {
                types.add(double.class);
            } else if ("float".equals(type)) {
                types.add(float.class);
            } else if ("long".equals(type)) {
                types.add(long.class);
            } else if ("int".equals(type)) {
                types.add(int.class);
            } else if ("short".equals(type)) {
                types.add(short.class);
            } else if ("byte".equals(type)) {
                types.add(byte.class);
            } else {
                throw new IllegalArgumentException("Column type " + type + " not supported");
            }
        }
        return types;
    }

    public List<Object> getColumnValues(String string, List<Class<?>> types) {
        JsonArray array = getJsonArray(string);
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            Class<?> type = types.get(i);
            if (String.class.equals(type)) {
                result.add(toListString(array.getJsonArray(i)));
            } else if (double.class.equals(type)) {
                result.add(toListDouble(array.getJsonArray(i)));
            } else if (float.class.equals(type)) {
                result.add(toListFloat(array.getJsonArray(i)));
            } else if (long.class.equals(type)) {
                result.add(toListLong(array.getJsonArray(i)));
            } else if (int.class.equals(type)) {
                result.add(toListInt(array.getJsonArray(i)));
            } else if (short.class.equals(type)) {
                result.add(toListShort(array.getJsonArray(i)));
            } else if (byte.class.equals(type)) {
                result.add(toListByte(array.getJsonArray(i)));
            } else {
                throw new IllegalArgumentException("Column type " + type + " not supported");
            }
        }
        return result;
    }
    
    public Integer getInteger(String string) {
        if (isNull(string)) {
            return null;
        }
        return getInt(string);
    }
    
    public Double getNotNullDouble(String string) {
        if (isNull(string)) {
            return Double.NaN;
        }
        return getJsonNumber(string).doubleValue();
    }

    @Override
    public JsonArray getJsonArray(String string) {
        return json.getJsonArray(string);
    }

    @Override
    public VTypeJsonMapper getJsonObject(String string) {
        return new VTypeJsonMapper(json.getJsonObject(string));
    }

    @Override
    public JsonNumber getJsonNumber(String string) {
        return json.getJsonNumber(string);
    }

    @Override
    public JsonString getJsonString(String string) {
        return json.getJsonString(string);
    }

    @Override
    public String getString(String string) {
        return json.getString(string);
    }

    @Override
    public String getString(String string, String string1) {
        return json.getString(string, string1);
    }

    @Override
    public int getInt(String string) {
        return json.getInt(string);
    }

    @Override
    public int getInt(String string, int i) {
        return json.getInt(string, i);
    }

    @Override
    public boolean getBoolean(String string) {
        return json.getBoolean(string);
    }

    @Override
    public boolean getBoolean(String string, boolean bln) {
        return json.getBoolean(string, bln);
    }

    @Override
    public boolean isNull(String string) {
        return json.isNull(string);
    }

    @Override
    public ValueType getValueType() {
        return json.getValueType();
    }

    @Override
    public int size() {
        return json.size();
    }

    @Override
    public boolean isEmpty() {
        return json.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return json.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return json.containsValue(value);
    }

    @Override
    public JsonValue get(Object key) {
        return json.get(key);
    }

    @Override
    public JsonValue put(String key, JsonValue value) {
        return json.put(key, value);
    }

    @Override
    public JsonValue remove(Object key) {
        return json.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends JsonValue> m) {
        json.putAll(m);
    }

    @Override
    public void clear() {
        json.clear();
    }

    @Override
    public Set<String> keySet() {
        return json.keySet();
    }

    @Override
    public Collection<JsonValue> values() {
        return json.values();
    }

    @Override
    public Set<Entry<String, JsonValue>> entrySet() {
        return json.entrySet();
    }

}
