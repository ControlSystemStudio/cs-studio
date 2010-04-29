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

import java.util.Collections;
import java.util.EnumMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.util.StringUtil;
import org.csstudio.utility.ldap.LdapActivator;
import org.csstudio.utility.ldap.preference.PreferenceKey;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * The LDAP Connector.
 *
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 12.04.2007
 */
public class LDAPConnector {

    private final Logger _log = CentralLogger.getInstance().getLogger(this);

    private InitialLdapContext _ctx = null;

    private Map<PreferenceKey, String> _prefsMap = Collections.emptyMap();

    /**
     * The connection settings come from
     * {@link org.csstudio.utility.ldap.ui.preference}
     * @throws NamingException
     *
     */
    public LDAPConnector() throws NamingException{// throws NamingException{
        try {
            _prefsMap = getUIenv();
            _ctx = createInitialContext(_prefsMap); // does the same now, but better naming


        } catch (final NamingException e) {
            _log.error(e);
            _log.error("The follow setting(s) a invalid: \r\n"
                      +"RemainingName: " + e.getRemainingName()+"\r\n"
                      +"ResolvedObj: " + e.getResolvedObj()+"\r\n"
                      +"Explanation: " + e.getExplanation()
            );
            throw e;
        }
    }

    /**
     *
     * @return the LDAP Connection
     */
    @Nonnull
    public final DirContext getDirContext() {
        return _ctx;
    }

    /**
     * @return the DirContext
     * @throws NamingException
     */
    @Nonnull
    public final DirContext reconnect() throws NamingException {
        try {
            _ctx = createInitialContext(_prefsMap);

        } catch (final NamingException e) {
            _log.error(e);
            _log.error("The follow setting(s) a invalid: \r\n"
                      +e.getRemainingName()+"\r\n"
                      +e.getResolvedObj()+"\r\n"
                      +e.getExplanation()
            );
            throw e;
        }
        return _ctx;

    }


    /**
     * Read first the preferences in instance scope and if there is no
     * user defined setting, get them from default scope.
     *
     * @return env with the settings from PreferencPage
     */
    @Nonnull
    private Map<PreferenceKey, String> getUIenv() {

        final IEclipsePreferences prefs = new DefaultScope().getNode(LdapActivator.PLUGIN_ID);

        final String url = prefs.get(PreferenceKey.P_STRING_URL.name(), "");
        final String proto = prefs.get(PreferenceKey.SECURITY_PROTOCOL.name(), "");
        final String auth = prefs.get(PreferenceKey.SECURITY_AUTHENTICATION.name(), "");
        final String dn = prefs.get(PreferenceKey.P_STRING_USER_DN.name(), "");
        final String pw = prefs.get(PreferenceKey.P_STRING_USER_PASSWORD.name(), "");

        _prefsMap = new EnumMap<PreferenceKey, String>(PreferenceKey.class);

        if (StringUtil.hasLength(url)) {
            _prefsMap.put(PreferenceKey.P_STRING_URL, url);
        }
        if (StringUtil.hasLength(proto)) {
            _prefsMap.put(PreferenceKey.SECURITY_PROTOCOL, proto);
        }
        if (StringUtil.hasLength(auth)) {
            _prefsMap.put(PreferenceKey.SECURITY_AUTHENTICATION, auth);
        }
        if (StringUtil.hasLength(dn)) {
            _prefsMap.put(PreferenceKey.P_STRING_USER_DN, dn);
        }
        if (StringUtil.hasLength(pw)) {
            _prefsMap.put(PreferenceKey.P_STRING_USER_PASSWORD, pw);
        }

        _log.debug("++++++++++++++++++++++++++++++++++++++++++++++");
        _log.debug("+ PLUGIN_ID: " + LdapActivator.PLUGIN_ID);
        _log.debug("+ P_STRING_URL: " + url);
        _log.debug("+ SECURITY_PROTOCOL: " + proto);
        _log.debug("+ SECURITY_AUTHENTICATION: " + auth);
        _log.debug("+ P_STRING_USER_DN: " + dn);
        _log.debug("+ P_STRING_USER_PASSWORD: " + pw);
        _log.debug("----------------------------------------------");

        return _prefsMap;
    }


    @Nonnull
    private InitialLdapContext createInitialContext(@Nonnull final Map<PreferenceKey, String> prefsMap) throws NamingException {

        final Hashtable<Object, String> env = new Hashtable<Object, String>();
        for (final Entry<PreferenceKey, String> entry : prefsMap.entrySet()) {
            env.put(entry.getKey().getContextId(), entry.getValue());
        }
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");

        return new InitialLdapContext(env, null);
    }

}
