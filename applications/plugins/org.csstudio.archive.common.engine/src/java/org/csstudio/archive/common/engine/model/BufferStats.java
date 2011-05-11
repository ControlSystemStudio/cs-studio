/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.csstudio.domain.desy.calc.AverageWithExponentialDecayCache;

/** Buffer statistics
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 */
@ThreadSafe
public final class BufferStats {
    @GuardedBy("this")
    private int _maxSize = 0;

    @GuardedBy("this")
    private final AverageWithExponentialDecayCache _avgSize = new AverageWithExponentialDecayCache(0.1);


    public synchronized int getMaxSize() {
        return _maxSize;
    }

    public synchronized double getAverageSize() {
        final Double value  = _avgSize.getValue();
        return value == null ? 0.0 : value.doubleValue();
    }

    public synchronized void reset() {
        _maxSize = 0;
        _avgSize.clear();
    }

    public synchronized void updateSizes(final int size) {
        if (size > _maxSize) {
            _maxSize = size;
        }
        _avgSize.accumulate(Double.valueOf(size));
    }
}
