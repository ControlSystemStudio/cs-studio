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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;

import org.csstudio.domain.common.preferences.AbstractPreference;
import org.csstudio.utility.ldap.LdapServiceImplActivator;
import org.csstudio.utility.ldap.preference.LdapPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The LDAP Connector.
 *
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 12.04.2007
 */
public class LDAPConnector {

    private static final Logger LOG = LoggerFactory.getLogger(LDAPConnector.class);
    
    private InitialLdapContext _ctx = null;

    private Map<String, String> _contextPrefs = Collections.emptyMap();

    /**
     * The connection settings come from
     * {@link org.csstudio.utility.ldap.ui.preference}
     * @throws NamingException
     *
     */
    public LDAPConnector() throws NamingException{// throws NamingException{
        try {
            _contextPrefs = createContextPrefsFromLdapPrefs();
            _ctx = createInitialContext(_contextPrefs); // does the same now, but better naming
        } catch (final NamingException e) {
            logError(e);
            throw e;
        }
    }

    private void logError(@Nonnull final NamingException e) {
        LOG.error("The LDAP settings are invalid.", e);
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
            _ctx = createInitialContext(_contextPrefs);

        } catch (final NamingException e) {
            logError(e);
            throw e;
        }
        return _ctx;

    }


    /**
     * Read first the preferences in instance scope and if there is no
     * user defined setting, get them from default scope.
     *
     * @return env with the settings from PreferencePage
     */
    @Nonnull
    private Map<String, String> createContextPrefsFromLdapPrefs() {

        LOG.debug("++++++++++++++++++++++++++++++++++++++++++++++");
        LOG.debug("+ PLUGIN_ID: {}", LdapServiceImplActivator.PLUGIN_ID);

        _contextPrefs = new HashMap<String, String>();

        for (final AbstractPreference<?> pref : LdapPreference.URL.getAllPreferences()) {
            final String key = pref.getKeyAsString();
            final String value = (String) pref.getValue();
            LOG.debug("+ {}: {}", key,  value);

            if (!"".equals(pref.getValue())) { // put only non empty strings in the map
                _contextPrefs.put( ((LdapPreference<?>) pref).getContextId(), value);
            }
        }

        LOG.debug("----------------------------------------------");

        return _contextPrefs;
    }


    @Nonnull
    private InitialLdapContext createInitialContext(@Nonnull final Map<String, String> contextPrefs) throws NamingException {

        final Hashtable<Object, String> env = new Hashtable<Object, String>(contextPrefs);

        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");

        return new InitialLdapContext(env, null);
    }

}
