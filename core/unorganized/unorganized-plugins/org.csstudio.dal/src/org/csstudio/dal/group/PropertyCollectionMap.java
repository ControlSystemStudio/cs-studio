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

package org.csstudio.dal.group;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.csstudio.dal.DynamicValueProperty;

import com.cosylab.util.ListenerList;


/**
 * PropertyCollection implementation based on HashMap.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 * @param <T> exct type of properties
 */
public class PropertyCollectionMap<T extends DynamicValueProperty<?>>
    implements PropertyCollection<T>
{

    class PIterator<Tt> implements Iterator<Tt> {
        private Iterator<Tt[]> it;
        private Tt[] elements;
        private int i=0;

        public PIterator(Iterator<Tt[]> it) {
            this.it=it;
        }

        public boolean hasNext() {
            return (elements!=null && elements.length>i) || it.hasNext();
        }

        public Tt next() {
            if (elements!=null) {
                if (i<elements.length) {
                    return elements[i++];
                } else {
                    elements=null;
                    return next();
                }
            }

            elements= it.next();
            i=0;
            return next();
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    protected Class<T> type;
    protected ListenerList groupListeners;
    protected Map<String, T[]> properties;
    private int size=0;
    private T[] zero_length;

    /**
         *
         */
    public PropertyCollectionMap(Class<T> cl)
    {
        super();
        this.type=cl;
        properties = new HashMap<String, T[]>();
        zero_length= (T[])Array.newInstance(type, 0);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#size()
     */
    public int size()
    {
        return size;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#isEmpty()
     */
    public boolean isEmpty()
    {
        return properties.isEmpty();
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#contains(java.lang.Object)
     */
    public boolean contains(Object property)
    {
        if (property==null) {
            return false;
        }
        if (type.isAssignableFrom(property.getClass())) {
            T[] t= get(((T)property).getName());
            if (t!=null) {
                for (int i = 0; i < t.length; i++) {
                    if (t[i]==property) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#iterator()
     */
    public Iterator<T> iterator()
    {
        return new PIterator<T>(properties.values().iterator());
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#toArray()
     */
    public Object[] toArray()
    {
        return toPropertyArray();
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#toPropertyArray()
     */
    public T[] toPropertyArray()
    {
        T[] t= (T[])Array.newInstance(type, size);
        Iterator<T> it= iterator();
        for (int i = 0; i < t.length; i++) {
            t[i]=it.next();
        }
        return t;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#toArray(E[])
     */
    public <E extends T> E[] toArray(E[] array)
    {
        ArrayList<E> l= new ArrayList<E>(size);

        for (T t : this) {
            if (array.getClass().getComponentType().isAssignableFrom(t.getClass())) {
                l.add((E)t);
            }
        }
        return l.toArray(array);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> colection)
    {
        for (Object p : colection) {
            if (!contains(p)) {
                return false;
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#contains(java.lang.String)
     */
    public boolean contains(String name)
    {
        return properties.containsKey(name);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#contains(java.lang.String)
     */
    public boolean contains(String name, Class<? extends T> type) {
        return getFirst(name, type)!=null;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#get(java.lang.String)
     */
    public T[] get(String name)
    {
        T[] t= properties.get(name);
        if (t==null) {
            return zero_length;
        }
        return t;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#get(java.lang.String)
     */
    public <A extends T> A[] get(String name, Class<A> type) {

        T[] t= get(name);
        if (t==null) {
            return (A[])Array.newInstance(type, 0);
        }
        ArrayList<A> l= new ArrayList<A>(t.length);

        for (int i = 0; i < t.length; i++) {
            if (type.isAssignableFrom(t[i].getClass())) {
                l.add((A)t[i]);
            }
        }
        return l.toArray((A[])Array.newInstance(type, l.size()));
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#get(java.lang.String)
     */
    public T getFirst(String name) {
        T[] t=properties.get(name);
        if (t != null && t.length>0) {
            return t[0];
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#get(java.lang.String)
     */
    public <A extends T> A getFirst(String name, Class<A> type) {
        T[] t= get(name);
        if (t!=null && t.length>0) {
            for (int i = 0; i < t.length; i++) {
                if (type.isAssignableFrom(t[i].getClass())) {
                    return (A)t[i];
                }
            }
        }
        return null;
    }



    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#getPropertyNames()
     */
    public String[] getPropertyNames()
    {
        return (String[])properties.keySet().toArray();
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#addGroupListner(org.csstudio.dal.group.GroupListener)
     */
    public void addPropertyGroupListner(PropertyGroupListener<T> l)
    {
        if (groupListeners == null) {
            groupListeners = new ListenerList(PropertyGroupListener.class);
        }

        groupListeners.add(l);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#removeGroupListner(org.csstudio.dal.group.GroupListener)
     */
    public void removePropertyGroupListner(PropertyGroupListener<T> l)
    {
        if (groupListeners != null) {
            groupListeners.remove(l);
        }
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.group.PropertyCollection#getGroupListners()
     */
    public PropertyGroupListener<T>[] getPropertyGroupListners()
    {
        if (groupListeners != null) {
            return (PropertyGroupListener<T>[])groupListeners.toArray();
        }

        return new PropertyGroupListener[0];
    }

    protected void fireGroupEvent(PropertyGroupEvent<T> event, boolean added)
    {
        if (groupListeners == null) {
            return;
        }

        PropertyGroupListener<T>[] l = (PropertyGroupListener<T>[])groupListeners.toArray();

        if (added) {
            for (int i = 0; i < l.length; i++) {
                try {
                    l[i].membersAdded(event);
                } catch (Exception e) {
                    Logger.getLogger(PropertyCollectionMap.class).warn("Error in event handler, continuing.", e);
                }
            }
        } else {
            for (int i = 0; i < l.length; i++) {
                try {
                    l[i].membersRemoved(event);
                } catch (Exception e) {
                    Logger.getLogger(PropertyCollectionMap.class).warn("Error in event handler, continuing.", e);
                }
            }
        }
    }

    /*
     * @see MutablePropertyCollection#add(T)
     */
    protected void add(T property)
    {
        String name= ((DynamicValueProperty<?>)property).getName();

        T[] t = (T[])Array.newInstance(type, 1);
        t[0] = property;

        T[] p= get(property.getName());

        if (p==null||p.length==0) {
            properties.put(name, t);
        } else {
            T[] pp = (T[])Array.newInstance(type, p.length+1);
            System.arraycopy(p, 0, pp, 0, p.length);
            pp[p.length]=property;
            properties.put(name, pp);
        }
        size++;
        fireGroupEvent(new PropertyGroupEvent<T>(this, t), true);
    }

    /*
     * @see MutablePropertyCollection#remove(T)
     */
    protected void remove(T property)
    {
        T[] t= get(property.getName());
        if (t==null || t.length==0) {
            return;
        }
        for (int i = 0; i < t.length; i++) {
            if (property==t[i]) {
                if (t.length==1) {
                    properties.remove(property.getName());
                } else {
                    T[] pp= (T[])Array.newInstance(type, t.length-1);
                    System.arraycopy(t, 0, pp, 0, i);
                    System.arraycopy(t, i+1, pp, i, t.length-1-i);
                    properties.put(property.getName(), pp);
                }
            }
        }

        size--;

        t = (T[])Array.newInstance(property.getClass(), 1);
        t[0] = property;
        fireGroupEvent(new PropertyGroupEvent<T>(this, t), false);
    }

    protected void clear() {
        properties.clear();
    }
}

/* __oOo__ */
