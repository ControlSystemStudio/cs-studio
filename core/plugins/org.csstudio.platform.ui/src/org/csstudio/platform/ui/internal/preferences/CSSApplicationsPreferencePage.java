package org.csstudio.platform.ui.internal.preferences;

import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * The root page for all CSS applications preference pages.
 * 
 * @author Stefan Hofer
 *
 */
public class CSSApplicationsPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected final Control createContents(final Composite parent) {
		Label label = new Label(parent, SWT.NULL);
		label.setText(Messages.getString("CSSApplicationsPreferencePage.MESSAGE")); //$NON-NLS-1$
		return label;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void init(final IWorkbench workbench) {
		// nothing to do here
	}
}
