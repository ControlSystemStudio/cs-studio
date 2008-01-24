package org.csstudio.sds.ui.internal.properties.view;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Configures dynamic aspects of a property.
 */
final class ConfigureDynamicAspectsAction extends PropertySheetAction {
	/**
	 * Creates the action.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param name
	 *            the name
	 */
	public ConfigureDynamicAspectsAction(final PropertySheetViewer viewer,
			final String name) {
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
			IWizard wizard = entry.getDynamicsDescriptionConfigurationWizard();

			if (wizard != null) {
				ModalWizardDialog.open(Display.getCurrent()
						.getActiveShell(), wizard);
			}
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
