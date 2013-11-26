package org.csstudio.shift.ui;


import gov.bnl.shiftClient.ShiftClient;

import java.io.IOException;

import org.csstudio.shift.ShiftBuilder;
import org.csstudio.shift.ShiftClientManager;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.core.runtime.Platform;
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

public class CloseShiftBuilderDialog extends Dialog {
    private ShiftBuilder shiftBuilder;
    // GUI
    private ShiftWidget shiftWidget;
    private UserCredentialsWidget userCredentialWidget;

    private final IPreferencesService service = Platform.getPreferencesService();
    private boolean authenticate = true;
    private ErrorBar errorBar;
    private boolean end;
	private ShiftClient shiftClient;

    public CloseShiftBuilderDialog(final Shell parentShell, final ShiftBuilder shiftBuilder, final boolean end) {
		super(parentShell);
		setBlockOnOpen(false);
		setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
		this.shiftBuilder = shiftBuilder;
		this.end = end;
    }

    @Override
    public Control createDialogArea(final Composite parent) {
        getShell().setText(end ? "End Shift" : "Close Shift");
        final Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.marginWidth = 2;
        gridLayout.marginHeight = 2;
        errorBar = new ErrorBar(container, SWT.NONE);

        try {
            authenticate = service.getBoolean("org.csstudio.shift.ui","Autenticate.user", true, null);
        } catch (Exception ex) {
            errorBar.setException(ex);
        }
        if (authenticate) {
            userCredentialWidget = new UserCredentialsWidget(container, SWT.NONE);
            userCredentialWidget.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        }

        shiftWidget = new ShiftWidget(container, SWT.NONE, false);
        final GridData gd_shiftWidget = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_shiftWidget.heightHint = 450;
        gd_shiftWidget.widthHint = 450;
        shiftWidget.setLayoutData(gd_shiftWidget);
        if (this.shiftBuilder != null) {
            try {
                shiftWidget.setShift(shiftBuilder.build());
            } catch (IOException e) {
            	errorBar.setException(e);
            }
        }

        return container;
    }

    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
    	// create OK and Cancel buttons by default
        createButton(parent, IDialogConstants.OK_ID, "Submit", true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void okPressed() {
        final Cursor originalCursor = getShell().getCursor();
        try {
            if (authenticate) {
                shiftClient = ShiftClientManager.getShiftClientFactory()
                		.getClient(userCredentialWidget.getUsername(), userCredentialWidget.getPassword());
            } else {
                shiftClient = ShiftClientManager.getShiftClientFactory().getClient();
            }

            getShell().setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_WAIT));
            if(end) {
            	shiftClient.end(shiftBuilder.build());
            } else {
            	shiftClient.close(shiftBuilder.build());
            }
            getShell().setCursor(originalCursor);
            setReturnCode(OK);
            close();
        } catch (Exception ex) {
            getShell().setCursor(originalCursor);
            errorBar.setException(ex);
        }
    }
}
