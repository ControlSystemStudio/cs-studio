/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jdbc;

import org.epics.pvmanager.service.Service;

/**
 *
 * @author carcassi
 */
public class JDBCService extends Service {

    public JDBCService(JDBCServiceDescription serviceDescription) {
        super(serviceDescription.createService());
    }
    
}
