package org.csstudio.saverestore.ui.browser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.saverestore.SearchCriterion;
import org.csstudio.ui.fx.util.FXBaseDialog;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 *
 * <code>SearchDialog</code> is a Jface based input dialog using JavaFX components for entering the snapshot search
 * parameters.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SearchDialog extends FXBaseDialog<String> {

    private static Map<Shell, SearchDialog> INSTANCE = new HashMap<>();

    /**
     * Creates and return the shared instance of this dialog. The dialog is specific to the parent and preserves the
     * selection.
     *
     * @param parent the parent of the dialog
     * @return the shared instance for the given parent shell
     */
    public static final SearchDialog getSingletonInstance(final Shell parent) {
        SearchDialog dialog = INSTANCE.get(parent);
        if (dialog == null) {
            dialog = new SearchDialog(parent);
            parent.addDisposeListener(e -> {
                INSTANCE.remove(parent);
            });
            INSTANCE.put(parent, dialog);
        }
        return dialog;
    }

    private TextField text;
    private CheckBox commentBox;
    private CheckBox tagNameBox;
    private CheckBox tagMessageBox;
    private String color;
    private List<SearchCriterion> lastResults = new ArrayList<>(0);

    /**
     * Creates an input dialog with OK and Cancel buttons. Note that the dialog will have no visual representation (no
     * widgets) until it is told to open.
     *
     * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
     */
    public SearchDialog(Shell parentShell) {
        super(parentShell, "Search for Snapshot",
            "Type in the text that you wish to search for and selected the fields on which you wish to perform the search",
            "", s -> s == null || s.trim().isEmpty() ? "Please enter a search parameter." : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        lastResults = new ArrayList<>();
        if (commentBox.isSelected()) {
            lastResults.add(SearchCriterion.COMMENT);
        }
        if (tagNameBox.isSelected()) {
            lastResults.add(SearchCriterion.TAG_NAME);
        }
        if (tagMessageBox.isSelected()) {
            lastResults.add(SearchCriterion.TAG_MESSAGE);
        }
        super.okPressed();
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
     * @see org.csstudio.ui.fx.util.FXBaseDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        RGB rgb = parent.getBackground().getRGB();
        String red = Integer.toHexString(rgb.red);
        String green = Integer.toHexString(rgb.green);
        String blue = Integer.toHexString(rgb.blue);
        color = "#" + red + green + blue;
        return super.createDialogArea(parent);
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
        commentBox = new CheckBox(SearchCriterion.COMMENT.name);
        commentBox.setOnAction(e -> validateInput());
        tagNameBox = new CheckBox(SearchCriterion.TAG_NAME.name);
        tagNameBox.setOnAction(e -> validateInput());
        tagMessageBox = new CheckBox(SearchCriterion.TAG_MESSAGE.name);
        tagMessageBox.setOnAction(e -> validateInput());
        tagNameBox.setSelected(true);
        if (!lastResults.isEmpty()) {
            commentBox.setSelected(lastResults.contains(SearchCriterion.COMMENT));
            tagNameBox.setSelected(lastResults.contains(SearchCriterion.TAG_NAME));
            tagMessageBox.setSelected(lastResults.contains(SearchCriterion.TAG_MESSAGE));
        }
        GridPane.setFillWidth(text, true);
        GridPane.setVgrow(commentBox, Priority.NEVER);
        GridPane.setVgrow(tagNameBox, Priority.NEVER);
        GridPane.setVgrow(tagMessageBox, Priority.NEVER);
        GridPane.setHgrow(text, Priority.ALWAYS);
        GridPane.setHgrow(commentBox, Priority.ALWAYS);
        GridPane.setHgrow(tagNameBox, Priority.ALWAYS);
        GridPane.setHgrow(tagMessageBox, Priority.ALWAYS);
        pane.setMaxWidth(Double.MAX_VALUE);
        pane.setVgap(5);
        pane.add(text, 0, 0);
        pane.add(commentBox, 0, 1);
        pane.add(tagNameBox, 0, 2);
        pane.add(tagMessageBox, 0, 3);
        pane.setPrefWidth(540);
        pane.setStyle("-fx-background-color:" + color + ";");
        return new Scene(pane);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.ui.fx.util.FXBaseDialog#validateInput()
     */
    @Override
    protected void validateInput() {
        if (!commentBox.isSelected() && !tagMessageBox.isSelected() && !tagNameBox.isSelected()) {
            setErrorMessage("Select at least one field to perform the search on");
        } else {
            super.validateInput();
        }
    }

    /**
     * Returns the selected criteria.
     *
     * @return the list of selected criteria
     */
    public List<SearchCriterion> getSelectedCriteria() {
        return lastResults;
    }
}
