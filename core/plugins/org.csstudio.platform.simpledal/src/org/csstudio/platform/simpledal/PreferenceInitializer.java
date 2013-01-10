package org.csstudio.platform.simpledal;

import org.csstudio.platform.SimpleDalPluginActivator;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
    
    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences node = new DefaultScope().getNode(SimpleDalPluginActivator.ID);
        initializeControlSystemPreferences(node);
    }
 
    
    /**
     * Initializes the control system settings.
     * @param node
     *            the preferences node to use
     */
    private void initializeControlSystemPreferences(final IEclipsePreferences node) {
        node.put(ProcessVariableAdressFactory.PROP_CONTROL_SYSTEM, ControlSystemEnum.EPICS.name());
        node.putBoolean(ProcessVariableAdressFactory.PROP_ASK_FOR_CONTROL_SYSTEM, true);
    }


}
