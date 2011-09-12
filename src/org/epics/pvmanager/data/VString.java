/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

/**
 * Scalar string with alarm and timestamp.
 *
 * @author carcassi
 */
public interface VString extends Scalar<String>, Alarm, Time {
}
