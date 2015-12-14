package org.csstudio.saverestore.ui.browser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.ui.Selector;
import org.csstudio.saverestore.ui.util.SnapshotDataFormat;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.csstudio.ui.fx.util.FXTaggingDialog;
import org.eclipse.fx.ui.workbench3.FXViewPart;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 *
 * <code>BrowserView</code> is the view that provides the browsing facilities for save and restore. The view consists of
 * an accordion panel, composed of three parts: isotopes, beamline sets and snapshots selector.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class BrowserView extends FXViewPart {

    private static final Image BEAMLINE_SET_IMAGE = new Image(BrowserView.class.getResourceAsStream("/icons/txt.png"));

    private static class BeamlineSetWrapper {

        final BeamlineSet set;
        final String[] path;

        BeamlineSetWrapper(BeamlineSet set, String name) {
            this.set = set;
            this.path = name != null ? name.split("\\/") : set.getPath();
        }

        boolean isFolder() {
            return set == null;
        }

        @Override
        public String toString() {
            return path[path.length - 1];
        }

    }

    private static class BeamlineSetTreeItem extends TreeItem<BeamlineSetWrapper> {
        BeamlineSetTreeItem(BeamlineSetWrapper set) {
            super(set, new ImageView(BEAMLINE_SET_IMAGE));
        }

        BeamlineSetTreeItem(String name) {
            super(new BeamlineSetWrapper(null, name));
        }
    }

    private BaseLevelBrowser<BaseLevel> baseLevelBrowser;
    private TreeView<BeamlineSetWrapper> beamlineSetsTree;
    private ListView<Snapshot> snapshotsList;
    private TitledPane snapshotsPane;
    private TitledPane baseLevelPane;
    private TitledPane beamlineSetsPane;
    private VBox mainPane;
    private VBox dataPane;

    private Selector selector = new Selector(this);
    private ActionManager actionManager = new ActionManager(selector, this);

    private boolean searchMode = false;

    private Scene scene;

    /**
     * @return the selector bound to this view
     */
    public Selector getSelector() {
        return selector;
    }

    /**
     * @return the action manager bound to this view
     */
    public ActionManager getActionManager() {
        return actionManager;
    }

    @Override
    protected Scene createFxScene() {
        BorderPane main = new BorderPane();
        scene = new Scene(main);

        Node beamlineSets = createBeamlineSetsPane();
        Node snapshots = createSnapshotsPane();

        mainPane = new VBox();
        dataPane = new VBox();
        VBox.setVgrow(beamlineSets, Priority.ALWAYS);
        VBox.setVgrow(snapshots, Priority.ALWAYS);
        dataPane.getChildren().addAll(beamlineSets, snapshotsPane);
        VBox.setVgrow(dataPane, Priority.ALWAYS);

        beamlineSetsPane.setExpanded(true);
        snapshotsPane.setExpanded(true);
        Optional<BaseLevelBrowser<BaseLevel>> browser = new BaseLevelBrowserProvider().getBaseLevelBrowser();
        if (browser.isPresent()) {
            Node elements = createBaseLevelsPane(browser.get());
            VBox.setVgrow(baseLevelPane, Priority.NEVER);
            mainPane.getChildren().setAll(elements, dataPane);
            baseLevelPane.setExpanded(true);
        } else {
            mainPane.getChildren().setAll(dataPane);
        }
        main.setCenter(mainPane);

        init();
        return scene;
    }

    private Node createBaseLevelsPane(BaseLevelBrowser<BaseLevel> browser) {
        BorderPane content = new BorderPane();

        baseLevelBrowser = browser;

        content.setCenter(baseLevelBrowser.getFXContent());
        baseLevelPane = new TitledPane(browser.getTitleFor(Optional.empty(), Optional.empty()), content);
        baseLevelPane.setMaxHeight(Double.MAX_VALUE);

        GridPane titleBox = new GridPane();
        titleBox.setHgap(5);
        Label titleText = new Label(browser.getTitleFor(Optional.empty(), Optional.empty()));
        titleText.textProperty().bind(baseLevelPane.textProperty());
        ToggleButton filterButton = new ToggleButton("",
            new ImageView(new Image(BrowserView.class.getResourceAsStream("/icons/filter_ps.png"))));
        filterButton.setTooltip(new Tooltip("Disable non-existing"));
        filterButton.selectedProperty().addListener((a, o, n) -> baseLevelBrowser.setShowOnlyAvailable(n));
        setUpTitlePaneNode(titleText, true);
        setUpTitlePaneNode(filterButton, false);
        titleBox.addRow(0, titleText, filterButton);
        titleBox.setMaxWidth(Double.MAX_VALUE);
        titleText.setMaxWidth(Double.MAX_VALUE);
        baseLevelPane.setGraphic(titleBox);
        baseLevelPane.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        titleBox.prefWidthProperty().bind(scene.widthProperty().subtract(34));
        filterButton.setSelected(true);

        return baseLevelPane;
    }

    private Node createBeamlineSetsPane() {
        GridPane content = new GridPane();

        beamlineSetsTree = new TreeView<>();
        beamlineSetsTree.getSelectionModel().selectedItemProperty().addListener((a, o, n) -> selector
            .selectedBeamlineSetProperty().set(n == null || n.getValue().isFolder() ? null : n.getValue().set));
        beamlineSetsTree.setShowRoot(false);

        final ContextMenu popup = new ContextMenu();
        MenuItem deleteSetItem = new MenuItem("Delete...");
        deleteSetItem.setOnAction(e -> {
            popup.hide();
            BeamlineSetTreeItem item = (BeamlineSetTreeItem) beamlineSetsTree.getSelectionModel().getSelectedItem();
            boolean delete = FXMessageDialog.openQuestion(getSite().getShell(), "Delete Beamline Set",
                "Are you sure you want to delete beamline set '" + item.getValue().set.getPathAsString() + "'?");
            if (delete) {
                actionManager.deleteBeamlineSet(item.getValue().set);
            }
        });
        popup.getItems().add(deleteSetItem);
        beamlineSetsTree.setContextMenu(popup);
        beamlineSetsTree.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                BeamlineSetTreeItem item = (BeamlineSetTreeItem) beamlineSetsTree.getSelectionModel().getSelectedItem();
                if (item.getValue().set != null) {
                    popup.show(beamlineSetsTree, e.getScreenX(), e.getScreenY());
                } else {
                    popup.hide();
                }
            }
        });

        GridPane.setVgrow(beamlineSetsTree, Priority.ALWAYS);
        GridPane.setHgrow(beamlineSetsTree, Priority.ALWAYS);
        GridPane.setFillWidth(beamlineSetsTree, true);
        GridPane.setFillHeight(beamlineSetsTree, true);
        content.add(beamlineSetsTree, 0, 0);

        beamlineSetsPane = new TitledPane("Beamline Sets", content);
        beamlineSetsPane.setMaxHeight(Double.MAX_VALUE);

        GridPane titleBox = new GridPane();
        titleBox.setHgap(5);
        Label titleText = new Label("Beamline Sets");
        titleText.textProperty().bind(beamlineSetsPane.textProperty());
        final Button newButton = new Button("New");
        newButton.setTooltip(new Tooltip("Create a new Beamline Set"));
        Button editButton = new Button("Edit");
        editButton.setTooltip(new Tooltip("Edit selected Beamline Set"));
        Button importButton = new Button("Import");
        importButton.setTooltip(new Tooltip("Import Beamline Sets from another location"));
        Button openButton = new Button("Open");
        openButton.setTooltip(new Tooltip("Open selected Beamline Set in Snapshot Viewer"));
        openButton.disableProperty()
            .bind(selector.selectedBeamlineSetProperty().isNull().or(beamlineSetsPane.expandedProperty().not()));
        openButton.setOnAction(e -> actionManager.openBeamlineSet(selector.selectedBeamlineSetProperty().get()));
        editButton.disableProperty()
            .bind(selector.selectedBeamlineSetProperty().isNull().or(beamlineSetsPane.expandedProperty().not()));
        editButton.setOnAction(e -> actionManager.editBeamlineSet(selector.selectedBeamlineSetProperty().get()));

        newButton.setOnAction(e -> actionManager.newBeamlineSet());
        importButton.disableProperty()
            .bind(selector.selectedBaseLevelProperty().isNull().or(beamlineSetsPane.expandedProperty().not()));
        importButton.setOnAction(
            e -> new ImportDataDialog(BrowserView.this).openAndWait().ifPresent(actionManager::importFrom));

        SaveRestoreService.getInstance().addPropertyChangeListener(SaveRestoreService.SELECTED_DATA_PROVIDER,
            (e) -> setUpSetButtons(newButton, importButton, (DataProviderWrapper) e.getNewValue()));
        setUpSetButtons(newButton, importButton, SaveRestoreService.getInstance().getSelectedDataProvider());

        setUpTitlePaneNode(titleText, true);
        setUpTitlePaneNode(newButton, false);
        setUpTitlePaneNode(importButton, false);
        setUpTitlePaneNode(editButton, false);
        setUpTitlePaneNode(openButton, false);
        titleBox.addRow(0, titleText, importButton, newButton, editButton, openButton);
        titleBox.setMaxWidth(Double.MAX_VALUE);
        titleText.setMaxWidth(Double.MAX_VALUE);
        beamlineSetsPane.setGraphic(titleBox);
        beamlineSetsPane.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        titleBox.prefWidthProperty().bind(scene.widthProperty().subtract(34));

        beamlineSetsPane.expandedProperty()
            .addListener((a, o, n) -> VBox.setVgrow(beamlineSetsPane, n ? Priority.ALWAYS : Priority.NEVER));
        return beamlineSetsPane;
    }

    private void setUpSetButtons(Button newButton, Button importButton, DataProviderWrapper dpw) {
        newButton.disableProperty().unbind();
        if (dpw != null) {
            if (dpw.provider.areBaseLevelsSupported()) {
                importButton.setVisible(true);
                newButton.disableProperty()
                    .bind(selector.selectedBaseLevelProperty().isNull().or(beamlineSetsPane.expandedProperty().not()));
            } else {
                importButton.setVisible(false);
                newButton.disableProperty().bind(beamlineSetsPane.expandedProperty().not());
            }
        } else {
            importButton.setVisible(false);
            newButton.setDisable(true);
        }
    }

    private Node createSnapshotsPane() {
        BorderPane content = new BorderPane();
        snapshotsList = new ListView<>();
        snapshotsList.setCellFactory(e -> new ListCell<Snapshot>() {
            public void updateItem(Snapshot item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove("tagged-cell");
                if (item != null && !empty) {
                    StringBuilder sb = new StringBuilder(300);
                    sb.append(item.getComment());
                    String message = item.getParameters().get(Snapshot.TAG_MESSAGE);
                    String tag = item.getParameters().get(Snapshot.TAG_NAME);
                    if (tag != null) {
                        sb.append("\n\n").append(tag).append('\n').append(message);
                        getStyleClass().add("tagged-cell");
                    }
                    setTooltip(new Tooltip(sb.toString()));
                    if (searchMode) {
                        StringBuilder text = new StringBuilder(300);
                        item.getBeamlineSet().getBaseLevel()
                            .ifPresent(e -> text.append('[').append(e).append(']').append(' '));
                        text.append(item.getBeamlineSet().getPathAsString()).append('\n');
                        text.append(item.toString());
                        setText(text.toString());
                    } else {
                        setText(item.toString());
                    }
                } else {
                    setTooltip(null);
                    setText(null);
                }
            }
        });
        snapshotsList.getStylesheets().add(this.getClass().getResource("taggedCell.css").toExternalForm());

        snapshotsList.setOnDragDetected(e -> {
            Dragboard db = snapshotsList.startDragAndDrop(TransferMode.ANY);
            ClipboardContent cc = new ClipboardContent();
            Snapshot snapshot = snapshotsList.getSelectionModel().getSelectedItem();
            snapshot.getBeamlineSet().updateBaseLevel();
            cc.put(SnapshotDataFormat.INSTANCE, snapshot);
            db.setContent(cc);
            e.consume();
        });
        final ContextMenu popup = new ContextMenu();
        final MenuItem deleteTagItem = new MenuItem("Remove Tag...");
        deleteTagItem.setOnAction(e -> {
            popup.hide();
            Snapshot item = snapshotsList.getSelectionModel().getSelectedItem();
            if (FXMessageDialog.openQuestion(getSite().getShell(), "Remove Tag",
                "Are you sure you want to remove the tag '" + item.getTagName().get() + "' from snapshot '"
                    + item.getDate() + "'?")) {
                actionManager.deleteTag(item);
            }
        });
        popup.getItems().add(deleteTagItem);
        snapshotsList.setContextMenu(popup);
        snapshotsList.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                Snapshot item = snapshotsList.getSelectionModel().getSelectedItem();
                if (item.getTagName().isPresent()) {
                    popup.show(beamlineSetsTree, e.getScreenX(), e.getScreenY());
                } else {
                    popup.hide();
                }
            }
        });
        snapshotsList.setOnMouseClicked(e -> {
            Snapshot snapshot = snapshotsList.getSelectionModel().getSelectedItem();
            deleteTagItem.setDisable(snapshot == null || !snapshot.getTagName().isPresent());
            if (e.getClickCount() == 2) {
                if (snapshot != null) {
                    actionManager.openSnapshot(snapshot);
                }
            }
        });

        content.setCenter(snapshotsList);
        snapshotsPane = new TitledPane("Snapshots", content);
        snapshotsPane.setMaxHeight(Double.MAX_VALUE);

        GridPane titleBox = new GridPane();
        titleBox.setHgap(5);
        Label titleText = new Label("Snapshots");
        titleText.textProperty().bind(snapshotsPane.textProperty());
        Button tagButton = new Button("Tag");
        tagButton.setTooltip(new Tooltip("Tag selected snapshot"));
        tagButton.disableProperty().bind(snapshotsList.selectionModelProperty().get().selectedItemProperty().isNull()
            .or(snapshotsPane.expandedProperty().not()));
        tagButton.setOnAction(e -> {
            final Snapshot snapshot = snapshotsList.getSelectionModel().getSelectedItem();
            final FXTaggingDialog dialog = new FXTaggingDialog(getSite().getShell());
            dialog.openAndWait().ifPresent(a -> actionManager.tagSnapshot(snapshot, a, dialog.getMessage()));
        });
        Button openButton = new Button("Open");
        openButton.setTooltip(new Tooltip("Open selected snapshot in a new Snapshot Viewer"));
        openButton.disableProperty().bind(snapshotsList.selectionModelProperty().get().selectedItemProperty().isNull()
            .or(snapshotsPane.expandedProperty().not()));
        openButton.setOnAction(e -> actionManager.openSnapshot(snapshotsList.getSelectionModel().getSelectedItem()));

        Button compareButton = new Button("Compare");
        compareButton.setTooltip(new Tooltip("Open selected snapshot the active Snapshot Viewer"));
        compareButton.disableProperty().bind(snapshotsList.selectionModelProperty().get().selectedItemProperty()
            .isNull().or(snapshotsPane.expandedProperty().not()));
        compareButton
            .setOnAction(e -> actionManager.compareSnapshot(snapshotsList.getSelectionModel().getSelectedItem()));

        setUpTitlePaneNode(titleText, true);
        setUpTitlePaneNode(tagButton, false);
        setUpTitlePaneNode(openButton, false);
        setUpTitlePaneNode(compareButton, false);

        int num = SaveRestoreService.getInstance().getNumberOfSnapshots();
        if (num > 0) {
            Button nextButton = new Button("",
                new ImageView(new Image(BrowserView.class.getResourceAsStream("/icons/1rightarrow.png"))));
            nextButton.setTooltip(new Tooltip("Load Next Batch"));
            nextButton.setOnAction(e -> selector.readSnapshots(false, false));
            nextButton.disableProperty()
                .bind(selector.selectedBeamlineSetProperty().isNull().or(selector.allSnapshotsLoadedProperty()));

            Button nextAllButton = new Button("",
                new ImageView(new Image(BrowserView.class.getResourceAsStream("/icons/2rightarrow.png"))));
            nextAllButton.setTooltip(new Tooltip("Load All"));
            nextAllButton.setOnAction(e -> selector.readSnapshots(false, true));
            nextAllButton.disableProperty()
                .bind(selector.selectedBeamlineSetProperty().isNull().or(selector.allSnapshotsLoadedProperty()));

            setUpTitlePaneNode(nextButton, false);
            setUpTitlePaneNode(nextAllButton, false);
            titleBox.addRow(0, titleText, nextButton, nextAllButton, openButton, compareButton, tagButton);
        } else {
            titleBox.addRow(0, titleText, openButton, compareButton, tagButton);
        }

        titleBox.setMaxWidth(Double.MAX_VALUE);
        titleText.setMaxWidth(Double.MAX_VALUE);
        snapshotsPane.setGraphic(titleBox);
        snapshotsPane.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        titleBox.prefWidthProperty().bind(scene.widthProperty().subtract(34));
        snapshotsPane.expandedProperty()
            .addListener((a, o, n) -> VBox.setVgrow(snapshotsPane, n ? Priority.ALWAYS : Priority.NEVER));
        return snapshotsPane;
    }

    private static void setUpTitlePaneNode(Region node, boolean title) {
        if (title) {
            GridPane.setHgrow(node, Priority.ALWAYS);
            GridPane.setHalignment(node, HPos.LEFT);
            GridPane.setFillWidth(node, true);
            GridPane.setValignment(node, VPos.CENTER);
        } else {
            GridPane.setHgrow(node, Priority.NEVER);
            GridPane.setHalignment(node, HPos.RIGHT);
            GridPane.setFillWidth(node, false);
            GridPane.setValignment(node, VPos.CENTER);
            node.setPadding(new Insets(3, 5, 3, 5));
        }
    }

    private void setUpElementsPaneTitle() {
        if (baseLevelBrowser != null) {
            BaseLevel bl = selector.selectedBaseLevelProperty().get();
            if (bl != null) {
                if (selector.isDefaultBranch()) {
                    baseLevelPane.setText(baseLevelBrowser.getTitleFor(Optional.of(bl), Optional.empty()));
                } else {
                    Branch branch = selector.selectedBranchProperty().get();
                    baseLevelPane
                        .setText(baseLevelBrowser.getTitleFor(Optional.of(bl), Optional.of(branch.getShortName())));
                }
            } else {
                baseLevelPane.setText(baseLevelBrowser.getTitleFor(Optional.empty(), Optional.empty()));
            }
        }
    }

    private void init() {
        selector.selectedBaseLevelProperty().addListener((a, o, n) -> {
            setUpElementsPaneTitle();
            beamlineSetsPane.setText("Beamline Sets for " + n.getPresentationName());
        });
        if (baseLevelBrowser != null) {
            selector.selectedBaseLevelProperty().bind(baseLevelBrowser.baseLevelProperty());
            selector.baseLevelsProperty().addListener((a, o, n) -> {
                try {
                    baseLevelBrowser.availableBaseLevelsProperty().set(baseLevelBrowser.transform(n));
                } catch (RuntimeException e) {
                    FXMessageDialog.openError(getSite().getShell(), "Base Level Error", e.getMessage());
                }
            });
        }
        selector.selectedBranchProperty().addListener((a, o, n) -> setUpElementsPaneTitle());
        selector.selectedBeamlineSetProperty().addListener((a, o, n) -> {
            if (n == null) {
                snapshotsPane.setText("Snapshots");
            } else {
                snapshotsPane.setText("Snapshots of " + n.getName());
            }
        });
        selector.beamlineSetsProperty().addListener((a, o, beamlineSets) -> {
            TreeItem<BeamlineSetWrapper> root = new BeamlineSetTreeItem("Root");
            Map<String, TreeItem<BeamlineSetWrapper>> items = new HashMap<>();
            items.put("", root);
            for (BeamlineSet set : beamlineSets) {
                String folder = set.getFolder();
                TreeItem<BeamlineSetWrapper> parent = items.get(folder);
                if (parent == null) {
                    parent = new BeamlineSetTreeItem(folder);
                    items.put(folder, parent);

                    String[] path = parent.getValue().path;
                    TreeItem<BeamlineSetWrapper> currentChild = parent;
                    for (int i = path.length - 1; i > -1; i--) {
                        String m = makeStringFromParts(path, 0, i);
                        TreeItem<BeamlineSetWrapper> ti = items.get(m);
                        if (ti == null) {
                            ti = new BeamlineSetTreeItem(m);
                            items.put(m, ti);
                            ti.getChildren().add(currentChild);
                            currentChild = ti;
                        } else {
                            ti.getChildren().add(currentChild);
                            break;
                        }
                    }
                }
                parent.getChildren().add(new BeamlineSetTreeItem(new BeamlineSetWrapper(set, null)));
            }
            beamlineSetsTree.setRoot(root);
            root.setExpanded(true);
        });
        selector.snapshotsProperty().addListener((a, o, n) -> {
            searchMode = false;
            snapshotsList.setItems(FXCollections.observableArrayList(n));
        });

        List<DataProviderWrapper> dpws = SaveRestoreService.getInstance().getDataProviders();
        if (!dpws.isEmpty()) {
            SaveRestoreService.getInstance().setSelectedDataProvider(dpws.get(0));
        }

        SaveRestoreService.getInstance().addPropertyChangeListener(SaveRestoreService.SELECTED_DATA_PROVIDER,
            e -> updateForDataProviderChange());
        updateForDataProviderChange();
    }

    private void updateForDataProviderChange() {
        DataProviderWrapper wrapper = SaveRestoreService.getInstance().getSelectedDataProvider();
        if (wrapper != null) {
            mainPane.getChildren().clear();
            if (wrapper.provider.areBaseLevelsSupported() && baseLevelPane != null) {
                mainPane.getChildren().addAll(baseLevelPane, dataPane);
            } else {
                mainPane.getChildren().addAll(dataPane);
            }
        }
    }

    private static String makeStringFromParts(String[] parts, int from, int to) {
        StringBuilder sb = new StringBuilder(100);
        for (int i = from; i < to; i++) {
            sb.append(parts[i]);
            if (i < to - 1) {
                sb.append('/');
            }
        }
        return sb.toString();
    }

    /**
     * Sets the snapshot search results. The list of snapshots replaces the current list of snapshots and the expression
     * is visible in the title of the snapshots pane. The base level and beamline sets panes are collapsed.
     *
     * @param expression the expression used for searching
     * @param snapshots the results
     */
    void setSearchResults(String expression, List<Snapshot> snapshots) {
        if (baseLevelPane != null) {
            baseLevelPane.expandedProperty().set(false);
        }
        beamlineSetsPane.expandedProperty().set(false);
        snapshotsPane.expandedProperty().set(true);
        snapshotsPane.setText("Search results for '" + expression + "'");
        searchMode = true;
        snapshotsList.setItems(FXCollections.observableArrayList(snapshots));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.fx.ui.workbench3.FXViewPart#setFxFocus()
     */
    @Override
    protected void setFxFocus() {
    }
}
