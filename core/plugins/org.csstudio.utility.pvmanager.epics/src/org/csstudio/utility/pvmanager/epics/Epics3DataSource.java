package org.csstudio.utility.pvmanager.epics;

import gov.aps.jca.JCALibrary;

import org.csstudio.platform.libs.epics.EpicsPlugin;
import org.epics.pvmanager.DataRecipe;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.jca.JCADataSource;

public class Epics3DataSource extends DataSource {
	
	private static final JCADataSource dataSource;
	
	static {
        boolean use_pure_java = EpicsPlugin.getDefault().usePureJava();
        String className = use_pure_java ?
                JCALibrary.CHANNEL_ACCESS_JAVA : JCALibrary.JNI_THREAD_SAFE;
        dataSource = new JCADataSource(className);
	}

	@Override
	public void connect(DataRecipe recipe) {
		dataSource.connect(recipe);
	}

	@Override
	public void disconnect(DataRecipe recipe) {
		dataSource.disconnect(recipe);

	}

}
