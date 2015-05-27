package de.desy.language.editor.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

import de.desy.language.editor.ui.EditorUIActivator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        final IEclipsePreferences node = DefaultScope.INSTANCE
                .getNode(EditorUIActivator.PLUGIN_ID);

        String value = StringConverter.asString(new RGB(230,230,255));
        node.put(PreferenceConstants.CURSOR_LINE_COLOR.getPreferenceStoreId(), value);
        node.put(PreferenceConstants.CURSOR_LINE_ENABLE.getPreferenceStoreId(), Boolean.toString(true));
        value = StringConverter.asString(new RGB(230,230,230));
        node.put(PreferenceConstants.MATCHING_CHARACTER_COLOR.getPreferenceStoreId(), value);
        node.put(PreferenceConstants.MATCHING_CHARACTER_ENABLE.getPreferenceStoreId(), Boolean.toString(true));
        value = StringConverter.asString(new RGB(0,0,0));
        node.put(PreferenceConstants.MARGIN_PAINTER_COLOR.getPreferenceStoreId(), value);
        node.put(PreferenceConstants.MARGIN_PAINTER_ENABLE.getPreferenceStoreId(), Boolean.toString(false));
        node.put(PreferenceConstants.MARGIN_COLUMNS.getPreferenceStoreId(), String.valueOf(80));
    }

}
