/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jca;

import java.util.Collection;
import org.epics.pvmanager.DataSourceTypeAdapterSet;

/**
 *
 * @author carcassi
 */
public interface JCATypeAdapterSet extends DataSourceTypeAdapterSet {
    
    @Override
    Collection<JCATypeAdapter> getAdapters();
}
