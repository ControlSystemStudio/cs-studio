package org.csstudio.opibuilder.pvmanager.util;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.data.ExpressionLanguage.vDoubleArrayOf;
import static org.epics.pvmanager.util.TimeDuration.ms;

import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.data.VDoubleArray;

public class PVManagerUtil {
	
	public static PVReader<VDoubleArray> createDoubleArrayPVReader(String pvName, int ms, boolean inUIThread){
		
		PVReader<VDoubleArray> pvReader;
		if(inUIThread)
			pvReader= PVManager.read(
				vDoubleArrayOf(channel(pvName))).notifyOn(SWTUtil.swtThread()).every(ms(ms));
		else
			pvReader= PVManager.read(
				vDoubleArrayOf(channel(pvName))).every(ms(ms));
		return pvReader;
		
	}
	
	public static PVReader<Object> createObjectPVReader(String pvName, int ms, boolean inUIThread){

		PVReader<Object> pvReader;
		if(inUIThread)
			pvReader= PVManager.read(channel(pvName)).notifyOn(SWTUtil.swtThread()).every(ms(ms));
		else
			pvReader= PVManager.read(channel(pvName)).every(ms(ms));
		return pvReader;
		
		
	}

}
