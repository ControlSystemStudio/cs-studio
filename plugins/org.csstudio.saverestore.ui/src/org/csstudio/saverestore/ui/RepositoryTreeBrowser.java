package org.csstudio.saverestore.ui;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.ui.util.RepositoryTree;
import org.csstudio.saverestore.ui.util.RepositoryTree.BrowsingTreeItem;
import org.csstudio.saverestore.ui.util.RepositoryTree.Type;
import org.csstudio.ui.fx.util.FXBaseDialog;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.csstudio.ui.fx.util.StaticTextField;
import org.csstudio.ui.fx.util.FXUtilities;
import org.eclipse.ui.IWorkbenchPart;

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
 * dialog displays all branches, base levels and beamline sets that already exists and provide means to select an
 * existing location or create a new location for a new beamline set.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class RepositoryTreeBrowser extends FXBaseDialog<BeamlineSet> {

    private IWorkbenchPart owner;
    private RepositoryTree treeView;
    private TextField nameField;
    private TextField fullNameField;

    private BeamlineSet initialValue;

    /**
     * Construct a new repository tree browser.
     *
     * @param owner the owner view
     * @param preselection the preselected beamline set
     */
    public RepositoryTreeBrowser(IWorkbenchPart owner, BeamlineSet preselection) {
        super(owner.getSite().getShell(), "Select Location", "Select location where to store the beamline set",
            preselection,
            e -> e == null || e.getName().isEmpty() ? "Beamline Set name not provided"
                : e.getBranch() == null ? "No branch selected"
                    : e.getBaseLevel().isPresent() ? (e.getPath().length == 0 ? "No name and path provided" : null)
                        : SaveRestoreService.getInstance().getSelectedDataProvider().provider.areBaseLevelsSupported()
                            ? "No base level selected" : null);
        this.owner = owner;
        this.initialValue = preselection;
    }

    private Node createContents() {
        treeView = new RepositoryTree(owner, true, initialValue);

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

        Label nameLabel = new Label("Beamline Set Name:");
        Label fullNameLabel = new Label("Full Name:");

        String cl = FXUtilities.toBackgroundColorStyle(getTopComposite().getBackground());
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
    protected BeamlineSet getValueFromComponent() {
        BeamlineSet set = treeView.getValueFromComponent();
        if (set == null) {
            return null;
        }
        String[] path = set.getPath();
        String name = nameField.getText().trim();
        if (!name.isEmpty()) {
            path[path.length - 1] = name;
        }

        BeamlineSet newSet = new BeamlineSet(set.getBranch(), set.getBaseLevel(), path, set.getDataProviderId());
        fullNameField.setText(newSet.getFullyQualifiedName());
        return newSet;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#setValueToComponent(java.lang.Object)
     */
    @Override
    protected void setValueToComponent(BeamlineSet value) {
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
            boolean b = FXMessageDialog.openConfirm(getShell(), "Overwrite",
                "Beamline set '" + value.getDisplayName() + "' already exists. Are you sure you want to overwrite it");
            if (!b) {
                return;
            }
        }
        super.okPressed();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#getScene()
     */
    @Override
    protected Scene getScene() {
        return new Scene(new BorderPane(createContents()));
    }
}
