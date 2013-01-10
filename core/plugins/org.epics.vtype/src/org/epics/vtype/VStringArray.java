/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype;

import java.util.List;

/**
 *
 * @author carcassi
 */
public interface VStringArray extends Array, Alarm, Time, VType {
    @Override
    List<String> getData();
}
