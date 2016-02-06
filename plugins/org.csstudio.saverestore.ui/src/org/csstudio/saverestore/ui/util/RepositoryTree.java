package org.csstudio.saverestore.ui.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.ui.Selector;
import org.csstudio.ui.fx.util.FXTextInputDialog;
import org.csstudio.ui.fx.util.InputValidator;
import org.eclipse.ui.IWorkbenchPart;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;

/**
 *
 * <code>RepositoryTree</code> is a component that provides a tree browser of the save and restore repository. It allows
 * user to select any single branch, base level, folder or beamline set within the repository. If created with the
 * <code>editable</code> parameter, user can also create new folders by selecting the appropriate item from the popup
 * menu.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class RepositoryTree extends TreeView<String> {

    /**
     * <code>Type</code> defines the type of the item in the tree
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    public static enum Type {
        ROOT, BRANCH, BASE, FOLDER, SET, LOADING, NOTLOADED
    }

    /**
     *
     * <code>BrowsingTreeItem</code> is used for all items in the tree.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    public class BrowsingTreeItem extends TreeItem<String> {
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

        /**
         * @return the type of this single item
         */
        public Type getType() {
            return type;
        }

        private void setUp() {
            expandedProperty().addListener((a, o, expanded) -> {
                if (expanded) {
                    List<TreeItem<String>> items = getChildren();
                    if (!items.isEmpty()) {
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

    private final IWorkbenchPart owner;
    private Selector selector;
    private TreeItem<String> root;
    private final boolean editable;
    private final BeamlineSet initialValue;
    private final String dataProviderId;

    /**
     * Constructs a new repository tree.
     *
     * @param owner the owner of this repository, used only for the popup message dialogs
     * @param editable true if user is allowed to create new folders
     * @param initialValue the initial value to select or add
     */
    public RepositoryTree(IWorkbenchPart owner, boolean editable, BeamlineSet initialValue) {
        this.owner = owner;
        this.editable = editable;
        this.initialValue = initialValue;
        this.dataProviderId = SaveRestoreService.getInstance().getSelectedDataProvider().getId();
        init();
    }

    private void init() {
        selector = new Selector(owner);
        setShowRoot(false);

        if (editable) {
            addEditableAspects();
        }

        root = new BrowsingTreeItem("Root", true);
        setRoot(root);
        root.getChildren().add(new BrowsingTreeItem());
        root.setExpanded(true);
        setPrefSize(500, 500);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        SaveRestoreService.getInstance().addPropertyChangeListener(SaveRestoreService.BUSY,
            e -> setDisable((Boolean) e.getNewValue()));
        selector.branchesProperty().addListener((a, o, n) -> branchesLoaded(n));
        selector.baseLevelsProperty().addListener((a, o, n) -> baseLevelsLoaded(n));
        selector.beamlineSetsProperty().addListener((a, o, n) -> beamlineSetsLoaded(n));
        branchesLoaded(selector.branchesProperty().get());

    }

    private void addEditableAspects() {
        final ContextMenu popup = new ContextMenu();
        MenuItem newFolderItem = new MenuItem("New Folder...");
        newFolderItem.setOnAction(e -> {
            popup.hide();
            final BrowsingTreeItem item = (BrowsingTreeItem) getSelectionModel().getSelectedItem();
            final List<String> names = new ArrayList<>();
            item.getChildren().forEach(x -> names.add(x.getValue()));
            FXTextInputDialog.get(owner.getSite().getShell(), "Folder Name", "Enter new folder name", null,
                new InputValidator<String>() {
                @Override
                public String validate(String s) {
                    return names.contains(s) ? "Folder '" + s + "' already exists."
                        : s.isEmpty() ? "Folder name cannot be empty."
                            : item.type == Type.BRANCH ? Selector.validateBaseLevelName(s) : null;
                }

                @Override
                public boolean isAllowedToProceed(String s) {
                    return !names.contains(s) && !s.trim().isEmpty();
                }
            }).ifPresent(f -> {
                if (item.type == Type.BRANCH) {
                    BrowsingTreeItem newItem = new BrowsingTreeItem(new BaseLevel(item.branch, f, f));
                    item.getChildren().add(newItem);
                    item.setExpanded(true);
                    getSelectionModel().select(newItem);
                } else if (item.type == Type.BASE || item.type == Type.FOLDER) {
                    BrowsingTreeItem newItem = new BrowsingTreeItem(f, false);
                    item.getChildren().add(newItem);
                    item.setExpanded(true);
                    getSelectionModel().select(newItem);
                }
            });
        });
        popup.getItems().add(newFolderItem);
        setContextMenu(popup);

        setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                final BrowsingTreeItem item = (BrowsingTreeItem) getSelectionModel().getSelectedItem();
                if (item.type == Type.LOADING || item.type == Type.NOTLOADED || item.type == Type.SET) {
                    return;
                }
                popup.show(RepositoryTree.this, e.getScreenX(), e.getScreenY());
            }
        });
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
                    if (initialValue != null) {
                        addPresetBaseLevel((BrowsingTreeItem) br, initialValue.getBaseLevel().orElse(null),
                            initialValue.getBranch());
                    }
                    break;
                }
            }
        } else {
            branchItem.getChildren().setAll(items);
            if (initialValue != null) {
                addPresetBaseLevel(branchItem, initialValue.getBaseLevel().orElse(null), initialValue.getBranch());
            }
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
            || (((BrowsingTreeItem) parentItem.getChildren().get(0)).type != Type.LOADING
                && ((BrowsingTreeItem) parentItem.getChildren().get(0)).type != Type.NOTLOADED)) {
            return;
        }

        Map<String, BrowsingTreeItem> items = new HashMap<>();
        parentItem.getChildren().clear();
        items.put("/", parentItem);
        for (BeamlineSet set : beamlineSets) {
            String[] path = set.getPath();
            StringBuilder currentPath = new StringBuilder(100);
            BrowsingTreeItem setParent;
            BrowsingTreeItem parent = parentItem;
            for (int i = 0; i < path.length - 1; i++) {
                currentPath.append('/').append(path[i]);
                setParent = items.get(currentPath.toString());
                if (setParent == null) {
                    setParent = new BrowsingTreeItem(path[i], false);
                    items.put(currentPath.toString(), setParent);
                    parent.getChildren().add(setParent);
                }
                parent = setParent;
            }
            parent.getChildren().add(new BrowsingTreeItem(set));
        }
        parentItem.setExpanded(true);
    }

    public BeamlineSet getValueFromComponent() {
        BrowsingTreeItem item = (BrowsingTreeItem) getSelectionModel().getSelectedItem();
        if (item == null) {
            return null;
        }
        List<String> names = new ArrayList<>();
        Branch branch = null;
        BaseLevel baseLevel = null;
        BeamlineSet set = null;
        TreeItem<String> theRoot = getRoot();
        while (item != theRoot) {
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

        if (set == null) {
            path[length] = "";
        } else {
            path[length] = set.getName();
        }

        BeamlineSet newSet = new BeamlineSet(branch, Optional.ofNullable(baseLevel), path, dataProviderId);
        StringBuilder sb = new StringBuilder(150).append('[').append(newSet.getBranch().getShortName());
        if (baseLevel != null) {
            sb.append('/').append(baseLevel.getStorageName());
        }
        sb.append(']').append(newSet.getPathAsString());
        return newSet;
    }

    public void setValueToComponent(BeamlineSet value) {
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
                if (base.getStorageName().equals(((BrowsingTreeItem) bl).base.getStorageName())) {
                    baseItem = (BrowsingTreeItem) bl;
                }
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
}
