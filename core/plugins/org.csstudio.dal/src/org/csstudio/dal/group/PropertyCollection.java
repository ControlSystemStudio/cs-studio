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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.csstudio.dal.DynamicValueProperty;


/**
 * Colection represents group of <code>DynamicValueProperty</code> objects. It is
 * designed by <code>Collection</code> interface. Primary functionality is
 * implementation unspecific acces to group of properties, it has no
 * modification accessors.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 * @see java.util.Collection
 */
public interface PropertyCollection<T extends DynamicValueProperty<?>> extends Iterable<T>
{
	/**
	 * Returns the number of elements in this collection.  If this
	 * collection contains more than <tt>Integer.MAX_VALUE</tt> elements,
	 * returns <tt>Integer.MAX_VALUE</tt>.
	 *
	 * @return the number of elements in this collection
	 */
	public int size();

	/**
	 * Returns <tt>true</tt> if this collection contains no elements.
	 *
	 * @return <tt>true</tt> if this collection contains no elements
	 */
	public boolean isEmpty();

	/**
	 * Returns <tt>true</tt> if this collection contains the specified
	 * element.  More formally, returns <tt>true</tt> if and only if this
	 * collection contains at least one element <tt>e</tt> such that
	 * <tt>(o==null ? e==null : o.equals(e))</tt>.
	 *
	 * @param property element whose presence in this collection is to be
	 *        tested.
	 *
	 * @return <tt>true</tt> if this collection contains the specified element
	 */
	public boolean contains(Object property);

	/**
	 * Returns an iterator over the elements in this collection.  There
	 * are no guarantees concerning the order in which the elements are
	 * returned (unless this collection is an instance of some class that
	 * provides a guarantee).
	 *
	 * @return an <tt>Iterator</tt> over the elements in this collection
	 */
	@Override
    public Iterator<T> iterator();

	/**
	 * Returns an array containing all of the elements in this
	 * collection.  If the collection makes any guarantees as to what order
	 * its elements are returned by its iterator, this method must return the
	 * elements in the same order.<p>The returned array will be "safe"
	 * in that no references to it are maintained by this collection.  (In
	 * other words, this method must allocate a new array even if this
	 * collection is backed by an array). The caller is thus free to modify
	 * the returned array.</p>
	 *  <p>This method acts as bridge between array-based and
	 * collection-based APIs.</p>
	 *
	 * @return an array containing all of the elements in this collection
	 */
	public Object[] toArray();

	/**
	 * Returns an array containing all of the elements in this
	 * collection.  If the collection makes any guarantees as to what order
	 * its elements are returned by its iterator, this method must return the
	 * elements in the same order.<p>The returned array will be "safe"
	 * in that no references to it are maintained by this collection.  (In
	 * other words, this method must allocate a new array even if this
	 * collection is backed by an array). The caller is thus free to modify
	 * the returned array.</p>
	 *  <p>This method acts as bridge between array-based and
	 * collection-based APIs.</p>
	 *
	 * @return an array containing all of the elements in this collection
	 */
	public T[] toPropertyArray();

	/**
	 * Returns an array containing all of the elements in this
	 * collection;  the runtime type of the returned array is that of the
	 * specified array.   If the collection fits in the specified array, it is
	 * returned therein.   Otherwise, a new array is allocated with the
	 * runtime type of the  specified array and the size of this collection.<p>If
	 * this collection fits in the specified array with room to spare (i.e.,
	 * the array has more elements than this collection), the element in the
	 * array immediately following the end of the collection is set to
	 * <tt>null</tt>.  This is useful in determining the length of this
	 * collection <i>only</i> if the caller knows that this collection does
	 * not contain any <tt>null</tt> elements.)</p>
	 *  <p>If this collection makes any guarantees as to what order its
	 * elements are returned by its iterator, this method must return the
	 * elements in the same order.</p>
	 *  <p>Like the <tt>toArray</tt> method, this method acts as bridge
	 * between array-based and collection-based APIs.  Further, this method
	 * allows precise control over the runtime type of the output array, and
	 * may, under certain circumstances, be used to save allocation costs</p>
	 *  <p>Suppose <tt>l</tt> is a <tt>List</tt> known to contain only
	 * strings. The following code can be used to dump the list into a newly
	 * allocated array of <tt>String</tt>:<pre>
	 *     String[] x = (String[]) v.toArray(new String[0]);</pre></p>
	 *  <p>Note that <tt>toArray(new Object[0])</tt> is identical in
	 * function to <tt>toArray()</tt>.</p>
	 *
	 * @param <E> array type
	 * @param array the array into which the elements of this collection are to
	 *        be stored, if it is big enough; otherwise, a new array of the
	 *        same runtime type is allocated for this purpose.
	 *
	 * @return an array containing the elements of this collection
	 */
	public <E extends T> E[] toArray(E[] array);

	/**
	 * Returns <tt>true</tt> if this collection contains all of the
	 * elements in the specified collection.
	 *
	 * @param colection collection to be checked for containment in this
	 *        collection.
	 *
	 * @return <tt>true</tt> if this collection contains all of the elements in
	 *         the specified collection
	 *
	 * @see #contains(Object)
	 */
	public boolean containsAll(Collection<?> colection);

