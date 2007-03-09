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

package org.epics.css.dal.impl;

import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.DeviceFamily;
import org.epics.css.dal.device.AbstractDevice;
import org.epics.css.dal.device.DeviceCollectionMap;
import org.epics.css.dal.group.GroupDataAccess;
import org.epics.css.dal.group.PropertyGroupConstrain;
import org.epics.css.dal.proxy.AbstractPlug;
import org.epics.css.dal.proxy.DeviceProxy;
import org.epics.css.dal.proxy.Proxy;
import org.epics.css.dal.spi.DeviceFactory;


/**
 * Default device family implementation.
 *
 * @version $Revision$
  *
 */
public class DeviceFamilyImpl<T extends AbstractDevice>
	extends DeviceCollectionMap<T> implements DeviceFamily<T>
{
	private DeviceFactory df;

	/**
	 * Creates a new DeviceFamilyImpl object.
	 *
	 * @param df Device factory.
	 */
	public DeviceFamilyImpl(DeviceFactory df)
	{
		this.df = df;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.epics.css.dal.context.DeviceFamily#destroyAll()
	 */
	public void destroyAll()
	{
		// TODO: perform destroy with plug object
		AbstractDeviceImpl[] devArray = new AbstractDeviceImpl[size()];
		devArray = (AbstractDeviceImpl[])devices.values().toArray(devArray);
		this.devices.clear();

		AbstractPlug plug = (AbstractPlug)df.getPlug();

		for (AbstractDeviceImpl dev : devArray) {
			Proxy proxy = dev.getProxy();
			plug.releaseProxy(proxy);
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see org.epics.css.dal.context.DeviceFamily#destroy(org.epics.css.dal.device.AbstractDevice)
	 */
	public void destroy(AbstractDevice device)
	{
		this.devices.remove(device);

		AbstractPlug plug = (AbstractPlug)df.getPlug();

		Proxy proxy = ((AbstractDeviceImpl)device).getProxy();
		plug.releaseProxy(proxy);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.epics.css.dal.context.DeviceFamily#getApplicationContext()
	 */
	public AbstractApplicationContext getApplicationContext()
	{
		return df.getApplicationContext();
	}

	/*
	 *  (non-Javadoc)
	 * @see org.epics.css.dal.group.GroupDataAccessProvider#getGroupDataAccess(java.lang.Class, java.lang.Class)
	 */
	public <T, P extends DynamicValueProperty> GroupDataAccess<T, P> getGroupDataAccess(
	    Class<T> dataType, Class<P> propertyType)
	{
		// NOT TO BE DONE
		throw new UnsupportedOperationException();
	}

	/*
	 *  (non-Javadoc)
	 * @see org.epics.css.dal.group.GroupDataAccessProvider#getGroupDataAccess(java.lang.Class, java.lang.Class, org.epics.css.dal.group.PropertyGroupConstrain)
	 */
	public <T, P extends DynamicValueProperty> GroupDataAccess<T, P> getGroupDataAccess(
	    Class<T> dataType, Class<P> propertyType,
	    PropertyGroupConstrain constrain)
	{
		// NOT TO BE DONE
		throw new UnsupportedOperationException();
	}

	/*
	 *  (non-Javadoc)
	 * @see org.epics.css.dal.device.DeviceCollectionMap#add(null)
	 */
	public void add(T object)
	{
		super.add(object);
	}
}

/* __oOo__ */
