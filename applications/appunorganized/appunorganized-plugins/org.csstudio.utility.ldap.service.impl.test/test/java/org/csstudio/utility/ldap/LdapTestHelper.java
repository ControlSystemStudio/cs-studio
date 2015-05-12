/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;

import junit.framework.Assert;

import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.testsuite.util.TestDataProvider;
import org.csstudio.utility.ldap.service.ILdapService;

/**
 * Set up methods required by many tests.
 *
 * @author bknerr
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 15.09.2010
 */
public final class LdapTestHelper {

    // CHECKSTYLE:OFF (Checkstyle bug - these constant fields shall be public)
    public static TestDataProvider PROV = createTestDataProvider();

    public static Map<String, String> LDAP_TEST_PREFS = createLdapTestServicePrefs();

    public static ILdapService LDAP_SERVICE = createLdapTestConnection();
    // CHECKSTYLE:ON


    private static TestDataProvider createTestDataProvider() {
        try {
            return TestDataProvider.getInstance(LdapServiceImplActivator.PLUGIN_ID);
        } catch (final Exception e) {
            Assert.fail("Unexpected exception: " + e.getMessage());
        }
        return null; // Nonnull annotation is correct, due to assertion failure on provider == null.
    }

    /**
     * Tests the method {@link ILdapService#reInitializeLdapConnection(Map)}
     * @return
     */
    private static ILdapService createLdapTestConnection() {
        try {
            final Map<String, String> map = createLdapTestServicePrefs();

            final ILdapService service = ServiceLocator.getService(ILdapService.class);
            Assert.assertNotNull(service);
            Assert.assertTrue(service.reInitializeLdapConnection(map));

            return service;
        } catch (final Exception e) {
            Assert.fail("Unexpected exception:\n" + e.getMessage());
        }
        return null; // Nonnull annotation is correct, due to assertion failure on service == null.
    }

    private static Map<String, String> createLdapTestServicePrefs() {
        final String url = (String) PROV.get("ldap.url");
        final String dn = (String) PROV.get("ldap.userDn");
        final String pw = (String) PROV.get("ldap.userPassword");

        // Hard-coded properties
        final Map<String, String> map = new HashMap<String, String>(5);
        map.put(Context.PROVIDER_URL, url);
        map.put(Context.SECURITY_PRINCIPAL, dn);
        map.put(Context.SECURITY_CREDENTIALS, pw);
        return map;
    }


    /**
     * Don't instantiate.
     */
    private LdapTestHelper() {
        // EMPTY
    }
}
