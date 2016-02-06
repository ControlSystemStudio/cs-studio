package org.csstudio.ui.fx.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;

/**
 *
 * <code>FXComboInputDialog</code> is a Jface based input dialog using JavaFX components to pick a value from a
 * predefined set of values of the same type. The dialog is using a combo box for a selection widget.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 * @param <T> the type of value that can be selected
 */
public class FXComboInputDialog<T> extends FXBaseDialog<T> {

    /**
     * Opens the dialog on the UI thread, blocking the calling thread for the time the dialog is open. When the dialog
     * is closed the value is returned as an optional. If something was selected, the optional contains the selected
     * value, otherwise the optional is empty.
     *
     * @param shell the parent shell for the dialog
     * @param title the title of the dialog
     * @param message the message to display
     * @param defaultValue the default selected value
     * @param values the values to show in the combo box
     * @return selected value
     */
    public static <T> Optional<T> pick(final Shell shell, final String title, final String message,
        final T defaultValue, final List<T> values) {
        if (shell.getDisplay().getThread() == Thread.currentThread()) {
            return new FXComboInputDialog<>(shell, title, message, defaultValue, values).openAndWait();
        } else {
            final List<Optional<T>> list = new ArrayList<>(1);
            shell.getDisplay().syncExec(
                () -> list.add(new FXComboInputDialog<>(shell, title, message, defaultValue, values).openAndWait()));
            return list.get(0);
        }
    }

    private ComboBox<T> combo;
    private final List<T> values;

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
        super(parentShell, dialogTitle, dialogMessage, initialValue, null);
        this.values = values;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#setFocus()
     */
    @Override
    protected void setFocus() {
        combo.requestFocus();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#getValueFromComponent()
     */
    @Override
    protected T getValueFromComponent() {
        return combo.getSelectionModel().getSelectedItem();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#setValueToComponent(java.lang.Object)
     */
    @Override
    protected void setValueToComponent(T value) {
        combo.getSelectionModel().select(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.ui.fx.util.FXBaseDialog#getScene(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Scene getScene(Composite parent) {
        combo = new ComboBox<>();
        combo.setEditable(false);
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.setPrefWidth(getInitialSize().x - 25);
        combo.getItems().addAll(values);
        combo.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !okButton.isDisable()) {
                buttonPressed(IDialogConstants.OK_ID);
            }
        });
        return new Scene(combo);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
     */
    @Override
    public void controlMoved(ControlEvent e) {
        // fx combo has a bug that it remains open and locked to the position where it was opened
        combo.hide();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.ui.fx.util.FXBaseDialog#controlResized(org.eclipse.swt.events.ControlEvent)
     */
    @Override
    public void controlResized(ControlEvent e) {
        // fx combo has a bug that it remains open and locked to the position where it was opened
        combo.hide();
    }
}
