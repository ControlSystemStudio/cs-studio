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
package org.csstudio.utility.ldap.treeconfiguration;

import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ORGANIZATION_UNIT_FIELD_NAME;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The tree configuration of the authorize id items. The enumeration constants defined in this
 * class store information about the name of the object class in the directory,
 * which attribute to use to construct the name of a directory entry, and the
 * value of the attributes in the directory.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 30.06.2010
 */
public enum LdapEpicsAuthorizeIdConfiguration implements ILdapTreeNodeConfiguration<LdapEpicsAuthorizeIdConfiguration> {

    /**
     * The root for any tree structure. This node type does not have a pendant in LDAP, hence 'virtual'.
     */
    VIRTUAL_ROOT("virtual tree configuration",
                 "vroot",
                 ImmutableSet.<String>builder().build()),
    /**
     * The factory aspect.
     */
    UNIT("organizationUnit",
         ORGANIZATION_UNIT_FIELD_NAME,
         ImmutableSet.<String>builder().build()),

    /**
     * The root (invisible in the alarm tree view).
     */
    OU("organizationUnit",
       ORGANIZATION_UNIT_FIELD_NAME,
       ImmutableSet.<String>builder().build()),

    /**
     * The id name.
     */
    ID_NAME("authorize id name",
            "eain",
            ImmutableSet.<String>builder().build()),

    /**
     * The component object class (ecom).
     */
    ID_ROLE("authorize id role",
            "eair",
            ImmutableSet.<String>builder().add(LdapEpicsAuthorizeIdFieldsAndAttributes.ATTR_EAIN_FIELD_NAME)
                                          .add(LdapEpicsAuthorizeIdFieldsAndAttributes.ATTR_EAIG_FIELD_NAME)
                                          .build());


    private static final Map<String, LdapEpicsAuthorizeIdConfiguration> CACHE_BY_NAME =
        Maps.newHashMapWithExpectedSize(values().length);

    static {
        ID_ROLE._nestedClasses = EnumSet.noneOf(LdapEpicsAuthorizeIdConfiguration.class);
        ID_NAME._nestedClasses = EnumSet.of(ID_ROLE);
        OU._nestedClasses = EnumSet.of(ID_NAME);
        UNIT._nestedClasses = EnumSet.of(OU);
        VIRTUAL_ROOT._nestedClasses = EnumSet.of(UNIT);

        for (final LdapEpicsAuthorizeIdConfiguration oc : values()) {
            CACHE_BY_NAME.put(oc.getNodeTypeName(), oc);
        }
    }

    private final String _description;
    private final String _nodeTypeName;
    private final ImmutableSet<String> _attributes;

    /**
     * The tree items that are nested into a container of this class.
     */
    private Set<LdapEpicsAuthorizeIdConfiguration> _nestedClasses;


    /**
     * Constructor.
     *
     * @param description the node type description
     * @param nodeTypeName the node type name
     * @param attributes the attribute names for this node type
     */
    private LdapEpicsAuthorizeIdConfiguration(final String description,
                                              final String nodeTypeName,
                                              final ImmutableSet<String> attributes) {
        _description = description;
        _nodeTypeName = nodeTypeName;
        _attributes = attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LdapEpicsAuthorizeIdConfiguration getRoot() {
        return UNIT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableSet<String> getAttributes() {
        return _attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return _description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableSet<LdapEpicsAuthorizeIdConfiguration> getNestedContainerTypes() {
        return Sets.immutableEnumSet(_nestedClasses);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LdapEpicsAuthorizeIdConfiguration getNodeTypeByNodeTypeName(final String nodeTypeName) {
        return CACHE_BY_NAME.get(nodeTypeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNodeTypeName() {
        return _nodeTypeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUnitTypeValue() {
        return "EpicsAuthorizeID";
    }

}
