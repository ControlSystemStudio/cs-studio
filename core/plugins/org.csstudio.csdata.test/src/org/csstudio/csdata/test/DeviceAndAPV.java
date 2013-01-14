package org.csstudio.csdata.test;

import java.io.Serializable;

import org.csstudio.csdata.Device;
import org.csstudio.csdata.ProcessVariable;

public class DeviceAndAPV implements Serializable
{
    /** @see Serializable */
    final private static long serialVersionUID = 1L;
    
	private final Device device;
	private final ProcessVariable pv;

	public DeviceAndAPV(String device, String pv) {
		this.device = new Device(device);
		this.pv = new ProcessVariable(pv);
	}

	public DeviceAndAPV(Device device, ProcessVariable pv) {
		this.device = device;
		this.pv = pv;
	}

	public Device getDevice() {
		return device;
	}

	public ProcessVariable getPv() {
		return pv;
	}

}
