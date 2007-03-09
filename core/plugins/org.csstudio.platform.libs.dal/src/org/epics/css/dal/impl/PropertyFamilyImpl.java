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

/**
 *
 */
package org.epics.css.dal.impl;

import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.PropertyFamily;
import org.epics.css.dal.group.GroupDataAccess;
import org.epics.css.dal.group.PropertyCollectionMap;
import org.epics.css.dal.group.PropertyGroupConstrain;
import org.epics.css.dal.proxy.AbstractPlug;
import org.epics.css.dal.proxy.PropertyProxy;
import org.epics.css.dal.spi.PropertyFactory;


/**
 * @author ikriznar
 *
 */
public class PropertyFamilyImpl<T extends DynamicValueProperty>
	extends PropertyCollectionMap<T> implements PropertyFamily<T>
{
	PropertyFactory pf;

	/**
	 *
	 */
	public PropertyFamilyImpl(PropertyFactory pf)
	{
		super();
		this.pf = pf;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.PropertyFamily#destroyAll()
	 */
	public void destroyAll()
	{
		DynamicValueProperty[] props = new DynamicValueProperty[size()];
		props = toArray((T[])props);
		this.properties.clear();

		AbstractPlug plug = (AbstractPlug)pf.getPlug();

		for (DynamicValueProperty p : props) {
			PropertyProxy proxy = ((DataAccessImpl)p).getProxy();
			proxy.destroy();
			plug.releaseProxy(proxy);
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.PropertyFamily#destroy(org.epics.css.dal.DynamicValueProperty)
	 */
	public void destroy(DynamicValueProperty prop)
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.PropertyFamily#getApplicationContext()
	 */
	public AbstractApplicationContext getApplicationContext()
	{
		return pf.getApplicationContext();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.GroupDataAccessProvider#getGroupDataAccess(java.lang.Class, java.lang.Class)
	 */
	public <T, P extends DynamicValueProperty> GroupDataAccess<T, P> getGroupDataAccess(
	    Class<T> dataType, Class<P> propertyType)
	{
		// TODO later
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.GroupDataAccessProvider#getGroupDataAccess(java.lang.Class, java.lang.Class, org.epics.css.dal.group.PropertyGroupConstrain)
	 */
	public <T, P extends DynamicValueProperty> GroupDataAccess<T, P> getGroupDataAccess(
	    Class<T> dataType, Class<P> propertyType,
	    PropertyGroupConstrain constrain)
	{
		// TODO later
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollectionMap#add(T)
	 */
	@Override
	public void add(T property)
	{
		super.add(property);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.PropertyContext#getProperty(java.lang.String)
	 */
	public DynamicValueProperty getProperty(String name)
	{
		return get(name);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.PropertyContext#containsProperty(java.lang.Object)
	 */
	public boolean containsProperty(Object property)
	{
		return contains(property);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.PropertyContext#containsProperty(java.lang.String)
	 */
	public boolean containsProperty(String name)
	{
		return contains(name);
	}
}

/* __oOo__ */
