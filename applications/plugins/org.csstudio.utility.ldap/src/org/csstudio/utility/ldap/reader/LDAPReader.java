package org.csstudio.utility.ldap.reader;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.utility.ldap.Activator;
import org.csstudio.utility.ldap.preference.PreferenceConstants;

public class LDAPReader {
	public enum  Ebene {KRYO, FACILITY, CONTROLLER, DEVICE, RECORD}

	private String name;
	private String filter;
	private int defaultScope=SearchControls.SUBTREE_SCOPE;


	public LDAPReader(String[] nameUFilter){
		name=nameUFilter[0];
		filter=nameUFilter[1];
	}
	public LDAPReader(String[] nameUFilter, int searchScope){
		name=nameUFilter[0];
		filter=nameUFilter[1];
		defaultScope = searchScope;
	}

	public LDAPReader(String name, String filter){
		this.name = name;
		this.filter = filter;
	}

	public LDAPReader(String name, String filter, int searchScope){
		this.name = name;
		this.filter = filter;
		defaultScope = searchScope;
	}

	private DirContext initial() {
		// Set up the environment for creating the initial context
		Hashtable<Object,String> env = new Hashtable<Object,String>(11);
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory"); //$NON-NLS-1$
		env.put(Context.PROVIDER_URL,
				Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.P_STRING_URL));
		// user
		env.put(Context.SECURITY_PRINCIPAL, Activator
				.getDefault().getPluginPreferences().getString(	PreferenceConstants.P_STRING_USER_DN));
		// password
		env.put(Context.SECURITY_CREDENTIALS, Activator
				.getDefault().getPluginPreferences().getString(	PreferenceConstants.P_STRING_USER_PASSWORD));
		// Create initial context
		try {
			return new InitialDirContext(env);
		}catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String[] getStringArray(){
		return getStringArray(defaultScope);
	}

	public String[] getStringArray(int searchScope){
		DirContext ctx;
		if((ctx = initial())!=null){
	        SearchControls ctrl = new SearchControls();
	        ctrl.setSearchScope(searchScope);
	        try{
	        	ArrayList<String> list = new ArrayList<String>();
	            NamingEnumeration answer = ctx.search(name, filter, ctrl);
				while(answer.hasMore()){
					String name = ((SearchResult)answer.next()).getName();
					list.add(name);

				}
				return list.toArray(new String[0]);
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void set(String name, String filter) {
		this.name=name;
		this.filter=filter;
	}

	public void setSearchScope(int searchScop) {
		defaultScope = searchScop;
	}
}
