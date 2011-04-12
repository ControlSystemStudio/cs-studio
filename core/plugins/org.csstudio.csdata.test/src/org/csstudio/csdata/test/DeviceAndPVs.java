package org.csstudio.csdata.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.csstudio.csdata.DeviceName;
import org.csstudio.csdata.ProcessVariableName;

public class DeviceAndPVs implements Serializable {
	/** @see Serializable */
	final private static long serialVersionUID = 1L;

	private final DeviceName device;
	private final List<ProcessVariableName> pvs;

	public DeviceAndPVs(DeviceName device, List<ProcessVariableName> pvs) {
		this.device = device;
		this.pvs = Collections
				.unmodifiableList(new ArrayList<ProcessVariableName>(pvs));
	}

	public DeviceName getDevice() {
		return device;
	}

	public List<ProcessVariableName> getPvs() {
		return pvs;
	}

}
