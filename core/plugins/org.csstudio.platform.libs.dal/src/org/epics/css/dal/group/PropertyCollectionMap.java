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

package org.epics.css.dal.group;

import com.cosylab.util.ListenerList;

import org.epics.css.dal.DynamicValueProperty;

import java.lang.reflect.Array;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * PropertyCollection implementation based on HashMap.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 * @param <T> exct type of properties
 */
public class PropertyCollectionMap<T extends DynamicValueProperty>
	implements PropertyCollection<T>
{
	protected Class type;
	protected ListenerList groupListeners;
	protected Map<String, T> properties;

	/**
	     *
	     */
	public PropertyCollectionMap()
	{
		super();
		properties = new HashMap<String, T>();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollection#size()
	 */
	public int size()
	{
		return properties.size();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollection#isEmpty()
	 */
	public boolean isEmpty()
	{
		return properties.isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollection#contains(java.lang.Object)
	 */
	public boolean contains(Object property)
	{
		return properties.containsValue(property);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollection#iterator()
	 */
	public Iterator<T> iterator()
	{
		return properties.values().iterator();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollection#toArray()
	 */
	public Object[] toArray()
	{
		return properties.values().toArray();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollection#toPropertyArray()
	 */
	public T[] toPropertyArray()
	{
		return properties.values().toArray((T[])(new Object[0]));
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollection#toArray(E[])
	 */
	public <E extends T> E[] toArray(E[] array)
	{
		return properties.values().toArray(array);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection colection)
	{
		return properties.values().contains(colection);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollection#contains(java.lang.String)
	 */
	public boolean contains(String name)
	{
		return properties.containsKey(name);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollection#get(java.lang.String)
	 */
	public T get(String name)
	{
		return properties.get(name);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollection#getPropertyNames()
	 */
	public String[] getPropertyNames()
	{
		return (String[])properties.keySet().toArray();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollection#addGroupListner(org.epics.css.dal.group.GroupListener)
	 */
	public void addGroupListner(GroupListener l)
	{
		if (groupListeners == null) {
			groupListeners = new ListenerList(GroupListener.class);
		}

		groupListeners.add(l);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollection#removeGroupListner(org.epics.css.dal.group.GroupListener)
	 */
	public void removeGroupListner(GroupListener l)
	{
		if (groupListeners != null) {
			groupListeners.remove(l);
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.group.PropertyCollection#getGroupListners()
	 */
	public GroupListener[] getGroupListners()
	{
		if (groupListeners != null) {
			return (GroupListener[])groupListeners.toArray();
		}

		return new GroupListener[0];
	}

	protected void fireGroupEvent(GroupEvent event, boolean added)
	{
		if (groupListeners == null) {
			return;
		}

		GroupListener[] l = (GroupListener[])groupListeners.toArray();

		if (added) {
			for (int i = 0; i < l.length; i++) {
				try {
					l[i].membersAdded(event);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			for (int i = 0; i < l.length; i++) {
				try {
					l[i].membersRemoved(event);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * @see MutablePropertyCollection#add(T)
	 */
	protected void add(T property)
	{
		if (contains(((DynamicValueProperty<?>)property).getName())) {
			throw new IllegalArgumentException(
			    "This collection already contains property with name '"
			    + ((DynamicValueProperty<?>)property).getName() + "'.");
		}

		properties.put(((DynamicValueProperty<?>)property).getName(), property);

		T[] t = (T[])Array.newInstance(property.getClass(), 1);
		t[0] = property;
		fireGroupEvent(new GroupEvent<T>(this, t), true);
	}

	/*
	 * @see MutablePropertyCollection#remove(T)
	 */
	protected void remove(T property)
	{
		properties.remove(((DynamicValueProperty<?>)property).getName());

		T[] t = (T[])Array.newInstance(property.getClass(), 1);
		t[0] = property;
		fireGroupEvent(new GroupEvent<T>(this, t), false);
	}
}

/* __oOo__ */
