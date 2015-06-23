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
package org.csstudio.utility.ldap.service.impl;

import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.COMPONENT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.RECORD;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.UNIT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.VIRTUAL_ROOT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsFieldsAndAttributes.ATTR_FIELD_RESPONSIBLE_PERSON;

import java.util.Map;
import java.util.Random;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;

import junit.framework.Assert;

import org.csstudio.utility.ldap.LdapTestHelper;
import org.csstudio.utility.ldap.LdapTestTreeBuilder;
import org.csstudio.utility.ldap.service.ILdapContentModelBuilder;
import org.csstudio.utility.ldap.service.ILdapReadCompletedCallback;
import org.csstudio.utility.ldap.service.ILdapReaderJob;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.LdapServiceException;
import org.csstudio.utility.ldap.service.util.LdapUtils;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.utils.LdapSearchParams;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.INodeComponent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Test class for LDAP service actions.
 *
 * Initializes the LDAP service to an LDAP test instance.
 */
public class LdapServiceImplHeadlessTest {

    /**
     * Property holder for end of job test purpose.
     *
     * @author bknerr
     * @author $Author: bknerr $
     * @version $Revision: 1.7 $
     * @since 14.09.2010
     */
    private static class Holder<T> {

        private T _value;
        /**
         * Constructor.
         * @param val
         */
        public Holder(final T val) {
            _value = val;
        }
            public T getValue() {
            return _value;
        }
        public void setValue(final T value) {
            _value = value;
        }
    }

    private static ILdapService LDAP_SERVICE;

    private static Random RANDOM = new Random(System.currentTimeMillis());
    private static String EFAN_NAME = "Test" + String.valueOf(Math.abs(RANDOM.nextInt())) + "Efan1";


    @BeforeClass
    public static void setUp() {

        LDAP_SERVICE = LdapTestHelper.LDAP_SERVICE;

        LdapTestTreeBuilder.createLdapEpicsControlsTestTree(LDAP_SERVICE, EFAN_NAME);
    }


    @Test
    public void testLdapContentModelBuilder() {
        final LdapName name =
            LdapUtils.createLdapName(FACILITY.getNodeTypeName(), EFAN_NAME,
                                     UNIT.getNodeTypeName(), UNIT.getUnitTypeValue());
        final ILdapSearchResult result =
            LDAP_SERVICE.retrieveSearchResultSynchronously(name,
                                                           LdapUtils.any(RECORD.getNodeTypeName()),
                                                           SearchControls.SUBTREE_SCOPE);
        Assert.assertNotNull(result);


        try {
            final ILdapContentModelBuilder<LdapEpicsControlsConfiguration> builder
                = LDAP_SERVICE.getLdapContentModelBuilder(VIRTUAL_ROOT, result);
            Assert.assertNotNull(builder);
            builder.build();
            final ContentModel<LdapEpicsControlsConfiguration> model = builder.getModel();
            final Map<String, INodeComponent<LdapEpicsControlsConfiguration>> records =
                model.getByType(RECORD);
            Assert.assertEquals(4, records.size());
        } catch (final CreateContentModelException e) {
            Assert.fail("Content model could not be created.");
        } catch (LdapServiceException e) {
            Assert.fail("Content model could not be created.");
        }


    }

    @Test
    public void testLdapReaderJob() {
        final LdapName name =
            LdapUtils.createLdapName(FACILITY.getNodeTypeName(), EFAN_NAME,
                                     UNIT.getNodeTypeName(), UNIT.getUnitTypeValue());

        final Holder<Boolean> read = new Holder<Boolean>(Boolean.FALSE);
        final ILdapReaderJob job =
            LDAP_SERVICE.createLdapReaderJob(new LdapSearchParams(name, LdapUtils.any(RECORD.getNodeTypeName())),
                                             new ILdapReadCompletedCallback() {
                                                @Override
                                                public void onLdapReadComplete() {
                                                    read.setValue(Boolean.TRUE);
                                                }
                                             });
        job.schedule();
        try {
            job.join();
        } catch (final InterruptedException e) {
            Assert.fail("Not supposed to be interrupted.");
        }
        Assert.assertTrue(read.getValue());
        Assert.assertEquals(4, job.getSearchResult().getAnswerSet().size());
    }

