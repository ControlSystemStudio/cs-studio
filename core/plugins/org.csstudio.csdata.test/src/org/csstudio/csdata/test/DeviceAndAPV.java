package org.csstudio.csdata.test;

import java.io.Serializable;

import org.csstudio.csdata.DeviceName;
import org.csstudio.csdata.ProcessVariable;

public class DeviceAndAPV implements Serializable
{
    /** @see Serializable */
    final private static long serialVersionUID = 1L;
    
	private final DeviceName device;
	private final ProcessVariable pv;

	public DeviceAndAPV(String device, String pv) {
		this.device = new DeviceName(device);
		this.pv = new ProcessVariable(pv);
	}

	public DeviceAndAPV(DeviceName device, ProcessVariable pv) {
		this.device = device;
		this.pv = pv;
	}

	public DeviceName getDevice() {
		return device;
	}

	public ProcessVariable getPv() {
		return pv;
	}

}
