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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class LDAPReader extends Job {
//	public enum  Ebene {KRYO, FACILITY, CONTROLLER, DEVICE, RECORD};

	private String name;
	private String filter;
	private int defaultScope=SearchControls.SUBTREE_SCOPE;
	private int searchScope;
	private ArrayList<String> list;
	private ErgebnisListe ergebnisListe;

	public LDAPReader(String[] nameUFilter, ErgebnisListe ergebnisListe){
		super("LDAPReader");
		this.ergebnisListe = ergebnisListe;
		name=nameUFilter[0];
		filter=nameUFilter[1];
	}
	public LDAPReader(String[] nameUFilter, int searchScope, ErgebnisListe ergebnisListe){
		super("LDAPReader");
		this.ergebnisListe = ergebnisListe;
		name=nameUFilter[0];
		filter=nameUFilter[1];
		defaultScope = searchScope;
	}

	public LDAPReader(String name, String filter, ErgebnisListe ergebnisListe){
		super("LDAPReader");
		this.ergebnisListe = ergebnisListe;
		this.name = name;
		this.filter = filter;
	}

	public LDAPReader(String name, String filter, int searchScope, ErgebnisListe ergebnisListe){
		super("LDAPReader");
		this.ergebnisListe = ergebnisListe;
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

//	public ArrayList<String> getStringArray(){
//		return list;
//	}
//
//	public void set(String name, String filter) {
//		this.name=name;
//		this.filter=filter;
//	}
//	public void setScope(int scope){
//		searchScope= scope;
//	}


	@Override
	protected IStatus run(IProgressMonitor monitor ) {
		DirContext ctx;
		if((ctx = initial())!=null){
	        SearchControls ctrl = new SearchControls();
	        // TODO: Muss noch richtig gemacht werden.
	        searchScope=defaultScope;
	        ctrl.setSearchScope(searchScope);
	        try{
	        	list = new ArrayList<String>();
	            NamingEnumeration answer = ctx.search(name, filter, ctrl);
				while(answer.hasMore()){
					String name = ((SearchResult)answer.next()).getName();
					list.add(name);
				}
				answer.close();
				ctx.close();
				ergebnisListe.setAnswer(list);
				return this.ASYNC_FINISH;
//				return Status.OK_STATUS;
//				return list.toArray(new String[0]);
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return Status.CANCEL_STATUS;
	}
}
