package org.csstudio.diirt.util;

import java.util.logging.Logger;

import org.diirt.datasource.CompositeDataSource;
import org.diirt.datasource.DataSource;
import org.diirt.datasource.DataSourceProvider;
import org.diirt.datasource.PVManager;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

/**
 * 
 * 
 * @author Kunal Shroff
 *
 */
public class RegisterDatasource implements BundleListener {

	private static final Logger logger = Logger.getLogger(RegisterDatasource.class.getCanonicalName());
	
	public void registerDatasource(DataSourceProvider dataSourceProvider) throws Exception {
		logger.info("register:" + dataSourceProvider.getName());
		DataSource defaultDataSource = PVManager.getDefaultDataSource();
		if (defaultDataSource instanceof CompositeDataSource) {
			((CompositeDataSource) defaultDataSource).putDataSource(dataSourceProvider);
		}
	}

	public void deregisterDatasource(DataSourceProvider dataSourceProvider) throws Exception {
		logger.info("deregister:" + dataSourceProvider.getName());

	}

	@Override
	public void bundleChanged(BundleEvent event) {

	}


}
