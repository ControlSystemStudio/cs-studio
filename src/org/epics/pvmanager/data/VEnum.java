/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

/**
 * Scalar enum with alarm and timestamp.
 * Given that enumerated values are of very limited use without
 * the labels, and that the current label is the data most likely used, the
 * enum is of type {@link String}. The index is provided as an extra field, and
 * the list of all possible values is always provided.
 *
 * @author carcassi
 */
public interface VEnum extends Scalar<String>, Enum, Alarm, Time, VType {

    /**
     * Return the index of the value in the list of labels.
     *
     * @return the current index
     */
    int getIndex();

}
