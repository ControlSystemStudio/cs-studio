/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 * Exception thrown when a {@link PVReader} or {@link PVWriter} exceed their
 * timeout.
 *
 * @author carcassi
 */
public class TimeoutException extends RuntimeException {

    public TimeoutException(String message) {
        super(message);
    }
    
}
