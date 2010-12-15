/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

/**
 *
 * @author carcassi
 */
public interface VStringArray extends Array<String>, Alarm, Time {
    @Override
    String[] getArray();
}
