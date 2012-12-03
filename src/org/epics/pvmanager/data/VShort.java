/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

/**
 * Scalar short with alarm, timestamp, display and control information.
 * Auto-unboxing makes the extra method for the primitive type
 * unnecessary.
 * 
 * @author carcassi
 */
public interface VShort extends VNumber, VType {
    /**
     * {@inheritDoc }
     */
    @Override
    Short getValue();
}
