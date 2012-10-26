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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.cosylab.naming.URIName;


/**
 * Simple implementation of <code>DirContext</code>, which stores all
 * attributes and objects in a flat simple hashmap by their names. Works for
 * lookup and bind.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public class DirContextSupport implements DirContext
{
	class DALSearchEnumeration implements NamingEnumeration<SearchResult>
	{
		private Map<String, DALDescriptor> items;
		private Iterator<String> iterator;

		public DALSearchEnumeration(Map<String, DALDescriptor> items)
		{
			this.items = items;
			iterator = items.keySet().iterator();
		}

		public void close() throws NamingException
		{
			iterator = null;
			items = null;
		}

		public boolean hasMore() throws NamingException
		{
			return hasMoreElements();
		}

		public SearchResult next() throws NamingException
		{
			return nextElement();
		}

		public boolean hasMoreElements()
		{
			return iterator.hasNext();
		}

		public SearchResult nextElement()
		{
			String key = iterator.next();
			DALDescriptor d = items.get(key);

			return new SearchResult(key, d, d, true);
		}
	}

	protected final static String EMPTY = "";
	protected String name;
	protected URINameParser parser = new URINameParser();
	protected Hashtable<String, Object> env = new Hashtable<String, Object>();
	protected Map<String, Object> values = new Hashtable<String, Object>();
	protected Map<String, Attributes> attributes = new Hashtable<String, Attributes>();

	/**
	 * Creates a new DirContextSupport object.
	 */
	public DirContextSupport()
	{
		super();
	}

	/**
	 * Transforms Name to string, at same time cheks name if it is
	 * valid and performs any necessary translation.
	 *
	 * @param name Name instance
	 *
	 * @return string supported by this context
	 */
	protected String resolveName(Name name) throws NamingException
	{
		if (name == null || name.isEmpty()) {
			return "";
		}

		if (name instanceof URIName) {
			URIName n = (URIName)name;

			if (!n.isRelative()) {
				throw new NamingException("URI " + name + " is not relative.");
			}
		}

		return name.toString();
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#getAttributes(javax.naming.Name)
	 */
	public Attributes getAttributes(Name name) throws NamingException
	{
		return getAttributes(resolveName(name));
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#getAttributes(java.lang.String)
	 */
	public Attributes getAttributes(String name) throws NamingException
	{
		Attributes a = attributes.get(name);

		return a;
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#getAttributes(javax.naming.Name,
	 *      java.lang.String[])
	 */
	public Attributes getAttributes(Name name, String[] attrIds)
		throws NamingException
	{
		return getAttributes(resolveName(name), attrIds);
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#getAttributes(java.lang.String,
	 *      java.lang.String[])
	 */
	public Attributes getAttributes(String name, String[] attrIds)
		throws NamingException
	{
		Attributes a = attributes.get(name);

		if (a == null) {
			return a;
		}

		BasicAttributes at = new BasicAttributes();

		for (int i = 0; i < attrIds.length; i++) {
			Attribute aa = a.get(attrIds[i]);

			if (aa != null) {
				at.put(aa);
			}
		}

		return at;
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#modifyAttributes(javax.naming.Name,
	 *      int, javax.naming.directory.Attributes)
	 */
	public void modifyAttributes(Name name, int mod_op, Attributes attrs)
		throws NamingException
	{
		// TODO Auto-generated method stub
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#modifyAttributes(java.lang.String,
	 *      int, javax.naming.directory.Attributes)
	 */
	public void modifyAttributes(String name, int mod_op, Attributes attrs)
		throws NamingException
	{
		// TODO Auto-generated method stub
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#modifyAttributes(javax.naming.Name,
	 *      javax.naming.directory.ModificationItem[])
	 */
	public void modifyAttributes(Name name, ModificationItem[] mods)
		throws NamingException
	{
		// TODO Auto-generated method stub
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#modifyAttributes(java.lang.String,
	 *      javax.naming.directory.ModificationItem[])
	 */
	public void modifyAttributes(String name, ModificationItem[] mods)
		throws NamingException
	{
		// TODO Auto-generated method stub
	}

	public void bind(String name, Object obj, Attributes attrs)
		throws NamingException
	{
		if (obj != null && values.containsKey(name)) {
			throw new NamingException("Name '" + name + "' already binded.");
		}

		if (attrs != null && attributes.containsKey(name)) {
			throw new NamingException("Name '" + name + "' already binded.");
		}

		if (obj != null) {
			values.put(name, obj);
		}

		if (attrs != null) {
			attributes.put(name, attrs);
		} else if (obj instanceof Attributes) {
			attributes.put(name, (Attributes)obj);
		}
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#bind(javax.naming.Name,
	 *      java.lang.Object, javax.naming.directory.Attributes)
	 */
	public void bind(Name name, Object obj, Attributes attrs)
		throws NamingException
	{
		bind(resolveName(name), obj, attrs);
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#rebind(javax.naming.Name,
	 *      java.lang.Object, javax.naming.directory.Attributes)
	 */
	public void rebind(Name name, Object obj, Attributes attrs)
		throws NamingException
	{
		rebind(resolveName(name), obj, attrs);
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#rebind(java.lang.String,
	 *      java.lang.Object, javax.naming.directory.Attributes)
	 */
	public void rebind(String name, Object obj, Attributes attrs)
		throws NamingException
	{
		if (obj != null) {
			values.put(name, obj);
		}

		if (attrs != null) {
			attributes.put(name, attrs);
		} else if (obj instanceof Attributes) {
			attributes.put(name, (Attributes)obj);
		}
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#createSubcontext(javax.naming.Name,
	 *      javax.naming.directory.Attributes)
	 */
	public DirContext createSubcontext(Name name, Attributes attrs)
		throws NamingException
	{
		throw new OperationNotSupportedException();
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#createSubcontext(java.lang.String,
	 *      javax.naming.directory.Attributes)
	 */
	public DirContext createSubcontext(String name, Attributes attrs)
		throws NamingException
	{
		throw new OperationNotSupportedException();
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#getSchema(javax.naming.Name)
	 */
	public DirContext getSchema(Name name) throws NamingException
	{
		throw new OperationNotSupportedException();
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#getSchema(java.lang.String)
	 */
	public DirContext getSchema(String name) throws NamingException
	{
		throw new OperationNotSupportedException();
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#getSchemaClassDefinition(javax.naming.Name)
	 */
	public DirContext getSchemaClassDefinition(Name name)
		throws NamingException
	{
		throw new OperationNotSupportedException();
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#getSchemaClassDefinition(java.lang.String)
	 */
	public DirContext getSchemaClassDefinition(String name)
		throws NamingException
	{
		throw new OperationNotSupportedException();
	}

	/*
	 *  (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(javax.naming.Name, javax.naming.directory.Attributes, java.lang.String[])
	 */
	public NamingEnumeration<SearchResult> search(Name name,
	    Attributes matchingAttributes, String[] attributesToReturn)
		throws NamingException
	{
		return search(resolveName(name), matchingAttributes, attributesToReturn);
	}

	/**
	 * Ignores attributesToReturn.
	 * @see javax.naming.directory.DirContext#search(java.lang.String,
	 *      javax.naming.directory.Attributes, java.lang.String[])
	 */
	public NamingEnumeration<SearchResult> search(String name,
	    Attributes matchingAttributes, String[] attributesToReturn)
		throws NamingException
	{
		if (!(matchingAttributes instanceof org.epics.css.dal.directory.Attributes)) {
			throw new NamingException(
			    "Onlu instance of org.epics.css.dal.directory.Attributes is supported.");
		}

		/*
		 * At this moment only searching trough DALDescriptor is supported and makes sense.
		 */
		org.epics.css.dal.directory.Attributes att = (org.epics.css.dal.directory.Attributes)matchingAttributes;

		Map<String, DALDescriptor> result = new HashMap<String, DALDescriptor>();

		DescriptorType type = (DescriptorType)att.getAttributeValue(DALDescriptor.DESCRIPTOR_TYPE);
		Class ctype = (Class)att.getAttributeValue(DALDescriptor.CLASS_TYPE);

		Iterator<String> it = values.keySet().iterator();
		String key;

		while (it.hasNext()) {
			key = it.next();

			if (!key.startsWith(name)) {
				continue;
			}

			Object o = values.get(key);

			if (!(o instanceof DALDescriptor)) {
				continue;
			}

			DALDescriptor desc = (DALDescriptor)o;

			if (type != null && !type.equals(desc.getDescriptorType())) {
				continue;
			}

			if (ctype != null && !ctype.equals(desc.getClassType())) {
				continue;
			}

			result.put(key, desc);
		}

		return new DALSearchEnumeration(result);
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#search(javax.naming.Name,
	 *      javax.naming.directory.Attributes)
	 */
	public NamingEnumeration<SearchResult> search(Name name,
	    Attributes matchingAttributes) throws NamingException
	{
		return search(resolveName(name), matchingAttributes, null);
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#search(java.lang.String,
	 *      javax.naming.directory.Attributes)
	 */
	public NamingEnumeration<SearchResult> search(String name,
	    Attributes matchingAttributes) throws NamingException
	{
		return search(name, matchingAttributes, null);
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#search(javax.naming.Name,
	 *      java.lang.String, javax.naming.directory.SearchControls)
	 */
	public NamingEnumeration<SearchResult> search(Name name, String filter,
	    SearchControls cons) throws NamingException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#search(java.lang.String,
	 *      java.lang.String, javax.naming.directory.SearchControls)
	 */
	public NamingEnumeration<SearchResult> search(String name, String filter,
	    SearchControls cons) throws NamingException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#search(javax.naming.Name,
	 *      java.lang.String, java.lang.Object[],
	 *      javax.naming.directory.SearchControls)
	 */
	public NamingEnumeration<SearchResult> search(Name name, String filterExpr,
	    Object[] filterArgs, SearchControls cons) throws NamingException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 * @see javax.naming.directory.DirContext#search(java.lang.String,
	 *      java.lang.String, java.lang.Object[],
	 *      javax.naming.directory.SearchControls)
	 */
	public NamingEnumeration<SearchResult> search(String name,
	    String filterExpr, Object[] filterArgs, SearchControls cons)
		throws NamingException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 * @see javax.naming.Context#lookup(javax.naming.Name)
	 */
	public Object lookup(Name name) throws NamingException
	{
		if (name == null) {
			throw new InvalidNameException("Name is null.");
		}

		if (name.isEmpty()) {
			return this;
		}

		return values.get(resolveName(name));
	}

	/**
	 *
	 * @see javax.naming.Context#lookup(java.lang.String)
	 */
	public Object lookup(String name) throws NamingException
	{
		if (name == null) {
			throw new InvalidNameException("Name is null.");
		}

		if (name.length() == 0) {
			return this;
		}

		return values.get(name);
	}

	/**
	 *
	 * @see javax.naming.Context#bind(javax.naming.Name, java.lang.Object)
	 */
	public void bind(Name name, Object obj) throws NamingException
	{
		bind(resolveName(name), obj, null);
	}

	/**
	 *
	 * @see javax.naming.Context#bind(java.lang.String, java.lang.Object)
	 */
	public void bind(String name, Object obj) throws NamingException
	{
		bind(name, obj, null);
	}

	/**
	 *
	 * @see javax.naming.Context#rebind(javax.naming.Name, java.lang.Object)
	 */
	public void rebind(Name name, Object obj) throws NamingException
	{
		rebind(resolveName(name), obj);
	}

	/**
	 *
	 * @see javax.naming.Context#rebind(java.lang.String, java.lang.Object)
	 */
	public void rebind(String name, Object obj) throws NamingException
	{
		values.put(name, obj);
	}

	/**
	 *
	 * @see javax.naming.Context#unbind(javax.naming.Name)
	 */
	public void unbind(Name name) throws NamingException
	{
		unbind(resolveName(name));
	}

	/**
	 *
	 * @see javax.naming.Context#unbind(java.lang.String)
	 */
	public void unbind(String name) throws NamingException
	{
		values.remove(name);
		attributes.remove(name);
	}

	/**
	 *
	 * @see javax.naming.Context#rename(javax.naming.Name, javax.naming.Name)
	 */
	public void rename(Name oldName, Name newName) throws NamingException
	{
		rename(resolveName(oldName), resolveName(newName));
	}

	/**
	 *
	 * @see javax.naming.Context#rename(java.lang.String, java.lang.String)
	 */
	public void rename(String oldName, String newName)
		throws NamingException
	{
		if (values.containsKey(newName)) {
			throw new NamingException("Name '" + newName + "' already binded.");
		}

		if (attributes.containsKey(newName)) {
			throw new NamingException("Name '" + newName + "' already binded.");
		}

		Object o = values.remove(oldName);
		Attributes a = attributes.remove(oldName);

		if (o != null) {
			values.put(newName, o);
		}

		if (a != null) {
			attributes.put(newName, a);
		} else if (o instanceof Attributes) {
			attributes.put(newName, (Attributes)o);
		}
	}

	public NamingEnumeration<NameClassPair> list(Name name)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		return new NameClassEnumeration(values);
	}

	/**
	 *
	 * @see javax.naming.Context#list(java.lang.String)
	 */
	public NamingEnumeration<NameClassPair> list(String name)
		throws NamingException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 * @see javax.naming.Context#listBindings(javax.naming.Name)
	 */
	public NamingEnumeration<Binding> listBindings(Name name)
		throws NamingException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 * @see javax.naming.Context#listBindings(java.lang.String)
	 */
	public NamingEnumeration<Binding> listBindings(String name)
		throws NamingException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 * @see javax.naming.Context#destroySubcontext(javax.naming.Name)
	 */
	public void destroySubcontext(Name name) throws NamingException
	{
		throw new OperationNotSupportedException();
	}

	/**
	 *
	 * @see javax.naming.Context#destroySubcontext(java.lang.String)
	 */
	public void destroySubcontext(String name) throws NamingException
	{
		throw new OperationNotSupportedException();
	}

	/**
	 *
	 * @see javax.naming.Context#createSubcontext(javax.naming.Name)
	 */
	public Context createSubcontext(Name name) throws NamingException
	{
		throw new OperationNotSupportedException();
	}

	/**
	 *
	 * @see javax.naming.Context#createSubcontext(java.lang.String)
	 */
	public Context createSubcontext(String name) throws NamingException
	{
		throw new OperationNotSupportedException();
	}

	/**
	 *
	 * @see javax.naming.Context#lookupLink(javax.naming.Name)
	 */
	public Object lookupLink(Name name) throws NamingException
	{
		return lookup(name);
	}

	/**
	 *
	 * @see javax.naming.Context#lookupLink(java.lang.String)
	 */
	public Object lookupLink(String name) throws NamingException
	{
		return lookup(name);
	}

	/**
	 *
	 * @see javax.naming.Context#getNameParser(javax.naming.Name)
	 */
	public NameParser getNameParser(Name name) throws NamingException
	{
		return parser;
	}

	/**
	 *
	 * @see javax.naming.Context#getNameParser(java.lang.String)
	 */
	public NameParser getNameParser(String name) throws NamingException
	{
		return parser;
	}

	/**
	 *
	 * @see javax.naming.Context#composeName(javax.naming.Name,
	 *      javax.naming.Name)
	 */
	public Name composeName(Name name, Name prefix) throws NamingException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 * @see javax.naming.Context#composeName(java.lang.String,
	 *      java.lang.String)
	 */
	public String composeName(String name, String prefix)
		throws NamingException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 * @see javax.naming.Context#addToEnvironment(java.lang.String,
	 *      java.lang.Object)
	 */
	public Object addToEnvironment(String propName, Object propVal)
		throws NamingException
	{
		if (propName == null) {
			throw new NullPointerException("propName");
		}

		return env.put(propName, propVal);
	}

	/**
	 *
	 * @see javax.naming.Context#removeFromEnvironment(java.lang.String)
	 */
	public Object removeFromEnvironment(String propName)
		throws NamingException
	{
		return env.remove(propName);
	}

	/**
	 *
	 * @see javax.naming.Context#getEnvironment()
	 */
	public Hashtable<String, Object> getEnvironment() throws NamingException
	{
		return new Hashtable<String, Object>(env);
	}

	/**
	 *
	 * @see javax.naming.Context#close()
	 */
	public void close() throws NamingException
	{
		// TODO Auto-generated method stub
	}

	/**
	 *
	 * @see javax.naming.Context#getNameInNamespace()
	 */
	public String getNameInNamespace() throws NamingException
	{
		return name;
	}
} /* __oOo__ */


/* __oOo__ */
