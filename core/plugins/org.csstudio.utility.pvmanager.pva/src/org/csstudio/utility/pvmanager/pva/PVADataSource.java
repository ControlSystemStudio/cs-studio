package org.csstudio.utility.pvmanager.pva;

import java.util.logging.Logger;

public class PVADataSource extends org.epics.pvmanager.pva.PVADataSource {
	
	private static final Logger logger = Logger.getLogger(PVADataSource.class.getName());
	
	static {
		logger.config("Loading EPICS v4 pvAccess data source.");
	}
	
	public PVADataSource() {
		super();
	}

}
