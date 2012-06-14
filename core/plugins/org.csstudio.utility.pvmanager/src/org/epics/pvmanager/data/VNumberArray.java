/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import org.epics.util.array.ListNumber;

/**
 *
 * @author carcassi
 */
public interface VNumberArray extends Alarm, Time, Display, VType {
    ListNumber getData();
}
