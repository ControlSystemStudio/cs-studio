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

import org.csstudio.dal.DynamicValueProperty;

import java.util.Collection;


/**
 * This collection allows changing group of properties.
 *
 * @author <a href="mailto:igor.kriznar@cosylab.com">Igor Kriznar</a>
 */
public interface MutablePropertyCollection<T extends DynamicValueProperty<T>>
    extends PropertyCollection<T>, Collection<T>
{
    // Modification Operations
    /**
     * Adds the specified property to this collection. If a collection
     * refuses to add a particular element for any reason other than that it
     * already contains the element, it <i>must</i> throw an exception (rather
     * than returning <tt>false</tt>).  This preserves the invariant that a
     * collection always contains the specified element after this call
     * returns.
     *
     * @param property property to be appended to this collection.
     *
     * @return <tt>true</tt> if this collection changed as a result of the
     *         call.  (Returns <tt>false</tt> if this collection does not
     *         permit duplicates and already contains the specified element.)
     */
    boolean add(T property);

    /**
     * Removes all property elements from this collection, whose
     * name matches with the provided name.
     *
     * @param name unique name of removed properties
     *
     * @return true if collection was modified by this operation
     */
    boolean removeAll(String name);

    /**
     * Removes all property elements from this collection, whose
     * name matches with the provided name and type.
     *
     * @param name the name of removed properties
     * @param type the type of the property to be removed
     *
     * @return true if collection was modified by this operation
     */
    boolean removeAll(String name, Class<? extends T> type);
    /**
     * Removes provided property from this collection, if exists in
     * collection.
     *
     * @param property the property to be removed
     *
     * @return true if collection was modified by this operation
     */
    boolean remove(T property);

    /**
     * Adds all of the elements in the specified collection to this set
     * if they're not already present (optional operation).  If the specified
     * collection is also a set, the <tt>addAll</tt> operation effectively
     * modifies this set so that its value is the <i>union</i> of the two
     * sets.  The behavior of this operation is unspecified if the specified
     * collection is modified while the operation is in progress.
     *
     * @param c collection whose elements are to be added to this set.
     *
     * @return <tt>true</tt> if this set changed as a result of the call.
     *
     * @see #add(T)
     */
    boolean addAll(PropertyCollection<DynamicValueProperty<T>> c);

    /**
     * Retains only the elements in this set that are contained in the
     * specified collection (optional operation).  In other words, removes
     * from this set all of its elements that are not contained in the
     * specified collection.  If the specified collection is also a set, this
     * operation effectively modifies this set so that its value is the
     * <i>intersection</i> of the two sets.
     *
     * @param c collection that defines which elements this set will retain.
     *
     * @return <tt>true</tt> if this collection changed as a result of the
     *         call.
     *
     * @see #remove(Object)
     */
    boolean retainAll(PropertyCollection<DynamicValueProperty<T>> c);

    /**
     * Removes from this set all of its elements that are contained in
     * the specified collection (optional operation).  If the specified
     * collection is also a set, this operation effectively modifies this set
     * so that its value is the <i>asymmetric set difference</i> of the two
     * sets.
     *
     * @param c collection that defines which elements will be removed from
     *        this set.
     *
     * @return <tt>true</tt> if this set changed as a result of the call.
     *
     * @see #remove(String)
     */
    boolean removeAll(PropertyCollection<DynamicValueProperty<T>> c);

    /**
     * Removes all of the elements from this set. This set will be
     * empty after this call returns (unless it throws an exception).
     */
    void clear();
} /* __oOo__ */


/* __oOo__ */
