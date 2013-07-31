/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.service;

import java.util.Collection;

/**
 * A factory class for Services.
 *
 * @author carcassi
 */
public interface ServiceFactory {
    
    /**
     * Returns a collection of services to be registered.
     * 
     * @return an immutable collection of services; never null
     */
    public Collection<Service> createServives();
    
}
