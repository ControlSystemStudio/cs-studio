/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager;

import java.util.Collection;

/**
 * A set of type adapters. This optional class is provided to help
 * create a more flexible type support in a datasource, so that support
 * for individual types is done through runtime configuration.
 *
 * @author carcassi
 */
public interface DataSourceTypeAdapterSet {
    
    /**
     * Returns a collation of adapters. The collection must be
     * immutable.
     * 
     * @return a collection; not null
     */
    Collection<? extends DataSourceTypeAdapter<?, ?>> getAdapters();
}
