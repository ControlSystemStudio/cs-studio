package org.csstudio.sds.ui.internal.properties.view;

import org.eclipse.ui.PlatformUI;

/**
 * Help context ids for the properties view.
 * <p>
 * This interface contains constants only; it is not intended to be implemented
 * or extended.
 * </p>
 * 
 * @author Sven Wende
 * 
 */
interface IPropertiesHelpContextIds {
	/**
	 * Prefix for all ids.
	 */
	String PREFIX = PlatformUI.PLUGIN_ID + "."; //$NON-NLS-1$

	/**
	 * Help context id for the categories action.
	 */
	String CATEGORIES_ACTION = PREFIX + "properties_categories_action_context"; //$NON-NLS-1$

	/**
	 * Help context id for the defaults action.
	 */
	String DEFAULTS_ACTION = PREFIX + "properties_defaults_action_context"; //$NON-NLS-1$

	/**
	 * Help context id for the filter action.
	 */
	String FILTER_ACTION = PREFIX + "properties_filter_action_context"; //$NON-NLS-1$
	
	/**
	 * Help context id for the filter action.
	 */
	String ALIAS_ACTION = PREFIX + "properties_alias_action_context"; //$NON-NLS-1$

	/**
	 * Help context id for the copy action.
	 */
	String COPY_PROPERTY_ACTION = PREFIX + "properties_copy_action_context"; //$NON-NLS-1$

	/**
	 * Help context id for the property sheet view.
	 */
	String PROPERTY_SHEET_VIEW = PREFIX + "property_sheet_view_context"; //$NON-NLS-1$
}
