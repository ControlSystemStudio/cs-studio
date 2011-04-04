package org.csstudio.csdata.test;

import java.io.Serializable;

import org.csstudio.csdata.DeviceName;
import org.csstudio.csdata.ProcessVariableName;

public class DeviceAndAPV implements Serializable
{
    /** @see Serializable */
    final private static long serialVersionUID = 1L;
    
	private final DeviceName device;
	private final ProcessVariableName pv;

	public DeviceAndAPV(String device, String pv) {
		this.device = new DeviceName(device);
		this.pv = new ProcessVariableName(pv);
	}

	public DeviceAndAPV(DeviceName device, ProcessVariableName pv) {
		this.device = device;
		this.pv = pv;
	}

	public DeviceName getDevice() {
		return device;
	}

	public ProcessVariableName getPv() {
		return pv;
	}

}
