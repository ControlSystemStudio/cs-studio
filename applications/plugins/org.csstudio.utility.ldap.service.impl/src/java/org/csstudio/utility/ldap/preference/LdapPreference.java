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
package org.csstudio.utility.ldap.preference;

import javax.annotation.Nonnull;
import javax.naming.Context;

import org.csstudio.domain.common.preferences.AbstractPreference;
import org.csstudio.utility.ldap.LdapServiceImplActivator;

/**
 * Preferences (mimicked enum with inheritance).
 *
 * @param <T> the type of the preference. It must match the type of the default value.
 *
 * @author bknerr
 * @version $Revision$
 * @since 30.03.2010
 */
public final class LdapPreference<T> extends AbstractPreference<T> {
    
    // CHECKSTYLE:OFF (Checkstyle bug, these static final fields shall be public!) 
    public static LdapPreference<String> URL =
        new LdapPreference<String>("url", "ldap://krynfs.desy.de:389/o=DESY,c=DE", Context.PROVIDER_URL);
    public static LdapPreference<String> USER_DN =
        new LdapPreference<String>("userDn", "uid=css_user,ou=people,o=DESY,c=DE", Context.SECURITY_PRINCIPAL);
    public static LdapPreference<String> USER_PASSWORD =
        new LdapPreference<String>("userPassword", "cssPass", Context.SECURITY_CREDENTIALS);
    public static LdapPreference<String> SECURITY_PROTOCOL =
        new LdapPreference<String>("securityProtocol", "", Context.SECURITY_PROTOCOL);
    public static LdapPreference<String> SECURITY_AUTH =
        new LdapPreference<String>("securityAuth", "", Context.SECURITY_AUTHENTICATION);
    // CHECKSTYLE:ON

    private final String _contextId;

    /**
     * Constructor.
     * @param keyAsString property identifier in ini files
     * @param defaultValue
     * @param contextId the LDAP context identifier of this preference
     */
    private LdapPreference(@Nonnull final String keyAsString,
                           @Nonnull final T defaultValue,
                           @Nonnull final String contextId) {
        super(keyAsString, defaultValue);

        _contextId = contextId;
    }

    /**
     * The corresponding LDAP context id of this property.
     * @return the context id
     */
    @Nonnull
    public String getContextId() {
        return _contextId;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) LdapPreference.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getPluginID() {
        return LdapServiceImplActivator.PLUGIN_ID;
    }
}
