package org.csstudio.sds.ui.internal.properties.view;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog for the "modal" Wizards. It is ensured that there is only one
 * opened dialog per time. If there is alredy a dialog opened, it will be closed
 * and replaced by one that carries the passed in IWizard instance.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class ModalWizardDialog extends WizardDialog {

	/**
	 * The unique instance of this dialog.
	 */
	private static ModalWizardDialog _instance = null;

	/**
	 * Private constructor due to singleton pattern.
	 * 
	 * @param parentShell
	 *            the parent shell
	 * @param newWizard
	 *            the wizard this dialog is working on
	 */
	private ModalWizardDialog(final Shell parentShell,
			final IWizard newWizard) {
		super(parentShell, newWizard);

		setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE
				| SWT.BORDER | SWT.RESIZE);
	}

	/**
	 * Open the dialog. Ensure that there is only one opened dialog per time. If
	 * there is alredy a dialog opened, it will be closed and replaced by one
	 * that carries the passed in IWizard instance.
	 * 
	 * @param parentShell
	 *            the parent shell
	 * @param newWizard
	 *            the wizard this dialog is working on
	 * @return the return code
	 */
	public static int open(final Shell parentShell, final IWizard newWizard) {
		if (_instance == null) {
			_instance = new ModalWizardDialog(parentShell, newWizard);
		} else {
			final Rectangle currentBounds = _instance.getShell() == null ? null
					: _instance.getShell().getBounds();
			_instance.close();
			_instance = new ModalWizardDialog(parentShell, newWizard);

			// if the dialog was previously closed, there was no old shell and
			// no old bounds
			if (currentBounds != null) {
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						_instance.getShell().setBounds(currentBounds);
					}
				});
			}
		}

		return _instance.open();
	}
}
