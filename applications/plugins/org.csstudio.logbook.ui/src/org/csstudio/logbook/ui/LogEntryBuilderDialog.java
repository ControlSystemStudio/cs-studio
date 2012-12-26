/**
 * 
 */
package org.csstudio.logbook.ui;

import org.csstudio.logbook.LogEntryBuilder;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

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
		this.logEntryBuilder = logEntryBuilder;
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

		LogEntryWidget logEntryWidget = new LogEntryWidget(container, SWT.NONE, true, true);
		GridData gd_logEntryWidget = new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1);
		gd_logEntryWidget.heightHint = 450;
		logEntryWidget.setLayoutData(gd_logEntryWidget);
		if (this.logEntryBuilder != null) {
			logEntryWidget.setLogEntry(logEntryBuilder.build());
		}
		return container;
	}
}
