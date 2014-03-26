/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javax.sql.DataSource;
import org.epics.pvmanager.service.ServiceDescription;

/**
 * The description on how to construct a JDBC service.
 * <p>
 * This class encapsulate the description of a service, including:
 * <ul>
 *   <li>The connection parameters</li>
 *   <li>A number queries</li>
 *   <li>The arguments for each query and how should they be mapped</li>
 *   <li>The results of each query</li>
 * </ul>
 *
 * @author carcassi
 */
public class JDBCServiceDescription {
    
    final ServiceDescription serviceDescription;
    DataSource dataSource;
    ExecutorService executorService;
    private List<JDBCServiceMethodDescription> jdbcServiceMethodDescriptions = new ArrayList<>();
    
    /**
     * A new service description with the given service name and description.
     * 
     * @param name the name of the service
     * @param description a brief description
     */
    public JDBCServiceDescription(String name, String description) {
        serviceDescription = new ServiceDescription(name, description);
    }

    /**
     * Adds a service method (i.e. a query) to the service.
     * 
     * @param jdbcServiceMethodDescription a method description
     * @return this
     */
    public JDBCServiceDescription addServiceMethod(JDBCServiceMethodDescription jdbcServiceMethodDescription) {
        jdbcServiceMethodDescriptions.add(jdbcServiceMethodDescription);
        return this;
    }

    /**
     * The JDBC DataSource to use for database connection.
     * <p>
     * Use {@link SimpleDataSource} if you have a JDBC url.
     * 
     * @param dataSource a JDBC datasource
     * @return this
     */
    public JDBCServiceDescription dataSource(DataSource dataSource) {
        if (this.dataSource != null) {
            throw new IllegalArgumentException("DataSource was already set");
        }
        this.dataSource = dataSource;
        return this;
    }
    
    /**
     * The ExecutorService on which to execute the query.
     * 
     * @param executorService an executor service
     * @return this
     */
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
