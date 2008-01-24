package org.csstudio.sds.preferences;

import org.csstudio.sds.model.properties.actions.WidgetAction;

/**
 * This class contains the id for the preference page entries used within the sds.
 * @author Kai Meyer
 *
 */
public final class PreferenceConstants {
	
	/**
	 * Property ID for the schema setting.
	 */
	public static final String PROP_SCHEMA = "schema"; //$NON-NLS-1$
	/**
	 * Constant, which is used in the preference page for enable antialiasing.
	 */
	public static final String PROP_ANTIALIASING = "PROP_ANTIALIASING";
	/**
	 * The ID for the grid spacing property on the preference page.
	 */
	public static final String PROP_GRID_SPACING = "PROP_GRID_SPACING";
//	/**
//	 * The Id for using the workspace as root property on the preference pages.
//	 */
//	public static final String PROP_USE_WORKSPACE_ID = "PROP_USE_WORKSPACE_ID";
	/**
	 * The Id for the default cursor on the preference pages.
	 */
	public static final String PROP_DEFAULT_CURSOR = "PROP_DEFAULT_CURSOR";
	/**
	 * The Id for the cursor to show that a widget has {@link WidgetAction}s defined and it is enabled.
	 */
	public static final String PROP_ENABLED_ACTION_CURSOR = "PROP_ENABLED_ACTION_CURSOR";
	/**
	 * The Id for the cursor to show that a widget has {@link WidgetAction}s defined and it is disabled.
	 */
	public static final String PROP_DISABLED_ACTION_CURSOR = "PROP_DISABLED_ACTION_CURSOR";
//	XXX Removed, because the default dialog font should be used (23.11.2007) 
//	/**
//	 * The ID for the dialog font property.
//	 */
//	public static final String PROP_USE_DIALOG_FONT = "PROP_USE_DIALOG_FONT";
	
	/**
	 * Private constructor to avoid instantiation.
	 */
	private PreferenceConstants() {
		//do nothing.
	}

}
