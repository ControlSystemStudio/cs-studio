/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.alarm.treeView.ldap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.utility.ldap.ILdapObjectClass;
import static org.csstudio.alarm.treeView.ldap.AlarmTreeLdapConstants.*;


/**
 * The object class of an alarm tree item. The enumeration constants defined in this
 * class store information about the name of the object class in the directory,
 * which attribute to use to construct the name of a directory entry, and the
 * value of the epicsCssType attribute in the directory.
 *
 * @author Joerg Rathlev
 */
public enum LdapEpicsAlarmCfgObjectClass implements ILdapObjectClass<LdapEpicsAlarmCfgObjectClass> {
    ROOT("root", "ROOT", "root"),

    /**
     * The facility object class (efan).
     */
    FACILITY("epicsFacility", EFAN_FIELD_NAME, "facility"),

    /**
     * The component object class (ecom).
     */
    COMPONENT("epicsComponent", ECOM_FIELD_NAME, "component"),

    /**
     * The subcomponent object class (esco).
     * FIXME (bknerr) : might be obsolete
     */
    SUBCOMPONENT("epicsSubComponent", ESCO_FIELD_NAME, "subComponent"),

    IOC("epicsIOC", ECON_FIELD_NAME, "ioc"),

    /**
     * The record object class (eren).
     */
    RECORD("epicsRecord", EREN_FIELD_NAME, "record");


    private static final Map<String, LdapEpicsAlarmCfgObjectClass> CACHE_BY_RDN_TYPE =
        new HashMap<String, LdapEpicsAlarmCfgObjectClass>();


    static {
        // Initialize the _nestedClass attribute
        RECORD._nestedClasses = Collections.emptySet();

        IOC._nestedClasses.add(RECORD);

        SUBCOMPONENT._nestedClasses.addAll(IOC._nestedClasses);
        SUBCOMPONENT._nestedClasses.add(IOC);
        SUBCOMPONENT._nestedClasses.add(SUBCOMPONENT);

        COMPONENT._nestedClasses.addAll(SUBCOMPONENT._nestedClasses);
        COMPONENT._nestedClasses.add(COMPONENT);

        FACILITY._nestedClasses.addAll(COMPONENT._nestedClasses);

        ROOT._nestedClasses.add(FACILITY);

        for (final LdapEpicsAlarmCfgObjectClass oc : LdapEpicsAlarmCfgObjectClass.values()) {
            CACHE_BY_RDN_TYPE.put(oc.getRdnType(), oc);
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
    private final String _rdnType;


    /**
     * The object class of a container nested within a container of this object
     * class. <code>null</code> if this object class is not a container or if
     * there is no standard nested class for this class.
     */
    private Set<LdapEpicsAlarmCfgObjectClass> _nestedClasses = new HashSet<LdapEpicsAlarmCfgObjectClass>();

    /**
     * The value for the epicsCssType attribute for entries of this class in the
     * directory.
     * // FIXME (bknerr) : might be obsolete
     */
    private final String _cssType;

    /**
     * Creates a new object class.
     *
     * @param description
     *            the name of this object class in the directory.
     * @param rdnType
     *            the name of the attribute to use for the RDN.
     * @param cssType
     *            the value for the epicsCssType attribute in the directory.
     */
    //CHECKSTYLE:OFF
    private LdapEpicsAlarmCfgObjectClass(final String description,
                                         final String rdnType,
                                         final String cssType) {
        //CHECKSTYLE:ON
        _description = description;
        _rdnType = rdnType;
        _cssType = cssType;
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
    public String getRdnType() {
        return _rdnType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Set<LdapEpicsAlarmCfgObjectClass> getNestedContainerClasses() {
        return _nestedClasses;
    }

    /**
     * Returns the value to use for the epicsCssType attribute of entries of
     * this object class.
     * // FIXME (bknerr) : might be obsolete
     * @return the value to use for the epicsCssType attribute.
     */
    @Nonnull
    public String getCssType() {
        return _cssType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public LdapEpicsAlarmCfgObjectClass getObjectClassByRdnType(@Nonnull final String rdn) {
        return getObjectClassByRdnTypeStatic(rdn);
    }

    /**
     * Static method to encapsulate access to static CACHE object
     * @param rdn
     * @return
     */
    @CheckForNull
    private static LdapEpicsAlarmCfgObjectClass getObjectClassByRdnTypeStatic(@Nonnull final String rdn) {
        return CACHE_BY_RDN_TYPE.get(rdn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LdapEpicsAlarmCfgObjectClass getRoot() {
        return ROOT;
    }

}
