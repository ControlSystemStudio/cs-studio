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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.utility.ldap.ILdapObjectClass;
import org.csstudio.utility.ldap.LdapFieldsAndAttributes;

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
public enum LdapEpicsControlsObjectClass implements ILdapObjectClass<LdapEpicsControlsObjectClass> {
    ROOT(LdapFieldsAndAttributes.OU_FIELD_NAME, "root"),

    /**
     * The facility object class (efan).
     */
    FACILITY(LdapFieldsAndAttributes.EFAN_FIELD_NAME, "facility"),

    /**
     * The component object class (ecom).
     */
    COMPONENT(LdapFieldsAndAttributes.ECOM_FIELD_NAME, "component"),

    /**
     * The IOC object class (econ).
     */
    IOC(LdapFieldsAndAttributes.ECON_FIELD_NAME, "ioc"),

    /**
     * The record object class (eren).
     */
    RECORD(LdapFieldsAndAttributes.EREN_FIELD_NAME, "record");


    private static final Map<String, LdapEpicsControlsObjectClass> CACHE_BY_RDN_TYPE =
        new HashMap<String, LdapEpicsControlsObjectClass>();


    static {
        // Initialize the _nestedClass attribute
        RECORD._nestedClasses = Collections.emptySet();

        IOC._nestedClasses.add(RECORD);

        COMPONENT._nestedClasses.add(IOC);

        FACILITY._nestedClasses.add(COMPONENT);

        ROOT._nestedClasses.add(FACILITY);

        for (final LdapEpicsControlsObjectClass oc : LdapEpicsControlsObjectClass.values()) {
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
    private Set<LdapEpicsControlsObjectClass> _nestedClasses = new HashSet<LdapEpicsControlsObjectClass>();

    /**
     * Creates a new object class.
     *
     * @param rdnType
     *            the name of the attribute to use for the RDN.
     * @param description
     *            the description of this tree component.
     */
    //CHECKSTYLE:OFF
    private LdapEpicsControlsObjectClass(final String rdnType,
                                         final String description) {
    //CHECKSTYLE:ON
        _description = description;
        _rdnType = rdnType;
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
    @CheckForNull
    public Set<LdapEpicsControlsObjectClass> getNestedContainerClasses() {
        return _nestedClasses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public LdapEpicsControlsObjectClass getObjectClassByRdnType(@Nonnull final String rdn) {
        return getObjectClassByRdnTypeStatic(rdn);
    }

    @CheckForNull
    private static LdapEpicsControlsObjectClass getObjectClassByRdnTypeStatic(@Nonnull final String rdn) {
        return CACHE_BY_RDN_TYPE.get(rdn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRootName() {
        return LdapFieldsAndAttributes.EPICS_CTRL_FIELD_VALUE;
    }
}

