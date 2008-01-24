package org.csstudio.sds.ui.internal.properties.view;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;

/**
 * Removes dynamic aspects of a property.
 */
final class RemoveDynamicAspectsAction extends
		PropertySheetAction {
	/**
	 * Creates the action.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param name
	 *            the name
	 */
	public RemoveDynamicAspectsAction(final PropertySheetViewer viewer, final String name) {
		super(viewer, name);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
				IPropertiesHelpContextIds.COPY_PROPERTY_ACTION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		// Get the selected property
		IStructuredSelection selection = (IStructuredSelection) getPropertySheet()
				.getSelection();
		if (selection.isEmpty()) {
			return;
		}

		// Assume single selection
		IPropertySheetEntry entry = (IPropertySheetEntry) selection
				.getFirstElement();

		
		// Open Wizard
		if (entry != null) {
			entry.applyDynamicsDescriptor(null);
		}
	}

	/**
	 * Updates enablement based on the current selection.
	 * 
	 * @param sel
	 *            the selection
	 */
	public void selectionChanged(final IStructuredSelection sel) {
		setEnabled(!sel.isEmpty());
	}
}