    @Test
    public void testAttributeAccess() {
        final LdapName name =
            LdapUtils.createLdapName(IOC.getNodeTypeName(), "TestEcon2",
                                     COMPONENT.getNodeTypeName(), "TestEcom2",
                                     FACILITY.getNodeTypeName(), EFAN_NAME,
                                     UNIT.getNodeTypeName(), UNIT.getUnitTypeValue());

        try {
            Attributes attrs = LDAP_SERVICE.getAttributes(name);
            Assert.assertNotNull(attrs);
            Attribute attr = attrs.get(ATTR_FIELD_RESPONSIBLE_PERSON);
            Assert.assertNotNull(attr);
            String value = (String) attr.get();
            Assert.assertEquals("bastian.knerr@desy.de", value);

            ModificationItem[] items = new ModificationItem[]{new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attr)};
            LDAP_SERVICE.modifyAttributes(name, items);

            attrs = LDAP_SERVICE.getAttributes(name);
            Assert.assertNotNull(attrs);
            final Attribute attrNull = attrs.get(ATTR_FIELD_RESPONSIBLE_PERSON);
            Assert.assertNull(attrNull);

            items = new ModificationItem[]{new ModificationItem(DirContext.ADD_ATTRIBUTE, attr)};
            LDAP_SERVICE.modifyAttributes(name, items);

            attrs = LDAP_SERVICE.getAttributes(name);
            Assert.assertNotNull(attrs);
            attr = attrs.get(ATTR_FIELD_RESPONSIBLE_PERSON);
            Assert.assertNotNull(attr);
            value = (String) attr.get();
            Assert.assertEquals("bastian.knerr@desy.de", value);

        } catch (final NamingException e) {
            Assert.fail("Unexpected Exception on attribute modification.");
        }
    }

    @Test
    public void testRenameAndLookup() {
        final LdapName name1 =
            LdapUtils.createLdapName(RECORD.getNodeTypeName(), "TestEren3",
                                     IOC.getNodeTypeName(), "TestEcon2",
                                     COMPONENT.getNodeTypeName(), "TestEcom2",
                                     FACILITY.getNodeTypeName(), EFAN_NAME,
                                     UNIT.getNodeTypeName(), UNIT.getUnitTypeValue());

        final LdapName name2 =
            LdapUtils.createLdapName(RECORD.getNodeTypeName(), "NedFlanders",
                                     IOC.getNodeTypeName(), "TestEcon2",
                                     COMPONENT.getNodeTypeName(), "TestEcom2",
                                     FACILITY.getNodeTypeName(), EFAN_NAME,
                                     UNIT.getNodeTypeName(), UNIT.getUnitTypeValue());

        try {
            LDAP_SERVICE.rename(name1, name2);
            Assert.assertNotNull(LDAP_SERVICE.lookup(name2));
            LDAP_SERVICE.rename(name2, name1);
            Assert.assertNotNull(LDAP_SERVICE.lookup(name1));
        } catch (final NamingException e) {
            Assert.fail("Rename failed");
        }
    }

    /**
     * Tests the service method {@link ILdapService#removeComponent(Enum, LdapName)} and consequently
     * the {@link ILdapService#removeLeafComponent(LdapName)}.
     */
    @AfterClass
    public static void removeTestLdapStructure() {

        final LdapName name =
            LdapUtils.createLdapName(FACILITY.getNodeTypeName(), EFAN_NAME,
                                     UNIT.getNodeTypeName(), UNIT.getUnitTypeValue());
        try {
            Assert.assertTrue(LDAP_SERVICE.removeComponent(VIRTUAL_ROOT, name));
        } catch (final InvalidNameException e) {
            Assert.fail("Unexpected exception:\n" + e.getMessage());
        } catch (final CreateContentModelException e) {
            Assert.fail("Content model could not be created:\n" + e.getMessage());
        } catch (LdapServiceException e) {
            Assert.fail("Content model could not be created:\n" + e.getMessage());
        }
    }

}
