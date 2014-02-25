/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype;

import java.util.List;
import org.epics.util.array.CollectionNumbers;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListNumber;

/**
 * Multi dimensional array, which can be used for waveforms or more rich data.
 * <p>
 * The data is stored in a linear structure. The sizes array gives the dimensionality
 * and size for each dimension.
 *
 * @author carcassi
 */
public interface Array {

    /**
     * Return the object containing the array data.
     * <p>
     * This method will either return a {@link List} or a {@link ListNumber}
     * depending of the array type. A collection is returned, instead of an
     * array, so that the type implementation can be immutable or can at
     * least try to prevent modifications. ListNumber has also several
     * advantages over the Java arrays, including the ability to iterate the list
     * regardless of numeric type.
     * <p>
     * If a numeric array is actually needed, refer to {@link CollectionNumbers}.
     * 
     * @return 
     */
    Object getData();

    /**
     * 
     * 
     * @return 
     */
    ListInt getSizes();
}
