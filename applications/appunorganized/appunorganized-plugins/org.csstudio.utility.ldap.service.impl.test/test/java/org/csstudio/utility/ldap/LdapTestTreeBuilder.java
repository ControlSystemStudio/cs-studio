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

import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsFieldsAndAttributes.ATTR_FIELD_RESPONSIBLE_PERSON;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_VAL_COM_OBJECT_CLASS;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_VAL_FAC_OBJECT_CLASS;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_VAL_IOC_OBJECT_CLASS;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_VAL_REC_OBJECT_CLASS;


import javax.naming.InvalidNameException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import junit.framework.Assert;

import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.util.LdapUtils;
import org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;

/**
 * Builder for test structures in LDAP.
 *
 * @author bknerr
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 23.09.2010
 */
public final class LdapTestTreeBuilder {

    /**
     * Don't instantiate.
     */
    private LdapTestTreeBuilder() {
        // EMPTY
    }

    private static Attributes EC_EFAN_ATTRS = new BasicAttributes();
    private static Attributes EC_ECOM_ATTRS = new BasicAttributes();
    private static Attributes EC_ECON_ATTRS = new BasicAttributes();
    private static Attributes EC_EREN_ATTRS = new BasicAttributes();

    static {
        EC_EFAN_ATTRS.put(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_FAC_OBJECT_CLASS);
        EC_ECOM_ATTRS.put(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_COM_OBJECT_CLASS);
        EC_ECON_ATTRS.put(ATTR_FIELD_RESPONSIBLE_PERSON, "bastian.knerr@desy.de");
        EC_ECON_ATTRS.put(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_IOC_OBJECT_CLASS);
        EC_EREN_ATTRS.put(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_REC_OBJECT_CLASS);
    }

    private static Attributes EA_EFAN_ATTRS = new BasicAttributes();
    private static Attributes EA_ECOM_ATTRS = new BasicAttributes();
    private static Attributes EA_EREN_ATTRS = new BasicAttributes();

    private static final String ATTR_TEST_CONTENT = "TestContent";

    static {
        EA_EFAN_ATTRS.put(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_FAC_OBJECT_CLASS);
        EA_ECOM_ATTRS.put(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_COM_OBJECT_CLASS);
        EA_EREN_ATTRS.put(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_REC_OBJECT_CLASS);

        for (final String attrID : EpicsAlarmcfgTreeNodeAttribute.getLdapAttributes()) {
            EA_ECOM_ATTRS.put(attrID, ATTR_TEST_CONTENT);
            EA_EREN_ATTRS.put(attrID, ATTR_TEST_CONTENT);
        }
    }

    /**
     * Tests the method {@link ILdapService#createComponent(LdapName, Attributes)}.
     */
    public static void createLdapEpicsControlsTestTree(final ILdapService service,
                                                       final String efanName) {
        try {
            final LdapName name =
                LdapUtils.createLdapName(LdapEpicsControlsConfiguration.FACILITY.getNodeTypeName(), efanName,
                                         LdapEpicsControlsConfiguration.UNIT.getNodeTypeName(), LdapEpicsControlsConfiguration.UNIT.getUnitTypeValue());
            Assert.assertTrue(service.createComponent(name, EC_EFAN_ATTRS));


            name.add(new Rdn(LdapEpicsControlsConfiguration.COMPONENT.getNodeTypeName(), "TestEcom1"));
            Assert.assertTrue(service.createComponent(name, EC_ECOM_ATTRS));

            name.add(new Rdn(LdapEpicsControlsConfiguration.IOC.getNodeTypeName(), "TestEcon1"));
            Assert.assertTrue(service.createComponent(name, EC_ECON_ATTRS));

            name.add(new Rdn(LdapEpicsControlsConfiguration.RECORD.getNodeTypeName(), "TestEren1"));
            Assert.assertTrue(service.createComponent(name, EC_EREN_ATTRS));

            name.remove(name.size() - 1);
            name.add(new Rdn(LdapEpicsControlsConfiguration.RECORD.getNodeTypeName(), "TestEren2"));
            Assert.assertTrue(service.createComponent(name, EC_EREN_ATTRS));

            final LdapName name2 =
                LdapUtils.createLdapName(LdapEpicsControlsConfiguration.FACILITY.getNodeTypeName(), efanName,
                                         LdapEpicsControlsConfiguration.UNIT.getNodeTypeName(), LdapEpicsControlsConfiguration.UNIT.getUnitTypeValue());

            name2.add(new Rdn(LdapEpicsControlsConfiguration.COMPONENT.getNodeTypeName(), "TestEcom2"));
            Assert.assertTrue(service.createComponent(name2, EC_ECOM_ATTRS));

            name2.add(new Rdn(LdapEpicsControlsConfiguration.IOC.getNodeTypeName(), "TestEcon2"));
            Assert.assertTrue(service.createComponent(name2, EC_ECON_ATTRS));

            name2.add(new Rdn(LdapEpicsControlsConfiguration.RECORD.getNodeTypeName(), "TestEren3"));
            Assert.assertTrue(service.createComponent(name2, EC_EREN_ATTRS));

            name2.remove(name2.size() - 1);
            name2.add(new Rdn(LdapEpicsControlsConfiguration.RECORD.getNodeTypeName(), "TestEren4"));
            Assert.assertTrue(service.createComponent(name2, EC_EREN_ATTRS));

        } catch (final InvalidNameException e) {
            Assert.fail("LDAP name composition failed.");
        }
    }


