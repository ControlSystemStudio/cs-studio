/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.utility.ldap.treeconfiguration;

import java.net.URL;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Enumeration of the additional properties that can be set on an alarm tree
 * node.
 *
 * @author Joerg Rathlev
 */
public enum EpicsAlarmcfgTreeNodeAttribute {

    CSS_ALARM_DISPLAY("epicsCssAlarmDisplay",
                      "The CSS alarm display.",
                      URL.class),
    CSS_DISPLAY("epicsCssDisplay",
                "The CSS display.",
                URL.class),
    CSS_STRIP_CHART("epicsCssStripChart",
                    "The CSS strip chart.",
                    URL.class),
    HELP_GUIDANCE("epicsHelpGuidance",
                  "A short description of the object.",
                  String.class),
    HELP_PAGE("epicsHelpPage",
              "The help page. This should be the URL of a web page.",
              URL.class);


    private static ImmutableSet<String> LDAP_ATTRIBUTES;

    static {
        final Builder<String> builder = ImmutableSet.builder();
        for (final EpicsAlarmcfgTreeNodeAttribute id : values()) {
            builder.add(id.getLdapAttribute());
        }
        LDAP_ATTRIBUTES = builder.build();
    }

    private final String _ldapAttribute;

    private final String _description;

    private final Class<?> _propertyClass;

    /**
     * Constructor.
     * @param ldapAttribute the name as it is defined as attribute in LDAP
     */
    private EpicsAlarmcfgTreeNodeAttribute(final String ldapAttribute,
                                           final String description,
                                           final Class<?> clazz) {
        _ldapAttribute = ldapAttribute;
        _description = description;
        _propertyClass = clazz;

    }

    public String getLdapAttribute() {
        return _ldapAttribute;
    }

    /**
     * Compares the given string for the LDAP attribute with the field of the enum objects.
     * @return returns the enum object with the first match of its attribute
     */
    public static EpicsAlarmcfgTreeNodeAttribute getIdByLdapAttribute(final String attribute) {
        for (final EpicsAlarmcfgTreeNodeAttribute id : values()) {
            if (id.getLdapAttribute().equals(attribute)) {
                return id;
            }
        }
        return null;
    }

    /**
     * @return an immutable collection of the LDAP attributes of an alarm tree node
     */
    public static ImmutableSet<String> getLdapAttributes() {
        return LDAP_ATTRIBUTES;
    }

    /**
     * A describing string for this property.
     * @return the description
     */
    public String getDescription() {
        return _description;
    }

    /**
     * The class to which a property value of this type can be cast.
     * @return the class
     */
    public Class<?> getPropertyClass() {
        return _propertyClass;
    }
}
