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
package org.csstudio.utility.ldap.model;

import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ECOM_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ECON_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EFAN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EPICS_CTRL_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EREN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.any;
import static org.csstudio.utility.ldap.LdapUtils.createLdapQuery;

import java.util.EnumMap;
import java.util.Map;

import javax.naming.directory.SearchControls;

import junit.framework.Assert;

import org.csstudio.utility.ldap.LdapActivator;
import org.csstudio.utility.ldap.model.builder.LdapContentModelBuilder;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test content model class and builder from LDAP.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 06.05.2010
 */
public class LdapContentModelBuilderTest {

    private static ContentModel<LdapEpicsControlsObjectClass> MODEL_ONE;

    private static ContentModel<LdapEpicsControlsObjectClass> MODEL_TWO;

    private static final ILdapService SERVICE = LdapActivator.getDefault().getLdapService();

    private static final Map<LdapEpicsControlsObjectClass, Integer> RESULT_CHILDREN_BY_TYPE =
        new EnumMap<LdapEpicsControlsObjectClass, Integer>(LdapEpicsControlsObjectClass.class);
    static {
        RESULT_CHILDREN_BY_TYPE.put(LdapEpicsControlsObjectClass.FACILITY, 3);
        RESULT_CHILDREN_BY_TYPE.put(LdapEpicsControlsObjectClass.COMPONENT, 4);
        RESULT_CHILDREN_BY_TYPE.put(LdapEpicsControlsObjectClass.IOC, 19);
    }

    @BeforeClass
    public static void modelSetup() {

        LdapSearchResult searchResult = SERVICE.retrieveSearchResultSynchronously(createLdapQuery(EFAN_FIELD_NAME, "TEST",
                                                                                                  OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE),
                                                                                                  any(ECON_FIELD_NAME),
                                                                                                  SearchControls.SUBTREE_SCOPE);
        try {
            if (searchResult != null) {
                final LdapContentModelBuilder<LdapEpicsControlsObjectClass> builder =
                    new LdapContentModelBuilder<LdapEpicsControlsObjectClass>(LdapEpicsControlsObjectClass.ROOT, searchResult);

                builder.build();
                MODEL_ONE = builder.getModel();

            } else {
                Assert.fail("Model setup failed. Search result is null.");
            }

            searchResult = SERVICE.retrieveSearchResultSynchronously(createLdapQuery(ECON_FIELD_NAME, "testLDAP",
                                                                                     ECOM_FIELD_NAME, "EPICS-IOC",
                                                                                     EFAN_FIELD_NAME, "TEST",
                                                                                     OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE),
                                                                                     any(EREN_FIELD_NAME),
                                                                                     SearchControls.SUBTREE_SCOPE);
            if (searchResult != null) {
                final LdapContentModelBuilder<LdapEpicsControlsObjectClass> builder =
                    new LdapContentModelBuilder<LdapEpicsControlsObjectClass>(LdapEpicsControlsObjectClass.ROOT, searchResult);

                builder.build();
                MODEL_TWO = builder.getModel();
            } else {
                Assert.fail("Model setup failed. Search result is null.");
            }
        } catch (final CreateContentModelException e) {
            Assert.fail("Exception when reading model from search result.");
        }
    }

    @Test
    public void testGetChildrenByLdapNameCache() {

        Map<String, ISubtreeNodeComponent<LdapEpicsControlsObjectClass>> childrenByType =
            MODEL_ONE.getChildrenByTypeAndLdapName(LdapEpicsControlsObjectClass.FACILITY);
        Assert.assertEquals(childrenByType.size(), RESULT_CHILDREN_BY_TYPE.get(LdapEpicsControlsObjectClass.FACILITY).intValue());

        childrenByType = MODEL_ONE.getChildrenByTypeAndLdapName(LdapEpicsControlsObjectClass.COMPONENT);
        Assert.assertEquals(childrenByType.size(), RESULT_CHILDREN_BY_TYPE.get(LdapEpicsControlsObjectClass.COMPONENT).intValue());

        childrenByType = MODEL_ONE.getChildrenByTypeAndLdapName(LdapEpicsControlsObjectClass.IOC);
        Assert.assertEquals(childrenByType.size(), RESULT_CHILDREN_BY_TYPE.get(LdapEpicsControlsObjectClass.IOC).intValue());
    }

    @Test
    public void testGetChildrenByTypeOnRootComponent() {
        int size = MODEL_ONE.getRoot().getChildrenByType(LdapEpicsControlsObjectClass.FACILITY).size();
        Assert.assertEquals(size, RESULT_CHILDREN_BY_TYPE.get(LdapEpicsControlsObjectClass.FACILITY).intValue());

        size = MODEL_ONE.getRoot().getChildrenByType(LdapEpicsControlsObjectClass.COMPONENT).size();
        Assert.assertEquals(size, RESULT_CHILDREN_BY_TYPE.get(LdapEpicsControlsObjectClass.COMPONENT).intValue());

        size = MODEL_ONE.getRoot().getChildrenByType(LdapEpicsControlsObjectClass.IOC).size();
        Assert.assertEquals(size, RESULT_CHILDREN_BY_TYPE.get(LdapEpicsControlsObjectClass.IOC).intValue());
    }

    @Test
    public void testBothNameCaches() {

        ISubtreeNodeComponent<LdapEpicsControlsObjectClass> comp = MODEL_TWO.getByTypeAndSimpleName(LdapEpicsControlsObjectClass.IOC, "testLDAP");

        Assert.assertEquals(createLdapQuery(ECON_FIELD_NAME, "testLDAP",
                                            ECOM_FIELD_NAME, "EPICS-IOC",
                                            EFAN_FIELD_NAME, "TEST"), comp.getLdapName());

        Assert.assertEquals(createLdapQuery(ECOM_FIELD_NAME, "EPICS-IOC",
                                            EFAN_FIELD_NAME, "TEST"), comp.getParent().getLdapName());


        comp = MODEL_TWO.getByTypeAndSimpleName(LdapEpicsControlsObjectClass.RECORD, "testLdap:alive");

        Assert.assertEquals(createLdapQuery(EREN_FIELD_NAME, "testLdap:alive",
                                            ECON_FIELD_NAME, "testLDAP",
                                            ECOM_FIELD_NAME, "EPICS-IOC",
                                            EFAN_FIELD_NAME, "TEST"), comp.getLdapName());

        Assert.assertEquals(createLdapQuery(ECON_FIELD_NAME, "testLDAP",
                                            ECOM_FIELD_NAME, "EPICS-IOC",
                                            EFAN_FIELD_NAME, "TEST"), comp.getParent().getLdapName());

    }
}
