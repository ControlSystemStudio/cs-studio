/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
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
