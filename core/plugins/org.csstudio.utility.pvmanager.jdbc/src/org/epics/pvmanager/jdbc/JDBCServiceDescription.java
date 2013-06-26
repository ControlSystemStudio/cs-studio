/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javax.sql.DataSource;
import org.epics.pvmanager.service.ServiceDescription;

/**
 *
 * @author carcassi
 */
public class JDBCServiceDescription {
    
    final ServiceDescription serviceDescription;
    DataSource dataSource;
    ExecutorService executorService;
    private List<JDBCServiceMethodDescription> jdbcServiceMethodDescriptions = new ArrayList<>();
    
    public JDBCServiceDescription(String name, String description) {
        serviceDescription = new ServiceDescription(name, description);
    }
    
    public JDBCServiceDescription addServiceMethod(JDBCServiceMethodDescription jdbcServiceMethodDescription) {
        jdbcServiceMethodDescriptions.add(jdbcServiceMethodDescription);
        return this;
    }
    
    public JDBCServiceDescription dataSource(DataSource dataSource) {
        if (this.dataSource != null) {
            throw new IllegalArgumentException("DataSource was already set");
        }
        this.dataSource = dataSource;
        return this;
    }
    
    public JDBCServiceDescription executorService(ExecutorService executorService) {
        if (this.executorService != null) {
            throw new IllegalArgumentException("ExecutorService was already set");
        }
        this.executorService = executorService;
        return this;
    }
    
    ServiceDescription createService() {
        for (JDBCServiceMethodDescription jdbcServiceMethodDescription : jdbcServiceMethodDescriptions) {
            jdbcServiceMethodDescription.dataSource(dataSource);
            jdbcServiceMethodDescription.executorService(executorService);
            serviceDescription.addServiceMethod(new JDBCServiceMethod(jdbcServiceMethodDescription));
        }
        return serviceDescription;
    }
}
