/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

import java.util.List;

/**
 * Metadata for enumerations. 
 *
 * @author carcassi
 */
public interface Enum {

    /**
     * All the possible labels. Never null.
     *
     * @return the possible values
     */
    @Metadata
    List<String> getLabels();

}
