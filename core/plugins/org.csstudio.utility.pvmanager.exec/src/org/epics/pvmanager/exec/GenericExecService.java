/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.exec;

import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceDescription;

/**
 *
 * @author carcassi
 */
public class GenericExecService extends Service {

    public GenericExecService() {
        super(new ServiceDescription("exec", "Command execution service")
                .addServiceMethod(new GenericExecServiceMethod()));
    }
    
}
