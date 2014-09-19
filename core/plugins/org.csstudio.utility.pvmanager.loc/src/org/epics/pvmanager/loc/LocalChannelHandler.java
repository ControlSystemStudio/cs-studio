/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.loc;

import java.util.ArrayList;
import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.ChannelHandlerReadSubscription;
import org.epics.pvmanager.MultiplexedChannelHandler;
import org.epics.pvmanager.ChannelHandlerWriteSubscription;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.epics.vtype.ValueFactory.*;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ListDouble;
import org.epics.vtype.VDouble;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VEnum;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

/**
 * Implementation for channels of a {@link LocalDataSource}.
 *
 * @author carcassi
 */
class LocalChannelHandler extends MultiplexedChannelHandler<Object, Object> {
    
    private static Logger log = Logger.getLogger(LocalChannelHandler.class.getName());

    LocalChannelHandler(String channelName) {
        super(channelName);
    }

    @Override
    public void connect() {
        processConnection(new Object());
    }

    @Override
    public void disconnect() {
        initialArguments = null;
        type = null;
        processConnection(null);
    }

    @Override
    protected synchronized void addReader(ChannelHandlerReadSubscription subscription) {
        // Override for test visibility purposes
        super.addReader(subscription);
    }

    @Override
    protected synchronized void addWriter(ChannelHandlerWriteSubscription subscription) {
        // Override for test visibility purposes
        super.addWriter(subscription);
    }

    @Override
    protected synchronized void removeReader(ChannelHandlerReadSubscription subscription) {
        // Override for test visibility purposes
        super.removeReader(subscription);
    }

    @Override
    protected synchronized void removeWrite(ChannelHandlerWriteSubscription subscription) {
        // Override for test visibility purposes
        super.removeWrite(subscription);
    }
    
    private Object checkValue(Object value) {
        if (type != null && !type.isInstance(value)) {
            throw new IllegalArgumentException("Value " + value + " is not of type " + type.getSimpleName());
        }
        return value;
    }

    @Override
    public void write(Object newValue, ChannelWriteCallback callback) {
        try {
            // XXX Actual write is not enforcing the type!
            
            if (VEnum.class.equals(type)) {
                // Handle enum writes
                int newIndex = -1;
                // TODO calculate the newIndex from the new value
                // Add error message if type does not match
                VEnum firstEnum = (VEnum) initialValue;
                newValue = ValueFactory.newVEnum(newIndex, firstEnum.getLabels(), alarmNone(), timeNow());
            } else {
            
                // If the string can be parse to a number, do it
                if (newValue instanceof String) {
                    String value = (String) newValue;
                    try {
                        newValue = Double.valueOf(value);
                    } catch (NumberFormatException ex) {
                    }
                }
                // If new value is not a VType, try to convert it
                if (!(newValue instanceof VType)) {
                    newValue = checkValue(ValueFactory.toVTypeChecked(newValue));
                }
            }
            processMessage(newValue);
            callback.channelWritten(null);
        } catch (Exception ex) {
            callback.channelWritten(ex);
        }
    }

    @Override
    protected boolean isWriteConnected(Object payload) {
        return isConnected(payload);
    }
    
    private Object initialArguments;
    private Object initialValue;
    private Class<?> type;
    
    synchronized void setInitialValue(Object value) {
        if (initialArguments != null && !initialArguments.equals(value)) {
            String message = "Different initialization for local channel " + getChannelName() + ": " + value + " but was " + initialArguments;
            log.log(Level.WARNING, message);
            throw new RuntimeException(message);
        }
        initialArguments = value;
        if (getLastMessagePayload() == null) {
            if (VEnum.class.equals(type)) {
                List<?> args = (List<?>) initialArguments;
                // TODO error message if not Number
                int index = ((Number) args.get(0)).intValue();
                List<String> labels = new ArrayList<>();
                for (Object arg : args.subList(1, args.size())) {
                    // TODO error message if not String
                    labels.add((String) arg);
                }
                
                initialValue = ValueFactory.newVEnum(index, labels, alarmNone(), timeNow());
            } else {
                initialValue = checkValue(ValueFactory.toVTypeChecked(value));
            }
            processMessage(initialValue);
        }
    }
    
    synchronized void setType(String typeName) {
        if (typeName == null) {
            return;
        }
        Class<?> newType = null;
        if ("VDouble".equals(typeName)) {
            newType = VDouble.class;
        }
        if ("VString".equals(typeName)) {
            newType = VString.class;
        }
        if ("VDoubleArray".equals(typeName)) {
            newType = VDoubleArray.class;
        }
        if ("VStringArray".equals(typeName)) {
            newType = VStringArray.class;
        }
        if ("VTable".equals(typeName)) {
            newType = VTable.class;
        }
        if ("VEnum".equals(typeName)) {
            newType = VEnum.class;
        }
        if (newType == null) {
            throw new IllegalArgumentException("Type " + typeName + " for channel " + getChannelName() + " is not supported by local datasource.");
        }
        if (type != null && !type.equals(newType)) {
            throw new IllegalArgumentException("Type mismatch for channel " + getChannelName() + ": " + typeName + " but was " + type.getSimpleName());
        }
        type = newType;
    }

    @Override
    public synchronized Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("Name", getChannelName());
        properties.put("Type", type);
        properties.put("Initial Value", initialArguments);
        return properties;
    }
    
}
