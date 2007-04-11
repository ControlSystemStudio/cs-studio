/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
	private String name;
	private String filter;
	private int defaultScope=SearchControls.SUBTREE_SCOPE;
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

	public static DirContext initial() {
		// MCL changed to static
		// Set up the environment for creating the initial context
		Hashtable<Object,String> env = new Hashtable<Object,String>(11);
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory"); //$NON-NLS-1$
		env.put(Context.PROVIDER_URL,
				Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.P_STRING_URL));
		if(Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.SECURITY_PROTOCOL).trim().length()>0)
			env.put(Context.SECURITY_PROTOCOL,
					Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.SECURITY_PROTOCOL));
		if(Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.SECURITY_AUTHENTICATION).trim().length()>0)
			env.put(Context.SECURITY_AUTHENTICATION,
				Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.SECURITY_AUTHENTICATION));
		// user
		if(Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.P_STRING_USER_DN).trim().length()>0)
			env.put(Context.SECURITY_PRINCIPAL, Activator
				.getDefault().getPluginPreferences().getString(	PreferenceConstants.P_STRING_USER_DN));
		// password
		if(Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.P_STRING_USER_PASSWORD).trim().length()>0)
			env.put(Context.SECURITY_CREDENTIALS, Activator
				.getDefault().getPluginPreferences().getString(	PreferenceConstants.P_STRING_USER_PASSWORD));
		// Create initial context
		try {
			InitialDirContext ctx = new InitialDirContext(env);
			return ctx;
		}catch (NamingException e) {
			Activator.logException("Ungültiger LDAP Pfad", e);
		}
		return null;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor ) {
		DirContext ctx;
		if((ctx = initial())!=null){
	        SearchControls ctrl = new SearchControls();
	        ctrl.setSearchScope(defaultScope);
	        try{
	        	list = new ArrayList<String>();
	            NamingEnumeration answer = ctx.search(name, filter, ctrl);
	            ctx.search(name, filter, ctrl);
				try {
					while(answer.hasMore()){
						String name = ((SearchResult)answer.next()).getName();
						list.add(name);
					}
				} catch (NamingException e) {
					Activator.logException("LDAP Fehler", e);
				}
				answer.close();
				ctx.close();
				ergebnisListe.setAnswer(list);
				return ASYNC_FINISH;
			} catch (NamingException e) {
				Activator.logException("Falscher LDAP Suchpfad.", e);
				e.printStackTrace();
			}
		}
		return Status.CANCEL_STATUS;
	}
}
