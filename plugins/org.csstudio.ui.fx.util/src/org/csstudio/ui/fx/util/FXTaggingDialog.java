package org.csstudio.ui.fx.util;

import org.eclipse.swt.widgets.Shell;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 *
 * <code>FXTaggingDialog</code> is a Jface based input dialog using JavaFX components to input the tag name and tag
 * message.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class FXTaggingDialog extends FXBaseDialog<String> {

    private TextField tagName;
    private TextArea tagMessage;

    /**
     * Creates an input dialog with OK and Cancel buttons. Note that the dialog will have no visual representation (no
     * widgets) until it is told to open.
     *
     * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
     */
    public FXTaggingDialog(Shell parentShell) {
        super(parentShell, "Tag Snapshot",
                "Provide the name of the tag and an optional tag message for the selected snapshot:", "",
                e -> (e == null || e.trim().isEmpty()) ? "The tag name cannot be empty." : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#setFocus()
     */
    @Override
    protected void setFocus() {
        tagName.requestFocus();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#setValueToComponent(java.lang.Object)
     */
    @Override
    protected void setValueToComponent(String value) {
        tagName.setText(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#getValueFromComponent()
     */
    @Override
    protected String getValueFromComponent() {
        return tagName.getText();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#getScene()
     */
    @Override
    protected Scene getScene() {
        GridPane pane = new GridPane();
        tagName = new TextField();
        tagName.textProperty().addListener((a, o, n) -> validateInput());
        pane.add(tagName, 0, 0);
        pane.add(new javafx.scene.control.Label("Tag message:"), 0, 1);
        tagMessage = new TextArea();
        tagMessage.setEditable(true);
        GridPane.setFillHeight(tagMessage, true);
        GridPane.setFillWidth(tagMessage, true);
        GridPane.setFillHeight(tagName, true);
        GridPane.setVgrow(tagMessage, Priority.ALWAYS);
        GridPane.setHgrow(tagName, Priority.ALWAYS);
        GridPane.setHgrow(tagMessage, Priority.ALWAYS);
        pane.setVgap(3);
        pane.add(tagMessage, 0, 2);
        return new Scene(pane);
    }

    /**
     * Returns the string typed into the tag message area.
     *
     * @return the tag message
     */
    public String getMessage() {
        return tagMessage.getText();
    }
}
