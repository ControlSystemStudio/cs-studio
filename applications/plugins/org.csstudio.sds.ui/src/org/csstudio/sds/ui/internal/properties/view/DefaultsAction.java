package org.csstudio.sds.ui.internal.properties.view;

import org.eclipse.ui.PlatformUI;

/**
 * This action resets the <code>PropertySheetViewer</code> values back to the
 * default values.
 * 
 * [Issue: should listen for selection changes in the viewer and set enablement]
 * 
 * @author Sven Wende
 */
final class DefaultsAction extends PropertySheetAction {
	/**
	 * Create the Defaults action. This action is used to set the properties
	 * back to their default values.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param name
	 *            the name
	 */
	public DefaultsAction(final PropertySheetViewer viewer, final String name) {
		super(viewer, name);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
				IPropertiesHelpContextIds.DEFAULTS_ACTION);
	}

	/**
	 * Reset the properties to their default values.
	 */
	@Override
	public void run() {
		getPropertySheet().deactivateCellEditor();
		getPropertySheet().resetProperties();
	}
}
