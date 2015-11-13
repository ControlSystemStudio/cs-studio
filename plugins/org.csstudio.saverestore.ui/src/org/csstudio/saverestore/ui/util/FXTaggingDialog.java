package org.csstudio.saverestore.ui.util;

import java.util.Optional;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * An input dialog for soliciting an input from the user. This dialog offers a combo box and allows user to select one
 * of the predefined items.
 * <p>
 * This dialog is a modified copy of the {@link InputDialog} and is intended for tagging the snapshots.
 * </p>
 */
public class FXTaggingDialog extends Dialog {

    private String title;
    private String message;
    private String value = "";//$NON-NLS-1$
    private IInputValidator validator;
    private TextField tagName;
    private TextArea tagMessage;
    private Text errorMessageText;
    private String errorMessage;

    /**
     * Creates an input dialog with OK and Cancel buttons. Note that the dialog
     * will have no visual representation (no widgets) until it is told to open.
     * <p>
     * Note that the <code>open</code> method blocks for input dialogs.
     * </p>
     *
     * @param parentShell
     *            the parent shell, or <code>null</code> to create a top-level
     *            shell
     * @param dialogTitle
     *            the dialog title, or <code>null</code> if none
     * @param dialogMessage
     *            the dialog message, or <code>null</code> if none
     * @param validator
     *            an input validator, or <code>null</code> if none
     */
    public FXTaggingDialog(Shell parentShell) {
        super(parentShell);
        this.title= "Tag Snapshot";
        this.message = "Provide the name of the tag and an optional tag message for the selected snapshot:";
        this.validator = new IInputValidator() {
            @Override
            public String isValid(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    return "The tag name cannot be empty.";
                }
                return null;
            }
        };
        value = "";
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            value = tagName.getText();
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
        tagName.requestFocus();
        if (value != null) {
            tagName.setText(value);
            tagName.selectAll();
            validateInput();
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        Label label = new Label(composite, SWT.WRAP);
        label.setText(message);
        GridData data = new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL
                | GridData.VERTICAL_ALIGN_CENTER);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        label.setLayoutData(data);
        label.setFont(parent.getFont());

        new FXCanvasMaker(){
            @Override
            protected Scene createFxScene() {
                GridPane pane = new GridPane();
                tagName = new TextField();
                tagName.textProperty().addListener((a,o,n)->validateInput());
                pane.add(tagName,0,0);
                pane.add(new javafx.scene.control.Label("Tag message:"),0,1);
                tagMessage = new TextArea();
                tagMessage.setEditable(true);
                GridPane.setFillHeight(tagMessage, true);
                GridPane.setFillWidth(tagMessage, true);
                GridPane.setFillHeight(tagName, true);
                GridPane.setVgrow(tagMessage, Priority.ALWAYS);
                GridPane.setHgrow(tagName, Priority.ALWAYS);
                GridPane.setHgrow(tagMessage, Priority.ALWAYS);
                pane.setVgap(3);
                pane.add(tagMessage,0,2);
                return new Scene(pane);
            }
        }.createPartControl(composite);

        errorMessageText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
        errorMessageText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        errorMessageText.setBackground(errorMessageText.getDisplay()
                .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        setErrorMessage(errorMessage);

        applyDialogFont(composite);
        return composite;
    }

    /**
     * Opens the dialog and returns the selected item if it exists.
     *
     * @return the selected item
     */
    public Optional<String> openAndWait() {
        open();
        return getValue();
    }

    /**
     * Returns the string typed into this input dialog.
     *
     * @return the input string
     */
    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }

    /**
     * Returns the string typed into the tag message area.
     *
     * @return the tag message
     */
    public String getMessage() {
        return tagMessage.getText();
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
            errorMessage = validator.isValid(tagName.getText());
        }
        // Bug 16256: important not to treat "" (blank error) the same as null
        // (no error)
        setErrorMessage(errorMessage);
    }

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

