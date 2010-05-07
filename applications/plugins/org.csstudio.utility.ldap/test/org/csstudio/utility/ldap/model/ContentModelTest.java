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

import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ECON_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EFAN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EPICS_CTRL_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.any;
import static org.csstudio.utility.ldap.LdapUtils.createLdapQuery;

import java.util.EnumMap;
import java.util.Map;

import javax.naming.directory.SearchControls;

import junit.framework.Assert;

import org.csstudio.utility.ldap.LdapActivator;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 06.05.2010
 */
public class ContentModelTest {

    private static ContentModel<LdapEpicsControlsObjectClass> MODEL;
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

        final LdapSearchResult searchResult = SERVICE.retrieveSearchResultSynchronously(createLdapQuery(EFAN_FIELD_NAME, "TEST",
                                                                                                         OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE),
                                                                                         any(ECON_FIELD_NAME),
                                                                                         SearchControls.SUBTREE_SCOPE);
       MODEL = new ContentModel<LdapEpicsControlsObjectClass>(searchResult, LdapEpicsControlsObjectClass.ROOT);

    }

    @Test
    public void testGetChildrenByTypeCache() {

        Map<String, ILdapComponent<LdapEpicsControlsObjectClass>> childrenByType = MODEL.getChildrenByType(LdapEpicsControlsObjectClass.FACILITY);
        Assert.assertEquals(childrenByType.size(), RESULT_CHILDREN_BY_TYPE.get(LdapEpicsControlsObjectClass.FACILITY).intValue());

        childrenByType = MODEL.getChildrenByType(LdapEpicsControlsObjectClass.COMPONENT);
        Assert.assertEquals(childrenByType.size(), RESULT_CHILDREN_BY_TYPE.get(LdapEpicsControlsObjectClass.COMPONENT).intValue());

        childrenByType = MODEL.getChildrenByType(LdapEpicsControlsObjectClass.IOC);
        Assert.assertEquals(childrenByType.size(), RESULT_CHILDREN_BY_TYPE.get(LdapEpicsControlsObjectClass.IOC).intValue());
    }

    @Test
    public void testGetChildrenByTypeOnRootComponent() {
        int size = MODEL.getRoot().getChildrenByType(LdapEpicsControlsObjectClass.FACILITY).size();
        Assert.assertEquals(size, RESULT_CHILDREN_BY_TYPE.get(LdapEpicsControlsObjectClass.FACILITY).intValue());

        size = MODEL.getRoot().getChildrenByType(LdapEpicsControlsObjectClass.COMPONENT).size();
        Assert.assertEquals(size, RESULT_CHILDREN_BY_TYPE.get(LdapEpicsControlsObjectClass.COMPONENT).intValue());

        size = MODEL.getRoot().getChildrenByType(LdapEpicsControlsObjectClass.IOC).size();
        Assert.assertEquals(size, RESULT_CHILDREN_BY_TYPE.get(LdapEpicsControlsObjectClass.IOC).intValue());
    }
}
