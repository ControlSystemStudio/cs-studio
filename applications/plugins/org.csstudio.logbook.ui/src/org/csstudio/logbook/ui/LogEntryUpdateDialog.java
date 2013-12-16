/**
 * 
 */
package org.csstudio.logbook.ui;

import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author shroffk
 * 
 */
public class LogEntryUpdateDialog extends Dialog {
    private LogEntry logEntry;
    // GUI
    private LogEntryWidget logEntryWidget;
    private UserCredentialsWidget userCredentialWidget;

    private final IPreferencesService service = Platform.getPreferencesService();
    private boolean authenticate = true;    
    private ErrorBar errorBar;
    
    /** The listeners. */
    final private CopyOnWriteArrayList<LogEntryBuilderListener> listeners =
    new CopyOnWriteArrayList<LogEntryBuilderListener>();
    
    
    public LogEntryUpdateDialog(Shell parentShell, LogEntry logEntryBuilder) {
	super(parentShell);
	setBlockOnOpen(false);
	setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
	this.logEntry = logEntryBuilder;
    }

    @Override
    public Control createDialogArea(Composite parent) {
	getShell().setText("Update Log Entry");
	Composite container = (Composite) super.createDialogArea(parent);
	GridLayout gridLayout = (GridLayout) container.getLayout();
	gridLayout.marginWidth = 2;
	gridLayout.marginHeight = 2;
	errorBar = new ErrorBar(container, SWT.NONE);

	try {
	    authenticate = service.getBoolean("org.csstudio.logbook.ui","Autenticate.user", true, null);
	    service.getString("org.csstudio.logbook.ui","Default.logbook", "", null);
	    service.getString("org.csstudio.logbook.ui","Default.level", "", null);
	} catch (Exception ex) {
	    errorBar.setException(ex);
	}
	if (authenticate) {
	    userCredentialWidget = new UserCredentialsWidget(container, SWT.NONE);
	    userCredentialWidget.setLayoutData(new GridData(SWT.FILL,
		    SWT.CENTER, true, false, 1, 1));
	}

	logEntryWidget = new LogEntryWidget(container, SWT.NONE, true, true);
	GridData gd_logEntryWidget = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
	gd_logEntryWidget.heightHint = 450;
	gd_logEntryWidget.widthHint = 450;
	logEntryWidget.setLayoutData(gd_logEntryWidget);
	if (this.logEntry != null) {
	    logEntryWidget.setLogEntry(logEntry);
	}else{
	    errorBar.setException(new IllegalArgumentException("Cannot reply to a non existing log entry"));
	}

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
		// Create the logEntry
		// Create logbook client
		final Cursor originalCursor = getShell().getCursor();
		// Disable Submmit
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		try {
			// get logbook client
			final LogbookClient logbookClient;
			if (authenticate) {
				logbookClient = LogbookClientManager.getLogbookClientFactory()
						.getClient(userCredentialWidget.getUsername(),
								userCredentialWidget.getPassword());

				fireInitializeSave(userCredentialWidget.getUsername());
			} else {
				logbookClient = LogbookClientManager.getLogbookClientFactory()
						.getClient();
				fireInitializeSave("");
			}

			getShell().setCursor(
					Display.getDefault().getSystemCursor(SWT.CURSOR_WAIT));

			// Start save process
			fireStartSave();
			Job job = new Job("Update Log Entry") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						logbookClient.updateLogEntry(logEntryWidget.getLogEntry());
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

								// Stop save process
								try {
									fireStopSave();
									close();
								} catch (Exception e) {
									errorBar.setException(e);

									// Cancel save process
									fireCancelSave();
								}
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

			// Cancel save process
			fireCancelSave();
		}
	}
	
	
    /**
     * @param listener
     * 				Listener to add
     */
    public void addListener(final LogEntryBuilderListener listener) {
        listeners.add(listener);
    }

    /** 
     * @param listener 
     * 				Listener to remove 
     * */
    public void removeListener(final LogEntryBuilderListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fire start save.
     *
     * @param userName the user name
     */
    void fireInitializeSave(String userName) {
        for (LogEntryBuilderListener listener : listeners)
            listener.initializeSaveAction(userName);
    }
    
    /**
     * Fire start save.
     * @throws Exception 
     */
    void fireStartSave() throws Exception {
        for (LogEntryBuilderListener listener : listeners)
            listener.saveProcessStatus(LogEntryBuilderEnum.START_SAVE);
    }
    
    /**
     * Fire cancel save.
     */
    void fireCancelSave()  {
        for (LogEntryBuilderListener listener : listeners) {
			try {
				listener.saveProcessStatus(LogEntryBuilderEnum.CANCEL_SAVE);
			} catch (Exception e) {
				continue;
			};	
        }
    }
    
    /**
     * Fire finish save.
     * @throws Exception 
     */
    void fireStopSave() throws Exception {
        for (LogEntryBuilderListener listener : listeners)
        	   listener.saveProcessStatus(LogEntryBuilderEnum.STOP_SAVE);
    }
}
