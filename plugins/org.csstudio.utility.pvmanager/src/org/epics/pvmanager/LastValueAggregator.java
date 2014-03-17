/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.List;

/**
 * Returns the last value of the list.
 * 
 * @author carcassi
 */
class LastValueAggregator<T> extends Aggregator<T, T> {

    LastValueAggregator(ReadFunction<List<T>> collector) {
        super(collector);
    }

    @Override
    protected T calculate(List<T> data) {
        return data.get(data.size() - 1);
    }

}
