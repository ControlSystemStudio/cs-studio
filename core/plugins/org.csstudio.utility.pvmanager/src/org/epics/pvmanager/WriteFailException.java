/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 * Exception thrown when a {@link PVWriter} is trying to write to a read-only
 * channel or data source. All subsequent writes to the channel will never terminate.
 *
 * @author carcassi
 */
public class WriteFailException extends RuntimeException {
    
}
