package org.csstudio.csdata.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.csdata.Device;
import org.csstudio.csdata.ProcessVariable;

public class DeviceAndPVs implements Serializable {
	/** @see Serializable */
	final private static long serialVersionUID = 1L;

	private final Device device;
	private final List<ProcessVariable> pvs;

	public DeviceAndPVs(Device device, List<ProcessVariable> pvs) {
		this.device = device;
		this.pvs = Collections
				.unmodifiableList(new ArrayList<ProcessVariable>(pvs));
	}

	public Device getDevice() {
		return device;
	}

	public List<ProcessVariable> getPvs() {
		return pvs;
	}

}
