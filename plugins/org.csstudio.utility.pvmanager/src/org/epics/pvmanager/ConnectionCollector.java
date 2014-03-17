/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.HashMap;
import java.util.Map;

/**
 * A specialized collector to handle multiple channels that can be added/removed
 * dynamically and which gets translated to a single connection flag for a
 * reader or writer.
 *
 * @author carcassi
 */
public class ConnectionCollector implements ReadFunction<Boolean> {

    private final Object lock = new Object();
    private final Map<String, Boolean> channelConnected = new HashMap<>();
    private final Map<String, ConnectionWriteFunction> writeFunctions = new HashMap<>();
    private Boolean connected;
    
    private class ConnectionWriteFunction implements WriteFunction<Boolean> {
        
        private final String name;
        private int counter = 1;

        public ConnectionWriteFunction(String name) {
            this.name = name;
        }

        @Override
        public void writeValue(Boolean newValue) {
            synchronized(lock) {
                if (isClosed()) {
                    throw new IllegalStateException("ConnectionCollector for '" + name + "' was closed.");
                }
                channelConnected.put(name, newValue);
                connected = null;
            }
        }
        
        private void open() {
            counter++;
        }
        
        private boolean isClosed() {
            return counter == 0;
        }

        private void close() {
            counter--;
        }
        
    }

    /**
     * Adds a new channel to the collector and returns the write function
     * to use to change the connection status.
     * 
     * @param name channel name
     * @return the write function
     */
    WriteFunction<Boolean> addChannel(final String name) {
        synchronized (lock) {
            if (channelConnected.containsKey(name)) {
                ConnectionWriteFunction writeFunction = writeFunctions.get(name);
                writeFunction.open();
                return writeFunction;
            } else {
                channelConnected.put(name, false);
                ConnectionWriteFunction writeFunction = new ConnectionWriteFunction(name);
                writeFunctions.put(name, writeFunction);
                connected = null;
                return writeFunction;
            }
        }
    }

    @Override
    public Boolean readValue() {
        synchronized (lock) {
            if (connected == null) {
                connected = calculate(channelConnected);
            }

            return connected;
        }
    }

    /**
     * Calculates the overall connection status based on the status of each
     * channel.
     * <p>
     * For future development, this is the method that one could
     * override to implement a different connection logic.
     * 
     * @param channelConnected the connection status of each channel
     * @return the overall connection status
     */
    boolean calculate(Map<String, Boolean> channelConnected) {
        for (Boolean conn : channelConnected.values()) {
            if (conn != Boolean.TRUE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Remove a channel from the collector.
     * 
     * @param channelName the channel name
     */
    void removeChannel(String channelName) {
        synchronized(lock) {
            ConnectionWriteFunction function = writeFunctions.get(channelName);
            if (function == null) {
                throw new IllegalArgumentException("Trying to remove channel '" + channelName + "' from ConnectionCollector, but it was already removed or never added.");
            } else {
                function.close();
                if (function.isClosed()) {
                    channelConnected.remove(channelName);
                    writeFunctions.remove(channelName);
                    connected = null;
                }
            }
        }
    }
}
