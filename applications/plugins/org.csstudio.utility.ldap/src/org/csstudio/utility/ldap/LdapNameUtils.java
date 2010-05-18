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
package org.csstudio.utility.ldap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;


/**
 * Utility functions for working with names from an LDAP directory.
 *
 * @author Joerg Rathlev, Jurij Kodre
 */
public final class LdapNameUtils {

    private static final Logger LOG = CentralLogger.getInstance().getLogger(LdapNameUtils.class);
    /**
     * Constructor.
     */
    private LdapNameUtils() {
        // Empty
    }

    /**
     * Static Parser Holder.
     *
     * @author bknerr
     * @author $Author$
     * @version $Revision$
     * @since 05.05.2010
     */
    private static final class ParserHolder {

        @CheckForNull
        private static NameParser createParser() {
            try {
                return Engine.getInstance().getLdapDirContext().getNameParser(new CompositeName());
            } catch (final NamingException e) {
                LOG.error("LDAP NameParser could not be created.", e);
            }
            return null;
        }

        private static final NameParser PARSER = createParser();
        /**
         * Don't instantiate.
         */
        private ParserHolder() {
            // EMPTY
        }
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

    /**
     * Returns the simple name of the given name.
     *
     * @param name
     *            the name.
     * @return the simple name.
     * @deprecated This method only works for names which do not contain any
     *             special characters that need escaping. Use
     *             {@link #simpleName(LdapName)} instead.
     */
    @Deprecated
    public static String simpleName(final String name){
        final int pos1 = name.indexOf("=");
        int pos2= name.indexOf(",");
        if (pos2 ==-1 ) {
            //if comma is not present, we must take last character
            pos2=name.length();
        }
        return name.substring(pos1+1,pos2);
    }

    /**
     * Returns the simple name of an object identified by the given LDAP name.
     * The simple name is the value of the least significant Rdn.
     *
     * @param name
     *            the LDAP name.
     * @return the simple name.
     */
    @Nonnull
    public static String simpleName(@Nonnull final LdapName name) {
        return (String) name.getRdn(name.size() - 1).getValue();
    }


    @CheckForNull
    public static String getValueOfRdnType(@Nonnull final LdapName ldapName, @Nonnull final String rdnType) {

        for (final Rdn rdn : ldapName.getRdns()) {
            if (rdn.getType().endsWith(rdnType)) {
                return (String) rdn.getValue();
            }
        }
        return null;
    }


    /**
     * Parses a given SearchResult entry from LDAP into an LdapName object.
     * @param row a search result row
     * @return the ldap composite name
     * @throws NamingException
     */
    @CheckForNull
    public static LdapName parseSearchResult(@Nonnull final SearchResult row) throws NamingException {
        if (ParserHolder.PARSER == null) {
            return null;
        }
        return (LdapName) ParserHolder.PARSER.parse(row.getNameInNamespace());
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
     * Removes those Rdns starting from direction that start with the given string.
     * (excluding) the very rdn.
     *
     * @param fullName the LDAP name to be modified
     * @param fieldNamePrefix the field
     * @param dir forward or backward
     * @return a new name object, might be empty if the string could not be found
     * @throws InvalidNameException
     */
    @Nonnull
    public static LdapName removeRdns(@Nonnull final LdapName fullName,
                                      @Nonnull final String fieldNamePrefix,
                                      @Nonnull final Direction dir) throws InvalidNameException {
        final LdapName name = new LdapName(fullName.getRdns());
        switch (dir) {
            case FORWARD :
                while ((name.size() > 0) && !name.get(0).startsWith(fieldNamePrefix)) {
                    name.remove(0);
                }
                break;
            case BACKWARD :
                while ((name.size() > 0) && !name.get(name.size() - 1).startsWith(fieldNamePrefix)) {
                    name.remove(name.size() - 1);
                }
                break;
            default:
        }

        return name;
    }
}
