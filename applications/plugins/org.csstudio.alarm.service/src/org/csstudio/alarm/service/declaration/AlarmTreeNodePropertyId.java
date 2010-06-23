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

package org.csstudio.alarm.service.declaration;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Enumeration of the additional properties that can be set on an alarm tree
 * node.
 *
 * @author Joerg Rathlev
 */
public enum AlarmTreeNodePropertyId {
	CSS_ALARM_DISPLAY("epicsCssAlarmDisplay"),
	CSS_DISPLAY("epicsCssDisplay"),
	CSS_STRIP_CHART("epicsCssStripChart"),
	HELP_GUIDANCE("epicsHelpGuidance"),
	HELP_PAGE("epicsHelpPage");


	private static ImmutableSet<String> LDAP_ATTRIBUTES;

    static {
	    final Builder<String> builder = ImmutableSet.builder();
	    for (final AlarmTreeNodePropertyId id : values()) {
	        builder.add(id.getLdapAttribute());
	    }
	    LDAP_ATTRIBUTES = builder.build();
	}

	private final String _ldapAttribute;

	/**
	 * Constructor.
	 * @param ldapAttribute the name as it is defined as attribute in LDAP
	 *
	 * CHECKSTYLE:Jsr305Annotations:OFF
	 */
	private AlarmTreeNodePropertyId(final String ldapAttribute) {
	    _ldapAttribute = ldapAttribute;
    }

	@Nonnull
	public String getLdapAttribute() {
	    return _ldapAttribute;
	}

    /**
     * Compares the given string for the LDAP attribute with the field of the enum objects.
     * @return returns the enum object with the first match of its attribute
     */
	@CheckForNull
	public static AlarmTreeNodePropertyId getIdByLdapAttribute(@Nonnull final String attribute) {
        for (final AlarmTreeNodePropertyId id : values()) {
            if (id.getLdapAttribute().equals(attribute)) {
                return id;
            }
        }
        return null;
    }

    /**
     * @return an immutable collection of the LDAP attributes of an alarm tree node
     */
    @Nonnull
	public static ImmutableSet<String> getLdapAttributes() {
        return LDAP_ATTRIBUTES;
    }
}
