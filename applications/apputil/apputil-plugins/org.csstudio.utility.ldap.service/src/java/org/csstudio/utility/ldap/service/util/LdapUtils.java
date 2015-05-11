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
package org.csstudio.utility.ldap.service.util;


import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.FIELD_ASSIGNMENT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.FIELD_WILDCARD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constants class for LDAP entries.
 *
 * @author bknerr
 * @version $Revision$
 * @since 11.03.2010
 */
public final class LdapUtils {

    private static final Logger LOG = LoggerFactory.getLogger(LdapUtils.class);

    /**
     * Constructor.
     */
    private LdapUtils() {
        // Dont instantiate
    }

    /**
     * Returns a filter for 'any' match of the field name (e.g. '<fieldName>=*').
     * @param fieldName the field to match any
     * @return .
     */
    public static String any(final String fieldName) {
        return equ(fieldName, FIELD_WILDCARD);
    }

    /**
     * Returns a filter for a direct match of the field name (e.g. '<fieldName>=<fieldValue>').
     * @param fieldName the field type
     * @param fieldValue the value the field type shall match
     * @return .
     */
    public static String equ(final String fieldName, final String fieldValue) {
        return fieldName + FIELD_ASSIGNMENT + fieldValue;
    }

    /**
     * Returns the attributes for a new entry with the given object class and
     * name.
     *
     * @param keysAndValues an array of Strings that represent key value pairs, consecutively (1st=key, 2nd=value, 3rd=key, 4th=value, etc.)
     * @return the attributes for the new entry.
     */
    public static Attributes attributesForLdapEntry(final String... keysAndValues) {
        if (keysAndValues.length % 2 > 0) {
            LOG.error("Ldap Attributes: For key value pairs the length of String array has to be multiple of 2!");
            throw new IllegalArgumentException("Length of parameter keysAndValues has to be multiple of 2.");
        }

        final BasicAttributes result = new BasicAttributes();
        for (int i = 0; i < keysAndValues.length; i+=2) {
            result.put(keysAndValues[i], keysAndValues[i + 1]);
        }
        return result;
    }

    /**
     * Assembles and LDAP query from field and value pairs.
     *
     * @param fieldsAndValues an array of Strings that represent key value pairs, consecutively (1st=key, 2nd=value, 3rd=key, 4th=value, etc.)
     * @return the String with <field1>=<value1>, <field2>=<value2> assignments.
     */
    public static LdapName createLdapName(final String... fieldsAndValues) {
        if (fieldsAndValues.length % 2 > 0) {
            LOG.error("Ldap Attributes: For field and value pairs the length of String array has to be multiple of 2!");
            throw new IllegalArgumentException("Length of parameter fieldsAndValues has to be multiple of 2.");
        }

        final List<Rdn> rdns = new ArrayList<Rdn>(fieldsAndValues.length >> 1);
        for (int i = 0; i < fieldsAndValues.length; i+=2) {

            try {
                final Rdn rdn = new Rdn(fieldsAndValues[i] + FIELD_ASSIGNMENT + fieldsAndValues[i + 1]);
                rdns.add(rdn);
            } catch (final InvalidNameException e) {
                // FIXME (bknerr) : missing dedicated exception for this layer -  LDAP soon obsolete
                e.printStackTrace();
            }
        }
        Collections.reverse(rdns);
        final LdapName name = new LdapName(rdns);
        return name;
    }
}
