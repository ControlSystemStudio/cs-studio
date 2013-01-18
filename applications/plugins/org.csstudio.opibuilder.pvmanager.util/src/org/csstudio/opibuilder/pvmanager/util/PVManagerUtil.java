package org.csstudio.opibuilder.pvmanager.util;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.vtype.ExpressionLanguage.vDoubleArray;
import static org.epics.util.time.TimeDuration.ofMillis;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.vtype.VDoubleArray;

public class PVManagerUtil {
	
	public static PVReader<VDoubleArray> createDoubleArrayPVReader(String pvName, int ms, boolean inUIThread){
		
		PVReader<VDoubleArray> pvReader;
		if(inUIThread)
			pvReader= PVManager.read(vDoubleArray(pvName)).notifyOn(SWTUtil.swtThread()).maxRate(ofMillis(ms));
		else
			pvReader= PVManager.read(vDoubleArray(pvName)).maxRate(ofMillis(ms));
		return pvReader;
		
	}
	
	public static PVReader<Object> createObjectPVReader(String pvName, int ms, boolean inUIThread){

		PVReader<Object> pvReader;
		if(inUIThread)
			pvReader= PVManager.read(channel(pvName)).notifyOn(SWTUtil.swtThread()).maxRate(ofMillis(ms));
		else
			pvReader= PVManager.read(channel(pvName)).maxRate(ofMillis(ms));
		return pvReader;
	}
}
