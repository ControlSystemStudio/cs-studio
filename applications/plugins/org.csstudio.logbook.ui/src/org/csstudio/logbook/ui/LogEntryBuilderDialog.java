/**
 * 
 */
package org.csstudio.logbook.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.csstudio.logbook.LogEntryBuilder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

/**
 * @author shroffk
 * 
 */
public class LogEntryBuilderDialog extends Dialog {
	private LogEntryBuilder logEntryBuilder;

	protected LogEntryBuilderDialog(Shell parentShell,
			LogEntryBuilder logEntryBuilder) {
		super(parentShell);
		setBlockOnOpen(false);
		setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
	}

	@Override
	protected Control createContents(Composite parent) {
		// Did not find any other way to avoid the creation of the button bar
		// other that override the createContents.
		getShell().setText("Create LogEntry");
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;

		LogEntryWidget logEntryWidget = new LogEntryWidget(container, SWT.NONE);
		GridData gd_logEntryWidget = new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1);
		gd_logEntryWidget.heightHint = 283;
		logEntryWidget.setLayoutData(gd_logEntryWidget);
		return container;
	}
}
