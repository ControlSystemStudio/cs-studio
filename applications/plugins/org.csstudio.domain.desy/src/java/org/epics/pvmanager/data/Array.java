/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.util.List;

/**
 * Multi dimensional array, which can be used for waveforms or more rich data.
 *
 * @param <T> the type for the multi channel values
 * @author carcassi
 */
public interface Array<T> {

    Object getArray();

    List<Integer> getSizes();
}
