/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.ui.browser;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
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

    private static class CriterionBox extends UnfocusableCheckBox {
        SearchCriterion criterion;
        CriterionBox(SearchCriterion criterion) {
            super(criterion.getReadableName());
            this.criterion = criterion;
            setSelected(this.criterion.isDefault());
        }
    }

    private TextField text;
    private CriterionBox[] criteriaBoxes;
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
        for (CriterionBox cb : criteriaBoxes) {
            if (cb.isSelected() && !cb.disabledProperty().get()) {
                lastResults.add(cb.criterion);
            }
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
        setGridConstraints(text, true, false, Priority.ALWAYS, Priority.ALWAYS);
        DataProviderWrapper wrapper = SaveRestoreService.getInstance().getSelectedDataProvider();
        List<SearchCriterion> availableCriteria = wrapper.getProvider().getSupportedSearchCriteria();
        criteriaBoxes = new CriterionBox[availableCriteria.size()];
        for (int i = 0; i < criteriaBoxes.length; i++) {
            SearchCriterion criterion = availableCriteria.get(i);
            criteriaBoxes[i] = new CriterionBox(criterion);
            criteriaBoxes[i].setOnAction(e -> {
                checkExclusiveSelection();
                validateInput();
            });
            if (!lastResults.isEmpty() && lastResults.contains(criterion)) {
                criteriaBoxes[i].setSelected(true);
            }
            setGridConstraints(criteriaBoxes[i], false, false, Priority.ALWAYS, Priority.NEVER);
        }
        checkExclusiveSelection();

        final DatePicker startPicker = new DatePicker();
        if (startDate != null) {
            startPicker.setValue(LocalDateTime
                    .ofInstant(Instant.ofEpochMilli(startDate.getTime()), ZoneId.systemDefault()).toLocalDate());
        }
        startPicker.setOnAction(e -> {
            LocalDate date = startPicker.getValue();
            startDate = date == null ? null : java.sql.Date.valueOf(date);
            validateInput();
        });
        final DatePicker endPicker = new DatePicker();
        if (endDate != null) {
            endPicker.setValue(LocalDateTime
                    .ofInstant(Instant.ofEpochMilli(endDate.getTime()), ZoneId.systemDefault()).toLocalDate());
        }
        endPicker.setOnAction(e -> {
            LocalDate date = endPicker.getValue();
            endDate = date == null ? null : java.sql.Date.valueOf(date);
            validateInput();
        });

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
        for (int i = 0; i < criteriaBoxes.length; i++) {
            pane.add(criteriaBoxes[i], 0, i+1);
        }
        pane.add(datePane, 0, 5);
        pane.setPrefWidth(getInitialSize().x);
        pane.setStyle(backgroundColor);
        return new Scene(pane);
    }

    private void checkExclusiveSelection() {
        for (int i = 0; i < criteriaBoxes.length; i++) {
            if (criteriaBoxes[i].criterion.isExclusive()) {
                if (criteriaBoxes[i].isSelected()) {
                    for (int j = 0; j < criteriaBoxes.length; j++) {
                        criteriaBoxes[j].setDisable(j != i);
                    }
                    return;
                }
            }
        }
        for (int j = 0; j < criteriaBoxes.length; j++) {
            criteriaBoxes[j].setDisable(false);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.ui.fx.util.FXBaseDialog#validateInput()
     */
    @Override
    protected void validateInput() {
        boolean noneSelected = true;
        for (CriterionBox cb : criteriaBoxes) {
            if (cb.isSelected()) {
                noneSelected = false;
                break;
            }
        }
        if (noneSelected && startDate == null && endDate == null) {
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
