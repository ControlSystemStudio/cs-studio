package org.csstudio.platform.internal.ldapauthorization;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.security.IAuthorizationProvider;
import org.csstudio.platform.security.Right;
import org.csstudio.platform.security.RightSet;
import org.csstudio.platform.security.User;
import org.eclipse.core.runtime.Preferences;


/**
 * Reads a user's roles and groups from an LDAP directory.
 * 
 * @author Joerg Rathlev
 */
public class LdapAuthorizationReader implements IAuthorizationProvider {

	private static final String CONFIG_FILE = "actionrights.conf";

	/* (non-Javadoc)
	 * @see org.csstudio.platform.internal.ldapauthorization.IAuthorizationProvider#getRights(org.csstudio.platform.security.User)
	 */
	public RightSet getRights(User user) {
		String username = user.getUsername();
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
			CentralLogger.getInstance().error(this,
					"Error reading authorization for user: " + user, e);
		}
		return rights;
	}
	

	/**
	 * Reads the rights associated with an action from a configuration file
	 * and returns the set of rights.
	 */
	public RightSet getRights(String actionId) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(CONFIG_FILE));
			
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				StringTokenizer tok = new StringTokenizer(line, " ");
				String name = tok.nextToken();
				if (name.equals(actionId)) {
					RightSet set = new RightSet(name);
					while (tok.hasMoreTokens()) {
						String text = tok.nextToken();
						text = text.substring(1, text.length()-1);
						
						String[] entries = text.split(",");
						Right recht = new Right(entries[0],entries[1]);
						set.addRight(recht);
					}
					return set;
				}
			}
		} catch (FileNotFoundException e) {
			// Currently, ignore this error. Using a configuration file for
			// the permissions is only a workaround until we have an LDAP-based
			// implementation, so it is ok if there is no configuration file.
		} catch (IOException e) {
			// This should be logged as a warning, not debug, but see above.
			CentralLogger.getInstance().debug(this,
					"Error reading rights associated with action: " + actionId, e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					CentralLogger.getInstance().warn(this,
							"Could not close input file.", e);
				}
			}
		}
		return null;
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
