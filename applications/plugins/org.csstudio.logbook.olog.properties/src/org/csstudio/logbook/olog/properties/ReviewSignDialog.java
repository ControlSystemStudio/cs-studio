package org.csstudio.logbook.olog.properties;

import static org.csstudio.logbook.PropertyBuilder.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.PropertyBuilder;
import org.csstudio.security.SecuritySupport;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ReviewSignDialog extends Dialog {

    private List<LogEntryBuilder> logs;

    private ErrorBar errorBar;
    private Label lblUsername;
    private Text username;
    private Label lblPassword;
    private Text password;

    private final Collection<LogEntryBuilder> data;

    protected ReviewSignDialog(Shell parentShell,
	    Collection<LogEntryBuilder> data) {
	super(parentShell);
	setBlockOnOpen(false);
	setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
	this.data = data;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
	getShell().setText("Sign Log Entries");
	Composite container = (Composite) super.createDialogArea(parent);
	GridLayout gridLayout = (GridLayout) container.getLayout();
	gridLayout.marginWidth = 2;
	gridLayout.marginHeight = 2;
	errorBar = new ErrorBar(container, SWT.NONE);
	errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,		2, 1));

	container.setLayout(new GridLayout(2, false));
	lblUsername = new Label(container, SWT.NONE);
	lblUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
		false, 1, 1));
	lblUsername.setText("User Name:");

	username = new Text(container, SWT.BORDER);
	username.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
		1, 1));

	lblPassword = new Label(container, SWT.NONE);
	lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
		false, 1, 1));
	lblPassword.setText("Password:");

	password = new Text(container, SWT.BORDER | SWT.PASSWORD);
	password.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
		1, 1));
	return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
	// create OK and Cancel buttons by default
	createButton(parent, IDialogConstants.OK_ID, "Submit", true);
	createButton(parent, IDialogConstants.CANCEL_ID,
		IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void okPressed() {
	final Cursor originalCursor = getShell().getCursor();
	
	getButton(IDialogConstants.OK_ID).setEnabled(false);

		try {
			// get logbook client
			final LogbookClient logbookClient = LogbookClientManager
					.getLogbookClientFactory().getClient(username.getText(),
							password.getText());
			getShell().setCursor(
					Display.getDefault().getSystemCursor(SWT.CURSOR_WAIT));

			String signature = SecuritySupport.getSubjectName(SecuritySupport
					.getSubject());
			PropertyBuilder SignOffProperty = property("SignOff").attribute(
					"signature", signature);

			StringBuffer sb = new StringBuffer();
			sb.append("signature:" + signature
					+ System.getProperty("line.separator"));

			final Collection<LogEntry> logEntires = new ArrayList<LogEntry>();
			for (LogEntryBuilder logEntryBuilder : data) {
				logEntryBuilder.addProperty(SignOffProperty);
				sb.append("logEntry: " + logEntryBuilder
						+ System.getProperty("line.separator"));
				logEntires.add(logEntryBuilder.build());
			}
			Job job = new Job("Review and Sign Log Entries") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						logbookClient.updateLogEntries(logEntires);
						return Status.OK_STATUS;
					} catch (final Exception e) {
						getShell().getDisplay().asyncExec(new Runnable() {

							@Override
							public void run() {
								getShell().setCursor(originalCursor);
								getButton(IDialogConstants.OK_ID).setEnabled(true);
								errorBar.setException(e);
							}
						});
						return Status.CANCEL_STATUS;
					}
				}
			};
			job.addJobChangeListener(new JobChangeAdapter() {
				public void done(IJobChangeEvent event) {
					if (event.getResult().isOK()) {
						getShell().getDisplay().asyncExec(new Runnable() {

							@Override
							public void run() {
								getShell().setCursor(originalCursor);
								getButton(IDialogConstants.OK_ID).setEnabled(true);
								setReturnCode(OK);
								close();
							}
						});

					}
				}
			});
			job.schedule();
		} catch (Exception ex) {
			getShell().setCursor(originalCursor);
			getButton(IDialogConstants.OK_ID).setEnabled(true);
			errorBar.setException(ex);
		}

	}

}
