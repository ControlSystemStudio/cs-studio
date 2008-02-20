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

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class LDAPReader extends Job {
	private String _name;
	private String _filter;
	private int defaultScope=SearchControls.SUBTREE_SCOPE;
	private ArrayList<String> list;
	private ErgebnisListe _ergebnisListe;
    private DirContext _ctx;

	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param nameUFilter<br> 0: name<br>1: = filter<br>
	 * @param ergebnisListe
	 */
	public LDAPReader(String[] nameUFilter, ErgebnisListe ergebnisListe, DirContext ctx){
		super("LDAPReader");
		setBasics(nameUFilter[0], nameUFilter[1], ergebnisListe, ctx);
	}


	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param nameUFilter<br> 0: name<br>1: = filter<br>
	 * @param searchScope set the Scope {@link SearchControls}
	 * @param ergebnisListe the list for the result {@link ErgebnisListe}
	 */

	public LDAPReader(String[] nameUFilter, int searchScope, ErgebnisListe ergebnisListe, DirContext ctx){

		super("LDAPReader");

		setBasics(nameUFilter[0], nameUFilter[1], ergebnisListe, ctx);
		setDefaultScope(searchScope);
	}

	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param name
	 * @param filter
	 * @param ergebnisListe the list for the result {@link ErgebnisListe}
	 */
	public LDAPReader(String name, String filter, ErgebnisListe ergebnisListe, DirContext ctx){
		super("LDAPReader");
		setBasics(name, filter, ergebnisListe, ctx);
	}

	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param name
	 * @param filter
	 * @param searchScope
	 * @param ergebnisListe the list for the result {@link ErgebnisListe}
	 */
	public LDAPReader(String name, String filter, int searchScope, ErgebnisListe ergebnisListe, DirContext ctx){
		super("LDAPReader");
		setBasics(name, filter, ergebnisListe, ctx);
		setDefaultScope(searchScope);
	}

	/**
	 * Need connection settings. (For Headless use)
	 *
	 * @param name
	 * @param filter
	 * @param searchScope
	 * @param ergebnisListe the list for the result {@link ErgebnisListe}
	 * @param env connection settings.
	 * 	@see javax.naming.directory.DirContext;
	 * 	@see	javax.naming.Context;
	 */

	public LDAPReader(String name, String filter, int searchScope, ErgebnisListe ergebnisListe, Hashtable<Object,String> env, DirContext ctx){
		super("LDAPReader");
		setBasics(name, filter, ergebnisListe, ctx);
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
	public LDAPReader(String name, String filter,  ErgebnisListe ergebnisListe, String[] env, DirContext ctx){
		super("LDAPReader");
		setBasics(name, filter, ergebnisListe,ctx);
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
	public LDAPReader(String name, String filter, int searchScope, ErgebnisListe ergebnisListe, String[] env, DirContext ctx){
		super("LDAPReader");
		setBasics(name, filter, ergebnisListe, ctx);
		setDefaultScope(searchScope);
	}

	/**
	 * @param name
	 * @param filter
	 * @param ergebnisListe
	 * @param ctx 
	 */
	private void setBasics(String name, String filter, ErgebnisListe ergebnisListe, DirContext ctx) {
		_ergebnisListe = ergebnisListe;
		_name = name;
		_filter = filter;
		_ctx = ctx;
    }

	/**
     * Set the Scope. @link SearchControls.
     * @param defaultScope set the given Scope.
     */
    private void setDefaultScope(int defaultScope) {
        this.defaultScope = defaultScope;
    }

	@Override
	protected IStatus run(IProgressMonitor monitor ) {
	    monitor.beginTask("LDAP Reader", IProgressMonitor.UNKNOWN);
		if(_ctx !=null){
	        SearchControls ctrl = new SearchControls();
	        ctrl.setSearchScope(defaultScope);
//	        ctrl.setReturningAttributes(null);
	        try{
	        	list = new ArrayList<String>();
	            NamingEnumeration<SearchResult> answer = _ctx.search(_name, _filter, ctrl);
				try {
					while(answer.hasMore()){
						String name = answer.next().getName()+","+_name;
						list.add(name);
						if(monitor.isCanceled()) {
							return Status.CANCEL_STATUS;
						}
					}
					if(list.size()<1){
						list.add("no entry found");
					}
				} catch (NamingException e) {
				    _ctx=null;
                    CentralLogger.getInstance().info(this,"LDAP Fehler");
                    CentralLogger.getInstance().info(this,e);
				}
				answer.close();
//				ctx.close();
				_ergebnisListe.setResultList(list);
				monitor.done();
				return Status.OK_STATUS;
			} catch (NamingException e) {
			    _ctx=null;
				CentralLogger.getInstance().info(this,"Falscher LDAP Suchpfad.");
                CentralLogger.getInstance().info(this,e);
			}
		}
		monitor.setCanceled(true);
		return Status.CANCEL_STATUS;
	}
}
