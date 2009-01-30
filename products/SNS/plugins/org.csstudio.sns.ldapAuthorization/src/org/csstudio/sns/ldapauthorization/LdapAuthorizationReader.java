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
 package org.csstudio.sns.ldapauthorization;
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

import org.csstudio.apptuil.securestorage.SecureStorage;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.security.IAuthorizationProvider;
import org.csstudio.platform.security.Right;
import org.csstudio.platform.security.RightSet;
import org.csstudio.platform.security.User;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.osgi.util.NLS;


/**
 * Reads a user's roles and groups from an LDAP directory, and the rights
 * associated with actions from a configuration file.
 * 
 * @author Joerg Rathlev
 * @author Xihui Chen
 */
public class LdapAuthorizationReader implements IAuthorizationProvider {

	/**
	 * Map of the rights associated with actions.
	 */
	private Map<String, RightSet> actionsrights;

	/* (non-Javadoc)
	 * @see org.csstudio.platform.internal.ldapauthorization.IAuthorizationProvider#getRights(org.csstudio.platform.security.User)
	 */
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
			ctrls.setReturningAttributes(new String[] {"memberUid"});
			ctrls.setReturningObjFlag(true);
			ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			
			// String filter = "(memberUid=" + username + ')'; //$NON-NLS-1$  
			String filter = NLS.bind("(memberUid={0})", username); //$NON-NLS-1$  
			
			NamingEnumeration<SearchResult> results =
				ctx.search("ou=CSSGroupRole", filter, ctrls);
			while (results.hasMore()) {
				SearchResult r = results.next();
				rights.addRight(parseSearchResult(r));
			}
		} catch (NamingException e) {
			CentralLogger.getInstance().error(this,
					"Error reading authorization for user: " + user, e);
		}
		return rights;
	}
	


	/* (non-Javadoc)
	 * @see org.csstudio.platform.security.IAuthorizationProvider#getRights(java.lang.String)
	 */
	public RightSet getRights(String authId) {
		synchronized (this) {
			if (actionsrights == null) {
				loadActionRightsFromLdap();
			}
		}
		return actionsrights.get(authId);
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
			
			String filter = "(objectClass=cssAuthorizeID)";
			NamingEnumeration<SearchResult> results =
				ctx.search("ou=CSSAuthorizeID", filter, ctrls);
			while (results.hasMore()) {
				SearchResult r = results.next();
				Attributes attributes = r.getAttributes();
				Attribute auid = attributes.get("auid");
				String authId = (String) auid.get();
				Attribute groupRoleAttr = attributes.get("cssGroupRole");
				
				RightSet rights = new RightSet(authId);
				parseGroupRoleAttr(groupRoleAttr, rights);
				actionsrights.put(authId, rights);
		
			}
			
			CentralLogger.getInstance().debug(this, "Authorization " +
					"information successfully loaded from LDAP directory.");
			
		} catch (NamingException e) {
			CentralLogger.getInstance().error(this,
					"Error loading authorization information from LDAP directory.", e);
		}
	}
	
	/**
	 * Parses cssGroupRole attribute into rights
	 * @param groupRoleAttr
	 * @param rights
	 * @throws NamingException
	 */
	@SuppressWarnings("unchecked")
	private void parseGroupRoleAttr(Attribute groupRoleAttr, RightSet rights)
		throws NamingException 
	{	
		for(NamingEnumeration ae = groupRoleAttr.getAll(); ae.hasMore(); ) {
			String grp = (String) ae.next();
			//each cssGroupRole attribute has a format of (group, role);
			String group = grp.substring(1, grp.indexOf(",")).trim();
			String role = grp.substring(grp.indexOf(",")+1, grp.indexOf(")")).trim();
			rights.addRight(new Right(role, group));			
		}		
	}


	/**
	 * Parses a search result into a right.
	 * @param r the LDAP search result.
	 */
	private Right parseSearchResult(SearchResult r) {
		String n = r.getName();
		String group = n.substring(n.indexOf("ou=")+3);
		String role = n.substring(n.indexOf("cn=")+3, n.indexOf(",ou="));
		return new Right(role, group);
	}

	/**
	 * Creates the environment for the LDAP connection.
	 */
	private Hashtable<String, String> createEnvironment() {
		Preferences prefs = Activator.getDefault().getPluginPreferences();
		String url = prefs.getString(PreferenceConstants.LDAP_URL);
		String user = SecureStorage.retrieveSecureStorage(
				Activator.PLUGIN_ID, PreferenceConstants.LDAP_USER);
		if(user == null)
			user = prefs.getString(PreferenceConstants.LDAP_USER);
		String password = SecureStorage.retrieveSecureStorage(
				Activator.PLUGIN_ID, PreferenceConstants.LDAP_PASSWORD);
		if(password == null)
			password = prefs.getString(PreferenceConstants.LDAP_PASSWORD);
		
		Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_PRINCIPAL, user);
		env.put(Context.SECURITY_CREDENTIALS, password);
		return env;
	}
}
