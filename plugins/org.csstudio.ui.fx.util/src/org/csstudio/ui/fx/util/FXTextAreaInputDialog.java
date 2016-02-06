package org.csstudio.ui.fx.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;

/**
 *
 * <code>FXTextAreaInputDialog</code> is a Jface based input dialog using JavaFX components for entering a multiline
 * text.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class FXTextAreaInputDialog extends FXBaseDialog<String> {

    /**
     * Opens the dialog on the UI thread and blocks the calling thread while the dialog is open. After the dialog is
     * closed the value entered into the dialog is returned as an optional.
     *
     * @param shell the parent shell
     * @param title the title of the dialog
     * @param message the message shown in the dialog
     * @param initialValue the initial value to show in the text area
     * @param validator the text validator
     * @return entered value if confirmed or empty if cancelled
     */
    public static Optional<String> get(final Shell shell, final String title, final String message,
        final String initialValue, final InputValidator<String> validator) {
        if (shell.getDisplay().getThread() == Thread.currentThread()) {
            return new FXTextAreaInputDialog(shell, title, message, initialValue, validator).openAndWait();
        } else {
            final List<Optional<String>> list = new ArrayList<>(1);
            shell.getDisplay().syncExec(
                () -> list.add(new FXTextAreaInputDialog(shell, title, message, initialValue, validator).openAndWait()));
            return list.get(0);
        }
    }

    private TextArea text;

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
    public FXTextAreaInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
        InputValidator<String> validator) {
        super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
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
     * @see org.csstudio.ui.fx.util.FXBaseDialog#getScene(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Scene getScene(Composite parent) {
        text = new TextArea();
        text.setPrefRowCount(5);
        text.textProperty().addListener((a, o, n) -> validateInput());
        return new Scene(text);
    }
}
