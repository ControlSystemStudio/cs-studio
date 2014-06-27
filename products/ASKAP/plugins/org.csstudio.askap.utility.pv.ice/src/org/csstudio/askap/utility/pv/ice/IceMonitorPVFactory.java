package org.csstudio.askap.utility.pv.ice;

import org.csstudio.utility.pv.IPVFactory;
import org.csstudio.utility.pv.PV;

public class IceMonitorPVFactory implements IPVFactory {

	/** PV type prefix */
	public static final String PREFIX = "ice";


	@Override
	public PV createPV(String name) throws Exception {
		Value value = new Value(name);
		return value;
	}

}
