/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype;

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
    List<String> getLabels();

}
