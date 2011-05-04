package org.csstudio.utility.pvmanager.epics;

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.aps.jca.JCALibrary;

import org.csstudio.platform.libs.epics.EpicsPlugin;
import org.epics.pvmanager.DataRecipe;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.jca.JCADataSource;

public class Epics3DataSource extends DataSource {
	
	private static final JCADataSource dataSource;
	private static Throwable cause;
	private static final Logger log = Logger.getLogger(Epics3DataSource.class.getName());
	
	static {
        boolean use_pure_java = EpicsPlugin.getDefault().usePureJava();
        String className = use_pure_java ?
                JCALibrary.CHANNEL_ACCESS_JAVA : JCALibrary.JNI_THREAD_SAFE;
        int mask = EpicsPlugin.getDefault().getMonitorMask().getMask();
        JCADataSource initDataSource = null;
        try {
        	initDataSource = new JCADataSource(className, mask);
        } catch(Throwable ex) {
        	cause = ex;
        	log.log(Level.SEVERE, "Could not initialize PVManager EPICS data source", ex);
        }
        
        dataSource = initDataSource;
	}

	@Override
	public void connect(DataRecipe recipe) {
		if (dataSource != null) {
			dataSource.connect(recipe);
		} else {
			throw new RuntimeException("EPICS data source was not correctly initialized", cause);
		}
	}

	@Override
	public void disconnect(DataRecipe recipe) {
		if (dataSource != null) {
			dataSource.disconnect(recipe);
		} else {
			throw new RuntimeException("EPICS data source was not correctly initialized", cause);
		}
	}

}
