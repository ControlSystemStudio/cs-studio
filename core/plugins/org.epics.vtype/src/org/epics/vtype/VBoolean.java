/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype;

/**
 * Scalar boolean with alarm and timestamp.
 *
 * @author carcassi
 */
public interface VBoolean extends Scalar, Alarm, Time, VType {
    
    /**
     * {@inheritDoc }
     */
    @Override
    Boolean getValue();
}
