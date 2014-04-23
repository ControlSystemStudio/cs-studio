/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

/**
 * Callback for any change in the PV value. Cannot simply use a PropertyChangedListener
 * because the payload of the PV will be typically updated in place for complex
 * data structures, and therefore the data object is the same and would not
 * trigger a PropertyChangedEvent.
 *
 * @param <T> the type of reader for the listener
 * @author carcassi
 */
public interface PVReaderListener<T> {

    /**
     * Notified when the value of the PV has changed.
     * 
     * @param event the reader event
     */
    void pvChanged(PVReaderEvent<T> event);

}
