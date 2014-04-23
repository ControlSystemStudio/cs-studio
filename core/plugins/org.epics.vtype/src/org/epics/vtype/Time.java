/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype;

import org.epics.util.time.Timestamp;

/**
 * Time information.
 *
 * @author carcassi
 */
public interface Time {
    
    /**
     * The timestamp of the value, typically indicating when it was
     * generated. If never connected, it returns the
     * time when it was last determined that no connection was made.
     * 
     * @return the timestamp
     */
    Timestamp getTimestamp();
    
    /**
     * Returns a user defined tag, that can be used to store extra
     * time information, such as beam shot.
     *
     * @return the user tag
     */
    Integer getTimeUserTag();

    /**
     * Returns a data source specific flag to indicate whether the time
     * information should be trusted. Typical cases are when records
     * were not processes and the timestamp has a zero time.
     *
     * @return true if the time information is valid
     */
    boolean isTimeValid();
}
