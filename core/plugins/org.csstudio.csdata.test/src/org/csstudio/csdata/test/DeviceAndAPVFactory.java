package org.csstudio.csdata.test;

import java.util.Collections;

import org.csstudio.csdata.DeviceName;
import org.csstudio.csdata.ProcessVariableName;
import org.eclipse.core.runtime.IAdapterFactory;

public class DeviceAndAPVFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (DeviceName.class.equals(adapterType)) {
			return ((DeviceAndAPV) adaptableObject).getDevice();
		}
		if (ProcessVariableName.class.equals(adapterType)) {
			return ((DeviceAndAPV) adaptableObject).getPv();
		}
		if (DeviceAndPVs.class.equals(adapterType)) {
			DeviceAndAPV obj = (DeviceAndAPV) adaptableObject;
			return new DeviceAndPVs(obj.getDevice(), Collections.singletonList(obj.getPv()));
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class<?>[] {DeviceName.class, ProcessVariableName.class, DeviceAndPVs.class};
	}

}
