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
package org.csstudio.alarm.service.declaration;

import static org.csstudio.alarm.service.declaration.AlarmTreeLdapConstants.ECOM_FIELD_NAME;
import static org.csstudio.alarm.service.declaration.AlarmTreeLdapConstants.ECON_FIELD_NAME;
import static org.csstudio.alarm.service.declaration.AlarmTreeLdapConstants.EFAN_FIELD_NAME;
import static org.csstudio.alarm.service.declaration.AlarmTreeLdapConstants.EREN_FIELD_NAME;
import static org.csstudio.alarm.service.declaration.AlarmTreeLdapConstants.ESCO_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.OU_FIELD_NAME;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.utility.treemodel.ITreeNodeConfiguration;

import com.google.common.collect.ImmutableSet;


/**
 * The object class of an alarm tree item. The enumeration constants defined in this
 * class store information about the name of the object class in the directory,
 * which attribute to use to construct the name of a directory entry, and the
 * value of the attributes in the directory.
 *
 * @author Joerg Rathlev
 */
public enum LdapEpicsAlarmcfgConfiguration implements ITreeNodeConfiguration<LdapEpicsAlarmcfgConfiguration> {

    /**
     * The root (invisible in the alarm tree view).
     */
    ROOT("organizationUnit",
         OU_FIELD_NAME,
         ImmutableSet.<String>builder().build()),

    /**
     * The facility object class (efan).
     */
    FACILITY("epicsFacility",
             EFAN_FIELD_NAME,
             AlarmTreeNodePropertyId.getLdapAttributes()),

    /**
     * The component object class (ecom).
     */
    COMPONENT("epicsComponent",
              ECOM_FIELD_NAME,
              AlarmTreeNodePropertyId.getLdapAttributes()),

    @Deprecated
    IOC("epicsIOC",
        ECON_FIELD_NAME,
        AlarmTreeNodePropertyId.getLdapAttributes()),
    @Deprecated
    SUBCOMPONENT("epicsSubComponent",
                 ESCO_FIELD_NAME,
                 AlarmTreeNodePropertyId.getLdapAttributes()),

    /**
     * The record object class (eren).
     */
    RECORD("epicsRecord",
           EREN_FIELD_NAME,
           AlarmTreeNodePropertyId.getLdapAttributes());


    private static final Map<String, LdapEpicsAlarmcfgConfiguration> CACHE_BY_NAME =
        new HashMap<String, LdapEpicsAlarmcfgConfiguration>();


    static {
        // Initialize the _nestedClass attribute
        RECORD._nestedClasses = Collections.emptySet();

        // FIXME (bknerr) : this structure is obsolete
        IOC._nestedClasses.add(RECORD);
        SUBCOMPONENT._nestedClasses.addAll(IOC._nestedClasses);
        SUBCOMPONENT._nestedClasses.add(IOC);
        SUBCOMPONENT._nestedClasses.add(SUBCOMPONENT);
        SUBCOMPONENT._nestedClasses.add(COMPONENT);

        COMPONENT._nestedClasses.addAll(SUBCOMPONENT._nestedClasses);
        COMPONENT._nestedClasses.add(COMPONENT);

        FACILITY._nestedClasses.addAll(COMPONENT._nestedClasses);

        ROOT._nestedClasses.add(FACILITY);

        for (final LdapEpicsAlarmcfgConfiguration oc : LdapEpicsAlarmcfgConfiguration.values()) {
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
    private final String _nodeName;


    private final ImmutableSet<String> _attributes;


    /**
     * The object class of a container nested within a container of this object
     * class. <code>null</code> if this object class is not a container or if
     * there is no standard nested class for this class.
     */
    private Set<LdapEpicsAlarmcfgConfiguration> _nestedClasses = new HashSet<LdapEpicsAlarmcfgConfiguration>();


    /**
     * Creates a new object class.
     *
     * @param description
     *            the name of this object class in the directory.
     * @param nodeName
     *            the name of the attribute to use for the RDN.
     * @param cssType
     *            the value for the epicsCssType attribute in the directory.
     */
    //CHECKSTYLE:OFF
    private LdapEpicsAlarmcfgConfiguration(final String description,
                                         final String nodeName,
                                         final ImmutableSet<String> attributes) {
        //CHECKSTYLE:ON
        _description = description;
        _nodeName = nodeName;
        _attributes = attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getDescription() {
        return _description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getNodeTypeName() {
        return _nodeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Set<LdapEpicsAlarmcfgConfiguration> getNestedContainerClasses() {
        return _nestedClasses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public LdapEpicsAlarmcfgConfiguration getNodeTypeByNodeTypeName(@Nonnull final String name) {
        return getNodeTypeByNodeNameStatic(name);
    }

    @CheckForNull
    private static LdapEpicsAlarmcfgConfiguration getNodeTypeByNodeNameStatic(@Nonnull final String name) {
        return CACHE_BY_NAME.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRootTypeName() {
        return AlarmTreeLdapConstants.EPICS_ALARM_CFG_FIELD_VALUE;
    }

    /**
     * Getter.
     * @return the immutable set of permitted attributes.
     */
    public ImmutableSet<String> getAttributes() {
        return _attributes;
    }
}
