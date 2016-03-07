package org.csstudio.sds.ui.internal.preferences;

import org.csstudio.auth.security.ActivationService;
import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class AllowWriteAccessPreferenceListener implements IPropertyChangeListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(PreferenceConstants.PROP_WRITE_ACCESS_DENIED)) {
            ActivationService.getInstance().handleRightsManagementEvent(null);
        }
    }

}
