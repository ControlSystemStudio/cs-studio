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

import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.ui.util.RepositoryTree;
import org.csstudio.ui.fx.util.FXBaseDialog;
import org.csstudio.ui.fx.util.StaticTextField;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Composite;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * <code>ImportDataDialog</code> is a dialog that allows to select the location from which the data will be copied. The
 * location is returned in a form of a {@link SaveSet} though it might not be an actual save set. If it is just a
 * folder it will be missing the last part of the path. If it is a base level, it will not have the path defined at all.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ImportDataDialog extends FXBaseDialog<SaveSet> {

    private final IShellProvider shellProvider;
    private RepositoryTree treeView;
    private TextField fullNameField;

    /**
     * Construct a new repository tree browser.
     *
     * @param shellProvider the provider of the parent shell of all popup dialogs
     */
    public ImportDataDialog(IShellProvider shellProvider) {
        super(shellProvider.getShell(), "Select Source", "Select the location from which to copy the data", null,
            e -> e == null || e.getBranch() == null
                || e.getPath().length == 0
                    ? "Select a single item"
                    : e.getBranch() != null && !e.getBaseLevel().isPresent()
                        && (e.getPath().length == 0 || e.getPath()[0].isEmpty()) ? "Select more than just a branch"
                            : null);
        this.shellProvider = shellProvider;
    }

    private Node createContents() {
        treeView = new RepositoryTree(shellProvider, false, null);
        treeView.getSelectionModel().selectedItemProperty().addListener((a, o, n) -> validateInput());
        fullNameField = new StaticTextField();
        fullNameField.setMaxWidth(Double.MAX_VALUE);
        Label fullNameLabel = new Label("Selected Item:");

        GridPane pane = new GridPane();
        pane.setVgap(5);
        pane.setHgap(3);

        setGridConstraints(treeView, true, true, Priority.ALWAYS, Priority.ALWAYS);
        setGridConstraints(fullNameField, true, false, Priority.ALWAYS, Priority.NEVER);
        setGridConstraints(fullNameLabel, false, false, Priority.NEVER, Priority.NEVER);

        pane.add(treeView, 0, 0, 2, 1);
        pane.add(fullNameLabel, 0, 1, 1, 1);
        pane.add(fullNameField, 1, 1, 1, 1);

        pane.setPrefWidth(getInitialSize().x);
        return pane;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#getValueFromComponent()
     */
    @Override
    protected SaveSet getValueFromComponent() {
        SaveSet set = treeView.getValueFromComponent();
        if (set == null) {
            fullNameField.setText("");
        } else {
            fullNameField.setText(set.getFullyQualifiedName());
        }
        return set;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#setValueToComponent(java.lang.Object)
     */
    @Override
    protected void setValueToComponent(SaveSet value) {
        treeView.setValueToComponent(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.ui.fx.util.FXBaseDialog#getScene(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Scene getScene(Composite parent) {
        parent.addDisposeListener(e -> treeView.dispose());
        return new Scene(new BorderPane(createContents()));
    }
}
