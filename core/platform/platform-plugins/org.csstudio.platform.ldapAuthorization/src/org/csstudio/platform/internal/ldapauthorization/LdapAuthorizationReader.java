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
 package org.csstudio.platform.internal.ldapauthorization;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.auth.security.IAuthorizationProvider;
import org.csstudio.auth.security.Right;
import org.csstudio.auth.security.RightSet;
import org.csstudio.auth.security.User;
import org.eclipse.core.runtime.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Reads a user's roles and groups from an LDAP directory, and the rights
 * associated with actions from a configuration file.
 * 
 * @author Joerg Rathlev
 */
public class LdapAuthorizationReader implements IAuthorizationProvider {
    
    private static final Logger LOG = LoggerFactory.getLogger(LdapAuthorizationReader.class);

	/**
	 * Map of the rights associated with actions.
	 */
	private Map<String, RightSet> actionsrights;

	/* (non-Javadoc)
	 * @see org.csstudio.platform.internal.ldapauthorization.IAuthorizationProvider#getRights(org.csstudio.platform.security.User)
	 */
	@Override
    public RightSet getRights(User user) {
		String username = user.getUsername();
		// If the user was authenticated via Kerberos, the username may be a
		// fully qualified name (name@EXAMPLE.COM). We only want the first
		// part of the name.
		if (username.contains("@")) {
			username = username.substring(0, username.indexOf('@'));
		}
		
		RightSet rights = new RightSet("LDAP Rights");
		try {
			DirContext ctx = new InitialDirContext(createEnvironment());
			
			SearchControls ctrls = new SearchControls();
			ctrls.setReturningAttributes(new String[] {"associatedName"});
			ctrls.setReturningObjFlag(true);
			ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			
			String filter = "(associatedName=eaun=" + username + ")";
			NamingEnumeration<SearchResult> results =
				ctx.search("ou=Css,ou=EpicsAuthorize", filter, ctrls);
			while (results.hasMore()) {
				SearchResult r = results.next();
				rights.addRight(parseSearchResult(r));
			}
		} catch (NamingException e) {
			LOG.error("Error reading authorization for user: {}", user,
					e);
		}
		return rights;
	}
	

	/**
	 * <p>Reads the rights associated with an action from a configuration file
	 * and returns the set of rights. The configuration file is expected to be
	 * in Java properties file format, with the action IDs as keys and the
	 * rights associated with each action as its respective value. Sets of
	 * rights can be specified as whitespace-separated lists of rights. Each
	 * right is written as <code>(role, group)</code>.</p>
	 * 
	 * <p>The following is an example of a configuration file that grants the
	 * right to use action1 to developers and admins in group1, and the right
	 * to use action2 to admins in group1 and in group2:</p>
	 * 
	 * <pre>
	 * action1 = (developer, group1) (admin, group1)
	 * action2 = (admin, group1) (admin, group2)
	 * </pre>
	 * 
	 * <p>Syntax errors in configuration files are ignored.</p>
	 */
	@Override
    public RightSet getRights(String actionId) {
		synchronized (this) {
			if (actionsrights == null) {
				loadActionRightsFromLdap();
			}
		}
		return actionsrights.get(actionId);
	}
	
	/**
	 * Loads the actions' rights from LDAP.
	 */
	private void loadActionRightsFromLdap() {
		actionsrights = new HashMap<String, RightSet>();
		
		try {
			DirContext ctx = new InitialDirContext(createEnvironment());
			
			SearchControls ctrls = new SearchControls();
			ctrls.setReturningObjFlag(false);
			ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			
			String filter = "(objectClass=epicsAuthIdGR)";
			NamingEnumeration<SearchResult> results =
				ctx.search("ou=EpicsAuthorizeID", filter, ctrls);
			while (results.hasMore()) {
				SearchResult r = results.next();
				Attributes attributes = r.getAttributes();
				Attribute eainAttr = attributes.get("eain");
				String authId = (String) eainAttr.get();
				Attribute eaigAttr = attributes.get("eaig");
				String group = (String) eaigAttr.get();
				Attribute eairAttr = attributes.get("eair");
				String role = (String) eairAttr.get();
				
				RightSet rights = actionsrights.get(authId);
				if (rights == null) {
					rights = new RightSet(authId);
					actionsrights.put(authId, rights);
				}
				rights.addRight(new Right(role, group));
			}
			
			LOG.debug("Authorization information successfully loaded from LDAP directory.");
			
		} catch (NamingException e) {
			LOG.error("Error loading authorization information from LDAP directory.",
					e);
		}
	}
	
	/**
	 * Parses a search result into a right.
	 * @param r the LDAP search result.
	 */
	private Right parseSearchResult(SearchResult r) {
		String n = r.getName();
		String group = n.substring(n.indexOf("ou=")+3);
		String role = n.substring(n.indexOf("eagn=")+5, n.indexOf(",ou="));
		return new Right(role, group);
	}

	/**
	 * Creates the environment for the LDAP connection.
	 */
	private Hashtable<String, String> createEnvironment() {
		Preferences prefs = Activator.getDefault().getPluginPreferences();
		String url = prefs.getString(PreferenceConstants.LDAP_URL);
		String user = prefs.getString(PreferenceConstants.LDAP_USER);
		String password = prefs.getString(PreferenceConstants.LDAP_PASSWORD);
		
		Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_PRINCIPAL, user);
		env.put(Context.SECURITY_CREDENTIALS, password);
		return env;
	}
}
