/** Authentication and Authorization support
 *
 *  <p>Main entry point is {@link org.csstudio.security.SecuritySupport}
 *
 *  <h2>Authentication</h2>
 *
 *  Authentication uses JAAS and can be based on plain
 *  password files, LDAP, Kerberos, ...
 *
 *  <p>Eclipse preferences are used to configure
 *  <ol>
 *  <li>Path to JAAS config file (may be in a plugin)
 *  <li>Name of JAAS setting within the file that should be used
 *  </ol>
 *
 *  For examples, see <code>org.csstudio.security/jaas.conf</code>.
 *
 *  <p>Eclipse plugin.xml can be used to contribute additional
 *  JAAS {@link javax.security.auth.spi.LoginModule}s if needed.
 *
 *
 *  <h2>Authorization</h2>
 *
 *  Authorization is obtained from an {@link org.csstudio.security.authorization.AuthorizationProvider}:
 *
 *  <ol>
 *  <li> {@link org.csstudio.security.authorization.FileBasedAuthorizationProvider}: List users and their authorizations in a plain file.
 *  <li> {@link org.csstudio.security.authorization.LDAPGroupAuthorizationProvider}: Use LDAP groups as authorizations.
 *  <li> {@link org.csstudio.security.authorization.ScriptAuthorizationProvider}: Determine authorizations by calling external script.
 *  <li> {@link org.csstudio.security.authorization.AuthorizationProvider}: Allows adding custom authorization providers.
 *  </ol>
 *
 *  <p>Eclipse preferences select the method.
 *
 *  <h2>Secure Preferences</h2>
 *  <ol>
 *  <li> {@link org.csstudio.security.preferences.SecurePreferences}: Read and write secure preferences.
 *  </ol>
 *
 *
 *  @author Kay Kasemir - This implementation
 *  @author Kai Meyer, Torsten Witte, Alexander Will, Sven Wende, Stefan Hofer, Joerg Rathlev, Xihui Chen - Original org.csstudio.auth and *platform code
 */
package org.csstudio.security;