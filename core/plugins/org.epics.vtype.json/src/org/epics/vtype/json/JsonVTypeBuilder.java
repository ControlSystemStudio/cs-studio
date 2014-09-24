/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import org.epics.util.array.ListBoolean;
import org.epics.util.array.ListByte;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListLong;
import org.epics.util.array.ListNumber;
import org.epics.util.array.ListShort;
import org.epics.vtype.Alarm;
import org.epics.vtype.Display;
import org.epics.vtype.Time;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;
import org.epics.vtype.ValueUtil;

/**
 *
 * @author carcassi
 */
class JsonVTypeBuilder implements JsonObjectBuilder {
    
    private final JsonObjectBuilder builder = Json.createObjectBuilder();

    @Override
    public JsonVTypeBuilder add(String string, JsonValue jv) {
        builder.add(string, jv);
        return this;
    }

    @Override
    public JsonVTypeBuilder add(String string, String string1) {
        builder.add(string, string1);
        return this;
    }

    @Override
    public JsonVTypeBuilder add(String string, BigInteger bi) {
        builder.add(string, bi);
        return this;
    }

    @Override
    public JsonVTypeBuilder add(String string, BigDecimal bd) {
        builder.add(string, bd);
        return this;
    }

    @Override
    public JsonVTypeBuilder add(String string, int i) {
        builder.add(string, i);
        return this;
    }

    @Override
    public JsonVTypeBuilder add(String string, long l) {
        builder.add(string, l);
        return this;
    }

    @Override
    public JsonVTypeBuilder add(String string, double d) {
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            builder.addNull(string);
        } else {
            builder.add(string, d);
        }
        return this;
    }

    @Override
    public JsonVTypeBuilder add(String string, boolean bln) {
        builder.add(string, bln);
        return this;
    }

    @Override
    public JsonVTypeBuilder addNull(String string) {
        builder.addNull(string);
        return this;
    }

    @Override
    public JsonVTypeBuilder add(String string, JsonObjectBuilder job) {
        builder.add(string, job);
        return this;
    }

    @Override
    public JsonVTypeBuilder add(String string, JsonArrayBuilder jab) {
        builder.add(string, jab);
        return this;
    }

    @Override
    public JsonObject build() {
        return builder.build();
    }
    
    public JsonVTypeBuilder addAlarm(Alarm alarm) {
        return add("alarm", new JsonVTypeBuilder()
                .add("severity", alarm.getAlarmSeverity().toString())
                .add("status", alarm.getAlarmName()));
    }
    
    public JsonVTypeBuilder addTime(Time time) {
        return add("time", new JsonVTypeBuilder()
                .add("unixSec", time.getTimestamp().getSec())
                .add("nanoSec", time.getTimestamp().getNanoSec())
                .addNullableObject("userTag", time.getTimeUserTag()));
    }
    
    public JsonVTypeBuilder addDisplay(Display display) {
        return add("display", new JsonVTypeBuilder()
                .add("lowAlarm", display.getLowerAlarmLimit())
                .add("highAlarm", display.getUpperAlarmLimit())
                .add("lowDisplay", display.getLowerDisplayLimit())
                .add("highDisplay", display.getUpperDisplayLimit())
                .add("lowWarning", display.getLowerWarningLimit())
                .add("highWarning", display.getUpperDisplayLimit())
                .add("units", display.getUnits()));
    }
    
    public JsonVTypeBuilder addEnum(org.epics.vtype.Enum en) {
        return add("enum", new JsonVTypeBuilder()
                .addListString("labels", en.getLabels()));
    }
    
    private JsonArrayBuilder listStringToJson(List<String> ls) {
        JsonArrayBuilder b = Json.createArrayBuilder();
        for (String element : ls) {
            b.add(element);
        }
        return b;
    }
    
    public JsonVTypeBuilder addListString(String string, List<String> ls) {
        add(string, listStringToJson(ls));
        return this;
    }
    
    public JsonVTypeBuilder addListColumnType(String string, List<Class<?>> ls) {
        JsonArrayBuilder b = Json.createArrayBuilder();
        for (Class<?> element : ls) {
            if (element.equals(String.class)) {
                b.add("String");
            } else if (element.equals(double.class)) {
                b.add("double");
            } else if (element.equals(float.class)) {
                b.add("float");
            } else if (element.equals(long.class)) {
                b.add("long");
            } else if (element.equals(int.class)) {
                b.add("int");
            } else if (element.equals(short.class)) {
                b.add("short");
            } else if (element.equals(byte.class)) {
                b.add("byte");
            }
        }
        add(string, b);
        return this;
    }

    public JsonVTypeBuilder  addListColumnValues(String string, VTable vTable) {
        JsonArrayBuilder b = Json.createArrayBuilder();
        for (int column = 0; column < vTable.getColumnCount(); column++) {
            Class<?> type = vTable.getColumnType(column);
            if (type.equals(String.class)) {
                @SuppressWarnings("unchecked")
                List<String> listString = (List<String>) vTable.getColumnData(column);
                b.add(listStringToJson(listString));
            } else if (type.equals(double.class) || type.equals(float.class) || type.equals(long.class) ||
                    type.equals(int.class) || type.equals(short.class) || type.equals(byte.class)) {
                b.add(listNumberToJson((ListNumber) vTable.getColumnData(column)));
            }
        }
        add(string, b);
        return this;
    }
    
    private JsonArrayBuilder listNumberToJson(ListNumber ln) {
        JsonArrayBuilder b = Json.createArrayBuilder();
        if (ln instanceof ListByte || ln instanceof ListShort || ln instanceof ListInt) {
            for (int i = 0; i < ln.size(); i++) {
                b.add(ln.getInt(i));
            }
        } else if (ln instanceof ListLong) {
            for (int i = 0; i < ln.size(); i++) {
                b.add(ln.getLong(i));
            }
        } else {
            for (int i = 0; i < ln.size(); i++) {
                double value = ln.getDouble(i);
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    b.addNull();
                } else {
                    b.add(value);
                }
            }
        }
        return b;
    }
    
    public JsonVTypeBuilder addListNumber(String string, ListNumber ln) {
        add(string, listNumberToJson(ln));
        return this;
    }
    
    public JsonVTypeBuilder addListBoolean(String string, ListBoolean lb) {
        JsonArrayBuilder b = Json.createArrayBuilder();
        for (int i = 0; i < lb.size(); i++) {
            b.add(lb.getBoolean(i));
        }
        add(string, b);
        return this;
    }
    
    public JsonVTypeBuilder addNullableObject(String string, Object o) {
        if (o == null) {
            addNull(string);
        } else {
            addObject(string, o);
        }
        return this;
    }
    
    public JsonVTypeBuilder addObject(String string, Object o) {
        if (o == null) {
            return this;
        }
        
        if (o instanceof Double || o instanceof Float) {
            add(string, ((Number) o).doubleValue());
        } else if (o instanceof Byte || o instanceof Short || o instanceof Integer) {
            add(string, ((Number) o).intValue());
        } else if (o instanceof Long) {
            add(string, ((Number) o).longValue());
        } else if (o instanceof ListNumber) {
            addListNumber(string, (ListNumber) o);
        } else if (o instanceof ListBoolean) {
            addListBoolean(string, (ListBoolean) o);
        } else {
            throw new UnsupportedOperationException("Class " + o.getClass() + " not supported");
        }
    
        return this;
    }
    
    public JsonVTypeBuilder addType(VType vType) {
        Class<?> clazz = ValueUtil.typeOf(vType);
        return add("type", new JsonVTypeBuilder()
                .add("name", clazz.getSimpleName())
                .add("version", 1));
    }
}
