/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epics.util.array.CircularBufferDouble;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListNumber;
import org.epics.util.text.StringUtil;
import org.epics.util.time.Timestamp;
import org.epics.util.time.TimestampFormat;
import org.epics.vtype.Alarm;
import org.epics.vtype.Time;
import org.epics.vtype.VEnum;
import org.epics.vtype.VEnumArray;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.ValueUtil;

/**
 *
 * @author carcassi
 */
public class CSVIO {

    // TODO: we should take these from a default place
    private static TimestampFormat timeFormat = new TimestampFormat(
            "yyyy/MM/dd HH:mm:ss.N Z"); //$NON-NLS-1$

    public void export(Object value, Writer writer) {
        if (!canExport(value)) {
            throw new IllegalArgumentException("Type " + value.getClass().getSimpleName() + " is not supported by this data export.");
        }

        try {
            Time time = ValueUtil.timeOf(value);
            if (time != null && time.getTimestamp() != null) {
                writer.append("\"")
                        .append(timeFormat.format(time.getTimestamp()))
                        .append("\" ");
            }

            Alarm alarm = ValueUtil.alarmOf(value);
            if (alarm != null) {
                writer.append(alarm.getAlarmSeverity().name())
                        .append(" ")
                        .append(alarm.getAlarmName());
            }

            if (value instanceof VNumber) {
                writer.append(" ")
                        .append(Double.toString(((VNumber) value).getValue().doubleValue()));
            }

            if (value instanceof VString) {
                writer.append(" \"")
                        .append(((VString) value).getValue())
                        .append("\"");
            }

            if (value instanceof VEnum) {
                writer.append(" \"")
                        .append(((VEnum) value).getValue())
                        .append("\"");
            }

            if (value instanceof VNumberArray) {
                ListNumber data = ((VNumberArray) value).getData();
                for (int i = 0; i < data.size(); i++) {
                    writer.append(" ")
                            .append(Double.toString(data.getDouble(i)));
                }
            }

            if (value instanceof VStringArray) {
                List<String> data = ((VStringArray) value).getData();
                for (int i = 0; i < data.size(); i++) {
                    writer.append(" \"")
                            .append(data.get(i))
                            .append("\"");
                }
            }

            if (value instanceof VEnumArray) {
                List<String> data = ((VEnumArray) value).getData();
                for (int i = 0; i < data.size(); i++) {
                    writer.append(" \"")
                            .append(data.get(i))
                            .append("\"");
                }
            }

            if (value instanceof VTable) {
                VTable table = (VTable) value;
                boolean first = true;
                for (int i = 0; i < table.getColumnCount(); i++) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(" ");
                    }
                    writer.append("\"")
                            .append(table.getColumnName(i))
                            .append("\"");
                }
                writer.append("\n");
                for (int row = 0; row < table.getRowCount(); row++) {
                    first = true;
                    for (int column = 0; column < table.getColumnCount(); column++) {
                        if (first) {
                            first = false;
                        } else {
                            writer.append(" ");
                        }
                        writer.append(toString(table, row, column));
                    }
                    writer.append("\n");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Write failed", e);
        }
    }
    
    public VTable importVTable(Reader reader) {
        try {
            BufferedReader br = new BufferedReader(reader);
            
            // Parse column names
            String titleLine = br.readLine();
            List<String> columnNames = new ArrayList<>();
            for (Object token : StringUtil.parseCSVLine(titleLine, "\\s*")) {
                if (token instanceof String) {
                    columnNames.add((String) token);
                } else {
                    throw new IllegalArgumentException("First line must be column names (quoted strings separated by spaces)");
                }
            }
            
            String line;
            List<Object> columnData = new ArrayList<>(Collections.nCopies(columnNames.size(), null));
            List<Class<?>> columnTypes = new ArrayList<>();
            columnTypes.addAll(Collections.nCopies(columnNames.size(), (Class<Object>) null));
            while ((line = br.readLine()) != null) {
                List<Object> tokens = StringUtil.parseCSVLine(line, "\\s*");
                if (tokens.size() != columnNames.size()) {
                    throw new IllegalArgumentException("All rows need to have the same number of elements");
                }
                
                for (int i = 0; i < tokens.size(); i++) {
                    Object token = tokens.get(i);
                    
                    // Token is a String
                    if (token instanceof String) {
                        // Add list for the column
                        if (columnData.get(i) == null) {
                            columnData.set(i, new ArrayList<String>());
                            columnTypes.set(i, String.class);
                        }
                        
                        // If type does not match, end
                        if (!(columnData.get(i) instanceof List)) {
                            throw new IllegalArgumentException("Column " + i + " is not made of homogeneous elements (all strings or all numbers)");
                        }
                        
                        // Add element
                        @SuppressWarnings("unchecked")
                        List<String> data = (List<String>) columnData.get(i);
                        data.add((String) token);
                    }
                    
                    // Token is a Double
                    if (token instanceof Double) {
                        // Add list for the column
                        if (columnData.get(i) == null) {
                            columnData.set(i, new CircularBufferDouble(1000000));
                            columnTypes.set(i, double.class);
                        }
                        
                        // If type does not match, end
                        if (!(columnData.get(i) instanceof CircularBufferDouble)) {
                            throw new IllegalArgumentException("Column " + i + " is not made of homogeneous elements (all strings or all numbers)");
                        }
                        
                        // Add element
                        @SuppressWarnings("unchecked")
                        CircularBufferDouble data = (CircularBufferDouble) columnData.get(i);
                        data.addDouble((Double) token);
                    }
                }
            }
            
            return ValueFactory.newVTable(columnTypes, columnNames, columnData);
        } catch (IOException ex) {
            throw new RuntimeException("Couldn't process data", ex);
        }
    }
    
    private String toString(VTable table, int row, int column) {
        Class<?> clazz = table.getColumnType(column);
        if (clazz.equals(String.class)) {
            return "\"" + ((List) table.getColumnData(column)).get(row) + "\"";
        }
        if (clazz.equals(Double.TYPE)) {
            return Double.toString(((ListDouble) table.getColumnData(column)).getDouble(row));
        }
        if (clazz.equals(Integer.TYPE)) {
            return Integer.toString(((ListInt) table.getColumnData(column)).getInt(row));
        }
        if (clazz.equals(Timestamp.class)) {
            @SuppressWarnings("unchecked")
            List<?> timestamp = (List<?>) table.getColumnData(column);
            return "\"" + timeFormat.format(timestamp.get(row)) + "\"";
        }
        throw new UnsupportedOperationException("Can't export columns of type " + clazz.getSimpleName());
    }

    public boolean canExport(Object data) {
        if (data instanceof VNumber) {
            return true;
        }

        if (data instanceof VString) {
            return true;
        }

        if (data instanceof VEnum) {
            return true;
        }

        if (data instanceof VNumberArray) {
            return true;
        }

        if (data instanceof VStringArray) {
            return true;
        }

        if (data instanceof VEnumArray) {
            return true;
        }

        if (data instanceof VTable) {
            return true;
        }

        return false;
    }
}
