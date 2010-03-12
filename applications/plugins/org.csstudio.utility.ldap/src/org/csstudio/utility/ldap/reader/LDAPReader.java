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
import java.util.List;

import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class LDAPReader extends Job {
	private String _searchRoot;
	private String _filter;
	private int _defaultScope = SearchControls.SUBTREE_SCOPE;
	private List<String> _list;
	private LdapResultList _resultList;

	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param nameUFilter<br> 0: name<br>1: = filter<br>
	 * @param resultList
	 */
	public LDAPReader(String[] nameUFilter, LdapResultList resultList){
		super("LDAPReader");
		setBasics(nameUFilter[0], nameUFilter[1], resultList);
	}


	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param nameUFilter<br> 0: name<br>1: = filter<br>
	 * @param searchScope set the Scope {@link SearchControls}
	 * @param ergebnisListe the list for the result {@link LdapResultList}
	 */

	public LDAPReader(String[] nameUFilter, int searchScope, LdapResultList resultList){

		super("LDAPReader");

		setBasics(nameUFilter[0], nameUFilter[1], resultList);
		setDefaultScope(searchScope);
	}

	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param name
	 * @param filter
	 * @param ldapResultList the list for the result {@link LdapResultList}
	 */
	public LDAPReader(String name, String filter, LdapResultList ldapResultList){
		super("LDAPReader");
		setBasics(name, filter, ldapResultList);
	}

	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param name
	 * @param filter
	 * @param searchScope
	 * @param resultList the list for the result {@link LdapResultList}
	 */
	public LDAPReader(String name, String filter, int searchScope, LdapResultList resultList){
		super("LDAPReader");
		setBasics(name, filter, resultList);
		setDefaultScope(searchScope);
	}

	/**
	 * Need connection settings. (For Headless use)
	 *
	 * @param name
	 * @param filter
	 * @param searchScope
	 * @param resultList the list for the result {@link LdapResultList}
	 * @param env connection settings.
	 */

	public LDAPReader(String name, String filter, int searchScope, LdapResultList resultList, Hashtable<Object,String> env){
		super("LDAPReader");
		setBasics(name, filter, resultList);
		setDefaultScope(searchScope);
	}
	/**
	 * Need connection settings. (For Headless use)
	 *
	 * @param name
	 * @param filter
	 * @param resultList
	 * @param env value for<br>
	 * 	0: Context.PROVIDER_URL<br>
	 *  1: Context.SECURITY_PROTOCOL<br>
	 *  2: Context.SECURITY_AUTHENTICATION<br>
	 *  3: Context.SECURITY_PRINCIPAL<br>
	 *  4: Context.SECURITY_CREDENTIALS<br>
	 *
	 */
	public LDAPReader(String name, String filter,  LdapResultList resultList, String[] env){
		super("LDAPReader");
		setBasics(name, filter, resultList);
	}
	/**
	 *
	 * @param name
	 * @param filter
	 * @param searchScope
	 * @param resultList
	 * @param env value for<br>
	 * 	0: Context.PROVIDER_URL<br>
	 *  1: Context.SECURITY_PROTOCOL<br>
	 *  2: Context.SECURITY_AUTHENTICATION<br>
	 *  3: Context.SECURITY_PRINCIPAL<br>
	 *  4: Context.SECURITY_CREDENTIALS<br>
	 *
	 */
	public LDAPReader(String searchRoot, String filter, int searchScope, LdapResultList resultList, String[] env){
		super("LDAPReader");
		setBasics(searchRoot, filter, resultList);
		setDefaultScope(searchScope);
	}

	/**
	 * @param searchRoot
	 * @param filter
	 * @param resultList
	 * @param ctx 
	 */
	private void setBasics(String searchRoot, String filter, LdapResultList resultList) {
		_resultList = resultList;
		_searchRoot = searchRoot;
		_filter = filter;
    }

	/**
     * Set the Scope. @link SearchControls.
     * @param defaultScope set the given Scope.
     */
    private void setDefaultScope(int defaultScope) {
        this._defaultScope = defaultScope;
    }

	@Override
	protected IStatus run(IProgressMonitor monitor ) {
	    monitor.beginTask("LDAP Reader", IProgressMonitor.UNKNOWN);
	    DirContext ctx = Engine.getInstance().getLdapDirContext();
		if(ctx !=null){
	        SearchControls ctrl = new SearchControls();
	        ctrl.setSearchScope(_defaultScope);
	        try{
	        	_list = new ArrayList<String>();
	            NamingEnumeration<SearchResult> answer = ctx.search(_searchRoot, _filter, ctrl);
				try {
					while(answer.hasMore()){
						String name = answer.next().getName() + "," + _searchRoot;
						_list.add(name);
						if(monitor.isCanceled()) {
							return Status.CANCEL_STATUS;
						}
					}
					if(_list.size()<1){
						_list.add("no entry found");
					}
				} catch (NamingException e) {
				    ctx = Engine.getInstance().reconnectDirContext();
                    CentralLogger.getInstance().info(this,"LDAP Fehler");
                    CentralLogger.getInstance().info(this,e);
				}
				answer.close();
				_resultList.setResultList(_list);
				monitor.done();
				return Status.OK_STATUS;
			} catch (NameNotFoundException nnfe){
				Engine.getInstance().reconnectDirContext();
				CentralLogger.getInstance().info(this,"Falscher LDAP Name oder so.");
				
                CentralLogger.getInstance().info(this,nnfe);
			} catch (NamingException e) {
			    Engine.getInstance().reconnectDirContext();
				CentralLogger.getInstance().info(this,"Falscher LDAP Suchpfad.");
                CentralLogger.getInstance().info(this,e);
			}
			
		}
		monitor.setCanceled(true);
		_resultList.setResultList(_list);
		return Status.CANCEL_STATUS;
	}
}
