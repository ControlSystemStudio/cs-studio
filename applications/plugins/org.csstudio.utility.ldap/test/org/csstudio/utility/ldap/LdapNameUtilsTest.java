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
 *
 * $Id$
 */
package org.csstudio.utility.ldap;

import static org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration.COMPONENT;
import static org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration.FACILITY;
import static org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration.ROOT;
import static org.csstudio.utility.ldap.utils.LdapFieldsAndAttributes.ECOM_EPICS_IOC_FIELD_VALUE;
import static org.csstudio.utility.ldap.utils.LdapFieldsAndAttributes.O_FIELD_NAME;
import static org.csstudio.utility.ldap.utils.LdapUtils.createLdapQuery;
import static org.junit.Assert.assertEquals;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import junit.framework.Assert;

import org.csstudio.utility.ldap.utils.LdapNameUtils;
import org.csstudio.utility.ldap.utils.LdapNameUtils.Direction;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for LdapNameUtils.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 07.05.2010
 */
public class LdapNameUtilsTest {

    private static final String COUNTRY_FIELD_NAME = "c";
    private static final String COUNTRY_FIELD_VALUE = "DE";
    private static final String ECON_FIELD_VALUE = "berndTest";
    private static final String EFAN_FIELD_VALUE = "TEST";
    private static final String O_FIELD_VALUE = "DESY";

    private static SearchResult RESULT;
    private static LdapName QUERY;


    @BeforeClass
    public static void setUp() {

        RESULT = new SearchResult("econ=berndTest",
                                   null,
                                   null);

        QUERY = createLdapQuery(IOC.getNodeTypeName(), ECON_FIELD_VALUE,
                                COMPONENT.getNodeTypeName(), ECOM_EPICS_IOC_FIELD_VALUE,
                                FACILITY.getNodeTypeName(), EFAN_FIELD_VALUE,
                                ROOT.getNodeTypeName(), ROOT.getRootTypeValue(),
                                O_FIELD_NAME, O_FIELD_VALUE,
                                COUNTRY_FIELD_NAME,COUNTRY_FIELD_VALUE);

        RESULT.setNameInNamespace(QUERY.toString());
    }

    @Test
    public void testSimpleNameOfSingleRdnName() throws Exception {
        final LdapName name = new LdapName("foo=bar");
        assertEquals("bar", LdapNameUtils.simpleName(name));
    }

    @Test
    public void testSimpleNameOfHierarchicalLdapName() throws Exception {
        final LdapName name = new LdapName("foo=bar,ou=Test,dc=example,dc=com");
        assertEquals("bar", LdapNameUtils.simpleName(name));
    }

    @Test
    public void testSimpleNameOfNameWithSpecialCharacters() throws Exception {
        final LdapName name = new LdapName("foo=name/with\\=special\\,characters");
        assertEquals("name/with=special,characters", LdapNameUtils.simpleName(name));
    }

    @Test
    public void testLdapNameParsing() {

        try {
            LdapNameUtils.parseSearchResult(RESULT);
        } catch (final NamingException e) {
            Assert.fail();
        }
    }

    @Test
    public void testGetValue() {
        String value = LdapNameUtils.getValueOfRdnType(QUERY, IOC.getNodeTypeName());
        Assert.assertEquals(ECON_FIELD_VALUE, value);


        value = LdapNameUtils.getValueOfRdnType(QUERY, FACILITY.getNodeTypeName());
        Assert.assertEquals(EFAN_FIELD_VALUE, value);

        value = LdapNameUtils.getValueOfRdnType(QUERY, COUNTRY_FIELD_NAME);
        Assert.assertEquals(COUNTRY_FIELD_VALUE, value);

        value = LdapNameUtils.getValueOfRdnType(QUERY, "Käse");
        Assert.assertNull(value);
    }

    @Test
    public void testRemoveRdns() {
        final LdapName first = new LdapName(QUERY.getRdns());

        try {
            LdapName modifiedFirst = LdapNameUtils.removeRdns(first, "IDoNotExist", Direction.FORWARD);
            Assert.assertEquals(modifiedFirst.toString(), "");

            modifiedFirst = LdapNameUtils.removeRdns(first, "IDoNotExist", Direction.BACKWARD);
            Assert.assertEquals(modifiedFirst.toString(), "");

            modifiedFirst = LdapNameUtils.removeRdns(first, IOC.getNodeTypeName(), Direction.FORWARD);
            Assert.assertTrue(modifiedFirst.toString().startsWith(IOC.getNodeTypeName()));
            Assert.assertTrue(modifiedFirst.toString().endsWith(ECON_FIELD_VALUE));

            modifiedFirst = LdapNameUtils.removeRdns(first, IOC.getNodeTypeName(), Direction.BACKWARD);
            Assert.assertTrue(modifiedFirst.toString().startsWith(IOC.getNodeTypeName()));
            Assert.assertTrue(modifiedFirst.toString().endsWith(COUNTRY_FIELD_VALUE));

            modifiedFirst = LdapNameUtils.removeRdns(first, COUNTRY_FIELD_NAME, Direction.FORWARD);
            Assert.assertTrue(modifiedFirst.toString().startsWith(IOC.getNodeTypeName()));
            Assert.assertTrue(modifiedFirst.toString().endsWith(COUNTRY_FIELD_VALUE));

            modifiedFirst = LdapNameUtils.removeRdns(first, COUNTRY_FIELD_NAME, Direction.BACKWARD);
            Assert.assertTrue(modifiedFirst.toString().startsWith(COUNTRY_FIELD_NAME));
            Assert.assertTrue(modifiedFirst.toString().endsWith(COUNTRY_FIELD_VALUE));

        } catch (final InvalidNameException e) {
            Assert.fail();
            e.printStackTrace();
        }
    }
}
