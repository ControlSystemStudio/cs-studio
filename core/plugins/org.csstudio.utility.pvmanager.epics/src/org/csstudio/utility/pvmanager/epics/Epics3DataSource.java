package org.csstudio.utility.pvmanager.epics;

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;

import org.csstudio.platform.libs.epics.EpicsPlugin;
import org.epics.pvmanager.jca.JCADataSource;

public class Epics3DataSource extends JCADataSource {
	
	private static final Logger log = Logger.getLogger(Epics3DataSource.class.getName());
	private static String paramClassName;
	private static int paramMask;
	private static Context context;
	
	static {
        boolean use_pure_java = EpicsPlugin.getDefault().usePureJava();
        paramClassName = use_pure_java ?
                JCALibrary.CHANNEL_ACCESS_JAVA : JCALibrary.JNI_THREAD_SAFE;
        paramMask = EpicsPlugin.getDefault().getMonitorMask().getMask();
        log.config("Loading epics data source parameters: " + paramClassName + " - " + paramMask);
        JCALibrary jca = JCALibrary.getInstance();
        try {
        	context = jca.createContext(paramClassName);
        } catch (CAException ex) {
        	log.log(Level.SEVERE, "Couldn't create JCA context", ex);
        }
	}
	
	public Epics3DataSource() {
		super(context, paramMask);
	}

}
