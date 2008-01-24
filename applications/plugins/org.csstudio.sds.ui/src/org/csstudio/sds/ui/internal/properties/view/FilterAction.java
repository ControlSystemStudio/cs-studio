package org.csstudio.sds.ui.internal.properties.view;

import org.eclipse.ui.PlatformUI;

/**
 * This action hides or shows expert properties in the
 * <code>PropertySheetViewer</code>.
 * 
 * @author Sven Wende
 */
final class FilterAction extends PropertySheetAction {
	/**
	 * Create the Filter action. This action is used to show or hide expert
	 * properties.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param name
	 *            the name
	 */
	public FilterAction(final PropertySheetViewer viewer, final String name) {
		super(viewer, name);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
				IPropertiesHelpContextIds.FILTER_ACTION);
	}

	/**
	 * Toggle the display of expert properties.
	 */

	@Override
	public void run() {
		PropertySheetViewer ps = getPropertySheet();
		ps.deactivateCellEditor();
		if (isChecked()) {
			ps.showExpert();
		} else {
			ps.hideExpert();
		}
	}
}
