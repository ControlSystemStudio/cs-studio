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

package org.csstudio.dal.device;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.csstudio.dal.group.DeviceGroupEvent;
import org.csstudio.dal.group.DeviceGroupListener;

import com.cosylab.util.ListenerList;


/**
 * DeviceCollection implementation based on HashMap.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public class DeviceCollectionMap<T extends AbstractDevice>
    implements DeviceCollection<T>
{
    protected ListenerList groupListeners;
    protected Map<String, T> devices;
    protected Class<T> type;

    /**
     * Creates a new DeviceCollectionMap object.
     */
    public DeviceCollectionMap(Class<T> type)
    {
        super();
        this.type = type;
        devices = new HashMap<String, T>();
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.DeviceCollection#size()
     */
    public int size()
    {
        return devices.size();
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.DeviceCollection#isEmpty()
     */
    public boolean isEmpty()
    {
        return devices.isEmpty();
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.DeviceCollection#contains(java.lang.Object)
     */
    public boolean contains(Object property)
    {
        return devices.containsValue(property);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.DeviceCollection#iterator()
     */
    public Iterator<T> iterator()
    {
        return devices.values().iterator();
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.DeviceCollection#toArray()
     */
    public Object[] toArray()
    {
        return devices.values().toArray();
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.DeviceCollection#toDeviceArray()
     */
    public T[] toDeviceArray()
    {
        T[] devs = (T[])Array.newInstance(type, devices.size());
        Iterator<T> it= iterator();
        for (int i = 0; i < devs.length; i++) {
            devs[i]=it.next();
        }
        return devs;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.DeviceCollection#toArray(E[])
     */
    public <E extends T> E[] toArray(E[] array)
    {
        return devices.values().toArray(array);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.DeviceCollection#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> colection)
    {
        return devices.values().contains(colection);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.DeviceCollection#contains(java.lang.String)
     */
    public boolean contains(String name)
    {
        return devices.containsKey(name);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.DeviceCollection#get(java.lang.String)
     */
    public T get(String name)
    {
        return devices.get(name);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.DeviceCollection#getDeviceNames()
     */
    public String[] getDeviceNames()
    {
        return (String[])devices.keySet().toArray();
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.DeviceCollection#addGroupListner(org.csstudio.dal.group.GroupListener)
     */
    public void addDeviceGroupListner(DeviceGroupListener<T> l)
    {
        if (groupListeners == null) {
            groupListeners = new ListenerList(DeviceGroupListener.class);
        }

        groupListeners.add(l);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.DeviceCollection#removeGroupListner(org.csstudio.dal.group.GroupListener)
     */
    public void removeDeviceGroupListner(DeviceGroupListener<T> l)
    {
        if (groupListeners != null) {
            groupListeners.remove(l);
        }
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.DeviceCollection#getGroupListners()
     */
    public DeviceGroupListener<T>[] getDeviceGroupListners()
    {
        if (groupListeners != null) {
            return (DeviceGroupListener<T>[])groupListeners.toArray();
        }

        return new DeviceGroupListener[0];
    }

    /**
     * Notifies all the <code>GroupListener<code> objects, about members added/removed.
     *
     * @param event Group event
     * @param added <code>true</code> if members were added, <code>false</code> otherwise.
     */
    protected void fireGroupEvent(DeviceGroupEvent<T> event, boolean added)
    {
        if (groupListeners == null) {
            return;
        }

        DeviceGroupListener<T>[] l = (DeviceGroupListener<T>[])groupListeners.toArray();

        if (added) {
            for (int i = 0; i < l.length; i++) {
                try {
                    l[i].membersAdded(event);
                } catch (Exception e) {
                    Logger.getLogger(DeviceCollectionMap.class).warn("Error in event handler, continuing.", e);
                }
            }
        } else {
            for (int i = 0; i < l.length; i++) {
                try {
                    l[i].membersRemoved(event);
                } catch (Exception e) {
                    Logger.getLogger(DeviceCollectionMap.class).warn("Error in event handler, continuing.", e);
                }
            }
        }
    }

    /**
     * Adds property to this collection.
     *
     * @param device The property to add to this collection
     *
     * @throws IllegalArgumentException If a property with the same name already exists in the collection
     */
    protected void add(T device)
    {
        if (contains(((AbstractDevice)device).getUniqueName())) {
            throw new IllegalArgumentException(
                "This collection already contains device with name '"
                + ((AbstractDevice)device).getUniqueName() + "'.");
        }

        devices.put(((AbstractDevice)device).getUniqueName(), device);
        //infer generics type safety
        T[] devices = (T[])Array.newInstance(device.getClass(), 1);
        devices[0] = device;
        fireGroupEvent(new DeviceGroupEvent<T>(this, devices), true);
    }

    /**
     * Removes property from this collection.
     *
     * @param device The property to remove.
     */
    protected void remove(T device)
    {
        T removed = devices.remove(((AbstractDevice)device).getUniqueName());
        if (removed != null) {
            //infer generics type safety
            T[] devices = (T[])Array.newInstance(device.getClass(), 1);
            devices[0] = device;
            fireGroupEvent(new DeviceGroupEvent<T>(this, devices), false);
        }
    }
}

/* __oOo__ */
