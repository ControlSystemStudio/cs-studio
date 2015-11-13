package org.csstudio.saverestore.ui.browser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.csstudio.saverestore.BaseLevel;
import org.csstudio.saverestore.BeamlineSet;
import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.Engine;
import org.csstudio.saverestore.Snapshot;
import org.csstudio.saverestore.ui.browser.logic.ActionManager;
import org.csstudio.saverestore.ui.browser.logic.Selector;
import org.csstudio.saverestore.ui.util.FXTaggingDialog;
import org.csstudio.saverestore.ui.util.SnapshotDataFormat;
import org.eclipse.fx.ui.workbench3.FXViewPart;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Callback;

/**
 *
 * <code>BrowserView</code> is the view that provides the browsing facilities for save and restore. The view consists
 * of an accordion panel, composed of three parts: isotopes, beamline sets and snapshots selector.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class BrowserView extends FXViewPart {

    private static final Image BEAMLINE_SET_IMAGE = new Image(BrowserView.class.getResourceAsStream("/icons/txt.png"));
    private static final ImageView ROOT_IMAGE = new ImageView(new Image(BrowserView.class.getResourceAsStream("/icons/kalzium.png")));

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
            return path[path.length-1];
        }

    }

    private static class BeamlineSetTreeItem extends TreeItem<BeamlineSetWrapper> {
        BeamlineSetTreeItem(BeamlineSetWrapper set) {
            super(set,new ImageView(BEAMLINE_SET_IMAGE));
        }
        BeamlineSetTreeItem(String name,ImageView graphics) {
            super(new BeamlineSetWrapper(null, name),graphics);
        }
        BeamlineSetTreeItem(String name) {
            super(new BeamlineSetWrapper(null, name));
        }
    }

    private BaseLevelBrowser<BaseLevel> baseLevelBrowser;
    private TreeView<BeamlineSetWrapper> beamlineSetsTree;
    private ListView<Snapshot> snapshotsList;
    private TitledPane snapshotsPane;
    private TitledPane elementsPane;
    private TitledPane beamlineSetsPane;
    private VBox mainPane;
    private VBox dataPane;

    private Selector selector = new Selector();
    private ActionManager actionManager = new ActionManager(selector, this);

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
            VBox.setVgrow(elementsPane, Priority.NEVER);
            mainPane.getChildren().setAll(elements,dataPane);
            elementsPane.setExpanded(true);
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
        elementsPane = new TitledPane(browser.getTitleFor(Optional.empty(), Optional.empty()), content);
        elementsPane.setMaxHeight(Double.MAX_VALUE);

        final GridPane titleBox = new GridPane();
        titleBox.setHgap(5);
        final Label titleText = new Label(browser.getTitleFor(Optional.empty(), Optional.empty()));
        titleText.textProperty().bind(elementsPane.textProperty());
        final ToggleButton filterButton = new ToggleButton("",new ImageView(new Image(BrowserView.class.getResourceAsStream("/icons/filter_ps.png"))));
        filterButton.setTooltip(new Tooltip("Disable non-existing"));
        filterButton.selectedProperty().addListener((a,o,n)->baseLevelBrowser.setShowOnlyAvailable(n));
        setUpTitlePaneNode(titleText,true);
        setUpTitlePaneNode(filterButton,false);
        titleBox.addRow(0, titleText, filterButton);
        titleBox.setMaxWidth(Double.MAX_VALUE);
        titleText.setMaxWidth(Double.MAX_VALUE);
        elementsPane.setGraphic(titleBox);
        elementsPane.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        titleBox.prefWidthProperty().bind(scene.widthProperty().subtract(34));
        filterButton.setSelected(true);

        return elementsPane;
    }

    private Node createBeamlineSetsPane() {
        GridPane content = new GridPane();

        beamlineSetsTree = new TreeView<>();
        beamlineSetsTree.getSelectionModel().selectedItemProperty().addListener((a,o,n) ->
                selector.selectedBeamlineSetProperty().set(
                        n == null || n.getValue().isFolder() ? null : n.getValue().set));
        beamlineSetsTree.setShowRoot(false);
        GridPane.setVgrow(beamlineSetsTree, Priority.ALWAYS);
        GridPane.setHgrow(beamlineSetsTree, Priority.ALWAYS);
        GridPane.setFillWidth(beamlineSetsTree, true);
        GridPane.setFillHeight(beamlineSetsTree, true);
        content.add(beamlineSetsTree,0,0);

        beamlineSetsPane = new TitledPane("Beamline Sets", content);
        beamlineSetsPane.setMaxHeight(Double.MAX_VALUE);

        final GridPane titleBox = new GridPane();
        titleBox.setHgap(5);
        final Label titleText = new Label("Beamline Sets");
        titleText.textProperty().bind(beamlineSetsPane.textProperty());
        final Button newButton = new Button("New");
        newButton.setTooltip(new Tooltip("Create a new Beamline Set"));
        final Button editButton = new Button("Edit");
        editButton.setTooltip(new Tooltip("Edit selected Beamline Set"));
        final Button copyButton = new Button("Copy");
        copyButton.setTooltip(new Tooltip("Copy Beamline Sets from another location"));
        final Button openButton = new Button("Open");
        openButton.setTooltip(new Tooltip("Open selected Beamline Set in Snapshot Viewer"));
        openButton.disableProperty().bind(selector.selectedBeamlineSetProperty().isNull()
                .or(beamlineSetsPane.expandedProperty().not()));
        openButton.setOnAction(e -> actionManager.openBeamlineSet(selector.selectedBeamlineSetProperty().get()));
        editButton.disableProperty().bind(selector.selectedBeamlineSetProperty().isNull()
                .or(beamlineSetsPane.expandedProperty().not()));
        editButton.setOnAction(e -> actionManager.editBeamlineSet(selector.selectedBeamlineSetProperty().get()));
        newButton.disableProperty().bind(selector.selectedBaseLevelProperty().isNull()
                .or(beamlineSetsPane.expandedProperty().not()));

        copyButton.disableProperty().bind(selector.selectedBaseLevelProperty().isNull()
                .or(beamlineSetsPane.expandedProperty().not()));

        //TODO add button actions
        setUpTitlePaneNode(titleText,true);
        setUpTitlePaneNode(newButton,false);
        setUpTitlePaneNode(copyButton,false);
        setUpTitlePaneNode(editButton,false);
        setUpTitlePaneNode(openButton,false);
        titleBox.addRow(0, titleText, copyButton, newButton, editButton, openButton);
        titleBox.setMaxWidth(Double.MAX_VALUE);
        titleText.setMaxWidth(Double.MAX_VALUE);
        beamlineSetsPane.setGraphic(titleBox);
        beamlineSetsPane.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        titleBox.prefWidthProperty().bind(scene.widthProperty().subtract(34));

        beamlineSetsPane.expandedProperty().addListener((a,o,n) -> VBox.setVgrow(beamlineSetsPane, n ? Priority.ALWAYS : Priority.NEVER));
        return beamlineSetsPane;
    }

    private Node createSnapshotsPane() {
        BorderPane content = new BorderPane();
        snapshotsList = new ListView<>();
        snapshotsList.setCellFactory(new Callback<ListView<Snapshot>, ListCell<Snapshot>>() {
            @Override
            public ListCell<Snapshot> call(ListView<Snapshot> param) {

                return new ListCell<Snapshot>(){
                    public void updateItem(Snapshot item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setTooltip(new Tooltip(item.getComment()));
                            setText(item.toString());
                        }
                    }
                };
            }
        });
        snapshotsList.setOnMouseClicked(e -> {
           if (e.getClickCount() == 2) {
               Snapshot snapshot = snapshotsList.getSelectionModel().getSelectedItem();
               if (snapshot != null) {
                   actionManager.openSnapshot(snapshot);
               }
           }
        });

        snapshotsList.setOnDragDetected(e -> {
            Dragboard db = snapshotsList.startDragAndDrop(TransferMode.ANY);
            ClipboardContent cc = new ClipboardContent();
            Snapshot snapshot = snapshotsList.getSelectionModel().getSelectedItem();
            snapshot.getBeamlineSet().updateBaseLevel();
            cc.put(SnapshotDataFormat.INSTANCE, snapshot);
            db.setContent(cc);
            e.consume();
        });

        content.setCenter(snapshotsList);
        snapshotsPane = new TitledPane("Snapshots", content);
        snapshotsPane.setMaxHeight(Double.MAX_VALUE);

        final GridPane titleBox = new GridPane();
        titleBox.setHgap(5);
        final Label titleText = new Label("Snapshots");
        titleText.textProperty().bind(snapshotsPane.textProperty());
        final Button tagButton = new Button("Tag");
        tagButton.setTooltip(new Tooltip("Tag selected snapshot"));
        tagButton.disableProperty().bind(snapshotsList.selectionModelProperty().get().selectedItemProperty().isNull()
                .or(snapshotsPane.expandedProperty().not()));
        tagButton.setOnAction(e -> {
            final Snapshot snapshot = snapshotsList.getSelectionModel().getSelectedItem();
            final FXTaggingDialog dialog = new FXTaggingDialog(getSite().getShell());
            dialog.openAndWait().ifPresent(a -> actionManager.tagSnapshot(snapshot,a,dialog.getMessage()));
        });
        final Button openButton = new Button("Open");
        openButton.setTooltip(new Tooltip("Open selected snapshot in a new Snapshot Viewer"));
        openButton.disableProperty().bind(snapshotsList.selectionModelProperty().get().selectedItemProperty().isNull()
                .or(snapshotsPane.expandedProperty().not()));
        openButton.setOnAction(e -> actionManager.openSnapshot(snapshotsList.getSelectionModel().getSelectedItem()));

        final Button compareButton = new Button("Compare");
        compareButton.setTooltip(new Tooltip("Open selected snapshot the active Snapshot Viewer"));
        compareButton.disableProperty().bind(snapshotsList.selectionModelProperty().get().selectedItemProperty().isNull()
                .or(snapshotsPane.expandedProperty().not()));
        compareButton.setOnAction(e -> actionManager.compareSnapshot(snapshotsList.getSelectionModel().getSelectedItem()));

        setUpTitlePaneNode(titleText,true);
        setUpTitlePaneNode(tagButton,false);
        setUpTitlePaneNode(openButton,false);
        setUpTitlePaneNode(compareButton,false);
        titleBox.addRow(0, titleText, openButton, compareButton, tagButton);
        titleBox.setMaxWidth(Double.MAX_VALUE);
        titleText.setMaxWidth(Double.MAX_VALUE);
        snapshotsPane.setGraphic(titleBox);
        snapshotsPane.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        titleBox.prefWidthProperty().bind(scene.widthProperty().subtract(34));
        snapshotsPane.expandedProperty().addListener((a,o,n) -> VBox.setVgrow(snapshotsPane, n ? Priority.ALWAYS : Priority.NEVER));
        return snapshotsPane;
    }

    private static void setUpTitlePaneNode(Region node, boolean title) {
        if (title) {
            GridPane.setHgrow(node, Priority.ALWAYS);
            GridPane.setHalignment(node, HPos.LEFT);
            GridPane.setFillWidth(node,true);
            GridPane.setValignment(node, VPos.CENTER);
        } else {
            GridPane.setHgrow(node, Priority.NEVER);
            GridPane.setHalignment(node, HPos.RIGHT);
            GridPane.setFillWidth(node,false);
            GridPane.setValignment(node, VPos.CENTER);
            node.setPadding(new Insets(3,3,3,3));
        }
    }

    private void setUpElementsPaneTitle() {
        if (baseLevelBrowser != null) {
            BaseLevel bl = selector.selectedBaseLevelProperty().get();
            if (bl != null) {
                if (selector.isMainBranch()) {
                    elementsPane.setText(baseLevelBrowser.getTitleFor(Optional.of(bl), Optional.empty()));
                } else {
                    String branch = selector.selectedBranchProperty().get();
                    elementsPane.setText(baseLevelBrowser.getTitleFor(Optional.of(bl),Optional.of(branch)));
                }
            } else {
                elementsPane.setText(baseLevelBrowser.getTitleFor(Optional.empty(), Optional.empty()));
            }
        }
    }

    private void init() {
        selector.selectedBaseLevelProperty().addListener((a,o,n) -> {
            setUpElementsPaneTitle();
            beamlineSetsPane.setText("Beamline Sets for " + n.getPresentationName());
        });
        if (baseLevelBrowser != null) {
            selector.selectedBaseLevelProperty().bind(baseLevelBrowser.baseLevelProperty());
            selector.baseLevelsProperty().addListener((a,o,n) -> {
                baseLevelBrowser.availableBaseLevelsProperty().set(baseLevelBrowser.transform(n));
            });
        }
        selector.selectedBranchProperty().addListener((a,o,n) -> setUpElementsPaneTitle());
        selector.selectedBeamlineSetProperty().addListener((a,o,n) -> {
            if (n == null) {
                snapshotsPane.setText("Snapshots");
            } else {
                if (n.getBaseLevel().isPresent()) {
                    snapshotsPane.setText("Snapshots of " + n.getName()  + " ("
                            + n.getBaseLevel().get().getPresentationName() + ")");
                } else {
                    snapshotsPane.setText("Snapshots of " + n.getName());
                }
            }
        });
        selector.beamlineSetsProperty().addListener((a,o,beamlineSets) -> {
            TreeItem<BeamlineSetWrapper> root = new BeamlineSetTreeItem("Root",ROOT_IMAGE);
            Map<String,TreeItem<BeamlineSetWrapper>> items = new HashMap<>();
            items.put("", root);
            for (BeamlineSet set : beamlineSets) {
                String folder = set.getFolder();
                TreeItem<BeamlineSetWrapper> parent = items.get(folder);
                if (parent == null) {
                    parent = new BeamlineSetTreeItem(folder);
                    items.put(folder,parent);

                    String[] path = parent.getValue().path;
                    TreeItem<BeamlineSetWrapper> currentChild = parent;
                    for (int i = path.length - 1; i > -1; i--) {
                        String m = makeStringFromParts(path, 0, i);
                        TreeItem<BeamlineSetWrapper> ti = items.get(m);
                        if (ti == null) {
                            ti = new BeamlineSetTreeItem(m);
                            items.put(m,ti);
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
        selector.snapshotsProperty().addListener((a,o,n) -> snapshotsList.getItems().setAll(n));

        List<DataProviderWrapper> dpws = Engine.getInstance().getDataProviders();
        if (dpws.size() > 0) {
            Engine.getInstance().setSelectedDataProvider(dpws.get(0));
        }

        Engine.getInstance().addPropertyChangeListener(Engine.SELECTED_DATA_PROVIDER, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                updateForDataProviderChange();
            }
        });
        updateForDataProviderChange();
    }

    private void updateForDataProviderChange() {
        DataProviderWrapper wrapper = Engine.getInstance().getSelectedDataProvider();
        if (wrapper != null) {
            mainPane.getChildren().clear();
            if (wrapper.provider.areBaseLevelsSupported() && elementsPane != null) {
                mainPane.getChildren().addAll(elementsPane, dataPane);
            } else {
                mainPane.getChildren().addAll(dataPane);
            }
        }
    }

    private static String makeStringFromParts(String[] parts, int from, int to) {
        StringBuilder sb = new StringBuilder(100);
        for (int i = from; i < to; i++) {
            sb.append(parts[i]);
            if (i < to - 1){
                sb.append('/');
            }
        }
        return sb.toString();
    }

    @Override
    protected void setFxFocus() {
    }

    public Window getWindow() {
        return scene.getWindow();
    }
}
