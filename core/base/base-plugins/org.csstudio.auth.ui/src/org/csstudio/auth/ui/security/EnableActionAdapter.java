package org.csstudio.auth.ui.security;

import org.csstudio.auth.security.IActivationAdapter;
import org.eclipse.jface.action.IAction;

public class EnableActionAdapter implements IActivationAdapter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate(final Object o, final boolean activate) {
        if (o instanceof IAction) {
            ((IAction)o).setEnabled(activate);
        }
    }

}
