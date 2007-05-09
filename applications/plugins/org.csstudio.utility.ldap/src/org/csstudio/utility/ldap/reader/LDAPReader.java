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

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.utility.ldap.connection.LDAPConnector;
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

	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param nameUFilter<br> 0: name<br>1: = filter<br>
	 * @param ergebnisListe
	 */
	public LDAPReader(String[] nameUFilter, ErgebnisListe ergebnisListe){
		super("LDAPReader");
		setBasics(nameUFilter[0], nameUFilter[1], ergebnisListe);
	}


	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param nameUFilter<br> 0: name<br>1: = filter<br>
	 * @param searchScope
	 * @param ergebnisListe
	 */

	public LDAPReader(String[] nameUFilter, int searchScope, ErgebnisListe ergebnisListe){

		super("LDAPReader");

		setBasics(nameUFilter[0], nameUFilter[1], ergebnisListe);
		setDefaultScope(searchScope);
	}

	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param name
	 * @param filter
	 * @param ergebnisListe
	 */
	public LDAPReader(String name, String filter, ErgebnisListe ergebnisListe){
		super("LDAPReader");
		setBasics(name, filter, ergebnisListe);
	}

	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param name
	 * @param filter
	 * @param searchScope
	 * @param ergebnisListe
	 */
	public LDAPReader(String name, String filter, int searchScope, ErgebnisListe ergebnisListe){
		super("LDAPReader");
		setBasics(name, filter, ergebnisListe);
		setDefaultScope(searchScope);
	}

	/**
	 * Need connection settings. (For Headless use)
	 *
	 * @param name
	 * @param filter
	 * @param searchScope
	 * @param ergebnisListe
	 * @param env connection settings.
	 * 	@see javax.naming.directory.DirContext;
	 * 	@see	javax.naming.Context;
	 */

	public LDAPReader(String name, String filter, int searchScope, ErgebnisListe ergebnisListe, Hashtable<Object,String> env){
		super("LDAPReader");
		setBasics(name, filter, ergebnisListe);
		setDefaultScope(searchScope);
	}
	/**
	 * Need connection settings. (For Headless use)
	 *
	 * @param name
	 * @param filter
	 * @param ergebnisListe
	 * @param env value for<br>
	 * 	0: Context.PROVIDER_URL<br>
	 *  1: Context.SECURITY_PROTOCOL<br>
	 *  2: Context.SECURITY_AUTHENTICATION<br>
	 *  3: Context.SECURITY_PRINCIPAL<br>
	 *  4: Context.SECURITY_CREDENTIALS<br>
	 *
	 * 	@see javax.naming.directory.DirContext;
	 * 	@see	javax.naming.Context;
	 */
	public LDAPReader(String name, String filter,  ErgebnisListe ergebnisListe, String[] env){
		super("LDAPReader");
		setBasics(name, filter, ergebnisListe);
	}
	/**
	 *
	 * @param name
	 * @param filter
	 * @param searchScope
	 * @param ergebnisListe
	 * @param env value for<br>
	 * 	0: Context.PROVIDER_URL<br>
	 *  1: Context.SECURITY_PROTOCOL<br>
	 *  2: Context.SECURITY_AUTHENTICATION<br>
	 *  3: Context.SECURITY_PRINCIPAL<br>
	 *  4: Context.SECURITY_CREDENTIALS<br>
	 *
	 * 	@see javax.naming.directory.DirContext;
	 * 	@see	javax.naming.Context;
	 */
	public LDAPReader(String name, String filter, int searchScope, ErgebnisListe ergebnisListe, String[] env){
		super("LDAPReader");
		setBasics(name, filter, ergebnisListe);
		setDefaultScope(searchScope);
	}


	/**
	 * @param name
	 * @param filter
	 * @param ergebnisListe
	 */
	private void setBasics(String name, String filter, ErgebnisListe ergebnisListe) {
		this.ergebnisListe = ergebnisListe;
		this.name = name;
		this.filter = filter;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor ) {
		DirContext ctx;
		LDAPConnector ldpc = new LDAPConnector();
		if((ctx = ldpc.getDirContext())!=null){
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
//					Activator.logException("LDAP Fehler", e);
					System.out.println("LDAP Fehler");
					e.printStackTrace();
				}
				answer.close();
				ctx.close();
				ergebnisListe.setAnswer(list);
				return ASYNC_FINISH;
			} catch (NamingException e) {
//				Activator.logException("Falscher LDAP Suchpfad.", e);
				System.out.println("Falscher LDAP Suchpfad.");
				e.printStackTrace();
			}
		}
		return Status.CANCEL_STATUS;
	}




	private void setDefaultScope(int defaultScope) {
		this.defaultScope = defaultScope;
	}
}