    public static void createLdapEpicsAlarmcfgTestTree(final ILdapService service,
                                                       final String efanName) {
        try {
            final LdapName name =
                LdapUtils.createLdapName(LdapEpicsAlarmcfgConfiguration.FACILITY.getNodeTypeName(), efanName,
                                         LdapEpicsAlarmcfgConfiguration.UNIT.getNodeTypeName(), LdapEpicsAlarmcfgConfiguration.UNIT.getUnitTypeValue());
            Assert.assertTrue(service.createComponent(name, EA_EFAN_ATTRS));

            name.add(new Rdn(LdapEpicsAlarmcfgConfiguration.RECORD.getNodeTypeName(), "TestEren1"));
            Assert.assertTrue(service.createComponent(name, EA_EREN_ATTRS));

            name.remove(name.size() - 1);
            name.add(new Rdn(LdapEpicsAlarmcfgConfiguration.COMPONENT.getNodeTypeName(), "TestEcom1"));
            Assert.assertTrue(service.createComponent(name, EA_ECOM_ATTRS));

            name.add(new Rdn(LdapEpicsAlarmcfgConfiguration.RECORD.getNodeTypeName(), "TestEren2"));
            Assert.assertTrue(service.createComponent(name, EA_EREN_ATTRS));

            name.remove(name.size() - 1);
            name.add(new Rdn(LdapEpicsAlarmcfgConfiguration.COMPONENT.getNodeTypeName(), "TestEcom2"));
            Assert.assertTrue(service.createComponent(name, EA_ECOM_ATTRS));

            name.add(new Rdn(LdapEpicsAlarmcfgConfiguration.RECORD.getNodeTypeName(), "TestEren3"));
            Assert.assertTrue(service.createComponent(name, EA_EREN_ATTRS));

            name.remove(name.size() - 1);
            name.add(new Rdn(LdapEpicsAlarmcfgConfiguration.RECORD.getNodeTypeName(), "TestEren4"));
            Assert.assertTrue(service.createComponent(name, EA_EREN_ATTRS));


            final LdapName name2 =
                LdapUtils.createLdapName(LdapEpicsAlarmcfgConfiguration.FACILITY.getNodeTypeName(), efanName,
                                         LdapEpicsAlarmcfgConfiguration.UNIT.getNodeTypeName(), LdapEpicsAlarmcfgConfiguration.UNIT.getUnitTypeValue());

            name2.add(new Rdn(LdapEpicsAlarmcfgConfiguration.COMPONENT.getNodeTypeName(), "TestEcom3"));
            Assert.assertTrue(service.createComponent(name2, EA_ECOM_ATTRS));

            name2.add(new Rdn(LdapEpicsAlarmcfgConfiguration.COMPONENT.getNodeTypeName(), "TestEcom4"));
            Assert.assertTrue(service.createComponent(name2, EA_ECOM_ATTRS));

            name2.add(new Rdn(LdapEpicsAlarmcfgConfiguration.RECORD.getNodeTypeName(), "TestEren5"));
            Assert.assertTrue(service.createComponent(name2, EA_EREN_ATTRS));

            name2.remove(name2.size() - 1);
            name2.add(new Rdn(LdapEpicsAlarmcfgConfiguration.RECORD.getNodeTypeName(), "TestEren6"));
            Assert.assertTrue(service.createComponent(name2, EA_EREN_ATTRS));

        } catch (final InvalidNameException e) {
            Assert.fail("LDAP name composition failed.");
        }
    }
}
