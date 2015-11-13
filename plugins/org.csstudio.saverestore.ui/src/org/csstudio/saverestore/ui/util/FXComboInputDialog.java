package org.csstudio.saverestore.ui.util;

import java.util.List;
import java.util.Optional;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import javafx.scene.Scene;
import javafx.scene.control.ComboBox;

public class FXComboInputDialog<T> extends Dialog {

    private String title;
    private String message;
    private T value;
    private ComboBox<T> text;
    private List<T> values;

    /**
     * Creates a combo input dialog with OK and Cancel buttons. Note that the dialog will have no visual representation
     * (no widgets) until it is told to open.
     *
     * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
     * @param dialogTitle the dialog title, or <code>null</code> if none
     * @param dialogMessage the dialog message, or <code>null</code> if none
     * @param initialValue the initial input value, or <code>null</code>
     * @param values the list of a predefined values
     */
    public FXComboInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, T initialValue,
            List<T> values) {
        super(parentShell);
        this.title = dialogTitle;
        message = dialogMessage;
        if (initialValue == null) {
            value = null;
        } else {
            value = initialValue;
        }
        this.values = values;
        setBlockOnOpen(true);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            value = text.getValue();
        } else {
            value = null;
        }
        super.buttonPressed(buttonId);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        text.requestFocus();
        if (value != null) {
            text.getSelectionModel().select(value);
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
        // create message
        if (message != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(message);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL
                    | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }
        new FXCanvasMaker() {
            @Override
            protected Scene createFxScene() {
                text = new ComboBox<>();
                text.setMaxWidth(Double.MAX_VALUE);
                text.setPrefWidth(450);
                text.getItems().addAll(values);
                return new Scene(text);
            }
        }.createPartControl(composite);

        applyDialogFont(composite);
        return composite;
    }

    /**
     * Opens the dialog and returns the selected item if it exists.
     *
     * @return the selected item
     */
    public Optional<T> openAndWait() {
        open();
        return getValue();
    }

    /**
     * Returns the selected item if it exists.
     *
     * @return the selected item
     */
    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }
}
