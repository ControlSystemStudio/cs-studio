package org.epics.css.dal.impl;

import java.util.Collections;

import org.epics.css.dal.context.DeviceFamily;
import org.epics.css.dal.device.AbstractDevice;
import org.epics.css.dal.spi.DeviceFactory;

/**
 * 
 * <code>SynchronizedDeviceFamilyImpl</code> is an instance of the DeviceFamily,
 * which uses a synchronized collection to store the devices. In contrast
 * to the {@link DeviceFamilyImpl}, there is no need to synchronized the 
 * calls, which make structural changes to this family.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 * @param <T>
 */
public class SynchronizedDeviceFamilyImpl<T extends AbstractDevice>
	extends DeviceFamilyImpl<T> implements DeviceFamily<T> {

	/**
	 * Constructs a new DeviceFamily, which uses a synchronized collection
	 * to store the devices.
	 * 
	 * @param df the device factory, which ownes this family
	 */
	public SynchronizedDeviceFamilyImpl(DeviceFactory df) {
		super(df);
		devices = Collections.synchronizedMap(devices);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.device.DeviceCollectionMap#remove(org.epics.css.dal.device.AbstractDevice)
	 */
	@Override
	protected synchronized void remove(T device) {
		super.remove(device);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.impl.DeviceFamilyImpl#add(org.epics.css.dal.device.AbstractDevice)
	 */
	@Override
	public synchronized void add(T object) {
		super.add(object);
	}

}
