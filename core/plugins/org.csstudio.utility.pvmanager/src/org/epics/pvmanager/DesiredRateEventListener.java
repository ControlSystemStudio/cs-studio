/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.pvmanager;

/**
 * Processes the callback for events at the desired rate.
 *
 * @author carcassi
 */
interface DesiredRateEventListener {
    
    /**
     * New event to be processed.
     */
    void desiredRateEvent(DesiredRateEvent event);
}
