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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

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
 * Reads a user's roles and groups from an LDAP directory, and the rights
 * associated with actions from a configuration file.
 * 
 * @author Joerg Rathlev
 */
public class LdapAuthorizationReader implements IAuthorizationProvider {

	/**
	 * The name of the configuration file from which rights associated with
	 * actions are read.
	 */
	private static final String CONFIG_FILE = "actionrights.conf";
	
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
	public RightSet getRights(String actionId) {
		if (actionsrights == null) {
			loadActionRights();
		}
		return actionsrights.get(actionId);
	}
	
	/**
	 * Loads the configuration file with the actions' rights.
	 */
	private void loadActionRights() {
		actionsrights = new HashMap<String, RightSet>();
		InputStream input = null;
		try {
			input = new FileInputStream(CONFIG_FILE);
			Properties p = new Properties();
			p.load(input);
			
			for (Enumeration<?> e = p.propertyNames(); e.hasMoreElements(); ) {
				String actionId = (String) e.nextElement();
				RightSet rights = RightsParser.parseRightSet(p.getProperty(actionId), actionId);
				actionsrights.put(actionId, rights);
			}
		} catch (FileNotFoundException fnfe) {
			CentralLogger.getInstance().info(this,
					"Rights configuration file could not be found. Creating a sample file.");
			PrintStream out = null;
			try {
				out = new PrintStream(CONFIG_FILE);
				out.println("# Rights configuration file for the Control System Studio");
				out.println("# ");
				out.println("# Rights are configured using the following syntax:");
				out.println("# ");
				out.println("# action = (role, group) ...");
				out.println("# ");
				out.println("# where action is the id of the action, and role and group are");
				out.println("# the role and group of the users that are granted permission to");
				out.println("# execute actions with the given id. To grant permission to more");
				out.println("# than one role group combination, specify multiple role group");
				out.println("# combinations separated by white space.");
				out.println("");
				out.println("# Example entry:");
				out.println("example = (admin, css) (developer, css)");
			} catch (IOException e) {
				CentralLogger.getInstance().warn(this,
						"Error creating sample rights configuration file.", e);
			} finally {
				if (out != null) {
					out.close();
				}
			}
		} catch (IOException e) {
			// Currently, ignore this error. Using a configuration file for
			// the permissions is only a workaround until we have an LDAP-based
			// implementation, so it is ok if the file cannot be read.
			CentralLogger.getInstance().debug(this,
					"Error reading rights associated with actions.", e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					CentralLogger.getInstance().warn(this,
							"Could not close input file.", e);
				}
			}
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
