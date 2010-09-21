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

package org.epics.css.dal.directory;

import java.util.Iterator;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;


/**
 *
 * <code>BindingEnumeration</code> is a simple implementation of the NamingEnumeration. It
 * takes a HashMap of objects and iterates over the values of this object. The returned objects
 * of this enumeration are instances of javax.naming.Binding
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 * @since VERSION
 */
public class BindingEnumeration implements NamingEnumeration<Binding>
{
	private Map<String, ?> data;
	private Iterator<String> iterator;

	/**
	 * Creates a new BindingEnumeration object.
	 *
	 * @param en DOCUMENT ME!
	 */
	public BindingEnumeration(Map<String, ?> rawData)
	{
		this.data = rawData;
		iterator = data.keySet().iterator();
	}

	/*
	 * @see javax.naming.NamingEnumeration#close()
	 */
	public void close() throws NamingException
	{
		data.clear();
	}

	/*
	 * @see javax.naming.NamingEnumeration#hasMore()
	 */
	public boolean hasMore() throws NamingException
	{
		return hasMoreElements();
	}

	/*
	 * @see javax.naming.NamingEnumeration#next()
	 */
	public Binding next() throws NamingException
	{
		return nextElement();
	}

	/*
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements()
	{
		return iterator.hasNext();
	}

	/*
	 * @see java.util.Enumeration#nextElement()
	 */
	public Binding nextElement()
	{
		String key = iterator.next();

		return new Binding(key, data.get(key));
	}
}

/* __oOo__ */
