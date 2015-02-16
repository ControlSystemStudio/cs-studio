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
package org.epics.css.dal.directory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;


/**
 * Implementation of Attributes interface, which tries to optimize new Attribute object creation. New instance of
 * Attribute implementation (SingleValueAttribute) is created only when necessary. Allows direct usage of internal
 * Map containg attributes as objects.
 *
 * @author ikriznar
 *
 */
public class Attributes implements javax.naming.directory.Attributes
{
	private static final long serialVersionUID = 4747575955857437856L;
	protected Map<String, Object> elements;

	/**
	 * Default constructor.
	 */
	public Attributes()
	{
		super();
		elements = new HashMap<String, Object>();
	}

	/**
	 * Default constructor.
	 */
	private Attributes(Map<String, Object> elements)
	{
		super();
		this.elements = new HashMap<String, Object>(elements);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.Attributes#isCaseIgnored()
	 */
	public boolean isCaseIgnored()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.Attributes#size()
	 */
	public int size()
	{
		return elements.size();
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.Attributes#get(java.lang.String)
	 */
	public Attribute get(String attrID)
	{
		Object o = elements.get(attrID);

		if (o == null) {
			return null;
		}

		if (o instanceof Attribute) {
			return (Attribute)o;
		}

		Attribute a = new SingleValueAttribute(attrID, o);
		elements.put(attrID, a);

		return a;
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.Attributes#getAll()
	 */
	public NamingEnumeration<?extends Attribute> getAll()
	{
		return new NamingEnumeration<Attribute>() {
				Iterator<String> it = elements.keySet().iterator();

				public Attribute nextElement()
				{
					return get(it.next());
				}

				public boolean hasMoreElements()
				{
					return it.hasNext();
				}

				public void close() throws NamingException
				{
					it = null;
				}

				public boolean hasMore() throws NamingException
				{
					return it.hasNext();
				}

				public Attribute next() throws NamingException
				{
					return get(it.next());
				}
			};
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.Attributes#getIDs()
	 */
	public NamingEnumeration<String> getIDs()
	{
		return new NamingEnumeration<String>() {
				Iterator<String> it = elements.keySet().iterator();

				public String nextElement()
				{
					return it.next();
				}

				public boolean hasMoreElements()
				{
					return it.hasNext();
				}

				public void close() throws NamingException
				{
					it = null;
				}

				public boolean hasMore() throws NamingException
				{
					return it.hasNext();
				}

				public String next() throws NamingException
				{
					return it.next();
				}
			};
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.Attributes#put(java.lang.String, java.lang.Object)
	 */
	public Attribute put(String attrID, Object val)
	{
		Attribute a = get(attrID);
		elements.put(attrID, val);

		return a;
	}

	public Object putAttributeValue(String attrID, Object val)
	{
		Object a = getAttributeValue(attrID);
		elements.put(attrID, val);

		return a;
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.Attributes#put(javax.naming.directory.Attribute)
	 */
	public Attribute put(Attribute attr)
	{
		Attribute a = get(attr.getID());
		elements.put(attr.getID(), attr);

		return a;
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.Attributes#remove(java.lang.String)
	 */
	public Attribute remove(String attrID)
	{
		Attribute a = get(attrID);
		elements.remove(attrID);

		return a;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone()
	{
		return new Attributes(elements);
	}

	/**
	 * Return attibute value.
	 * @param id
	 * @return attribute value if exists
	 */
	public Object getAttributeValue(String id)
	{
		Object o = elements.get(id);

		if (o instanceof Attribute) {
			try {
				return ((Attribute)o).get();
			} catch (NamingException n) {
				return null;
			}
		}

		return o;
	}
}

/* __oOo__ */
