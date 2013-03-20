/** Authentication and Authorization support
 * 
 *  <p>Main entry point is {@link org.csstudio.security.SecuritySupport}
 *  
 *  <h2>Authentication</h2>
 *  
 *  Authentication is based on JAAS and can be based on plain
 *  password files, LDAP, Kerberos, ...
 *  
 *  <p>Eclipse preferences are used to configue
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
 *  <li> {@link org.csstudio.security.authorization.FileBasedAuthorizationProvider}: List users and their authorizations in a plain file
 *  <li> TODO Make authorization pluggable
 *  <li> TODO LDAP-based, using Linux group membership
 *  <li> TODO LDAP-based, using custom schema
 *  </ol>
 *  
 *  @author Kay Kasemir - This implementation
 *  @author Kai Meyer, Torsten Witte, Alexander Will, Sven Wende, Stefan Hofer, Xihui Chen - Original org.csstudio.auth and *platform code
 */
package org.csstudio.security;