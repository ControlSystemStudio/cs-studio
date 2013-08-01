/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jdbc;

import org.epics.pvmanager.service.Service;

/**
 * A pvmanager service based on database queries.
 *
 * @author carcassi
 */
public class JDBCService extends Service {

    /**
     * Creates a new database service from the given service description.
     * <p>
     * The description will consist of connection parameters, queries
     * and how arguments and results should be mapped.
     * 
     * @param serviceDescription 
     */
    public JDBCService(JDBCServiceDescription serviceDescription) {
        super(serviceDescription.createService());
    }
    
}
