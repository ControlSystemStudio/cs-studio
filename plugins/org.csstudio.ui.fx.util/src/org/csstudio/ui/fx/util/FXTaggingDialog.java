package org.csstudio.ui.fx.util;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
     * @see org.csstudio.ui.fx.util.FXBaseDialog#getScene(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Scene getScene(Composite parent) {
        GridPane pane = new GridPane();
        tagName = new TextField();
        tagName.textProperty().addListener((a, o, n) -> validateInput());
        pane.add(tagName, 0, 0);
        pane.add(new Label("Tag message:"), 0, 1);
        tagMessage = new TextArea();
        tagMessage.setEditable(true);
        GridPane.setMargin(tagName, new Insets(0, 0, 10, 0));
        GridPane.setMargin(tagMessage, new Insets(5, 0, 0, 0));
        setGridConstraints(tagMessage, true, true, Priority.ALWAYS, Priority.ALWAYS);
        setGridConstraints(tagName, true, true, Priority.ALWAYS, Priority.ALWAYS);
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
