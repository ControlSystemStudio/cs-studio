/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.vtype;

import org.epics.util.array.ListInt;

/**
 * Int array with alarm, timestamp, display and control information.
 *
 * @author carcassi
 */
public interface VIntArray extends VNumberArray, VType {
    
    @Override
    ListInt getData();
}
