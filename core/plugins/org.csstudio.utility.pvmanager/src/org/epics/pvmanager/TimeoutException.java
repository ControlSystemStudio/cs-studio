/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
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

    TimeoutException(String message) {
        super(message);
    }
    
}
