/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldap;



import org.csstudio.servicelocator.ServiceLocatorFactory;
import org.csstudio.utility.ldap.preference.LdapPreference;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.impl.LdapServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public final class LdapServiceImplActivator implements BundleActivator {


    /**
     * The id of this Java plug-in (value <code>{@value}</code> as defined in MANIFEST.MF.
     */
    public static final String PLUGIN_ID = "org.csstudio.utility.ldap.service.impl";

    private static final Logger LOG = LoggerFactory.getLogger(LdapServiceImplActivator.class);

    private static LdapServiceImplActivator INSTANCE;

    /**
     * Don't instantiate.
     * Called by framework.
     */
    public LdapServiceImplActivator() {
        if (INSTANCE != null) {
            throw new IllegalStateException("TreeModelActivator " + PLUGIN_ID + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
    }

    @Override
    public void start(final BundleContext context) throws Exception {

        // FIXME (jpenning) Hack: Find a better way to find out whether to use ldap
        // TODO (jpenning) Hack: Find a better way to find out whether to use ldap
        final String ldapURL = LdapPreference.URL.getValue();
        final boolean useLDAP = ldapURL != null && ldapURL.length() > 5;

        if (useLDAP) {
            ServiceLocatorFactory.registerServiceWithTracker("LDAP service implementation", context, ILdapService.class, new LdapServiceImpl());
        } else {
            LOG.info("Do not register LDAP-Service");
        }

    }

    /* (non-Javadoc)
     * @see org.csstudio.platform.AbstractCssPlugin#doStop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        // nothing to do
    }

    /**
     * Returns the singleton instance.
     *
     * @return the instance
     */
    public static LdapServiceImplActivator getDefault() {
        return INSTANCE;
    }

}
