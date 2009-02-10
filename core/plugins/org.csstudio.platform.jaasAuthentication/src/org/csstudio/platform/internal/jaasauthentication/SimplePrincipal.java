package org.csstudio.platform.internal.jaasauthentication;

import java.security.Principal;

/** Simple JAAS Principal that holds user name
 *  <p>
 *  Based on DummyPrincipal that used to be in DummyLoginModule
 *  @author Joerg Rathlev
 *  @autho<b>r ky9</b>
 */
public class SimplePrincipal implements Principal
{
    final private String username;
        
    public SimplePrincipal(final String username)
    {
        this.username = username;
    }
        
    public String getName()
    {
        return username;
    }
        
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof SimplePrincipal)
            return ((SimplePrincipal) o).username.equals(username);
        else
            return false;
    }
    
    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
