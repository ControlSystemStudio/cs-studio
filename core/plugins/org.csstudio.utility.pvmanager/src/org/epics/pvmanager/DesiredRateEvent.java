/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.pvmanager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents an event after the rate is decoupled. The event groups multiple
 * events of different types.
 * <p>
 * The class is thread-safe.
 *
 * @author carcassi
 */
class DesiredRateEvent {
    enum Type {READ_CONNECTION, WRITE_CONNECTION, VALUE, READ_EXCEPTION, WRITE_EXCEPTION, WRITE_SUCCEEDED, WRITE_FAILED};
    
    private final List<Type> types = new CopyOnWriteArrayList<>();
    private volatile Exception writeException;

    public List<Type> getTypes() {
        return types;
    }
    
    public void addType(Type type) {
        // TODO: may want to preserve ordering
        if (!types.contains(type)) {
            types.add(type);
        }
    }
    
    public void addWriteFailed(Exception ex) {
        if (writeException != null) {
            throw new UnsupportedOperationException("Right now, only one failed write can be queued");
        }
        types.add(Type.WRITE_FAILED);
        writeException = ex;
    }
}
