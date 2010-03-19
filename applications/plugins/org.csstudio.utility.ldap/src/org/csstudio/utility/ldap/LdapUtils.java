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
package org.csstudio.utility.ldap;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.util.StringUtil;

/**
 * Constants class for LDAP entries.
 * 
 * @author bknerr
 * @version $Revision$
 * @since 11.03.2010
 */
public class LdapUtils {
    
    private static final Logger LOG = CentralLogger.getInstance().getLogger(LdapUtils.class);
    
    public static final String LDAP_OU_FIELD_NAME = "ou";
    
    public static final String EPICS_CTRL_FIELD_VALUE = "EpicsControls";
    public static final String FIELD_SEPARATOR = ",";
    
    public static final String FIELD_ASSIGNMENT = "=";
    public static final String FIELD_WILDCARD = "*";
    public static final String EREN_FIELD_NAME = "eren";
    
    
    public static final String EFAN_FIELD_NAME = "efan";
    public static final String ECON_FIELD_NAME = "econ";
    public static final String ECOM_FIELD_NAME = "ecom";
    public static final String ECOM_FIELD_VALUE = "EPICS-IOC";
    public static final String LDAP_ATTR_FIELD_OBJECT_CLASS = "objectClass";
    
    public static final String LDAP_ATTR_VAL_OBJECT_CLASS = "epicsRecord";
    public static final String[] FORBIDDEN_SUBSTRINGS = new String[] {
        "/","\\","+","@"
    };
    
    
    /**
     * Returns a filter for 'any' match of the field name (e.g. '<fieldName>=*').
     * @param fieldName the field to match any
     * @return
     */
    public static final String any(final String fieldName) {
        return fieldName + FIELD_ASSIGNMENT + FIELD_WILDCARD;
    }
    
    /**
     * Returns the attributes for a new entry with the given object class and
     * name.
     * 
     * @param an array of Strings that represent key value pairs, consecutively (1st=key, 2nd=value, 3rd=key, 4th=value, etc.)
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
     * @param fieldsAndValues
     * @return the String with <field1>=<value1>, <field2>=<value2> assignements.
     */
    public static String createLdapQuery(final String... fieldsAndValues) {
        if (fieldsAndValues.length % 2 > 0) {
            LOG.error("Ldap Attributes: For field and value pairs the length of String array has to be multiple of 2!");
            throw new IllegalArgumentException("Length of parameter fieldsAndValues has to be multiple of 2.");
        }
        
        final StringBuilder query = new StringBuilder();
        for (int i = 0; i < fieldsAndValues.length; i+=2) {
            query.append(fieldsAndValues[i]).append(FIELD_ASSIGNMENT).append(fieldsAndValues[i + 1])
            .append(FIELD_SEPARATOR);
        }
        if (query.length() >= 1) {
            query.delete(query.length() - 1, query.length());
        }
        
        return query.toString();
    }
    
    /**
     * Filters for forbidden substrings {@link LdapUtils}.
     * @param recordName the name to filter
     * @return true, if the forbidden substring is contained, false otherwise (even for empty and null strings)
     */
    public static boolean filterLDAPNames(final String recordName) {
        if (!StringUtil.hasLength(recordName)) {
            return false;
        }
        for (final String s : FORBIDDEN_SUBSTRINGS) {
            if (recordName.contains(s)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Don't instantiate.
     */
    private LdapUtils() {
        // Empty.
    }
    
}
