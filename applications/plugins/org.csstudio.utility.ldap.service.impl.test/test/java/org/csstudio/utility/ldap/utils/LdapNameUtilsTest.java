/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldap.utils;

import static org.csstudio.utility.ldap.service.util.LdapUtils.createLdapName;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.RECORD;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration.UNIT;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.utility.ldap.service.util.LdapNameUtils;
import org.junit.Test;

import com.google.common.collect.ImmutableList;


/**
 * @author Joerg Rathlev
 * @author Bastian Knerr
 */
public class LdapNameUtilsTest {

    @Test
    public void testObjectClassOfSingleRdnName() throws Exception {
        final LdapName name = createLdapName(FACILITY.getNodeTypeName(), "foobar");
        final Rdn rdn = name.getRdn(name.size() - 1);
        assertEquals(FACILITY, FACILITY.getNodeTypeByNodeTypeName(rdn.getType()));
    }

    @Test
    public void testObjectClassOfHierarchicalLdapName() throws Exception {
        final LdapName name = createLdapName(RECORD.getNodeTypeName(), "foobar",
                                              UNIT.getNodeTypeName(), "Test",
                                              "dc", "example",
                                              "dc","com");
        final Rdn rdn = name.getRdn(name.size() - 1);
        assertEquals(RECORD, RECORD.getNodeTypeByNodeTypeName(rdn.getType()));
    }

    @Test
    public void testRemoveRdns() throws InvalidNameException {

        final LdapName name0 = LdapNameUtils.removeRdns(new LdapName(""), Collections.<Rdn>emptyList());
        assertEquals(new LdapName(""), name0);

        final LdapName name1 = LdapNameUtils.removeRdns(new LdapName("dc=com"), Collections.<Rdn>emptyList());
        assertEquals(new LdapName("dc=com"), name1);

        final LdapName name2 = LdapNameUtils.removeRdns(new LdapName("dc=com"), ImmutableList.of(new Rdn("dc", "com")));
        assertEquals(new LdapName(""), name2);

        final LdapName namex = createLdapName(RECORD.getNodeTypeName(), "foobar",
                                              UNIT.getNodeTypeName(), "Test",
                                              "dc", "example",
                                              "dc","com");

        final ImmutableList<Rdn> rdns = ImmutableList.of(new Rdn("dc", "example"), new Rdn(UNIT.getNodeTypeName(), "Test"));
        final LdapName newName = LdapNameUtils.removeRdns(namex, rdns);

        assertEquals(newName, createLdapName(RECORD.getNodeTypeName(), "foobar",
                                             "dc","com"));
    }

}
