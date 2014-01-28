/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva;

import java.util.Collection;
import org.epics.pvmanager.DataSourceTypeAdapterSet;

/**
 *
 * @author carcassi
 */
public interface PVATypeAdapterSet extends DataSourceTypeAdapterSet {
    
    @Override
    Collection<PVATypeAdapter> getAdapters();
}
