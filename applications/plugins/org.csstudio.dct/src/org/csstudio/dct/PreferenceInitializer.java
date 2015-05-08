package org.csstudio.dct;

import static org.csstudio.dct.PreferenceSettings.DATALINK_FUNCTION_PARAMETER_3_PROPOSAL;
import static org.csstudio.dct.PreferenceSettings.DATALINK_FUNCTION_PARAMETER_4_PROPOSAL;
import static org.csstudio.dct.PreferenceSettings.FIELD_DESCRIPTION_SHOW_DESCRIPTION;
import static org.csstudio.dct.PreferenceSettings.*;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Preference initializer for the DCT.
 *
 * @author Sven Wende
 *
 */
public final class PreferenceInitializer extends AbstractPreferenceInitializer {

    /**
     *{@inheritDoc}
     */
    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences node = new DefaultScope().getNode(DctActivator.PLUGIN_ID);

        node.putBoolean(FIELD_DESCRIPTION_SHOW_DESCRIPTION.name(), true);
        node.putBoolean(FIELD_DESCRIPTION_SHOW_INITIAL_VALUE.name(), true);
        node.put(DATALINK_FUNCTION_PARAMETER_3_PROPOSAL.name(), "NMS,PP,CNPP,CPP");
        node.put(DATALINK_FUNCTION_PARAMETER_4_PROPOSAL.name(), "NMS,MS");

        node.put(SENSOR_ID_SERVICE_ID.name(), "");
        node.put(IO_NAME_SERVICE_ID.name(), "");

    }

}
