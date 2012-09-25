/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 * Exception thrown when a {@link PVReader} is trying to read a channel or
 * data source that cannot be read from. All subsequent reads will always
 * return null.
 *
 * @author carcassi
 */
public class ReadFailException extends RuntimeException {
    
}
