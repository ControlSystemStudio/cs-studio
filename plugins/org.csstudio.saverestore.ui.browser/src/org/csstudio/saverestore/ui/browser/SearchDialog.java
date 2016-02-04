package org.csstudio.saverestore.ui.browser;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.SearchCriterion;
import org.csstudio.ui.fx.util.FXBaseDialog;
import org.csstudio.ui.fx.util.FXUtilities;
import org.csstudio.ui.fx.util.UnfocusableCheckBox;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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

    private static final Map<Shell, SearchDialog> INSTANCE = new HashMap<>();

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
            parent.addDisposeListener(e -> INSTANCE.remove(parent));
            INSTANCE.put(parent, dialog);
        }
        return dialog;
    }

    private TextField text;
    private CheckBox commentBox;
    private CheckBox tagNameBox;
    private CheckBox tagMessageBox;
    private CheckBox userBox;
    private String backgroundColor;
    private Date startDate;
    private Date endDate;
    private List<SearchCriterion> lastResults = new ArrayList<>(0);

    /**
     * Creates an input dialog with OK and Cancel buttons. Note that the dialog will have no visual representation (no
     * widgets) until it is told to open.
     *
     * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
     */
    public SearchDialog(Shell parentShell) {
        super(parentShell, "Search for Snapshot",
            "Type in the text that you wish to search for and selected the fields on which you wish to perform the search:",
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
        if (userBox.isSelected()) {
            lastResults.add(SearchCriterion.USER);
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
        backgroundColor = FXUtilities.toBackgroundColorStyle(parent.getBackground());
        return super.createDialogArea(parent);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.ui.fx.util.FXBaseDialog#getScene(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Scene getScene(Composite parent) {
        GridPane pane = new GridPane();
        text = new TextField();
        text.setMaxWidth(Double.MAX_VALUE);
        text.textProperty().addListener((a, o, n) -> validateInput());
        text.setOnAction(e -> okPressed());
        commentBox = new UnfocusableCheckBox(SearchCriterion.COMMENT.readableName);
        commentBox.setOnAction(e -> validateInput());
        tagNameBox = new UnfocusableCheckBox(SearchCriterion.TAG_NAME.readableName);
        tagNameBox.setOnAction(e -> validateInput());
        tagMessageBox = new UnfocusableCheckBox(SearchCriterion.TAG_MESSAGE.readableName);
        tagMessageBox.setOnAction(e -> validateInput());
        userBox = new UnfocusableCheckBox(SearchCriterion.USER.readableName);
        userBox.setOnAction(e -> validateInput());
        tagNameBox.setSelected(true);
        DataProviderWrapper wrapper = SaveRestoreService.getInstance().getSelectedDataProvider();
        if (!wrapper.provider.isTaggingSupported()) {
            tagNameBox.setSelected(false);
            tagNameBox.setDisable(true);
            tagMessageBox.setDisable(true);
            commentBox.setSelected(true);
        }
        if (!lastResults.isEmpty()) {
            commentBox.setSelected(lastResults.contains(SearchCriterion.COMMENT));
            tagNameBox.setSelected(lastResults.contains(SearchCriterion.TAG_NAME));
            tagMessageBox.setSelected(lastResults.contains(SearchCriterion.TAG_MESSAGE));
            userBox.setSelected(lastResults.contains(SearchCriterion.USER));
        }
        final DatePicker startPicker = new DatePicker();
        startPicker.setOnAction(e -> {
            LocalDate date = startPicker.getValue();
            startDate = date == null ? null : java.sql.Date.valueOf(date);
            validateInput();
        });
        final DatePicker endPicker = new DatePicker();
        endPicker.setOnAction(e -> {
            LocalDate date = endPicker.getValue();
            endDate = date == null ? null : java.sql.Date.valueOf(date);
            validateInput();
        });
        setGridConstraints(text, true, false, Priority.ALWAYS, Priority.ALWAYS);
        setGridConstraints(commentBox, false, false, Priority.ALWAYS, Priority.NEVER);
        setGridConstraints(tagNameBox, false, false, Priority.ALWAYS, Priority.NEVER);
        setGridConstraints(tagMessageBox, false, false, Priority.ALWAYS, Priority.NEVER);
        setGridConstraints(userBox, false, false, Priority.ALWAYS, Priority.NEVER);

        GridPane datePane = new GridPane();
        datePane.setHgap(5);
        setGridConstraints(startPicker, false, false, Priority.NEVER, Priority.NEVER);
        setGridConstraints(endPicker, false, false, Priority.NEVER, Priority.NEVER);
        datePane.add(new Label("Start Date:"), 0, 0);
        datePane.add(new Label("End Date:"), 1, 0);
        datePane.add(startPicker, 0, 1);
        datePane.add(endPicker, 1, 1);

        pane.setMaxWidth(Double.MAX_VALUE);
        pane.setVgap(5);
        pane.add(text, 0, 0);
        pane.add(commentBox, 0, 1);
        pane.add(tagNameBox, 0, 2);
        pane.add(tagMessageBox, 0, 3);
        pane.add(userBox, 0, 4);
        pane.add(datePane, 0, 5);
        pane.setPrefWidth(getInitialSize().x);
        pane.setStyle(backgroundColor);
        return new Scene(pane);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.ui.fx.util.FXBaseDialog#validateInput()
     */
    @Override
    protected void validateInput() {
        if (!commentBox.isSelected() && !tagMessageBox.isSelected() && !tagNameBox.isSelected() && !userBox.isSelected()
            && startDate == null && endDate == null) {
            setErrorMessage("Select a time range or at least one field to perform the search on", false);
        } else if (startDate != null || endDate != null) {
            setErrorMessage(null, true);
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

    /**
     * Returns the start date of the selected time window.
     *
     * @return start date
     */
    public Optional<Date> getStartDate() {
        return Optional.ofNullable(startDate);
    }

    /**
     * Returns the end date of the selected time window.
     *
     * @return end date
     */
    public Optional<Date> getEndDate() {
        return Optional.ofNullable(endDate);
    }
}
