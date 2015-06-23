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

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The object class of an EPICS Controls item. The enumeration constants defined in this
 * class store information about the name of the object class in the directory,
 * which attribute to use to construct the name of a directory entry.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 03.05.2010
 */
public enum LdapEpicsControlsConfiguration implements ILdapTreeNodeConfiguration<LdapEpicsControlsConfiguration> {

    /**
     * The root for any tree structure. This node type does not have a pendant in LDAP, hence 'virtual'.
     */
    VIRTUAL_ROOT("vroot", "virtual tree configuration root"),

    UNIT(LdapFieldsAndAttributes.ORGANIZATION_UNIT_FIELD_NAME, "factory aspect"),

    /**
     * The facility object class (efan).
     */
    FACILITY("efan", "facility"),

    /**
     * The component object class (ecom).
     */
    COMPONENT("ecom", "component"),

    /**
     * The IOC object class (econ).
     */
    IOC("econ", "ioc"),

    /**
     * The record object class (eren).
     */
    RECORD("eren", "record");


    private static final Map<String, LdapEpicsControlsConfiguration> CACHE_BY_NAME =
        Maps.newHashMapWithExpectedSize(values().length);

    static {
        RECORD._nestedClasses =  EnumSet.noneOf(LdapEpicsControlsConfiguration.class);

        IOC._nestedClasses = EnumSet.of(RECORD);

        COMPONENT._nestedClasses = EnumSet.of(IOC);

        FACILITY._nestedClasses = EnumSet.of(COMPONENT);

        UNIT._nestedClasses = EnumSet.of(FACILITY);

        VIRTUAL_ROOT._nestedClasses = EnumSet.of(UNIT);

        for (final LdapEpicsControlsConfiguration oc : LdapEpicsControlsConfiguration.values()) {
            CACHE_BY_NAME.put(oc.getNodeTypeName(), oc);
        }
    }


    /**
     * The name of this object class in the directory.
     */
    private final String _description;

    /**
     * The name of the attribute to use for the RDN of entries of this class in
     * the directory.
     */
    private final String _nodeTypeName;


    /**
     * The tree items that are nested into a container of this class.
     */
    private Set<LdapEpicsControlsConfiguration> _nestedClasses;

    /**
     * Creates a new tree node type.
     *
     * @param nodeTypeName
     *            the name of the attribute to use for the RDN.
     * @param description
     *            the description of this tree component.
     */
    private LdapEpicsControlsConfiguration(final String nodeTypeName,
                                           final String description) {
        _nodeTypeName = nodeTypeName;
        _description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LdapEpicsControlsConfiguration getRoot() {
        return UNIT;
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
    public String getNodeTypeName() {
        return _nodeTypeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableSet<LdapEpicsControlsConfiguration> getNestedContainerTypes() {
        return Sets.immutableEnumSet(_nestedClasses);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LdapEpicsControlsConfiguration getNodeTypeByNodeTypeName(final String name) {
        return getNodeTypeByNodeNameStatic(name);
    }

    private static LdapEpicsControlsConfiguration getNodeTypeByNodeNameStatic(final String name) {
        return CACHE_BY_NAME.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUnitTypeValue() {
        return "EpicsControls";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableSet<String> getAttributes() {
        return ImmutableSet.<String>builder().build(); // Empty for this tree configuration type
    }

    public static String getDtdFilePath() throws IOException {
        return TreeConfigurationActivator.getResourceFromBundle("./res/dtd/epicsControls.dtd");
    }
}
