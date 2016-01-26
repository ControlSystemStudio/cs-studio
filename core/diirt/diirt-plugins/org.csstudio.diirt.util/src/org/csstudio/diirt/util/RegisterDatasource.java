package org.csstudio.diirt.util;

import java.util.logging.Logger;

import org.diirt.datasource.CompositeDataSource;
import org.diirt.datasource.DataSource;
import org.diirt.datasource.DataSourceProvider;
import org.diirt.datasource.PVManager;

/**
 *
 *
 * @author Kunal Shroff
 *
 */
public class RegisterDatasource {

    private static final Logger logger = Logger.getLogger(RegisterDatasource.class.getCanonicalName());

    public void registerDatasource(DataSourceProvider dataSourceProvider)
            throws Exception {
        logger.info("register Datasource:" + dataSourceProvider.getName());
        DataSource defaultDataSource = PVManager.getDefaultDataSource();
        if (defaultDataSource instanceof CompositeDataSource) {
            ((CompositeDataSource) defaultDataSource)
                    .putDataSource(dataSourceProvider);
        }
    }

    public void deregisterDatasource(DataSourceProvider dataSourceProvider)
            throws Exception {
        logger.info("deregister Datasource:" + dataSourceProvider.getName());

    }

}
