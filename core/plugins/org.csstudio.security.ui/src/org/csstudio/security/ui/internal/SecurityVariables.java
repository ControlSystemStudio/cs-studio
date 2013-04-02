package org.csstudio.security.ui.internal;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import org.csstudio.security.SecurityListener;
import org.csstudio.security.SecuritySupport;
import org.csstudio.security.authorization.Authorizations;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

/** Provide variables to be used in Eclipse Core Expressions
 *  to enable/disable UI items
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SecurityVariables extends AbstractSourceProvider implements SecurityListener
{
    final public static String AUTHENTICATED = "org.csstudio.security.ui.authenticated";
    final public static String CURRENT_USER = "org.csstudio.security.ui.current_user";
    
    final private Map<String, Object> variables = new HashMap<>();

    /** Initialize */
    public SecurityVariables()
    {
        final Boolean authenticated = SecuritySupport.getSubject() != null;
        variables.put(AUTHENTICATED, authenticated);
        variables.put(CURRENT_USER, SecuritySupport.isCurrentUser());
        SecuritySupport.addListener(this);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    public Map getCurrentState()
    {
        return variables;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getProvidedSourceNames()
    {
        return new String[] { AUTHENTICATED, CURRENT_USER };
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        SecuritySupport.removeListener(this);
        variables.clear();
    }

    /** Update variables as security info changes */
    @Override
    public void changedSecurity(final Subject subject,
            final boolean is_current_user, final Authorizations authorizations)
    {
        final Boolean authenticated = subject != null;
        variables.put(AUTHENTICATED, authenticated);
        variables.put(CURRENT_USER, is_current_user);
        fireSourceChanged(ISources.WORKBENCH, AUTHENTICATED, authenticated);
        fireSourceChanged(ISources.WORKBENCH, CURRENT_USER, is_current_user);
    }
}
