package org.csstudio.saverestore.ui.util;

import java.util.Optional;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

/**
 * An input dialog for soliciting an input from the user. This dialog offers a combo box and allows user to select one
 * of the predefined items.
 * <p>
 * This dialog is a modified copy of the {@link InputDialog}.
 * </p>
 */
public abstract class FXBaseDialog<T> extends Dialog {

    private String title;
    private String message;
    private T value;
    private IInputValidator validator;
    private Text errorMessageText;
    private String errorMessage;

    /**
     * Creates an input dialog with OK and Cancel buttons. Note that the dialog
     * will have no visual representation (no widgets) until it is told to open.
     *
     * @param parentShell
     *            the parent shell, or <code>null</code> to create a top-level
     *            shell
     * @param dialogTitle
     *            the dialog title, or <code>null</code> if none
     * @param dialogMessage
     *            the dialog message, or <code>null</code> if none
     * @param initialValue
     *            the initial input value, or <code>null</code> if none
     *            (equivalent to the empty string)
     * @param validator
     *            an input validator, or <code>null</code> if none
     */
    public FXBaseDialog(Shell parentShell, String dialogTitle,
            String dialogMessage, T initialValue, IInputValidator validator) {
        super(parentShell);
        this.title = dialogTitle;
        this.message = dialogMessage;
        this.value = initialValue;
        this.validator = validator;
        setBlockOnOpen(true);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            value = getValueFromComponent();
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
        createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
        getNode().requestFocus();
        if (value != null) {
            setValueToComponent(value);
        }
        validateInput();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
        // create message
        if (message != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(message);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL
                    | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }
        Composite fxComposite = new Composite(composite, SWT.NONE);
        fxComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        fxComposite.setLayout(new GridLayout());
        new FXCanvasMaker() {
            @Override
            protected Scene createFxScene() {
                return getScene();
            }
        }.createPartControl(fxComposite);

        if (validator != null) {
            errorMessageText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
            errorMessageText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                    | GridData.HORIZONTAL_ALIGN_FILL));
            errorMessageText.setBackground(errorMessageText.getDisplay()
                    .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            // Set the error message text
            // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=66292
            setErrorMessage(errorMessage);
        }

        applyDialogFont(composite);
        return composite;
    }

    protected abstract Node getNode();

    protected abstract T getValueFromComponent();

    protected abstract void setValueToComponent(T value);

    /**
     * Returns the string typed into this input dialog.
     *
     * @return the input string
     */
    public Optional<T> getValue() {
        return Optional.ofNullable(value);
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
     * Validates the input.
     * <p>
     * The default implementation of this framework method delegates the request
     * to the supplied input validator object; if it finds the input invalid,
     * the error message is displayed in the dialog's message line. This hook
     * method is called whenever the text changes in the input field.
     * </p>
     */
    protected void validateInput() {
        String errorMessage = null;
        if (validator != null) {
            errorMessage = validator.isValid(getValueFromComponent());
        }
        setErrorMessage(errorMessage);
    }

    protected abstract Scene getScene();

    /**
     * Sets or clears the error message.
     * If not <code>null</code>, the OK button is disabled.
     *
     * @param errorMessage
     *            the error message, or <code>null</code> to clear
     * @since 3.0
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        if (errorMessageText != null && !errorMessageText.isDisposed()) {
            errorMessageText.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$
            // Disable the error message text control if there is no error, or
            // no error text (empty or whitespace only).  Hide it also to avoid
            // color change.
            // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=130281
            boolean hasError = errorMessage != null && (StringConverter.removeWhiteSpaces(errorMessage)).length() > 0;
            errorMessageText.setEnabled(hasError);
            errorMessageText.setVisible(hasError);
            errorMessageText.getParent().update();
            // Access the ok button by id, in case clients have overridden button creation.
            // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=113643
            Control button = getButton(IDialogConstants.OK_ID);
            if (button != null) {
                button.setEnabled(errorMessage == null);
            }
        }
    }
}

