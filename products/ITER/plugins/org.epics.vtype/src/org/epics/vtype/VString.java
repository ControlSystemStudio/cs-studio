/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype;

/**
 * Scalar string with alarm and timestamp.
 *
 * @author carcassi
 */
public interface VString extends Scalar, Alarm, Time, VType {
    
    /**
     * {@inheritDoc }
     */
    @Override
    String getValue();
}
