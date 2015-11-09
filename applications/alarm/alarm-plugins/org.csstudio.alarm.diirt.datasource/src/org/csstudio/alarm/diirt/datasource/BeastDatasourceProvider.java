/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import org.diirt.datasource.ConfigurableDataSourceProvider;

/**
 * DatasourceProvider for jms access
 * 
 * @author Kunal Shroff
 *
 */
public class BeastDatasourceProvider extends ConfigurableDataSourceProvider<BeastDatasource, BeastDataSourceConfiguration> {

    public BeastDatasourceProvider() {
        super(BeastDataSourceConfiguration.class);
    }

    @Override
    public String getName() {
        return "beast";
    }

}
