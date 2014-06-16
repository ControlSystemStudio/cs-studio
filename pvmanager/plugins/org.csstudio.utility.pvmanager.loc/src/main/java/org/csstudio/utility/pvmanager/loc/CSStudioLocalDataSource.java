package org.csstudio.utility.pvmanager.loc;

import org.epics.pvmanager.loc.LocalDataSource;

public class CSStudioLocalDataSource extends LocalDataSource {
	
	public CSStudioLocalDataSource() {
		super(Activator.isZeroInitialization());
	}

}
