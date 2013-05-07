package org.csstudio.trends.sscan.scancontrol;

import org.csstudio.csdata.ProcessVariable;

public interface SscanListener {
	 /** Received a name, presumably a PV name
     *  @param name PV(?) name
     */
    public void PVName(String name);
    
    public void PVName(ProcessVariable name);

	public void ScanEvent(String name, int status);
}
