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

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.dal.simple.RemoteInfo;

import com.cosylab.naming.URIName;


/**
 * This is <code>DirContext</code> implementation for CSS federation
 * management of different protocol specific subcontexts. In order for this
 * federation manager to find appropriate subcontext following guidelines must
 * be followed.
 *  <ul>
 *      <li>All <code>Name</code> instances must be implementation of
 *      <code>URIName</code> or <code>RemoteInfo</code>.</li>
 *      <li><code>URIName</code> must present URI with schema defined
 *      as follows: <code>css-PLUG</code> where <code>PLUG</code> is protocol
 *      name which must match with Plug type.</li>
 *      <li>All particular plug implementation must bind their specific
 *      <code>DirContext</code> implementation under name <code>css-PLUG</code></li>
 *  </ul>
 *  <p>For example see Simulation implementation of
 * <code>DirContext</code>.</p>
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public class InitialDirContextImpl implements DirContext
{
	protected URINameParser parser;
	protected Hashtable env;
	protected Map<String, DirContext> dirs = new Hashtable<String, DirContext>();
	protected Attributes attr = new BasicAttributes(false);

	/**
	 * Creates a new InitialDirContextImpl object.
	 */
	public InitialDirContextImpl()
	{
		this(new Hashtable<String, Object>());
	}

	/**
	 * Creates a new InitialDirContextImpl object.
	 *
	 * @param env Environment HashTable
	 */
	public InitialDirContextImpl(Hashtable<?,?> env)
	{
		super();
		parser = new URINameParser();

		this.env = env;
	}

	/**
	 * Finds subcontext which is representing
	 *
	 * @param name must be absolute <code>URIName</code> instance.
	 *
	 * @return <code>DirContext</code> presenting requested schema part or
	 *         exception is thrown.
	 *
	 * @throws NamingException if context for schema is not loaded or error
	 *         occured.
	 * @throws InvalidNameException if name is not absolute
	 */
	public DirContext resolveSchema(Name name) throws NamingException
	{
		if (!(name instanceof URIName)) {
			throw new InvalidNameException("Name '" + name + " is not URIName.");
		}

		URIName un = (URIName)name;

		if (un.isRelative()) {
			throw new InvalidNameException("Name '" + name
			    + " is not absolute.");
		}

		DirContext ctx = dirs.get(un.get(0));

		if (ctx == null) {
			throw new NamingException("DirContext for schema from '" + name
			    + "' is not loaded.");
		}

		return ctx;
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getAttributes(javax.naming.Name)
	 */
	public Attributes getAttributes(Name name) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			return (Attributes)attr.clone();
		}

		return resolveSchema(name).getAttributes(name.getSuffix(1));
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getAttributes(java.lang.String)
	 */
	public Attributes getAttributes(String name) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		Name n = parser.parse(name);

		if (n.isEmpty()) {
			return (Attributes)attr.clone();
		}

		return resolveSchema(n).getAttributes(n.getSuffix(1));
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getAttributes(javax.naming.Name, java.lang.String[])
	 */
	public Attributes getAttributes(Name name, String[] attrIds)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			Attributes a = new BasicAttributes(false);

			for (int i = 0; i < attrIds.length; i++) {
				a.put(attr.get(attrIds[i]));
			}

			return a;
		}

		return resolveSchema(name).getAttributes(name.getSuffix(1), attrIds);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getAttributes(java.lang.String, java.lang.String[])
	 */
	public Attributes getAttributes(String name, String[] attrIds)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		return getAttributes(parser.parse(name), attrIds);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#modifyAttributes(javax.naming.Name, int, javax.naming.directory.Attributes)
	 */
	public void modifyAttributes(Name name, int mod_op, Attributes attrs)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			NamingEnumeration<?extends Attribute> enumeration = attrs.getAll();

			switch (mod_op) {
			case DirContext.ADD_ATTRIBUTE: {
				while (enumeration.hasMore()) {
					attr.put(enumeration.next());
				}

				return;
			}

			case DirContext.REPLACE_ATTRIBUTE: {
				Attribute a;

				while (enumeration.hasMore()) {
					a = enumeration.next();
					attr.put(a.getID(), a);
				}

				return;
			}

			case DirContext.REMOVE_ATTRIBUTE: {
				while (enumeration.hasMore()) {
					attr.remove(((Attribute)enumeration.next()).getID());
				}

				return;
			}

			default:
				return;
			}
		}

		resolveSchema(name).modifyAttributes(name.getSuffix(1), mod_op, attrs);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#modifyAttributes(java.lang.String, int, javax.naming.directory.Attributes)
	 */
	public void modifyAttributes(String name, int mod_op, Attributes attrs)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		modifyAttributes(parser.parse(name), mod_op, attrs);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#modifyAttributes(javax.naming.Name, javax.naming.directory.ModificationItem[])
	 */
	public void modifyAttributes(Name name, ModificationItem[] mods)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			for (int i = 0; i < mods.length; i++) {
				switch (mods[i].getModificationOp()) {
				case DirContext.ADD_ATTRIBUTE: {
					attr.put(mods[i].getAttribute());

					return;
				}

				case DirContext.REPLACE_ATTRIBUTE: {
					attr.put(mods[i].getAttribute().getID(),
					    mods[i].getAttribute());

					return;
				}

				case DirContext.REMOVE_ATTRIBUTE: {
					attr.remove(mods[i].getAttribute().getID());

					return;
				}

				default:
					return;
				}
			}
		}

		resolveSchema(name).modifyAttributes(name.getSuffix(1), mods);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#modifyAttributes(java.lang.String, javax.naming.directory.ModificationItem[])
	 */
	public void modifyAttributes(String name, ModificationItem[] mods)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		modifyAttributes(parser.parse(name), mods);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#bind(javax.naming.Name, java.lang.Object, javax.naming.directory.Attributes)
	 */
	public void bind(Name name, Object obj, Attributes attrs)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			throw new UnsupportedOperationException("Name may not be empty.");
		} else if (name.size() == 1) {
			if (obj instanceof DirContext) {
				URIName un = (URIName)name;

				if (un.isRelative()) {
					throw new InvalidNameException("Name '" + name
					    + " is not absolute.");
				}

				if (!un.get(0).toString().startsWith(RemoteInfo.DAL_TYPE_PREFIX)) {
					throw new InvalidNameException("Name '" + name
					    + " does not start with '" + RemoteInfo.DAL_TYPE_PREFIX
					    + "'.");
				}

				dirs.put(un.get(0), (DirContext)obj);
				modifyAttributes(name, DirContext.ADD_ATTRIBUTE, attrs);
			} else {
				throw new UnsupportedOperationException(
				    "Object must be a DirContext.");
			}
		} else {
			resolveSchema(name).bind(name.getSuffix(1), obj, attrs);
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#bind(java.lang.String, java.lang.Object, javax.naming.directory.Attributes)
	 */
	public void bind(String name, Object obj, Attributes attrs)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		bind(parser.parse(name), obj, attrs);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#rebind(javax.naming.Name, java.lang.Object, javax.naming.directory.Attributes)
	 */
	public void rebind(Name name, Object obj, Attributes attrs)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			throw new UnsupportedOperationException("Name may not be empty.");
		} else if (name.size() == 1) {
			if (obj instanceof DirContext) {
				URIName un = (URIName)name;

				if (un.isRelative()) {
					throw new InvalidNameException("Name '" + name
					    + " is not absolute.");
				}

				if (!un.get(0).toString().startsWith(RemoteInfo.DAL_TYPE_PREFIX)) {
					throw new InvalidNameException("Name '" + name
					    + " does not start with '" + RemoteInfo.DAL_TYPE_PREFIX
					    + "'.");
				}

				dirs.put(un.get(0), (DirContext)obj);
				modifyAttributes(name, DirContext.ADD_ATTRIBUTE, attrs);
			} else {
				throw new UnsupportedOperationException(
				    "Object must be a DirContext.");
			}
		} else {
			resolveSchema(name).rebind(name.getSuffix(1), attrs);
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#rebind(java.lang.String, java.lang.Object, javax.naming.directory.Attributes)
	 */
	public void rebind(String name, Object obj, Attributes attrs)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		rebind(parser.parse(name), obj, attrs);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#createSubcontext(javax.naming.Name, javax.naming.directory.Attributes)
	 */
	public DirContext createSubcontext(Name name, Attributes attrs)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			throw new UnsupportedOperationException("Name may not be empty.");
		} else if (name.size() == 1) {
			DirContext ctx = new DirContextSupport();
			URIName un = (URIName)name;

			if (un.isRelative()) {
				throw new InvalidNameException("Name '" + name
				    + " is not absolute.");
			}

			if (!un.get(0).toString().startsWith(RemoteInfo.DAL_TYPE_PREFIX)) {
				throw new InvalidNameException("Name '" + name
				    + " does not start with '" + RemoteInfo.DAL_TYPE_PREFIX
				    + "'.");
			}

			dirs.put(un.get(0), ctx);
			modifyAttributes(name, DirContext.ADD_ATTRIBUTE, attrs);

			return ctx;
		}

		return resolveSchema(name).createSubcontext(name.getSuffix(1), attrs);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#createSubcontext(java.lang.String, javax.naming.directory.Attributes)
	 */
	public DirContext createSubcontext(String name, Attributes attrs)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		return createSubcontext(parser.parse(name), attrs);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getSchema(javax.naming.Name)
	 */
	public DirContext getSchema(Name name) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			throw new UnsupportedOperationException("Name may not be empty.");
		} else {
			return resolveSchema(name).getSchema(name.getSuffix(1));
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getSchema(java.lang.String)
	 */
	public DirContext getSchema(String name) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		return getSchema(parser.parse(name));
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getSchemaClassDefinition(javax.naming.Name)
	 */
	public DirContext getSchemaClassDefinition(Name name)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			throw new UnsupportedOperationException("Name may not be empty.");
		}

		return resolveSchema(name).getSchemaClassDefinition(name.getSuffix(1));
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#getSchemaClassDefinition(java.lang.String)
	 */
	public DirContext getSchemaClassDefinition(String name)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		return getSchemaClassDefinition(parser.parse(name));
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(javax.naming.Name, javax.naming.directory.Attributes, java.lang.String[])
	 */
	public NamingEnumeration<SearchResult> search(Name name,
	    Attributes matchingAttributes, String[] attributesToReturn)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		return resolveSchema(name)
		.search(name.getSuffix(1), matchingAttributes, attributesToReturn);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(java.lang.String, javax.naming.directory.Attributes, java.lang.String[])
	 */
	public NamingEnumeration<SearchResult> search(String name,
	    Attributes matchingAttributes, String[] attributesToReturn)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		Name n = parser.parse(name);

		return search(n, matchingAttributes, attributesToReturn);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(javax.naming.Name, javax.naming.directory.Attributes)
	 */
	public NamingEnumeration<SearchResult> search(Name name,
	    Attributes matchingAttributes) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		return resolveSchema(name).search(name.getSuffix(1), matchingAttributes);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(java.lang.String, javax.naming.directory.Attributes)
	 */
	public NamingEnumeration<SearchResult> search(String name,
	    Attributes matchingAttributes) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		Name n = parser.parse(name);

		return resolveSchema(n).search(n.getSuffix(1), matchingAttributes);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(javax.naming.Name, java.lang.String, javax.naming.directory.SearchControls)
	 */
	public NamingEnumeration<SearchResult> search(Name name, String filter,
	    SearchControls cons) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		return resolveSchema(name).search(name.getSuffix(1), filter, cons);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(java.lang.String, java.lang.String, javax.naming.directory.SearchControls)
	 */
	public NamingEnumeration<SearchResult> search(String name, String filter,
	    SearchControls cons) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		Name n = parser.parse(name);

		return resolveSchema(n).search(n.getSuffix(1), filter, cons);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(javax.naming.Name, java.lang.String, java.lang.Object[], javax.naming.directory.SearchControls)
	 */
	public NamingEnumeration<SearchResult> search(Name name, String filterExpr,
	    Object[] filterArgs, SearchControls cons) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		return resolveSchema(name)
		.search(name.getSuffix(1), filterExpr, filterArgs, cons);
	}

	/* (non-Javadoc)
	 * @see javax.naming.directory.DirContext#search(java.lang.String, java.lang.String, java.lang.Object[], javax.naming.directory.SearchControls)
	 */
	public NamingEnumeration<SearchResult> search(String name,
	    String filterExpr, Object[] filterArgs, SearchControls cons)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		Name n = parser.parse(name);

		return resolveSchema(n)
		.search(n.getSuffix(1), filterExpr, filterArgs, cons);
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#lookup(javax.naming.Name)
	 */
	public Object lookup(Name name) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			//			Hashtable table = (Hashtable) env.clone();
			//			Attributes a = (Attributes) attr.clone();
			//			InitialDirContextImpl ctx = new InitialDirContextImpl(table);
			//			ctx.modifyAttributes(name, DirContext.ADD_ATTRIBUTE, a);
			//			return ctx;
			//avoid cloning
			return this;
		} else if (name.size() == 1) {
			URIName uri = (URIName)name;

			return dirs.get(uri.get(0));
		}

		return resolveSchema(name).lookup(name.getSuffix(1));
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#lookup(java.lang.String)
	 */
	public Object lookup(String name) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		return lookup(parser.parse(name));
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#bind(javax.naming.Name, java.lang.Object)
	 */
	public void bind(Name name, Object obj) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			throw new UnsupportedOperationException("Name may not be empty.");
		} else if (name.size() == 1) {
			if (obj instanceof DirContext) {
				URIName un = (URIName)name;

				if (un.isRelative()) {
					throw new InvalidNameException("Name '" + name
					    + " is not absolute.");
				}

				if (!un.get(0).toString().startsWith(RemoteInfo.DAL_TYPE_PREFIX)) {
					throw new InvalidNameException("Name '" + name
					    + " does not start with '" + RemoteInfo.DAL_TYPE_PREFIX
					    + "'.");
				}

				dirs.put(un.get(0), (DirContext)obj);
			} else {
				throw new UnsupportedOperationException(
				    "Object must be a DirContext.");
			}
		} else {
			resolveSchema(name).bind(name.getSuffix(1), obj);
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#bind(java.lang.String, java.lang.Object)
	 */
	public void bind(String name, Object obj) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		bind(parser.parse(name), obj);
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#rebind(javax.naming.Name, java.lang.Object)
	 */
	public void rebind(Name name, Object obj) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			throw new UnsupportedOperationException("Name may not be empty.");
		} else if (name.size() == 1) {
			if (obj instanceof DirContext) {
				URIName un = (URIName)name;

				if (un.isRelative()) {
					throw new InvalidNameException("Name '" + name
					    + " is not absolute.");
				}

				dirs.put(un.get(0), (DirContext)obj);
			} else {
				throw new UnsupportedOperationException(
				    "Object must be a DirContext.");
			}
		} else {
			resolveSchema(name).rebind(name.getSuffix(1), obj);
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#rebind(java.lang.String, java.lang.Object)
	 */
	public void rebind(String name, Object obj) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		rebind(parser.parse(name), obj);
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#unbind(javax.naming.Name)
	 */
	public void unbind(Name name) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			throw new UnsupportedOperationException("Name may not be empty.");
		} else if (name.size() == 1) {
			URIName un = (URIName)name;
			dirs.remove(un.get(0));
		} else {
			resolveSchema(name).unbind(name.getSuffix(1));
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#unbind(java.lang.String)
	 */
	public void unbind(String name) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		unbind(parser.parse(name));
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#rename(javax.naming.Name, javax.naming.Name)
	 */
	public void rename(Name oldName, Name newName) throws NamingException
	{
		if (oldName == null) {
			throw new NullPointerException("oldName");
		}

		if (newName == null) {
			throw new NullPointerException("newName");
		}

		if (oldName.isEmpty()) {
			throw new UnsupportedOperationException("oldName may not be empty.");
		}

		if (newName.isEmpty()) {
			throw new UnsupportedOperationException("newName may not be empty.");
		}

		if (oldName.size() == 1) {
			URIName un = (URIName)oldName;
			DirContext ctx = dirs.remove(un.get(0));
			URIName unnew = (URIName)newName;
			dirs.put(unnew.get(0), ctx);
		}

		resolveSchema(oldName).rename(oldName.getSuffix(1), newName);
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#rename(java.lang.String, java.lang.String)
	 */
	public void rename(String oldName, String newName)
		throws NamingException
	{
		if (oldName == null) {
			throw new NullPointerException("oldName");
		}

		if (newName == null) {
			throw new NullPointerException("newName");
		}

		rename(parser.parse(oldName), parser.parse(newName));
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#list(javax.naming.Name)
	 */
	public NamingEnumeration<NameClassPair> list(Name name)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			//			HashMap<Object, NameClassPair> map = new HashMap<Object, NameClassPair>();
			//			String n;
			//			Iterator<String> it = dirs.keySet().iterator();
			//
			//			while (it.hasNext()) {
			//				n = it.next();
			//
			//				NameClassPair ncp = new NameClassPair(n,
			//					    dirs.get(n).getClass().getName());
			//				map.put(n, ncp);
			//			}
			return new NameClassEnumeration(dirs);
		}

		return resolveSchema(name).list(name.getSuffix(1));
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#list(java.lang.String)
	 */
	public NamingEnumeration<NameClassPair> list(String name)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		return list(parser.parse(name));
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#listBindings(javax.naming.Name)
	 */
	public NamingEnumeration<Binding> listBindings(Name name)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			//			HashMap<String, Object> map = new HashMap<String, Object>();
			//			String n;
			//			Iterator<String> it = dirs.keySet().iterator();
			//
			//			while (it.hasNext()) {
			//				n = it.next();
			//				map.put(n, dirs.get(n));
			//			}
			return new BindingEnumeration(dirs);
		}

		return resolveSchema(name).listBindings(name.getSuffix(1));
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#listBindings(java.lang.String)
	 */
	public NamingEnumeration<Binding> listBindings(String name)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		return listBindings(parser.parse(name));
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#destroySubcontext(javax.naming.Name)
	 */
	public void destroySubcontext(Name name) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			throw new UnsupportedOperationException("Name may not be empty.");
		} else if (name.size() == 1) {
			URIName un = (URIName)name;
			DirContext ctx = dirs.remove(un.get(0));

			if (ctx == null) {
				throw new NameNotFoundException("Subcontext for name " + name
				    + "not found.");
			} else {
				ctx.close();
			}
		} else {
			resolveSchema(name).destroySubcontext(name.getSuffix(1));
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#destroySubcontext(java.lang.String)
	 */
	public void destroySubcontext(String name) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		destroySubcontext(parser.parse(name));
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#createSubcontext(javax.naming.Name)
	 */
	public Context createSubcontext(Name name) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			throw new UnsupportedOperationException("Name may not be empty.");
		} else if (name.size() == 1) {
			DirContext ctx = new DirContextSupport();
			URIName un = (URIName)name;

			if (un.isRelative()) {
				throw new InvalidNameException("Name '" + name
				    + " is not absolute.");
			}

			if (!un.get(0).toString().startsWith(RemoteInfo.DAL_TYPE_PREFIX)) {
				throw new InvalidNameException("Name '" + name
				    + " does not start with '" + RemoteInfo.DAL_TYPE_PREFIX
				    + "'.");
			}

			dirs.put(un.get(0), ctx);

			return ctx;
		} else {
			return resolveSchema(name).createSubcontext(name.getSuffix(1));
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#createSubcontext(java.lang.String)
	 */
	public Context createSubcontext(String name) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		return createSubcontext(parser.parse(name));
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#lookupLink(javax.naming.Name)
	 */
	public Object lookupLink(Name name) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			return this;
		} else if (name.size() == 1) {
			URIName un = (URIName)name;

			return dirs.get(un.get(0));
		} else {
			return resolveSchema(name).lookupLink(name.getSuffix(1));
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#lookupLink(java.lang.String)
	 */
	public Object lookupLink(String name) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		return lookupLink(parser.parse(name));
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#getNameParser(javax.naming.Name)
	 */
	public NameParser getNameParser(Name name) throws NamingException
	{
		return parser;
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#getNameParser(java.lang.String)
	 */
	public NameParser getNameParser(String name) throws NamingException
	{
		return parser;
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#composeName(javax.naming.Name, javax.naming.Name)
	 */
	public Name composeName(Name name, Name prefix) throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (name.isEmpty()) {
			//TODO
			return prefix;
		} else {
			return resolveSchema(name).composeName(name.getSuffix(1), prefix);
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#composeName(java.lang.String, java.lang.String)
	 */
	public String composeName(String name, String prefix)
		throws NamingException
	{
		if (name == null) {
			throw new NullPointerException("name");
		}

		Name n = parser.parse(name);

		if (n.isEmpty()) {
			return prefix;
		} else {
			return resolveSchema(n)
			.composeName(n.getSuffix(1).toString(), prefix);
		}
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#addToEnvironment(java.lang.String, java.lang.Object)
	 */
	public Object addToEnvironment(String propName, Object propVal)
		throws NamingException
	{
		if (propName == null) {
			throw new NullPointerException("propName");
		}

		return env.put(propName, propVal);
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#removeFromEnvironment(java.lang.String)
	 */
	public Object removeFromEnvironment(String propName)
		throws NamingException
	{
		if (propName == null) {
			throw new NullPointerException("propName");
		}

		return env.remove(propName);
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#getEnvironment()
	 */
	public Hashtable<?, ?> getEnvironment() throws NamingException
	{
		return new Hashtable(env);
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#close()
	 */
	public void close() throws NamingException
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see javax.naming.Context#getNameInNamespace()
	 */
	public String getNameInNamespace() throws NamingException
	{
		return null;
	}
}

/* __oOo__ */
