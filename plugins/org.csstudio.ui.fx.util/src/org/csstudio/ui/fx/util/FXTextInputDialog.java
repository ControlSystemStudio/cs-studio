package org.csstudio.ui.fx.util;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

/**
 *
 * <code>FXTextInputDialog</code> is a Jface based input dialog using JavaFX components to enter a single line text.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class FXTextInputDialog extends FXBaseDialog<String> {

    private TextField text;

    /**
     * Creates an input dialog with OK and Cancel buttons. Note that the dialog will have no visual representation (no
     * widgets) until it is told to open.
     *
     * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
     * @param dialogTitle the dialog title, or <code>null</code> if none
     * @param dialogMessage the dialog message, or <code>null</code> if none
     * @param initialValue the initial input value, or <code>null</code> if none (equivalent to the empty string)
     * @param validator an input validator, or <code>null</code> if none
     */
    public FXTextInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
        InputValidator<String> validator) {
        super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#getValueFromComponent()
     */
    @Override
    protected String getValueFromComponent() {
        return text.getText();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#setValueToComponent(java.lang.Object)
     */
    @Override
    protected void setValueToComponent(String value) {
        text.setText(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#setFocus()
     */
    @Override
    protected void setFocus() {
        text.requestFocus();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.ui.fx.util.FXBaseDialog#getScene(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Scene getScene(Composite parent) {
        text = new TextField();
        text.setMaxWidth(Double.MAX_VALUE);
        text.setPrefWidth(getInitialSize().x - 25);
        text.textProperty().addListener((a, o, n) -> validateInput());
        text.setOnAction(e -> {
            if (!okButton.isDisable()) {
                buttonPressed(IDialogConstants.OK_ID);
            }
        });
        return new Scene(new BorderPane(text));
    }
}
