package org.csstudio.utility.pvmanager.pva;


import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.epics.pvmanager.pva.rpcservice.PVARPCXMLServiceFactory;
import org.epics.pvmanager.service.Service;


public class CsstudioPVARPCServiceFactory extends PVARPCXMLServiceFactory {

	public CsstudioPVARPCServiceFactory() {
		super(getPVARPCServiceDirectory());
	}
	
	public static File getPVARPCServiceDirectory() {
		File productDirectory = new File(Platform.getInstallLocation().getURL().getFile() + "/configuration/services/pvarpc");
		Logger.getLogger(CsstudioPVARPCServiceFactory.class.getName()).log(Level.CONFIG, "Reading PVA RPC Services configuration directory " + productDirectory);
		return productDirectory;
	}
	
	@Override
	public Collection<Service> createServices() {
		if (getPVARPCServiceDirectory().exists()) {
			return super.createServices();
		} else {
			return Collections.emptyList();
		}
	}

}

