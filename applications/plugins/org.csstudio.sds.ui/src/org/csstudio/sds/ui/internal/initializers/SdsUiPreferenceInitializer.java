package org.csstudio.sds.ui.internal.initializers;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.preferences.PreferenceConstants;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Initializes the SDSUi preferences.
 * @author Kai Meyer
 *
 * @deprecated is never called!!!
 * 			The values are set in the {@link SdsPlugin}
 */
public final class SdsUiPreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope()
			.getNode(SdsPlugin.PLUGIN_ID);
		initializeDisplayOptionsPreferences(node);
	}
	
	/**
	 * Initializes the preference settings for the display options that are
	 * used by the SDS.
	 * 
	 * @param node
	 *            the preferences node to use
	 */
	private void initializeDisplayOptionsPreferences(
			final IEclipsePreferences node) {
		node.put(PreferenceConstants.PROP_GRID_SPACING,
				String.valueOf(DisplayEditor.GRID_SPACING));
		
		node.put(PreferenceConstants.PROP_ANTIALIASING, "false");
		
//		node.put(PreferenceConstants.PROP_USE_WORKSPACE_ID, "true");
//		XXX Removed, because the default dialog font should be used (23.11.2007) 
//		node.put(PreferenceConstants.PROP_USE_DIALOG_FONT, "true");
	}

}
