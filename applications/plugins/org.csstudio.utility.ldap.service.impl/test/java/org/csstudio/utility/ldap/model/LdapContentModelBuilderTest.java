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

import static org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration.COMPONENT;
import static org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration.FACILITY;
import static org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration.RECORD;
import static org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration.ROOT;
import static org.csstudio.utility.ldap.utils.LdapUtils.any;
import static org.csstudio.utility.ldap.utils.LdapUtils.createLdapQuery;

import java.util.EnumMap;
import java.util.Map;

import javax.naming.directory.SearchControls;

import junit.framework.Assert;

import org.csstudio.utility.ldap.LdapActivator;
import org.csstudio.utility.ldap.model.builder.LdapContentModelBuilder;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
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

    private static ContentModel<LdapEpicsControlsConfiguration> MODEL_ONE;

    private static ContentModel<LdapEpicsControlsConfiguration> MODEL_TWO;

    private static final Map<LdapEpicsControlsConfiguration, Integer> RESULT_CHILDREN_BY_TYPE =
        new EnumMap<LdapEpicsControlsConfiguration, Integer>(LdapEpicsControlsConfiguration.class);
    static {
        RESULT_CHILDREN_BY_TYPE.put(FACILITY, 3);
        RESULT_CHILDREN_BY_TYPE.put(COMPONENT, 4);
        RESULT_CHILDREN_BY_TYPE.put(IOC, 19);
    }



    @BeforeClass
    public static void modelSetup() {

        final ILdapService service = LdapActivator.getDefault().getLdapService();
        Assert.assertNotNull("LDAP service unavailable.", service);

        ILdapSearchResult searchResult = service.retrieveSearchResultSynchronously(createLdapQuery(FACILITY.getNodeTypeName(), "TEST",
                                                                                                  ROOT.getNodeTypeName(), ROOT.getRootTypeValue()),
                                                                                                  any(IOC.getNodeTypeName()),
                                                                                                  SearchControls.SUBTREE_SCOPE);
        try {
            if (searchResult != null) {
                final LdapContentModelBuilder<LdapEpicsControlsConfiguration> builder =
                    new LdapContentModelBuilder<LdapEpicsControlsConfiguration>(ROOT, searchResult);

                builder.build();
                MODEL_ONE = builder.getModel();

            } else {
                Assert.fail("Model setup failed. Search result is null.");
            }

            searchResult = service.retrieveSearchResultSynchronously(createLdapQuery(IOC.getNodeTypeName(), "testLDAP",
                                                                                     COMPONENT.getNodeTypeName(), "EPICS-IOC",
                                                                                     FACILITY.getNodeTypeName(), "TEST",
                                                                                     ROOT.getNodeTypeName(), ROOT.getRootTypeValue()),
                                                                                     any(RECORD.getNodeTypeName()),
                                                                                     SearchControls.SUBTREE_SCOPE);
            if (searchResult != null) {
                final LdapContentModelBuilder<LdapEpicsControlsConfiguration> builder =
                    new LdapContentModelBuilder<LdapEpicsControlsConfiguration>(ROOT, searchResult);

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

        Map<String, ISubtreeNodeComponent<LdapEpicsControlsConfiguration>> childrenByType =
            MODEL_ONE.getChildrenByTypeAndLdapName(FACILITY);
        Assert.assertEquals(childrenByType.size(), RESULT_CHILDREN_BY_TYPE.get(FACILITY).intValue());

        childrenByType = MODEL_ONE.getChildrenByTypeAndLdapName(COMPONENT);
        Assert.assertEquals(childrenByType.size(), RESULT_CHILDREN_BY_TYPE.get(COMPONENT).intValue());

        childrenByType = MODEL_ONE.getChildrenByTypeAndLdapName(IOC);
        Assert.assertEquals(childrenByType.size(), RESULT_CHILDREN_BY_TYPE.get(IOC).intValue());
    }

    @Test
    public void testGetChildrenByTypeOnRootComponent() {
        int size = MODEL_ONE.getRoot().getChildrenByType(FACILITY).size();
        Assert.assertEquals(size, RESULT_CHILDREN_BY_TYPE.get(FACILITY).intValue());

        size = MODEL_ONE.getRoot().getChildrenByType(COMPONENT).size();
        Assert.assertEquals(size, RESULT_CHILDREN_BY_TYPE.get(COMPONENT).intValue());

        size = MODEL_ONE.getRoot().getChildrenByType(IOC).size();
        Assert.assertEquals(size, RESULT_CHILDREN_BY_TYPE.get(IOC).intValue());
    }

    @Test
    public void testBothNameCaches() {

        ISubtreeNodeComponent<LdapEpicsControlsConfiguration> comp = MODEL_TWO.getByTypeAndSimpleName(IOC, "testLDAP");

        Assert.assertEquals(createLdapQuery(IOC.getNodeTypeName(), "testLDAP",
                                            COMPONENT.getNodeTypeName(), "EPICS-IOC",
                                            FACILITY.getNodeTypeName(), "TEST"), comp.getLdapName());

        Assert.assertEquals(createLdapQuery(COMPONENT.getNodeTypeName(), "EPICS-IOC",
                                            FACILITY.getNodeTypeName(), "TEST"), comp.getParent().getLdapName());


        comp = MODEL_TWO.getByTypeAndSimpleName(LdapEpicsControlsConfiguration.RECORD, "testLdap:alive");

        Assert.assertEquals(createLdapQuery(RECORD.getNodeTypeName(), "testLdap:alive",
                                            IOC.getNodeTypeName(), "testLDAP",
                                            COMPONENT.getNodeTypeName(), "EPICS-IOC",
                                            FACILITY.getNodeTypeName(), "TEST"), comp.getLdapName());

        Assert.assertEquals(createLdapQuery(IOC.getNodeTypeName(), "testLDAP",
                                            COMPONENT.getNodeTypeName(), "EPICS-IOC",
                                            FACILITY.getNodeTypeName(), "TEST"), comp.getParent().getLdapName());

    }
}
