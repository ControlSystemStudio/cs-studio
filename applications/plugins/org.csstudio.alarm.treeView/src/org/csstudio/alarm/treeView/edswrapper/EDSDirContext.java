package org.csstudio.alarm.treeView.edswrapper;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

import java.lang.UnsupportedOperationException;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class EDSDirContext extends Object implements DirContext {

	//rules from tutorial
	//1. I must not change the parameters
	//2. Parameters are valid only during invocation
	//3. Return values are owned by the caller - you must return a COPY of returned parameter
	
//	private Hashtable wrapping;
	protected Hashtable bindings = new Hashtable(11);
	protected Hashtable hEnv;
	protected NameParser namepars; 
	protected EDSDirContext parent;
	
	public EDSDirContext(Hashtable henv) {
		super();
		// TODO Auto-generated constructor stub
		this.hEnv = henv;
	}

	public EDSDirContext(Hashtable henv, Hashtable bind) {
		super();
		// TODO Auto-generated constructor stub
		this.hEnv = henv;
		this.bindings = bind;
	}	
	
	public Object lookup(Name name) throws NamingException {
		if (name.isEmpty()) {
			return (createContext(hEnv));
		}
		
		String nm = getMyComponents(name);
		
		Object res = bindings.get(nm);
		if (res == null){
			throw new NameNotFoundException(name + " not found");
		}
		return res;
 	}

	protected Object createContext(Hashtable env) {
		// TODO Tukaj pazi kateri name structure mas
		return new EDSDirContext(env, new Hashtable());
	}

	protected Object cloneContext(Hashtable env) {
		return new EDSDirContext(env,bindings);
	}
	
	private String getMyComponents(Name name) throws NamingException {
		if (name instanceof CompositeName) {
			if (name.size()>1){
				throw new InvalidNameException(name.toString() + " has more components than namespace can handle");
			}
			return name.get(0);
			//TODO: POZOR, ce je hierarchical namespace treba sparsat: http://java.sun.com/products/jndi/tutorial/provider/basics/names.html
		}
		else {
			// A compound name
			return name.toString();
		}
	}

	public Object lookup(String name) throws NamingException {
		// TODO Auto-generated method stub
		return lookup(new CompositeName(name));
	}

	public void bind(Name arg0, Object arg1) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();

	}

	public void bind(String arg0, Object arg1) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();

	}

	public void rebind(Name arg0, Object arg1) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();

	}

	public void rebind(String arg0, Object arg1) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();

	}

	public void unbind(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();

	}

	public void unbind(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();

	}

	public void rename(Name arg0, Name arg1) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();

	}

	public void rename(String arg0, String arg1) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();

	}

	public NamingEnumeration<NameClassPair> list(Name name)
			throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<NameClassPair> list(String name)
			throws NamingException {
		// TODO Auto-generated method stub
		return list(new CompositeName(name));
	}

	public NamingEnumeration<Binding> listBindings(Name arg0)
			throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<Binding> listBindings(String arg0)
			throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void destroySubcontext(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void destroySubcontext(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Context createSubcontext(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Context createSubcontext(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Object lookupLink(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Object lookupLink(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public NameParser getNameParser(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public NameParser getNameParser(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Name composeName(Name arg0, Name arg1) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String composeName(String arg0, String arg1) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Object addToEnvironment(String arg0, Object arg1)
			throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Object removeFromEnvironment(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Hashtable<?, ?> getEnvironment() throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void close() throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getNameInNamespace() throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Attributes getAttributes(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return getAttributes(name,null);
	}

	public Attributes getAttributes(String name) throws NamingException {
		// TODO Auto-generated method stub
		return getAttributes(new CompositeName(name));
	}

	public Attributes getAttributes(Name arg0, String[] attrIds) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Attributes getAttributes(String name, String[] attrIds) throws NamingException {
		// TODO Auto-generated method stub
		return getAttributes(new CompositeName(name),attrIds);
	}

	public void modifyAttributes(Name arg0, int arg1, Attributes arg2) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();		
	}

	public void modifyAttributes(String arg0, int arg1, Attributes arg2) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		
	}

	public void modifyAttributes(Name arg0, ModificationItem[] arg1) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		
	}

	public void modifyAttributes(String arg0, ModificationItem[] arg1) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		
	}

	public void bind(Name arg0, Object arg1, Attributes arg2) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		
	}

	public void bind(String arg0, Object arg1, Attributes arg2) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		
	}

	public void rebind(Name arg0, Object arg1, Attributes arg2) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		
	}

	public void rebind(String arg0, Object arg1, Attributes arg2) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		
	}

	public DirContext createSubcontext(Name arg0, Attributes arg1) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public DirContext createSubcontext(String arg0, Attributes arg1) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public DirContext getSchema(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public DirContext getSchema(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public DirContext getSchemaClassDefinition(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public DirContext getSchemaClassDefinition(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<SearchResult> search(Name arg0, Attributes arg1, String[] arg2) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<SearchResult> search(String arg0, Attributes arg1, String[] arg2) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<SearchResult> search(Name arg0, Attributes arg1) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<SearchResult> search(String arg0, Attributes arg1) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<SearchResult> search(Name arg0, String arg1, SearchControls arg2) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<SearchResult> search(String arg0, String arg1, SearchControls arg2) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<SearchResult> search(Name arg0, String arg1, Object[] arg2, SearchControls arg3) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<SearchResult> search(String arg0, String arg1, Object[] arg2, SearchControls arg3) throws NamingException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

//	 Class for enumerating name/class pairs
	class ListOfNames implements NamingEnumeration {
	    protected Enumeration names;

	    ListOfNames (Enumeration names) {
	        this.names = names;
	    }

	    public boolean hasMoreElements() {
		try {
		    return hasMore();
		} catch (NamingException e) {
		    return false;
		}
	    }

	    public boolean hasMore() throws NamingException {
	        return names.hasMoreElements();
	    }

	    public Object next() throws NamingException {
	        String name = (String)names.nextElement();
	        String className = bindings.get(name).getClass().getName();
	        return new NameClassPair(name, className);
	    }

	    public Object nextElement() {
	        try {
	            return next();
	        } catch (NamingException e) {
	  	    throw new NoSuchElementException(e.toString());
		}
	    }

	    public void close() {
	    }
	}	
	
}
