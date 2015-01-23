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

package org.csstudio.dal.context;

import org.csstudio.dal.DynamicValueProperty;


/**
 * Colection represents group of <code>TypelessProperty</code> objects. It is
 * designedby <code>Collection</code> interface. Primary functionality is
 * implementation unspecific acces to group of properties, it has no
 * modification accessors..
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 * @see java.util.Collection
 */
public interface PropertyContext
{
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
	public boolean containsProperty(Object property);

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
	public DynamicValueProperty[] toPropertyArray();

	/**
	 * Returns <tt>true</tt> if this collection contains property,
	 * whose unique name equals the specified name.
	 *
	 * @param name name of the typeless property depends on the context of the collection
	 *
	 * @return <tt>true</tt> if this collection contains property with the
	 *         specified unique name.
	 */
	public boolean containsProperty(String name);

	/**
	 * Returns <code>TypelessProperty</code> element whose unique name
	 * equals the specified name. If property with specified name is not
	 * present in collection, <code>null</code> is returned.
	 *
	 * @param name unique name of typless property
	 *
	 * @return <tt>true</tt> if this collection contains property with the
	 *         specified unique name.
	 */
	public DynamicValueProperty getProperty(String name);

	/**
	 * Returns names of properties in this collection. Name may be or
	 * proper unique name or relative name, which is valid only in context of
	 * this collection.
	 *
	 * @return names of properties in this collection
	 */
	public String[] getPropertyNames();
	
	/**
	 * Returns plug type string, which is distinguishing for plug which
	 * creates  proxies for particular communication layer.<p>For
	 * example plug that connects to EPICS device my return string "EPICS".</p>
	 *
	 * @return plug destingushing type name
	 */
	public String getPlugType();

} /* __oOo__ */


/* __oOo__ */
