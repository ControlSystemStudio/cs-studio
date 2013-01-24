/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

/**
 * Callback for any change in the PV value. Cannot simply use a PropertyChangedListener
 * because the payload of the PV will be typically updated in place for complex
 * data structures, and therefore the data object is the same and would not
 * trigger a PropertyChangedEvent.
 *
 * @author carcassi
 */
public interface PVReaderListener {

    /**
     * Notified when the value of the PV has changed.
     */
    void pvChanged();

}
