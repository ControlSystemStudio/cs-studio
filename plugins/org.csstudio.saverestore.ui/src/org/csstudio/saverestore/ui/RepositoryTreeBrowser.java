package org.csstudio.saverestore.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.SerializableBaseLevel;
import org.csstudio.ui.fx.util.FXBaseDialog;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.csstudio.ui.fx.util.FXTextInputDialog;
import org.eclipse.ui.IWorkbenchPart;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
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

    private static enum Type {
        ROOT, BRANCH, BASE, FOLDER, SET, LOADING, NOTLOADED
    }

    private class BrowsingTreeItem extends TreeItem<String> {
        Type type;
        Branch branch;
        BaseLevel base;
        BeamlineSet set;

        BrowsingTreeItem(Branch item) {
            super(item.getShortName());
            this.type = Type.BRANCH;
            this.branch = item;
            setUp();
        }

        BrowsingTreeItem(BaseLevel item) {
            super(item.getStorageName());
            this.type = Type.BASE;
            this.base = item;
            setUp();
        }

        BrowsingTreeItem(BeamlineSet item) {
            super(item.getName());
            this.set = item;
            this.type = Type.SET;
        }

        BrowsingTreeItem(String folder, boolean root) {
            super(folder);
            this.type = root ? Type.ROOT : Type.FOLDER;
        }

        BrowsingTreeItem() {
            super("Loading...");
            this.type = Type.NOTLOADED;
        }

        private void setUp() {
            expandedProperty().addListener((a, o, expanded) -> {
                if (expanded) {
                    List<TreeItem<String>> items = getChildren();
                    if (items.size() > 0) {
                        BrowsingTreeItem child = (BrowsingTreeItem) items.get(0);
                        if (child.type == Type.NOTLOADED) {
                            child.type = Type.LOADING;
                            if (type == Type.BRANCH) {
                                selector.selectedBranchProperty().setValue(branch);
                            } else if (type == Type.BASE) {
                                selector.selectedBranchProperty().setValue(base.getBranch());
                                selector.selectedBaseLevelProperty().setValue(base);
                            }
                        }
                    }
                }
            });
        }
    }

    private IWorkbenchPart owner;
    private TreeView<String> treeView;
    private TextField nameField;
    private Selector selector;
    private TreeItem<String> root;
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
                   : e.getPath().length == 0 ? "No name and path provided" : null);
        this.owner = owner;
        this.initialValue = preselection;
    }

    private Node createContents() {
        selector = new Selector(owner);
        treeView = new TreeView<>();
        treeView.setShowRoot(false);

        final ContextMenu popup = new ContextMenu();
        MenuItem newFolderItem = new MenuItem("New Folder...");
        newFolderItem.setOnAction(e -> {
            popup.hide();
            final BrowsingTreeItem item = (BrowsingTreeItem) treeView.getSelectionModel().getSelectedItem();
            final List<String> names = new ArrayList<>();
            item.getChildren().forEach(x -> names.add(x.getValue()));
            new FXTextInputDialog(getShell(), "Folder Name", "Enter new folder name", null,
                    s -> names.contains(s) ? "Folder '" + s + "' already exists."
                            : s.isEmpty() ? "Folder name cannot be empty."
                                    : item.type == Type.BRANCH ? selector.validateBaseLevelName(s) : null).openAndWait()
                                            .ifPresent(f -> {
                if (item.type == Type.BRANCH) {
                    BrowsingTreeItem newItem = new BrowsingTreeItem(new SerializableBaseLevel(f, f, item.branch));
                    item.getChildren().add(newItem);
                    item.setExpanded(true);
                    treeView.getSelectionModel().select(newItem);
                } else if (item.type == Type.BASE || item.type == Type.FOLDER) {
                    BrowsingTreeItem newItem = new BrowsingTreeItem(f, false);
                    item.getChildren().add(newItem);
                    item.setExpanded(true);
                    treeView.getSelectionModel().select(newItem);
                }
            });
        });
        popup.getItems().add(newFolderItem);
        treeView.setContextMenu(popup);

        treeView.setOnMouseReleased(e -> {
            if (e.isSecondaryButtonDown()) {
                final BrowsingTreeItem item = (BrowsingTreeItem) treeView.getSelectionModel().getSelectedItem();
                if (item.type == Type.LOADING || item.type == Type.NOTLOADED || item.type == Type.SET) {
                    return;
                }
                popup.show(treeView, e.getX(), e.getY());
            }
        });
        treeView.getSelectionModel().selectedItemProperty().addListener((a, o, n) -> {
            BrowsingTreeItem item = (BrowsingTreeItem) n;
            if (item.type == Type.SET) {
                nameField.setText(item.getValue());
            }
            validateInput();
        });

        root = new BrowsingTreeItem("Root", true);
        treeView.setRoot(root);
        root.getChildren().add(new BrowsingTreeItem());
        root.setExpanded(true);
        treeView.setPrefSize(500, 500);
        treeView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        nameField = new TextField();
        nameField.setMaxWidth(Double.MAX_VALUE);
        nameField.textProperty().addListener((a, o, n) -> validateInput());

        fullNameField = new TextField();
        fullNameField.setMaxWidth(Double.MAX_VALUE);
        fullNameField.setEditable(false);
        fullNameField.setFocusTraversable(false);

        Label nameLabel = new Label("Beamline Set Name:");
        Label fullNameLabel = new Label("Full Name:");

        GridPane pane = new GridPane();
        pane.setVgap(5);
        pane.setHgap(3);

        GridPane.setFillHeight(treeView, true);
        GridPane.setFillHeight(fullNameField, false);
        GridPane.setFillHeight(fullNameLabel, false);
        GridPane.setFillWidth(treeView, true);
        GridPane.setFillWidth(fullNameField, true);
        GridPane.setFillWidth(fullNameLabel, false);
        GridPane.setHgrow(treeView, Priority.ALWAYS);
        GridPane.setHgrow(fullNameField, Priority.ALWAYS);
        GridPane.setHgrow(fullNameLabel, Priority.NEVER);
        GridPane.setVgrow(treeView, Priority.ALWAYS);
        GridPane.setVgrow(fullNameField, Priority.NEVER);
        GridPane.setVgrow(fullNameLabel, Priority.NEVER);

        GridPane.setFillHeight(nameLabel, false);
        GridPane.setFillWidth(nameLabel, false);
        GridPane.setHgrow(nameLabel, Priority.NEVER);
        GridPane.setVgrow(nameLabel, Priority.NEVER);
        GridPane.setFillHeight(nameField, false);
        GridPane.setFillWidth(nameField, true);
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setVgrow(nameField, Priority.NEVER);

        pane.add(treeView, 0, 0, 2, 1);
        pane.add(nameLabel, 0, 1, 1, 1);
        pane.add(nameField, 1, 1, 1, 1);
        pane.add(fullNameLabel, 0, 2, 1, 1);
        pane.add(fullNameField, 1, 2, 1, 1);

        SaveRestoreService.getInstance().addPropertyChangeListener(SaveRestoreService.BUSY,
                (e) -> treeView.setDisable((Boolean) e.getNewValue()));
        selector.branchesProperty().addListener((a, o, n) -> branchesLoaded(n));
        selector.baseLevelsProperty().addListener((a, o, n) -> baseLevelsLoaded(n));
        selector.beamlineSetsProperty().addListener((a, o, n) -> beamlineSetsLoaded(n));
        branchesLoaded(selector.branchesProperty().get());

        return pane;
    }

    private void branchesLoaded(List<Branch> branches) {
        final List<BrowsingTreeItem> items = new ArrayList<>();
        branches.forEach(e -> {
            BrowsingTreeItem item = new BrowsingTreeItem(e);
            item.getChildren().add(new BrowsingTreeItem());
            items.add(item);
        });
        root.getChildren().setAll(items);
        root.setExpanded(true);
        setValueToComponent(initialValue);
    }

    private void baseLevelsLoaded(List<BaseLevel> baseLevels) {
        final List<BrowsingTreeItem> items = new ArrayList<>();
        List<TreeItem<String>> branches = root.getChildren();
        BrowsingTreeItem branchItem = null;
        for (BaseLevel b : baseLevels) {
            Branch branch = b.getBranch();
            if (branchItem == null) {
                for (TreeItem<String> br : branches) {
                    if (((BrowsingTreeItem) br).branch.equals(branch)) {
                        branchItem = (BrowsingTreeItem) br;
                        break;
                    }
                }
                if (branchItem == null) {
                    throw new IllegalStateException(
                            "The repository structure is corrupted. Could not find the branch '" + branch + "'.");
                }
                // if (branchItem.getChildren().size() != 1) {
                // Type type = ((BrowsingTreeItem) branchItem.getChildren().get(0)).type;
                // if (type == Type.LOADING || type == Type.NOTLOADED) {
                // return;
                // }
                // }
            }
            BrowsingTreeItem item = new BrowsingTreeItem(b);
            item.getChildren().add(new BrowsingTreeItem());
            items.add(item);
        }

        if (branchItem == null || baseLevels.isEmpty()) {
            // branchItem is always null here, but eclipse complains if there is no check
            for (TreeItem<String> br : branches) {
                if (br.isExpanded() && br.getChildren().size() == 1
                        && ((BrowsingTreeItem) br.getChildren().get(0)).type == Type.LOADING) {
                    br.getChildren().clear();
                    addPresetBaseLevel((BrowsingTreeItem) br, initialValue.getBaseLevel().orElse(null),
                            initialValue.getBranch());
                    break;
                }
            }
        } else {
            branchItem.getChildren().setAll(items);
            addPresetBaseLevel(branchItem, initialValue.getBaseLevel().orElse(null), initialValue.getBranch());
            branchItem.setExpanded(true);
        }
    }

    private void beamlineSetsLoaded(List<BeamlineSet> beamlineSets) {
        if (beamlineSets.isEmpty()) {
            return;
        }
        Branch branch = beamlineSets.get(0).getBranch();
        Optional<BaseLevel> base = beamlineSets.get(0).getBaseLevel();
        BrowsingTreeItem parentItem = null;
        if (base.isPresent()) {
            BaseLevel baseLevel = base.get();
            for (TreeItem<String> br : root.getChildren()) {
                if (((BrowsingTreeItem) br).branch.equals(branch)) {
                    for (TreeItem<String> bl : br.getChildren()) {
                        if (((BrowsingTreeItem) bl).base.equals(baseLevel)) {
                            parentItem = (BrowsingTreeItem) bl;
                            break;
                        }
                    }
                    break;
                }
            }
            if (parentItem == null) {
                throw new IllegalStateException("The repository structure is corrupted. Could not find the base level '"
                        + baseLevel.getPresentationName() + "'.");
            }
        } else {
            for (TreeItem<String> br : root.getChildren()) {
                if (((BrowsingTreeItem) br).branch.equals(branch)) {
                    parentItem = (BrowsingTreeItem) br;
                    break;
                }
            }
            if (parentItem == null) {
                throw new IllegalStateException(
                        "The repository structure is corrupted. Could not find the branch '" + branch + "'.");
            }
        }

        if (parentItem.getChildren().size() != 1
                || ((BrowsingTreeItem) parentItem.getChildren().get(0)).type != Type.LOADING) {
            return;
        }

        Map<String, BrowsingTreeItem> items = new HashMap<>();
        parentItem.getChildren().clear();
        items.put("/", parentItem);
        for (BeamlineSet set : beamlineSets) {
            String[] path = set.getPath();
            String currentPath = "";
            BrowsingTreeItem setParent = parentItem;
            BrowsingTreeItem parent = parentItem;
            for (int i = 0; i < path.length - 1; i++) {
                currentPath += "/" + path[i];
                setParent = items.get(currentPath);
                if (setParent == null) {
                    setParent = new BrowsingTreeItem(path[i], false);
                    items.put(currentPath, setParent);
                    parent.getChildren().add(setParent);
                }
                parent = setParent;
            }
            parent.getChildren().add(new BrowsingTreeItem(set));
        }
        parentItem.setExpanded(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#getValueFromComponent()
     */
    @Override
    protected BeamlineSet getValueFromComponent() {
        BrowsingTreeItem item = (BrowsingTreeItem) treeView.getSelectionModel().getSelectedItem();
        if (item == null) {
            return null;
        }
        List<String> names = new ArrayList<>();
        Branch branch = null;
        BaseLevel baseLevel = null;
        BeamlineSet set = null;
        while (item != root) {
            if (item.type == Type.SET) {
                set = item.set;
            } else if (item.type == Type.FOLDER) {
                names.add(item.getValue());
            } else if (item.type == Type.BASE) {
                baseLevel = item.base;
            } else if (item.type == Type.BRANCH) {
                branch = item.branch;
            }
            item = (BrowsingTreeItem) item.getParent();
        }
        int length = names.size();
        final String[] path = new String[length + 1];
        for (int i = 0; i < length; i++) {
            path[i] = names.get(length - 1 - i);
        }
        String name = nameField.getText().trim();
        if (!name.isEmpty()) {
            path[length] = name;
        } else if (set != null) {
            path[length] = set.getName();
        } else {
            path[length] = "";
        }

        BeamlineSet newSet = new BeamlineSet(branch, Optional.ofNullable(baseLevel), path);
        StringBuilder sb = new StringBuilder(150).append('[').append(newSet.getBranch().getShortName());
        if (baseLevel != null) {
            sb.append('/').append(baseLevel.getStorageName());
        }
        sb.append(']').append(newSet.getPathAsString());
        fullNameField.setText(sb.toString());
        return newSet;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#setValueToComponent(java.lang.Object)
     */
    @Override
    protected void setValueToComponent(BeamlineSet value) {
        if (value == null) {
            return;
        }
        Branch branch = value.getBranch();
        BaseLevel base = value.getBaseLevel().orElse(null);
        for (TreeItem<String> t : root.getChildren()) {
            if (((BrowsingTreeItem) t).branch.equals(branch)) {
                addPresetBaseLevel((BrowsingTreeItem) t, base, branch);
            }
        }
    }

    private BrowsingTreeItem addPresetBaseLevel(BrowsingTreeItem branch, BaseLevel base, Branch baseBranch) {
        if (base == null || !baseBranch.equals(branch.branch)) {
            return null;
        }
        BrowsingTreeItem baseItem = null;
        for (TreeItem<String> bl : branch.getChildren()) {
            if (((BrowsingTreeItem) bl).type == Type.BASE) {
                if (base.getStorageName().equals(((BrowsingTreeItem) bl).base.getStorageName()))
                    baseItem = (BrowsingTreeItem) bl;
                break;
            }
        }
        if (baseItem == null) {
            BrowsingTreeItem item = new BrowsingTreeItem(base);
            branch.getChildren().add(item);
        } else {
            baseItem.setExpanded(true);
        }
        return null;
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
                    "Beamline set '" + value.getFullName() + "' already exists. Are you sure you want to overwrite it");
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
        BorderPane pane = new BorderPane(createContents());
        Scene scene = new Scene(pane);
        return scene;
    }

}
