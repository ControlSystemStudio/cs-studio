/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
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
import org.csstudio.utility.ldap.engine.Engine;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 09.09.2008
 */
public class LDAPSyncReader {

    private String _name;
    private String _filter;
    private int _defaultScope =SearchControls.SUBTREE_SCOPE;

    /**
     * Used the connection settings from org.csstudio.utility.ldap.ui
     * (used with UI)
     *
     * @param nameUFilter<br> 0: name<br>1: = filter<br>
     * @param ergebnisListe
     */
    public LDAPSyncReader(String[] nameUFilter){
        setBasics(nameUFilter[0], nameUFilter[1]);
    }


    /**
     * Used the connection settings from org.csstudio.utility.ldap.ui
     * (used with UI)
     *
     * @param nameUFilter<br> 0: name<br>1: = filter<br>
     * @param searchScope set the Scope {@link SearchControls}
     * @param ergebnisListe the list for the result {@link ErgebnisListe}
     */

    public LDAPSyncReader(String[] nameUFilter, int searchScope){
        setBasics(nameUFilter[0], nameUFilter[1]);
        setDefaultScope(searchScope);
    }

    /**
     * Used the connection settings from org.csstudio.utility.ldap.ui
     * (used with UI)
     *
     * @param name
     * @param filter
     */
    public LDAPSyncReader(String name, String filter){
        setBasics(name, filter);
    }

    /**
     * Used the connection settings from org.csstudio.utility.ldap.ui
     * (used with UI)
     *
     * @param name
     * @param filter
     * @param searchScope
     */
    public LDAPSyncReader(String name, String filter, int searchScope){
        setBasics(name, filter);
        setDefaultScope(searchScope);
    }

    /**
     * Need connection settings. (For Headless use)
     *
     * @param name
     * @param filter
     * @param searchScope
     * @param env connection settings.
     */

    public LDAPSyncReader(String name, String filter, int searchScope, Hashtable<Object,String> env){
        setBasics(name, filter);
        setDefaultScope(searchScope);
    }
    /**
     * Need connection settings. (For Headless use)
     *
     * @param name
     * @param filter
     * @param env value for<br>
     *  0: Context.PROVIDER_URL<br>
     *  1: Context.SECURITY_PROTOCOL<br>
     *  2: Context.SECURITY_AUTHENTICATION<br>
     *  3: Context.SECURITY_PRINCIPAL<br>
     *  4: Context.SECURITY_CREDENTIALS<br>
     *
     */
    public LDAPSyncReader(String name, String filter, String[] env){
        setBasics(name, filter);
    }
    /**
     *
     * @param name
     * @param filter
     * @param searchScope
     * @param ergebnisListe
     * @param env value for<br>
     *  0: Context.PROVIDER_URL<br>
     *  1: Context.SECURITY_PROTOCOL<br>
     *  2: Context.SECURITY_AUTHENTICATION<br>
     *  3: Context.SECURITY_PRINCIPAL<br>
     *  4: Context.SECURITY_CREDENTIALS<br>
     *
     */
    public LDAPSyncReader(String name, String filter, int searchScope, String[] env){
        setBasics(name, filter);
        setDefaultScope(searchScope);
    }
    
    /**
     * @param name
     * @param filter
     * @param ctx 
     */
    private void setBasics(String name, String filter) {
        _name = name;
        _filter = filter;
    }

    /**
     * Set the Scope. @link SearchControls.
     * @param defaultScope set the given Scope.
     */
    private void setDefaultScope(int defaultScope) {
        _defaultScope = defaultScope;
    }

    public ErgebnisListe getAnswer(){
        DirContext ctx = Engine.getInstance().getLdapDirContext();
        if(ctx !=null){
            SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(_defaultScope);
            try{
                ArrayList<String> list = new ArrayList<String>();
                NamingEnumeration<SearchResult> answer = ctx.search(_name, _filter, ctrl);
                try {
                    while(answer.hasMore()){
                        String name = answer.next().getName()+","+_name;
                        list.add(name);
                    }
                    if(list.size()<1){
                        list.add("no entry found");
                    }
                } catch (NamingException e) {
                    ctx = Engine.getInstance().reconnectDirContext();
                    CentralLogger.getInstance().info(this,"LDAP Fehler");
                    CentralLogger.getInstance().info(this,e);
                }
                answer.close();
                ErgebnisListe ergebnisListe = new ErgebnisListe();
                ergebnisListe.setResultList(list);
                return ergebnisListe;
            } catch (NamingException e) {
                Engine.getInstance().reconnectDirContext();
                CentralLogger.getInstance().info(this,"Falscher LDAP Suchpfad.");
                CentralLogger.getInstance().info(this,e);
            }
        }
        return null;
    }
    public ArrayList<String> getAnswerString(){
        DirContext ctx = Engine.getInstance().getLdapDirContext();
        if(ctx !=null){
            SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(_defaultScope);
            try{
                ArrayList<String> list = new ArrayList<String>();
                NamingEnumeration<SearchResult> answer = ctx.search(_name, _filter, ctrl);
                try {
                    while(answer.hasMore()){
                        
                        String name = answer.next().getName();
                        if(name.trim().length()>0){
                            list.add(name+","+_name);
                        }
                    }
                    if(list.size()<1){
                        list.add("no entry found");
                    }
                } catch (NamingException e) {
                    ctx = Engine.getInstance().reconnectDirContext();
                    CentralLogger.getInstance().info(this,"LDAP Fehler");
                    CentralLogger.getInstance().info(this,e);
                }
                answer.close();
                return list;
            } catch (NamingException e) {
                Engine.getInstance().reconnectDirContext();
                CentralLogger.getInstance().info(this,"Falscher LDAP Suchpfad.");
                CentralLogger.getInstance().info(this,e);
            }
        }
        return null;
    }
}
