/*******************************************************************************
 * Copyright (c) 2008,2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.authorization;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.security.auth.Subject;

import org.csstudio.security.SecurityPreferences;
import org.csstudio.security.SecuritySupport;

import com.sun.security.auth.module.LdapLoginModule;

/** AuthorizationProvider based on LDAP group membership.
 * 
 *  <p>Performs an LDAP lookup of the user's group membership.
 *  The LDAP directory must support the <code>posixGroup</code> schema.
 *  
 *  <p>Example LDAP entry:
 *  <pre>
 *  dn: cn=archive_config,ou=Groups,dc=example,dc=com
 *  objectClass: top
 *  objectClass: posixGroup
 *  cn: archive_config
 *  description: Allow archive configuration
 *  gidNumber: 1234
 *  memberUid: fred
 *  memberUid: jane
 *  </pre>
 *  
 *  <p>The above entry defines a group "archive_config" with members
 *  "fred" and "jane".
 *  The {@link LDAPGroupAuthorizationProvider} will treat that
 *  as granting the "archive_config" authorization to users
 *  "fred" and "jane".
 *  
 *  <p>Note that members must specifically be listed via <code>memberUid</code>.
 *  In the above example, there may be a user with primary group ID <code>1234</code>
 *  that Linux would also consider to be a member of the "archive_config"
 *  Linux group, but for authorization purposes such a user must also be
 *  listed via <code>memberUid</code>. The numeric group ID is not used
 *  for authorization.
 *  
 *  <p>See comments in {@link LdapLoginModule} for Certificate Issues.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LDAPGroupAuthorizationProvider implements AuthorizationProvider
{
    final private String ldap_url;
    final private String group_base;
    
    /** Initialize based on Eclipse preferences */
    public LDAPGroupAuthorizationProvider()
    {
    	this(SecurityPreferences.getLDAPGroupURL(),
    	     SecurityPreferences.getLDAPGroupBase());
    }

    /** Initialize
     *  @param ldap_url URL of LDAP for authorization
     *  @param group_base Base DN for locating groups of a user
     */
    public LDAPGroupAuthorizationProvider(final String ldap_url, final String group_base)
    {
        this.ldap_url = ldap_url;
        this.group_base = group_base;
    }

    @Override
    public Authorizations getAuthorizations(final Subject user) throws Exception
    {
        final Logger logger = Logger.getLogger(getClass().getName());
        final Set<String> authorizations = new HashSet<>();
        final DirContext context = connect();
        try
        {
            // Search 'sub'
            final SearchControls ctrls = new SearchControls();
            ctrls.setReturningObjFlag(false);
            ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            // LDAP 'posixGroup' schema uses 'memberUid' to list group members
            final String filter_format = "(memberUid={0})";
            // Search all group
            final String name = SecuritySupport.getSubjectName(user);
            logger.log(Level.FINE, "Authorization lookup for {0}", name);
            final String filter = MessageFormat.format(filter_format, name);
            final NamingEnumeration<SearchResult> results =
                    context.search(group_base, filter, ctrls);
            while (results.hasMore())
            {
                final SearchResult r = results.next();
                logger.log(Level.FINE, "Authorization entry {0}", r);

                // LDAP 'posixGroup' schema uses 'cn' to name the group
                final String authorization = r.getAttributes().get("cn").get().toString();
                logger.log(Level.FINE, "Found: '" + authorization +  "'");
                authorizations.add(authorization);
            }
        }
        finally
        {
            context.close();
        }
        return new Authorizations(authorizations);
    }
    
    /** Connect to LDAP
     *  @return DirContext
     *  @throws Exception on error
     */
    private DirContext connect() throws Exception
    {
        final Hashtable<String, String> settings = new Hashtable<String, String>();
        settings.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        settings.put(Context.PROVIDER_URL, ldap_url);
        settings.put(Context.SECURITY_AUTHENTICATION, "none");
        // settings.put(Context.SECURITY_PRINCIPAL, ldap_user);
        // settings.put(Context.SECURITY_CREDENTIALS, ldap_password);
        
        return new InitialDirContext(settings);
    }
}
