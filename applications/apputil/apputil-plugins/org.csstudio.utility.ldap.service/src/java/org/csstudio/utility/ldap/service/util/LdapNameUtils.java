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
 *
 */
package org.csstudio.utility.ldap.service.util;

import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.FORBIDDEN_SUBSTRINGS;

import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility functions for working with names from an LDAP directory.
 *
 * @author Joerg Rathlev, Jurij Kodre
 * @author Bastian Knerr
 */
public final class LdapNameUtils {

    private static final Logger LOG = LoggerFactory.getLogger(LdapNameUtils.class);
    /**
     * Constructor.
     */
    private LdapNameUtils() {
        // Empty
    }

    /**
     * Returns the simple name of an object identified by the given LDAP name.
     * The simple name is the value of the least significant Rdn.
     * Returns <code>null</code> if simple name parsing failed.
     *
     * @param name
     *            the LDAP name.
     * @return the simple name.
     */
    public static String simpleName(final String name) {
        LdapName ldapName;
        try {
            ldapName = new LdapName(name);
            return LdapNameUtils.simpleName(ldapName);
        } catch (final InvalidNameException e) {
            LOG.warn("LDAP Name cannot be parsed for simple name. String '{}' is not a valid LdapName form", name);
        }
        return null;
    }

    /**
     * Returns the simple name of an object identified by the given LDAP name.
     * The simple name is the value of the least significant Rdn.
     *
     * @param name
     *            the LDAP name.
     * @return the simple name.
     */
    public static String simpleName(final LdapName name) {
        return (String) name.getRdn(name.size() - 1).getValue();
    }


    public static String getValueOfRdnType(final LdapName ldapName, final String rdnType) {

        for (final Rdn rdn : ldapName.getRdns()) {
            if (rdn.getType().endsWith(rdnType)) {
                return (String) rdn.getValue();
            }
        }
        return null;
    }

    /**
     * A direction indicator for some of this class' modification methods.
     *
     * @author bknerr
     * @author $Author$
     * @version $Revision$
     * @since 07.05.2010
     */
    public enum Direction {
      FORWARD,
      BACKWARD;
    }

    /**
     * Removes all those Rdns starting from the given direction that start with the given string.
     * (excluding) the matching rdn.
     *
     * @param fullName the LDAP name to be modified
     * @param fieldNamePrefix the field
     * @param dir forward or backward
     * @return a new name object, might be empty if the string could not be found
     * @throws InvalidNameException
     */
    public static LdapName removeRdns(final LdapName fullName,
                                      final String fieldNamePrefix,
                                      final Direction dir) throws InvalidNameException {
        final LdapName name = new LdapName(fullName.getRdns());
        switch (dir) {
            case FORWARD :
                while (name.size() > 0 && !name.get(0).startsWith(fieldNamePrefix)) {
                    name.remove(0);
                }
                break;
            case BACKWARD :
                while (name.size() > 0 && !name.get(name.size() - 1).startsWith(fieldNamePrefix)) {
                    name.remove(name.size() - 1);
                }
                break;
            default:
        }

        return name;
    }

    /**
     * Creates and returns a copy of the given LDAP name and removes the list of given Rdns from the
     * copy.
     * If a given Rdn is not contained in the LDAP name, nothing happens.
     *
     * @param the name
     * @param removeRdns the rdns to be removed
     * @return the newly created (shortened) name
     * @throws InvalidNameException
     */
    public static LdapName removeRdns(final LdapName name,
                                      final List<Rdn> removeRdns) throws InvalidNameException {


        final LdapName result = new LdapName("");
        for (final Rdn nameRdn : name.getRdns()) {
            boolean add = true;
            for (final Rdn removeRdn : removeRdns) {
                if (nameRdn.equals(removeRdn)) {
                    add = false;
                    break;
                }
            }
            if (add) {
                result.add(nameRdn);
            }
        }

        return result;
    }

    /**
     * Removes the last or simple name from the ldap name.
     * Always returning
     * @param iocLdapName
     * @return
     */
    public static LdapName baseName(final LdapName fullName) {
        if (fullName.size() > 0) {
            return (LdapName) fullName.getPrefix(fullName.size()-1);
        }
        return fullName;
    }


    /**
     * Filters for forbidden substrings {@link LdapUtils}.
     * @param recordName the name to filter
     * @return true, if the forbidden substring is contained, false otherwise (even for empty and null strings)
     */
    public static boolean filterName(final String recordName) {
        if ("".equals(recordName)) {
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
     * Removes double quotes from a string.
     *
     * @param toClean
     *            the string to be cleaned.
     * @return the cleaned string.
     * @deprecated This method is a hack to work with JNDI composite names, but
     *             it only works for names which do not contain any special
     *             characters that need escaping. Use JNDI correctly instead.
     */
    @Deprecated
    public static String removeQuotes(final String toClean) {
        final StringBuffer tc = new StringBuffer(toClean);
        final String grr = "\"";
        int pos = tc.indexOf(grr);
        while (pos>-1){
            tc.deleteCharAt(pos);
            pos = tc.indexOf(grr);
        }
        return tc.toString();
    }
}
