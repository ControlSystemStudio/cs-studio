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
package org.csstudio.saverestore.ui;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.ui.util.RepositoryTree;
import org.csstudio.saverestore.ui.util.RepositoryTree.BrowsingTreeItem;
import org.csstudio.saverestore.ui.util.RepositoryTree.Type;
import org.csstudio.ui.fx.util.FXBaseDialog;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.csstudio.ui.fx.util.FXUtilities;
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
 *
 * <code>RepositoryTreeBrowser</code> is a dialog that provides a tree browser for the save and restore repository. The
 * dialog displays all branches, base levels and save sets that already exists and provide means to select an
 * existing location or create a new location for a new save set.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class RepositoryTreeBrowser extends FXBaseDialog<SaveSet> {

    private final IShellProvider shellProvider;
    private RepositoryTree treeView;
    private TextField nameField;
    private TextField fullNameField;

    private final SaveSet initialValue;

    /**
     * Construct a new repository tree browser.
     *
     * @param shellProvider the provider of the parent shell for all popup dialogs
     * @param preselection the preselected save set
     */
    public RepositoryTreeBrowser(IShellProvider shellProvider, SaveSet preselection) {
        super(shellProvider.getShell(), "Select Location", "Select location where to store the save set",
            preselection,
            e -> e == null || e.getName().isEmpty() ? "Save Set name not provided"
                : e.getBranch() == null ? "No branch selected"
                    : e.getBaseLevel().isPresent() ? (e.getPath().length == 0 ? "No name and path provided" : null)
                        : SaveRestoreService.getInstance().getSelectedDataProvider().getProvider().areBaseLevelsSupported()
                            ? "No base level selected" : null);
        this.shellProvider = shellProvider;
        this.initialValue = preselection;
    }

    private Node createFXContents(Composite parent) {
        treeView = new RepositoryTree(shellProvider, true, initialValue);

        treeView.getSelectionModel().selectedItemProperty().addListener((a, o, n) -> {
            BrowsingTreeItem item = (BrowsingTreeItem) n;
            if (item.getType() == Type.SET) {
                nameField.setText(item.getValue());
            }
            validateInput();
        });

        nameField = new TextField();
        nameField.setMaxWidth(Double.MAX_VALUE);
        nameField.textProperty().addListener((a, o, n) -> validateInput());

        fullNameField = new StaticTextField();
        fullNameField.setMaxWidth(Double.MAX_VALUE);

        Label nameLabel = new Label("Save Set Name:");
        Label fullNameLabel = new Label("Full Name:");

        String cl = FXUtilities.toBackgroundColorStyle(parent.getBackground());
        nameLabel.setStyle(cl);
        fullNameLabel.setStyle(cl);

        GridPane pane = new GridPane();
        pane.setVgap(5);
        pane.setHgap(3);
        pane.setStyle(cl);

        setGridConstraints(treeView, true, true, Priority.ALWAYS, Priority.ALWAYS);
        setGridConstraints(fullNameField, true, false, Priority.ALWAYS, Priority.NEVER);
        setGridConstraints(nameField, true, false, Priority.ALWAYS, Priority.NEVER);
        setGridConstraints(fullNameLabel, false, false, Priority.NEVER, Priority.NEVER);
        setGridConstraints(nameLabel, false, false, Priority.NEVER, Priority.NEVER);

        pane.add(treeView, 0, 0, 2, 1);
        pane.add(nameLabel, 0, 1, 1, 1);
        pane.add(nameField, 1, 1, 1, 1);
        pane.add(fullNameLabel, 0, 2, 1, 1);
        pane.add(fullNameField, 1, 2, 1, 1);

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
            return null;
        }
        String[] path = set.getPath();
        String name = nameField.getText().trim();
        if (!name.isEmpty()) {
            path[path.length - 1] = name;
        }

        SaveSet newSet = new SaveSet(set.getBranch(), set.getBaseLevel(), path, set.getDataProviderId());
        fullNameField.setText(newSet.getFullyQualifiedName());
        return newSet;
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
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        String name = treeView.getSelectionModel().getSelectedItem().getValue();
        if (value.getName().equals(name)) {
            if (!FXMessageDialog.openConfirm(getShell(), "Overwrite",
                "Save set '" + value.getDisplayName() + "' already exists. Are you sure you want to overwrite it")) {
                return;
            }
        }
        super.okPressed();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.ui.fx.util.FXBaseDialog#getScene(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Scene getScene(Composite parent) {
        parent.addDisposeListener(e -> treeView.dispose());
        return new Scene(new BorderPane(createFXContents(parent)));
    }
}
