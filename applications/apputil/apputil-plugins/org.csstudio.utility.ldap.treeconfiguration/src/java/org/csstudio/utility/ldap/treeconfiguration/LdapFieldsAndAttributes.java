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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Common constants for LDAP field names, popular values, and forbidden symbols in LDAP entry names.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 20.04.2010
 */
public final class LdapFieldsAndAttributes {


    public static final String COUNTRY_FIELD_NAME = "c";
    public static final String ORGANIZATION_FIELD_NAME = "o";
    public static final String ORGANIZATION_UNIT_FIELD_NAME = "ou";

    public static final String FIELD_SEPARATOR  = ",";
    public static final String FIELD_ASSIGNMENT = "=";
    public static final String FIELD_WILDCARD   = "*";

    public static final String ATTR_FIELD_OBJECT_CLASS = "objectClass";

    public static final String ATTR_VAL_REC_OBJECT_CLASS = "epicsRecord";
    public static final String ATTR_VAL_IOC_OBJECT_CLASS = "epicsController";
    public static final String ATTR_VAL_IOC_IP_ADDRESS = "epicsIPAddress";
    public static final String ATTR_VAL_COM_OBJECT_CLASS = "epicsComponent";
    public static final String ATTR_VAL_FAC_OBJECT_CLASS = "epicsFacility";

    public static final Set<String> FORBIDDEN_SUBSTRINGS = new HashSet<String>();

    private static final Logger LOG = LoggerFactory.getLogger(LdapFieldsAndAttributes.class);

    // CHECKSTYLE OFF : |
    public static LdapName LDAP_ROOT;
    // CHECKSTYLE ON : |
    static {
        List<Rdn> rdns;
        try {
            rdns = Arrays.asList(new Rdn[] {
                                            new Rdn(COUNTRY_FIELD_NAME + FIELD_ASSIGNMENT + "DE"),
                                            new Rdn(ORGANIZATION_FIELD_NAME + FIELD_ASSIGNMENT + "DESY"),
                                            });
            LDAP_ROOT = new LdapName(rdns);
        } catch (final InvalidNameException e) {
            LOG.error("LDAP ROOT variable could not be initialised.", e);
        }
    }

    /**
     * See http://www.ietf.org/rfc/rfc2253.txt
     */
    static {
        FORBIDDEN_SUBSTRINGS.add(",");
        FORBIDDEN_SUBSTRINGS.add("+");
        FORBIDDEN_SUBSTRINGS.add("\"");
        FORBIDDEN_SUBSTRINGS.add("/");
        FORBIDDEN_SUBSTRINGS.add("\\");
        FORBIDDEN_SUBSTRINGS.add("<");
        FORBIDDEN_SUBSTRINGS.add(">");
        FORBIDDEN_SUBSTRINGS.add(";");
    }

    /**
     * Don't instantiate.
     */
    private LdapFieldsAndAttributes() {
        // Empty
    }
}
