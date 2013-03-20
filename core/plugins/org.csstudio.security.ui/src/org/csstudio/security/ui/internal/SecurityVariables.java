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
    
    final private Map<String, Object> variables = new HashMap<>();

    /** Initialize */
    public SecurityVariables()
    {
        final Boolean authenticated = SecuritySupport.getSubject() != null;
        variables.put(AUTHENTICATED, authenticated);
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
        return new String[] { AUTHENTICATED };
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
    public void changedSecurity(final Subject subject, final Authorizations authorizations)
    {
        final Boolean authenticated = subject != null;
        variables.put(AUTHENTICATED, authenticated);
        fireSourceChanged(ISources.WORKBENCH, AUTHENTICATED, authenticated);
        
        // TODO Update another variable that holds list of authorizations
    }
}
