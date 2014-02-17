/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype;

import org.epics.util.array.ListDouble;

/**
 * Double array with alarm, timestamp, display and control information.
 *
 * @author carcassi
 */
public interface VDoubleArray extends VNumberArray, VType {
    
    /**
     * {@inheritDoc }
     * @return the data
     */
    @Override
    ListDouble getData();
    
}
