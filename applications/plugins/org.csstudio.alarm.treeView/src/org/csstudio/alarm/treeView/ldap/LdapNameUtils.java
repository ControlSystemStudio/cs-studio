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

import java.util.HashMap;
import java.util.Map;

import javax.naming.ldap.LdapName;

import org.csstudio.alarm.treeView.model.ObjectClass;


/**
 * Utility functions for working with names from an LDAP directory.
 * 
 * @author Joerg Rathlev, Jurij Kodre
 */
public final class LdapNameUtils {
    
    private static final Map<String, ObjectClass> objectClass;
    static {
        objectClass = new HashMap<String, ObjectClass>();
        objectClass.put("efan", ObjectClass.FACILITY);
        objectClass.put("ecom", ObjectClass.COMPONENT);
        objectClass.put("esco", ObjectClass.SUBCOMPONENT);
        objectClass.put("econ", ObjectClass.IOC);
        objectClass.put("eren", ObjectClass.RECORD);
    }
    
    /**
     * Constructor.
     */
    private LdapNameUtils() {
        // Empty
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
        if (pos2 ==-1 ) {pos2=name.length();} //if comma is not present, we must take last character
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
    public static String simpleName(final LdapName name) {
        return (String) name.getRdn(name.size() - 1).getValue();
    }
    
    /**
     * Returns the object class of an LDAP name.
     * 
     * @param name
     *            the name.
     * @return the object class.
     */
    public static ObjectClass objectClass(final LdapName name) {
        return objectClass.get(name.getRdn(name.size() - 1).getType());
    }
}
