package org.csstudio.utility.pvmanager.exec;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.epics.pvmanager.exec.ExecXMLServiceFactory;
import org.epics.pvmanager.exec.GenericExecService;
import org.epics.pvmanager.service.Service;

public class CsstudioExecServiceFactory extends ExecXMLServiceFactory {

	public CsstudioExecServiceFactory() {
		super(getJdbcServiceDirectory());
	}
	
	public static File getJdbcServiceDirectory() {
		File productDirectory = new File(Platform.getInstallLocation().getURL().getFile() + "/configuration/services/exec");
		Logger.getLogger(CsstudioExecServiceFactory.class.getName()).log(Level.CONFIG, "Reading Exec Services configuration directory " + productDirectory);
		return productDirectory;
	}
	
	@Override
	public Collection<Service> createServices() {
		Collection<Service> allServices = new HashSet<>();
		if (getJdbcServiceDirectory().exists()) {
			Collection<Service> fileServices =  super.createServices();
			allServices.addAll(fileServices);
		}
		allServices.add(new GenericExecService());
		return allServices;
	}

}
