/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
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