	/**
	 * Compares the specified object with this collection for equality.<p>While
	 * the <tt>Collection</tt> interface adds no stipulations to the general
	 * contract for the <tt>Object.equals</tt>, programmers who implement the
	 * <tt>Collection</tt> interface "directly" (in other words, create a
	 * class that is a <tt>Collection</tt> but is not a <tt>Set</tt> or a
	 * <tt>List</tt>) must exercise care if they choose to override the
	 * <tt>Object.equals</tt>.  It is not necessary to do so, and the simplest
	 * course of action is to rely on <tt>Object</tt>'s implementation, but
	 * the implementer may wish to implement a "value comparison" in place of
	 * the default "reference comparison."  (The <tt>List</tt> and
	 * <tt>Set</tt> interfaces mandate such value comparisons.)</p>
	 *  <p>The general contract for the <tt>Object.equals</tt> method
	 * states that equals must be symmetric (in other words,
	 * <tt>a.equals(b)</tt> if and only if <tt>b.equals(a)</tt>).  The
	 * contracts for <tt>List.equals</tt> and <tt>Set.equals</tt> state that
	 * lists are only equal to other lists, and sets to other sets.  Thus, a
	 * custom <tt>equals</tt> method for a collection class that implements
	 * neither the <tt>List</tt> nor <tt>Set</tt> interface must return
	 * <tt>false</tt> when this collection is compared to any list or set. (By
	 * the same logic, it is not possible to write a class that correctly
	 * implements both the <tt>Set</tt> and <tt>List</tt> interfaces.)</p>
	 *
	 * @param o Object to be compared for equality with this collection.
	 *
	 * @return <tt>true</tt> if the specified object is equal to this
	 *         collection
	 *
	 * @see Object#equals(Object)
	 * @see Set#equals(Object)
	 * @see List#equals(Object)
	 */
	@Override
    public boolean equals(Object o);

	/**
	 * Returns the hash code value for this collection.  While the
	 * <tt>Collection</tt> interface adds no stipulations to the general
	 * contract for the <tt>Object.hashCode</tt> method, programmers should
	 * take note that any class that overrides the <tt>Object.equals</tt>
	 * method must also override the <tt>Object.hashCode</tt> method in order
	 * to satisfy the general contract for the <tt>Object.hashCode</tt>method.
	 * In particular, <tt>c1.equals(c2)</tt> implies that
	 * <tt>c1.hashCode()==c2.hashCode()</tt>.
	 *
	 * @return the hash code value for this collection
	 *
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 */
	@Override
    public int hashCode();

	// Named access
	/**
	 * Returns <tt>true</tt> if this collection contains property,
	 * whose name equals the specified name.
	 *
	 * @param name unique name of typeless property
	 *
	 * @return <tt>true</tt> if this collection contains property with the
	 *         specified unique name.
	 */
	public boolean contains(String name);
	/**
	 * Returns <tt>true</tt> if this collection contains property,
	 * whose name equals the specified name and is of specified type.
	 *
	 * @param name unique name of property
	 * @param type of property
	 *
	 * @return <tt>true</tt> if this collection contains property with the
	 *         specified unique name.
	 */
	public boolean contains(String name, Class<? extends T> type);

	/**
	 * Returns array of <code>TypelessProperty</code> elements whose name
	 * equals the specified name and type. If property no property with specified name is
	 * present in collection, 0 length array is returned.
	 *
	 * @param name unique name of property
	 * @param type of property
	 *
	 * @return array of <code>TypelessProperty</code> elements with the
	 *         specified name.
	 */
	public <A extends T>  A[] get(String name, Class<A> type);

	/**
	 * Returns <code>TypelessProperty</code> elements whose name
	 * equals the specified name and type. If there is more then one then first will be returned.
	 * There is no specific order. If property no property with specified name is
	 * present in collection, 0 length array is returned.
	 *
	 * @param name unique name of property
	 * @param type of property
	 *
	 * @return array of <code>TypelessProperty</code> elements with the
	 *         specified name.
	 */
	public <A extends T>  A getFirst(String name, Class<A> type);

	/**
	 * Returns array of <code>TypelessProperty</code> elements whose name
	 * equals the specified name. If property no property with specified name is
	 * present in collection, 0 length array is returned.
	 *
	 * @param name unique name of property
	 *
	 * @return array of <code>TypelessProperty</code> elements with the
	 *         specified name.
	 */
	public T[] get(String name);

	/**
	 * Returns <code>TypelessProperty</code> elements whose name
	 * equals the specified name.
	 * If there is more then one then first will be returned.
	 * There is no specific order.
	 * If property no property with specified name is
	 * present in collection, 0 length array is returned.
	 *
	 * @param name unique name of property
	 *
	 * @return array of <code>TypelessProperty</code> elements with the
	 *         specified name.
	 */
	public T getFirst(String name);
	/**
	 * Returns names of properties in this collection. Name may be or
	 * proper unique name or relative name, which is valid only in context of
	 * this collection.
	 *
	 * @return names of properties in this collection
	 */
	public String[] getPropertyNames();

	/**
	 * Registers provided listener for membership modification events.
	 * The listener is notified when members are added or removed from this
	 * collection.
	 *
	 * @param l the membership events listener
	 */
	public void addPropertyGroupListner(PropertyGroupListener<T> l);

	/**
	 * Deregisters provided listener for membership modification
	 * events.
	 *
	 * @param l the membership events listener
	 */
	public void removePropertyGroupListner(PropertyGroupListener<T> l);

	/**
	 * Returns an array of registered listeners.
	 *
	 * @return an array of registered listeners
	 */
	public PropertyGroupListener<T>[] getPropertyGroupListners();
}

/* __oOo__ */
