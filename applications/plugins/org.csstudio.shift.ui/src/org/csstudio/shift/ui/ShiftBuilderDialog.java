package org.csstudio.shift.ui;

import static org.csstudio.shift.ShiftBuilder.shift;
import gov.bnl.shiftClient.ShiftApiClient;

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

public class ShiftBuilderDialog extends Dialog {
    private ShiftBuilder shiftBuilder;
    // GUI
    private ShiftWidget shiftWidget;
    private UserCredentialsWidget userCredentialWidget;

    private final IPreferencesService service = Platform.getPreferencesService();
    private boolean authenticate = true;
    private ErrorBar errorBar;

    public ShiftBuilderDialog(Shell parentShell, ShiftBuilder shiftBuilder) {
		super(parentShell);
		setBlockOnOpen(false);
		setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
		this.shiftBuilder = shiftBuilder;
    }

    @Override
    public Control createDialogArea(final Composite parent) {
        getShell().setText("Start Shift");
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
            userCredentialWidget = new UserCredentialsWidget(container,
                SWT.NONE);
            userCredentialWidget.setLayoutData(new GridData(SWT.FILL,
                SWT.CENTER, true, false, 1, 1));
        }

        shiftWidget = new ShiftWidget(container, SWT.NONE, true);
        final GridData gd_shiftWidget = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_shiftWidget.heightHint = 450;
        gd_shiftWidget.widthHint = 450;
        shiftWidget.setLayoutData(gd_shiftWidget);
        if (shiftBuilder != null) {
            try {
            	//If using another shift that already exist as template remove the end date, close user and start date
            	shiftBuilder.setEndDate(null);
            	shiftBuilder.setStartDate(null);
            	shiftBuilder.setCloseShiftUser(null);
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
            ShiftApiClient shiftClient;
            if (authenticate) {
                shiftClient = ShiftClientManager.getShiftClientFactory()
                		.getClient(userCredentialWidget.getUsername(), userCredentialWidget.getPassword());
            } else {
                shiftClient = ShiftClientManager.getShiftClientFactory().getClient();
            }

            getShell().setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_WAIT));
            shiftBuilder = shift(shiftWidget.getShift()).setOwner(userCredentialWidget.getUsername());
            shiftClient.start(shiftBuilder.build());
            getShell().setCursor(originalCursor);
            setReturnCode(OK);
            close();
        } catch (Exception ex) {
            getShell().setCursor(originalCursor);
            errorBar.setException(ex);
        }
    }
}
