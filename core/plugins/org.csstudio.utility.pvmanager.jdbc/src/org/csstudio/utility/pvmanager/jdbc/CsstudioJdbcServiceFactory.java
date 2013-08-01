package org.csstudio.utility.pvmanager.jdbc;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.epics.pvmanager.jdbc.JDBCXMLServiceFactory;

public class CsstudioJdbcServiceFactory extends JDBCXMLServiceFactory {

	public CsstudioJdbcServiceFactory() {
		super(getJdbcServiceDirectory());
	}
	
	public static File getJdbcServiceDirectory() {
		File productDirectory = new File(Platform.getInstallLocation().getURL().getFile() + "/configuration/services/jdbc");
		Logger.getLogger(CsstudioJdbcServiceFactory.class.getName()).log(Level.CONFIG, "Reading JDBC Services configuration directory " + productDirectory);
		return productDirectory;
	}

}
