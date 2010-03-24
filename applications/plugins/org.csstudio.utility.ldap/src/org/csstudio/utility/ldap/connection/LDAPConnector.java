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
import javax.naming.ldap.InitialLdapContext;

import org.apache.log4j.Logger;
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
    
    private final Logger LOG = CentralLogger.getInstance().getLogger(this);
    
    private InitialLdapContext _ctx = null;
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
            _ctx = createInitialContext(_env); // does the same now, but better naming
            
            
        } catch (final NamingException e) {
            LOG.error(e);
            LOG.error("The follow setting(s) a invalid: \r\n"
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
    public LDAPConnector (final Hashtable<Object,String> env) throws NamingException{
        setEnv(env);
        _ctx = createInitialContext(_env);
    }
    
    /**
     *
     * @return the LDAP Connection
     */
    public DirContext getDirContext() {
        return _ctx;
    }
    
    public DirContext reconnect() throws NamingException {
        try {
            _ctx = createInitialContext(_env);
            
        } catch (final NamingException e) {
            LOG.error(e);
            LOG.error("The follow setting(s) a invalid: \r\n"
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
    private void setDefaultENV(final String[] env) {
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
     * @return env with the settings from PreferencPage
     */
    private void getUIenv() {
        
        final Preferences prefs = Activator.getDefault().getPluginPreferences();
        // Set up the environment for creating the initial context
        
        LOG.debug("++++++++++++++++++++++++++++++++++++++++++++++");
        LOG.debug("+ PLUGIN_ID: "+Activator.PLUGIN_ID);
        LOG.debug("+ P_STRING_URL: "+prefs.getString(PreferenceConstants.P_STRING_URL));
        LOG.debug("+ SECURITY_PROTOCOL: "+prefs.getString(PreferenceConstants.SECURITY_PROTOCOL));
        LOG.debug("+ SECURITY_AUTHENTICATION: "+prefs.getString(PreferenceConstants.SECURITY_AUTHENTICATION));
        LOG.debug("+ P_STRING_USER_DN: "+prefs.getString(PreferenceConstants.P_STRING_USER_DN));
        LOG.debug("+ P_STRING_USER_PASSWORD: "+prefs.getString(PreferenceConstants.P_STRING_USER_PASSWORD));
        LOG.debug("----------------------------------------------");
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
    
    /**
     * Sets the environment Hashtable with a defensive copy of the parameter Hashtable
     * @param env the environment
     * @throws NamingException
     */
    private void setEnv(final Hashtable<Object, String> env) throws NamingException {
        _env = new Hashtable<Object, String>();
        _env.putAll(env);
    }
    
    
    private InitialLdapContext createInitialContext(final Hashtable<Object, String> env) throws NamingException {
        return new InitialLdapContext(env, null);
    }
    
}
