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
/*
 * $Id$
 */
package org.csstudio.utility.ldap.connection;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.Activator;
import org.csstudio.utility.ldap.preference.PreferenceConstants;
import org.eclipse.core.runtime.Preferences;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 12.04.2007
 */
public class LDAPConnector {
    InitialDirContext ctx = null;
    private Hashtable<Object, String> _env;

    /**
     * The connection settings come from
     * {@link org.csstudio.utility.ldap.ui.preference}
     * @throws NamingException
     *
     */
    public LDAPConnector () throws NamingException{// throws NamingException{
        try {
            getUIenv();
            setEnv();
        } catch (NamingException e) {
            CentralLogger.getInstance().error(this,e);
            CentralLogger.getInstance().error(this,"The follow setting(s) a invalid: \r\n"
                    +"RemainingName: "+e.getRemainingName()+"\r\n"
                    +"ResolvedObj: "+e.getResolvedObj()+"\r\n"
                    +"Explanation: "+e.getExplanation()
                    );
            throw e;
        }
    }

    /**
     *
     * @param env connection settings.
     * @throws NamingException
     *  @see javax.naming.directory.DirContext;
     *  @see    javax.naming.Context;
     */
    public LDAPConnector (Hashtable<Object,String> env) throws NamingException{
        setEnv(env);
    }

    /**
     *
     * @return the LDAP Connection
     */
    public DirContext getDirContext() {
        return ctx;
    }

    public DirContext reconect() throws NamingException{
        try {
            setEnv();
        } catch (NamingException e) {
            CentralLogger.getInstance().error(this,e);
            CentralLogger.getInstance().error(this,"The follow setting(s) a invalid: \r\n"
                    +e.getRemainingName()+"\r\n"
                    +e.getResolvedObj()+"\r\n"
                    +e.getExplanation()
                    );
            throw e;
        }
        return getDirContext();

    }
    /**
     * @param env
     * @return
     */
    private void setDefaultENV(String[] env) {
        // Set up the environment for creating the initial context
        _env = new Hashtable<Object, String>(11);
        _env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory"); //$NON-NLS-1$
        int i=0;
        switch(env.length){
            default:
            case 5:
                i=4;
                if(env[i]!=null){
                    _env.put(Context.SECURITY_CREDENTIALS, env[i]);
                }
            case 4:
                i=3;
                if(env[i]!=null){
                    _env.put(Context.SECURITY_PRINCIPAL, env[i]);
                }
            case 3:
                i=2;
                if(env[i]!=null){
                    _env.put(Context.SECURITY_AUTHENTICATION, env[i]);
                }
            case 2:
                i=1;
                if(env[i]!=null){
                    _env.put(Context.SECURITY_PROTOCOL, env[i]);
                }
            case 1:
                i=0;
                if(env[i]!=null){
                    _env.put(Context.PROVIDER_URL, env[i]);
                }
            case 0:
                break;
        }
    }


    /**
     * Read first the preferences in instance scope and if there is no 
     * user defined setting, get them from default scope.
     * 
     * @return env with the setings from PreferencPage
     */
    private void getUIenv() {

        Preferences prefs = Activator.getDefault().getPluginPreferences();          
        // Set up the environment for creating the initial context
        
        CentralLogger.getInstance().debug(this,"++++++++++++++++++++++++++++++++++++++++++++++");
        CentralLogger.getInstance().debug(this,"+ PLUGIN_ID: "+Activator.PLUGIN_ID);
        CentralLogger.getInstance().debug(this,"+ P_STRING_URL: "+prefs.getString(PreferenceConstants.P_STRING_URL));
        CentralLogger.getInstance().debug(this,"+ SECURITY_PROTOCOL: "+prefs.getString(PreferenceConstants.SECURITY_PROTOCOL));
        CentralLogger.getInstance().debug(this,"+ SECURITY_AUTHENTICATION: "+prefs.getString(PreferenceConstants.SECURITY_AUTHENTICATION));
        CentralLogger.getInstance().debug(this,"+ P_STRING_USER_DN: "+prefs.getString(PreferenceConstants.P_STRING_USER_DN));
        CentralLogger.getInstance().debug(this,"+ P_STRING_USER_PASSWORD: "+prefs.getString(PreferenceConstants.P_STRING_USER_PASSWORD));
        CentralLogger.getInstance().debug(this,"----------------------------------------------");
        String[] env = null;
        // password
        if(prefs.getString(PreferenceConstants.P_STRING_USER_PASSWORD).trim().length()>0){
              env = new String[5];
              env[4] = prefs.getString(PreferenceConstants.P_STRING_USER_PASSWORD);
        }
        // user
        if(prefs.getString(PreferenceConstants.P_STRING_USER_DN).trim().length()>0){
            if(env==null){
              env = new String[4];
            }
            env[3] = prefs.getString(PreferenceConstants.P_STRING_USER_DN);
        }
        
        if(prefs.getString(PreferenceConstants.SECURITY_AUTHENTICATION).trim().length()>0){
            if(env==null){
                env = new String[3];
            }
            env[2]=prefs.getString(PreferenceConstants.SECURITY_AUTHENTICATION);
        }
        
        if(prefs.getString(PreferenceConstants.SECURITY_PROTOCOL).trim().length()>0){
            if(env==null){
                env = new String[2];
            }
            env[1]=prefs.getString(PreferenceConstants.SECURITY_PROTOCOL);
        }
        
        if(env==null){
            env = new String[1];
        }
        env[0]=prefs.getString(PreferenceConstants.P_STRING_URL);
        setDefaultENV(env);
    }


    private void setEnv(Hashtable<Object, String> env) throws NamingException {
        _env = new Hashtable<Object, String>();
        _env.putAll(env);
        setEnv();
    }

    private void setEnv() throws NamingException {
        ctx = new InitialDirContext(_env);
    }


}
