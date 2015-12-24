package org.csstudio.ui.fx.util;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;

import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 *
 * <code>FXTextListInputDialog</code> is a Jface based input dialog using JavaFX components to input a text and and
 * select an item from a list.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class FXTextListInputDialog<T> extends FXBaseDialog<String> {

    private TextField text;
    private ListView<T> list;
    private List<T> options;

    /**
     * Creates an input dialog with OK and Cancel buttons. Note that the dialog will have no visual representation (no
     * widgets) until it is told to open.
     *
     * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
     * @param title the title of the dialog
     * @param message the message shown in the dialog
     * @param validator the input validator for the text input field
     */
    public FXTextListInputDialog(Shell parentShell, String title, String message, InputValidator<String> validator,
            List<T> options) {
        super(parentShell, title, message, "", validator);
        this.options = options;
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
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#setValueToComponent(java.lang.Object)
     */
    @Override
    protected void setValueToComponent(String value) {
        text.setText(value);
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
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#getScene()
     */
    @Override
    protected Scene getScene() {
        GridPane pane = new GridPane();
        text = new TextField();
        text.setMaxWidth(Double.MAX_VALUE);
        text.textProperty().addListener((a, o, n) -> validateInput());
        text.setOnAction(e -> {
            if (!okButton.isDisable()) {
                buttonPressed(IDialogConstants.OK_ID);
            }
        });
        list = new ListView<>();
        list.setMaxWidth(Double.MAX_VALUE);
        list.getItems().addAll(options);
        if (!options.isEmpty()) {
            list.getSelectionModel().select(0);
        }
        GridPane.setFillHeight(list, true);
        GridPane.setFillWidth(list, true);
        GridPane.setFillHeight(text, true);
        GridPane.setFillWidth(text, true);
        GridPane.setVgrow(list, Priority.ALWAYS);
        GridPane.setHgrow(text, Priority.ALWAYS);
        GridPane.setHgrow(list, Priority.ALWAYS);
        pane.setMaxWidth(Double.MAX_VALUE);
        pane.setVgap(3);
        pane.add(text, 0, 0);
        pane.add(list, 0, 1);
        pane.setPrefWidth(540);
        pane.setPrefHeight(200);
        return new Scene(pane);
    }

    /**
     * Returns the item selected in the list.
     *
     * @return the selected item
     */
    public T getSelectedOption() {
        return list.getSelectionModel().getSelectedItem();
    }
}
