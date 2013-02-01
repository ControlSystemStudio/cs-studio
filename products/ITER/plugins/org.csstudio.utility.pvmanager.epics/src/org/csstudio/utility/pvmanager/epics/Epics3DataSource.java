package org.csstudio.utility.pvmanager.epics;

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;

import org.csstudio.platform.libs.epics.EpicsPlugin;
import org.epics.pvmanager.jca.JCADataSource;
import org.epics.pvmanager.jca.JCADataSourceBuilder;

public class Epics3DataSource extends JCADataSource {
	
	private static final Logger log = Logger.getLogger(Epics3DataSource.class.getName());
	private static String paramClassName;
	private static int paramMask;
	private static Context context;
	private static boolean dbePropertySupported;
	private static boolean honorZeroPrecision;
	private static boolean rtypValueOnly;
	private static Boolean varArraySupported;
	
	
	static {
		EpicsPlugin plugin = EpicsPlugin.getDefault();
		
        boolean use_pure_java = plugin.usePureJava();
        paramClassName = use_pure_java ?
                JCALibrary.CHANNEL_ACCESS_JAVA : JCALibrary.JNI_THREAD_SAFE;
        paramMask = plugin.getMonitorMask().getMask();
    	dbePropertySupported = plugin.isDbePropertySupported();
    	honorZeroPrecision = plugin.isHonorZeroPrecision();
    	rtypValueOnly = plugin.isRtypValueOnly();
    	varArraySupported = plugin.getVarArraySupported();
        log.config("Loading epics data source parameters: " + paramClassName + " - " + paramMask);
        JCALibrary jca = JCALibrary.getInstance();
        try {
        	context = jca.createContext(paramClassName);
        } catch (CAException ex) {
        	log.log(Level.SEVERE, "Couldn't create JCA context", ex);
        }
	}
	
	private static JCADataSourceBuilder builder() {
		JCADataSourceBuilder builder = new JCADataSourceBuilder()
			.jcaContext(context)
			.monitorMask(paramMask)
			.dbePropertySupported(dbePropertySupported)
			.honorZeroPrecision(honorZeroPrecision)
			.rtypValueOnly(rtypValueOnly);
		if (varArraySupported != null) {
			builder.varArraySupported(varArraySupported);
		}
		return builder;
	}
	
	public Epics3DataSource() {
		super(builder());
	}

}
