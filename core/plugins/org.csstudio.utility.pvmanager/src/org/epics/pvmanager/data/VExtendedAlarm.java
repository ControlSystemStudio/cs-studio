/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.util.List;

/**
 * Extended alarm information. This supplements the alarm information
 * given with scalar and array pvs, by showing all the possible alarm
 * conditions specific to a device, and which ones are active
 * at the moment.
 * <p>
 * The data type on the wire will look significantly different. The name
 * and severity of each conditions don't typically change and are considered
 * metadata. The actual alarm state (which ones are on or off) and the error
 * message are what changes at each notification.
 *
 * @author carcassi
 */
public interface VExtendedAlarm extends VType {
    
    /**
     * A single alarm condition that can be on or off.
     */
    public interface Condition {
        
        /**
         * A short name for the alarm condition.
         * 
         * @return condition name
         */
        @Metadata
        public String getName();
        
        /**
         * The severity associated with the alarm.
         * 
         * @return the alarm severity
         */
        @Metadata
        public AlarmSeverity getSeverity();
        
        /**
         * Whether the alarm is on.
         * 
         * @return true if alarm condition is on
         */
        public boolean isActive();
    }
    
    /**
     * All the possible alarm conditions for the device, and
     * whether they are on or off.
     * 
     * @return the alarm conditions
     */
    public List<Condition> getConditions();
    
    
    /**
     * A human readable error message associated to the error(s).
     * 
     * @return a message or null
     */
    public String getMessage();
}
