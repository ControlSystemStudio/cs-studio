/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.dal.impl;

import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.context.DeviceFamily;
import org.csstudio.dal.device.AbstractDevice;
import org.csstudio.dal.device.DeviceCollectionMap;
import org.csstudio.dal.group.GroupDataAccess;
import org.csstudio.dal.group.PropertyGroupConstrain;
import org.csstudio.dal.proxy.AbstractPlug;
import org.csstudio.dal.proxy.Proxy;
import org.csstudio.dal.spi.DeviceFactory;


/**
 * Default device family implementation. This class is based on the 
 * {@link DeviceCollectionMap} which means that all devices are stored 
 * in a HashMap, which is not synchronized. Therefore, when adding
 * and removing devices one should be careful about the synchronization
 * because HashMap is not syncrhonized.
 *
 */
public class DeviceFamilyImpl<T extends AbstractDevice>
	extends DeviceCollectionMap<T> implements DeviceFamily<T>
{
	private DeviceFactory df;
	static final Class<? extends AbstractDevice> c = AbstractDevice.class;

	/**
	 * Creates a new DeviceFamilyImpl object.
	 *
	 * @param df Device factory.
	 */
	public DeviceFamilyImpl(DeviceFactory df)
	{
		super((Class<T>) c);
		this.df = df;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.csstudio.dal.context.DeviceFamily#destroyAll()
	 */
	public void destroyAll()
	{
		Object[] devArray = devices.values().toArray();
		this.devices.clear();

		for (Object dev : devArray) {
			destroy((T)dev);
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see org.csstudio.dal.context.DeviceFamily#destroy(org.csstudio.dal.device.AbstractDevice)
	 */
	public void destroy(T device)
	{
		remove(device);
		if (!device.isDestroyed()) {

			AbstractPlug plug = (AbstractPlug)df.getPlug();
			Proxy[] proxy = ((AbstractDeviceImpl)device).releaseProxy(true);
			if (proxy != null && proxy[0]!=null) {
				plug.releaseProxy(proxy[0]);
			}
			if (proxy != null && proxy[1]!=null && proxy[1]!=proxy[0]) {
				plug.releaseProxy(proxy[1]);
			}
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see org.csstudio.dal.context.DeviceFamily#getApplicationContext()
	 */
	public AbstractApplicationContext getApplicationContext()
	{
		return df.getApplicationContext();
	}

	/*
	 *  (non-Javadoc)
	 * @see org.csstudio.dal.group.GroupDataAccessProvider#getGroupDataAccess(java.lang.Class, java.lang.Class)
	 */
	public <Tt, P extends DynamicValueProperty<?>> GroupDataAccess<Tt, P> getGroupDataAccess(
	    Class<Tt> dataType, Class<P> propertyType)
	{
		// NOT TO BE DONE
		throw new UnsupportedOperationException();
	}

	/*
	 *  (non-Javadoc)
	 * @see org.csstudio.dal.group.GroupDataAccessProvider#getGroupDataAccess(java.lang.Class, java.lang.Class, org.csstudio.dal.group.PropertyGroupConstrain)
	 */
	public <Tt, P extends DynamicValueProperty<Tt>> GroupDataAccess<Tt, P> getGroupDataAccess(
	    Class<Tt> dataType, Class<P> propertyType,
	    PropertyGroupConstrain constrain)
	{
		// NOT TO BE DONE
		throw new UnsupportedOperationException();
	}

	/*
	 *  (non-Javadoc)
	 * @see org.csstudio.dal.device.DeviceCollectionMap#add(null)
	 */
	public void add(T object)
	{
		super.add(object);
	}
}

/* __oOo__ */
